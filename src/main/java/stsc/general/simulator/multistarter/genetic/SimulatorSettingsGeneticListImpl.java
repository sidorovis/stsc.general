package stsc.general.simulator.multistarter.genetic;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodExecutionInstance;
import stsc.common.algorithms.MutableAlgorithmConfiguration;
import stsc.common.algorithms.StockExecutionInstance;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.Execution;
import stsc.general.simulator.ExecutionImpl;
import stsc.general.strategy.TradingStrategy;
import stsc.general.trading.TradeProcessorInit;
import stsc.storage.ExecutionInstancesStorage;

/**
 * Stores all possible values from {@link ExecutionImpl} for Genetic {@link TradingStrategy} Search.<br/>
 * 
 */
public final class SimulatorSettingsGeneticListImpl implements ExternalizableGeneticList {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/simulator_settings_genetic_list_log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger(SimulatorSettingsGeneticListImpl.class.getName());

	private AtomicLong id;
	private final Random randomizer = new Random();

	private final StockStorage stockStorage;
	private final FromToPeriod period;

	private final List<GeneticExecutionInitializer> stockInitializers;
	private final List<GeneticExecutionInitializer> eodInitializers;

	SimulatorSettingsGeneticListImpl(StockStorage stockStorage, FromToPeriod period, List<GeneticExecutionInitializer> stockInitializers,
			List<GeneticExecutionInitializer> eodInitializers) {
		super();
		this.id = new AtomicLong(0);
		this.stockStorage = stockStorage;
		this.period = period;
		this.stockInitializers = stockInitializers;
		this.eodInitializers = eodInitializers;
	}

	@Override
	public synchronized ExecutionImpl generateRandom() throws BadAlgorithmException {
		final ExecutionInstancesStorage executionsStorage = new ExecutionInstancesStorage();

		for (GeneticExecutionInitializer i : stockInitializers) {
			final StockExecutionInstance e = new StockExecutionInstance(i.getExecutionName(), i.algorithmName, i.generateRandom());
			executionsStorage.addStockExecution(e);
		}
		for (GeneticExecutionInitializer i : eodInitializers) {
			final EodExecutionInstance e = new EodExecutionInstance(i.getExecutionName(), i.algorithmName, i.generateRandom());
			executionsStorage.addEodExecution(e);
		}
		final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period, executionsStorage);
		final ExecutionImpl ss = new ExecutionImpl(id.getAndIncrement(), init);
		return ss;
	}

	@Override
	public Execution mutate(Execution settings) {
		final int initializersAmount = stockInitializers.size() + eodInitializers.size();
		final int mutateSettingIndex = randomizer.nextInt(initializersAmount);
		final Execution copy = settings.clone();
		if (stockInitializers.size() > mutateSettingIndex) {
			final GeneticExecutionInitializer init = stockInitializers.get(mutateSettingIndex);
			init.mutateStock(mutateSettingIndex, copy);
		} else {
			final int eodIndex = mutateSettingIndex - stockInitializers.size();
			final GeneticExecutionInitializer init = eodInitializers.get(eodIndex);
			init.mutateEod(eodIndex, copy);
		}
		return copy;
	}

	@Override
	public Execution merge(Execution left, Execution right) {
		final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period);
		final ExecutionInstancesStorage resultEs = init.getExecutionsStorage();

		mergeStocks(resultEs, left, right);
		mergeEods(resultEs, left, right);

		return new ExecutionImpl(id.getAndIncrement(), init);
	}

	private ExecutionInstancesStorage mergeStocks(ExecutionInstancesStorage result, Execution left, Execution right) {
		final List<StockExecutionInstance> leftList = left.getInit().getExecutionsStorage().getStockExecutions();
		final List<StockExecutionInstance> rightList = right.getInit().getExecutionsStorage().getStockExecutions();

		if (leftList.size() != stockInitializers.size()) {
			logger.error(id + " merge Stock SimulatorSettings have different amount of StockExecutions from stockInitializers");
		}

		if (leftList.size() != rightList.size()) {
			logger.error(id + " merge Stock SimulatorSettings have different amount of StockExecutions");
		}

		final Iterator<GeneticExecutionInitializer> initializer = stockInitializers.iterator();
		final Iterator<StockExecutionInstance> leftIterator = leftList.iterator();
		final Iterator<StockExecutionInstance> rightIterator = rightList.iterator();

		while (initializer.hasNext() && leftIterator.hasNext() && rightIterator.hasNext()) {
			final GeneticExecutionInitializer geneticInitializer = initializer.next();
			final StockExecutionInstance leftSe = leftIterator.next();
			final StockExecutionInstance rightSe = rightIterator.next();

			final MutableAlgorithmConfiguration settings = geneticInitializer.mergeStock(leftSe, rightSe);
			result.addStockExecution(new StockExecutionInstance(geneticInitializer.getExecutionName(), leftSe.getAlgorithmType(), settings));
		}
		return result;
	}

	private ExecutionInstancesStorage mergeEods(ExecutionInstancesStorage result, Execution left, Execution right) {
		final List<EodExecutionInstance> leftList = left.getInit().getExecutionsStorage().getEodExecutions();
		final List<EodExecutionInstance> rightList = right.getInit().getExecutionsStorage().getEodExecutions();

		if (leftList.size() != eodInitializers.size()) {
			logger.error(id + " merge Eod SimulatorSettings have different amount of StockExecutions from eodInitializers");
		}

		if (leftList.size() != rightList.size()) {
			logger.error(id + " merge Eod SimulatorSettings have different amount of StockExecutions");
		}

		final Iterator<GeneticExecutionInitializer> initializer = eodInitializers.iterator();
		final Iterator<EodExecutionInstance> leftIterator = leftList.iterator();
		final Iterator<EodExecutionInstance> rightIterator = rightList.iterator();

		while (initializer.hasNext() && leftIterator.hasNext() && rightIterator.hasNext()) {
			final GeneticExecutionInitializer geneticInitializer = initializer.next();
			final EodExecutionInstance leftSe = leftIterator.next();
			final EodExecutionInstance rightSe = rightIterator.next();

			final MutableAlgorithmConfiguration settings = geneticInitializer.mergeEod(leftSe, rightSe);
			result.addEodExecution(new EodExecutionInstance(geneticInitializer.getExecutionName(), leftSe.getAlgorithmType(), settings));
		}
		return result;
	}

	@Override
	public long getId() {
		return id.get();
	}

	public StockStorage getStockStorage() {
		return stockStorage;
	}

	@Override
	public FromToPeriod getPeriod() {
		return period;
	}

	@Override
	public List<GeneticExecutionInitializer> getStockInitializers() {
		return stockInitializers;
	}

	@Override
	public List<GeneticExecutionInitializer> getEodInitializers() {
		return eodInitializers;
	}

	@Override
	public long size() {
		long result = 1;
		for (GeneticExecutionInitializer ei : stockInitializers) {
			result *= ei.size();
		}
		for (GeneticExecutionInitializer ei : eodInitializers) {
			result *= ei.size();
		}
		return result;
	}

}
