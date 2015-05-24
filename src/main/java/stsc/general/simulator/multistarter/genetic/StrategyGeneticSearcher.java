package stsc.general.simulator.multistarter.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.StrategySearcher;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.strategy.TradingStrategy;
import stsc.general.strategy.selector.StrategySelector;

import com.google.common.math.DoubleMath;

/**
 * {@link StrategyGeneticSearcher} is a class. ;)<br/>
 * Implementation details: <br/>
 * a. multi-threaded (constructor argument control this setting);<br/>
 * b. store empirical default parameter value for genetic search (best percent
 * of population that should continue live after genetic cycle;<br/>
 * c. store empirical crossover default parameter value for genetic search
 * (percent of population that will be created in the result of merging);<br/>
 * <hr/>
 * Class Usage:<br/>
 * 1. Construct it: {@link StrategyGeneticSearcher#StrategyGeneticSearcher}; <br/>
 * 2. Call {@link StrategyGeneticSearcher#waitAndGetSelector()} to wait and
 * receive results;<br/>
 * 3*. Optional you can add listener (
 * {@link StrategyGeneticSearcher#addIndicatorProgress()} ) to receive progress
 * statuses; <br/>
 * 4*. You can also stop search process (it will not stop immediately!).<br/>
 * <b> If you will not do 2nd step - it's highly possible that process will
 * never finish.</b>
 * <hr/>
 * Algorithm details:<br/>
 * 1. Setups {@link CountDownLatch} for amount of tasks that we will resolve per
 * population cycle; <br/>
 * 2. Creates and starts for execution {@link GenerateInitialPopulationsTask};<br/>
 * 3. {@link GenerateInitialPopulationsTask} creates amount of
 * {@link SimulatorCalulatingTask} (equal to amount of population);<br/>
 * 4. Each {@link SimulatorCalulatingTask} decrease CountDownLatch amount and
 * when all tasks will be resolved we could continue; <br/>
 * 5. Executes genetic algorithm iteration:<br/>
 * 5.a. Creates new population by next rules:<br/>
 * 5.a.1 Copies amount of old population that we consider as 'best' and should
 * be used as stable population;<br/>
 * 5.a.2 Crossover stage: we merge random elements from old population and add
 * them to new population;<br/>
 * 5.a.3 Mutation stage: we mutate random elements from old population and add
 * them to new population;<br/>
 * 5.b. Create additional special check that population actually changed (we
 * compare sum of cost function for Metrics from old population and new
 * population). 6. Also check amount of already processed populations (we have
 * restriction in there).
 */
public class StrategyGeneticSearcher implements StrategySearcher {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/strategy_genetic_searcher_log4j2.xml");
	}

	static Logger logger = LogManager.getLogger("StrategyGeneticSearcher");

	private final static int MINIMUM_STEPS_AMOUNT = 10;

	private final Random indexRandomizator = new Random();
	private int currentSelectionIndex = 0;
	// maximum found population cost: we reset it each time when we found
	private double maxPopulationCost = -Double.MAX_VALUE;
	// we reset it after each genetic search iteration
	private CountDownLatch populationCalculationTasksLatch;

	private final SimulatorSettingsGeneticList simulatorSettingsGeneticList;
	private final StrategySelector strategySelector;
	private final ExecutorService executor;
	private final CostFunction populationCostFunction;
	private final GeneticSearchSettings settings;

	private final List<SimulatorCalulatingTask> simulatorCalculatingTasks = new ArrayList<>();
	List<TradingStrategy> population = Collections.synchronizedList(new ArrayList<TradingStrategy>());;
	// Boolean mean that Strategy was added as part of best strategies
	private final Map<TradingStrategy, Boolean> sortedPopulation = Collections.synchronizedMap(new HashMap<TradingStrategy, Boolean>());

	private boolean stoppedByRequest = false;
	private volatile IndicatorProgressListener progressIndicator = null;

	public static StrategyGeneticSearcherBuilder getBuilder() {
		return new StrategyGeneticSearcherBuilder();
	}

	StrategyGeneticSearcher(final StrategyGeneticSearcherBuilder builder) {
		Validate.notNull(builder.simulatorSettingsGeneticList);
		Validate.notNull(builder.strategySelector);
		Validate.notNull(builder.populationCostFunction);

		this.simulatorSettingsGeneticList = builder.simulatorSettingsGeneticList;
		this.strategySelector = builder.strategySelector;
		this.executor = Executors.newFixedThreadPool(builder.threadAmount);
		this.populationCostFunction = builder.populationCostFunction;
		this.settings = new GeneticSearchSettings(builder);

		this.populationCalculationTasksLatch = new CountDownLatch(builder.populationSize);

		startSearcher();
	}

	private void startSearcher() {
		executor.submit(new GenerateInitialPopulationsTask(this, settings.populationSize));
	}

	@Override
	public StrategySelector waitAndGetSelector() throws StrategySearcherException {
		try {
			waitResults();
		} catch (Exception e) {
			throw new StrategySearcherException(e.getMessage());
		}
		executor.shutdown();
		return strategySelector;
	}

	@Override
	public void stopSearch() {
		this.stoppedByRequest = true;
	}

	@Override
	public void addIndicatorProgress(IndicatorProgressListener r) {
		progressIndicator = r;
	}

	SimulatorSettings getRandomSimulatorSettings() throws BadAlgorithmException {
		return simulatorSettingsGeneticList.generateRandom();
	}

	private void waitResults() throws InterruptedException {
		double lastCostSum = maxPopulationCost;
		while (!stoppedByRequest && currentSelectionIndex < settings.maxPopulationsAmount) {
			populationCalculationTasksLatch.await();
			populationCalculationTasksLatch = new CountDownLatch(settings.getTasksSize());
			lastCostSum = geneticAlgorithmIteration(lastCostSum);
		}
	}

	private double geneticAlgorithmIteration(final double lastCostSum) {
		final double newCostSum = calculateCostSum();
		final List<TradingStrategy> currentPopulation = population;

		createNewPopulation(currentPopulation);
		crossover(currentPopulation);
		mutation(currentPopulation);
		checkResult(newCostSum, lastCostSum);

		return newCostSum;
	}

	private void checkResult(double newCostSum, double lastCostSum) {
		if (currentSelectionIndex > MINIMUM_STEPS_AMOUNT && shouldTerminate(newCostSum, lastCostSum)) {
			currentSelectionIndex = settings.maxPopulationsAmount;
			logger.debug("summary cost of statistics not changed throw iteration on valuable value");
		} else {
			for (SimulatorCalulatingTask task : simulatorCalculatingTasks) {
				executor.submit(task);
			}
			simulatorCalculatingTasks.clear();
		}
		if (lastCostSum > maxPopulationCost) {
			maxPopulationCost = lastCostSum;
		}
		currentSelectionIndex += 1;
		if (progressIndicator != null) {
			progressIndicator.processed((double) currentSelectionIndex / settings.maxPopulationsAmount);
		}
	}

	private void createNewPopulation(List<TradingStrategy> currentPopulation) {
		population = Collections.synchronizedList(new ArrayList<TradingStrategy>());

		if (settings.sizeOfBest > 0) {
			for (TradingStrategy strategy : strategySelector.getStrategies()) {
				final Boolean pe = sortedPopulation.get(strategy);
				if (pe != null && pe) {
					population.add(strategy);
					if (population.size() == settings.sizeOfBest) {
						break;
					}
				}
			}
		}

		sortedPopulation.clear();
		for (TradingStrategy strategy : population) {
			sortedPopulation.put(strategy, true);
		}
	}

	private void crossover(final List<TradingStrategy> currentPopulation) {
		final int size = currentPopulation.size();
		if (size == 0) {
			return;
		}
		final Random r = new Random();

		for (int i = 0; i < settings.crossoverSize; ++i) {
			final int leftIndex = r.nextInt(size);
			final int rightIndex = r.nextInt(size);

			final SimulatorSettings left = currentPopulation.get(leftIndex).getSettings();
			final SimulatorSettings right = currentPopulation.get(rightIndex).getSettings();

			final SimulatorSettings mergedStatistics = simulatorSettingsGeneticList.merge(left, right);
			simulatorCalculatingTasks.add(new SimulatorCalulatingTask(this, mergedStatistics));
		}
	}

	private void mutation(final List<TradingStrategy> currentPopulation) {
		final int size = currentPopulation.size();
		if (size == 0) {
			return;
		}
		for (int i = 0; i < settings.mutationSize; ++i) {
			final int index = indexRandomizator.nextInt(size);
			final SimulatorSettings settings = currentPopulation.get(index).getSettings();
			final SimulatorSettings mutatedSettings = simulatorSettingsGeneticList.mutate(settings);
			simulatorCalculatingTasks.add(new SimulatorCalulatingTask(this, mutatedSettings));
		}
	}

	private boolean shouldTerminate(double newCostSum, double lastCostSum) {
		final boolean isMaxCostSum = DoubleMath.fuzzyEquals(newCostSum, maxPopulationCost, Settings.doubleEpsilon);
		final boolean costSumNotChanged = DoubleMath.fuzzyEquals(newCostSum, lastCostSum, Settings.doubleEpsilon);
		return isMaxCostSum && costSumNotChanged;
	}

	private double calculateCostSum() {
		double lastCostSum = 0.0;
		for (TradingStrategy e : population) {
			lastCostSum += populationCostFunction.calculate(e.getMetrics());
		}
		return lastCostSum;
	}

	/**
	 * This method will be called from Genetic Search Subtasks for example:
	 * {@link SimulatorCalulatingTask} in case when task was resolved.<br/>
	 * It will be called in any case if simulation failed or succeed.
	 */
	void simulationCalculationFinished() {
		populationCalculationTasksLatch.countDown();
	}

	/**
	 * This method will be called from Genetic Search Subtasks for example:
	 * {@link SimulatorCalulatingTask} when simulation accomplished.
	 */
	boolean addTradingStrategy(final TradingStrategy strategy) {
		boolean result = false;
		final Optional<TradingStrategy> addedToStatistics = strategySelector.addStrategy(strategy);
		if (addedToStatistics.isPresent()) {
			if (!addedToStatistics.get().equals(strategy)) {
				population.add(strategy);
				sortedPopulation.put(strategy, true);
				result = true;
			} else {
				sortedPopulation.put(strategy, false);
			}
		} else {
			result = true;
		}
		return result;
	}

	/**
	 * This method will be called from Genetic Search Subtasks for example:
	 * {@link SimulatorCalulatingTask} when simulation accomplished.
	 */
	void addTaskToExecutor(final SimulatorCalulatingTask task) {
		this.executor.submit(task);
	}

}
