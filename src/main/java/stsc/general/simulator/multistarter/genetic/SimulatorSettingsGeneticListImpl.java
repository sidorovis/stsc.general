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
import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.MutableAlgorithmConfiguration;
import stsc.common.algorithms.StockExecution;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.SimulatorConfiguration;
import stsc.general.simulator.SimulatorConfigurationImpl;
import stsc.general.strategy.TradingStrategy;
import stsc.general.trading.TradeProcessorInit;
import stsc.storage.ExecutionsStorage;

/**
 * Stores all possible values from {@link SimulatorConfigurationImpl} for Genetic {@link TradingStrategy} Search.<br/>
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
	public synchronized SimulatorConfigurationImpl generateRandom() throws BadAlgorithmException {
		final ExecutionsStorage executionsStorage = new ExecutionsStorage();

		for (GeneticExecutionInitializer i : stockInitializers) {
			final StockExecution e = new StockExecution(i.getExecutionName(), i.algorithmName, i.generateRandom());
			executionsStorage.addStockExecution(e);
		}
		for (GeneticExecutionInitializer i : eodInitializers) {
			final EodExecution e = new EodExecution(i.getExecutionName(), i.algorithmName, i.generateRandom());
			executionsStorage.addEodExecution(e);
		}
		final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period, executionsStorage);
		final SimulatorConfigurationImpl ss = new SimulatorConfigurationImpl(id.getAndIncrement(), init);
		return ss;
	}

	@Override
	public SimulatorConfiguration mutate(SimulatorConfiguration settings) {
		final int initializersAmount = stockInitializers.size() + eodInitializers.size();
		final int mutateSettingIndex = randomizer.nextInt(initializersAmount);
		final SimulatorConfiguration copy = settings.clone();
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
	public SimulatorConfiguration merge(SimulatorConfiguration left, SimulatorConfiguration right) {
		final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period);
		final ExecutionsStorage resultEs = init.getExecutionsStorage();

		mergeStocks(resultEs, left, right);
		mergeEods(resultEs, left, right);

		return new SimulatorConfigurationImpl(id.getAndIncrement(), init);
	}

	private ExecutionsStorage mergeStocks(ExecutionsStorage result, SimulatorConfiguration left, SimulatorConfiguration right) {
		final List<StockExecution> leftList = left.getInit().getExecutionsStorage().getStockExecutions();
		final List<StockExecution> rightList = right.getInit().getExecutionsStorage().getStockExecutions();

		if (leftList.size() != stockInitializers.size()) {
			logger.error(id + " merge Stock SimulatorSettings have different amount of StockExecutions from stockInitializers");
		}

		if (leftList.size() != rightList.size()) {
			logger.error(id + " merge Stock SimulatorSettings have different amount of StockExecutions");
		}

		final Iterator<GeneticExecutionInitializer> initializer = stockInitializers.iterator();
		final Iterator<StockExecution> leftIterator = leftList.iterator();
		final Iterator<StockExecution> rightIterator = rightList.iterator();

		while (initializer.hasNext() && leftIterator.hasNext() && rightIterator.hasNext()) {
			final GeneticExecutionInitializer geneticInitializer = initializer.next();
			final StockExecution leftSe = leftIterator.next();
			final StockExecution rightSe = rightIterator.next();

			final MutableAlgorithmConfiguration settings = geneticInitializer.mergeStock(leftSe, rightSe);
			result.addStockExecution(new StockExecution(geneticInitializer.getExecutionName(), leftSe.getAlgorithmType(), settings));
		}
		return result;
	}

	private ExecutionsStorage mergeEods(ExecutionsStorage result, SimulatorConfiguration left, SimulatorConfiguration right) {
		final List<EodExecution> leftList = left.getInit().getExecutionsStorage().getEodExecutions();
		final List<EodExecution> rightList = right.getInit().getExecutionsStorage().getEodExecutions();

		if (leftList.size() != eodInitializers.size()) {
			logger.error(id + " merge Eod SimulatorSettings have different amount of StockExecutions from eodInitializers");
		}

		if (leftList.size() != rightList.size()) {
			logger.error(id + " merge Eod SimulatorSettings have different amount of StockExecutions");
		}

		final Iterator<GeneticExecutionInitializer> initializer = eodInitializers.iterator();
		final Iterator<EodExecution> leftIterator = leftList.iterator();
		final Iterator<EodExecution> rightIterator = rightList.iterator();

		while (initializer.hasNext() && leftIterator.hasNext() && rightIterator.hasNext()) {
			final GeneticExecutionInitializer geneticInitializer = initializer.next();
			final EodExecution leftSe = leftIterator.next();
			final EodExecution rightSe = rightIterator.next();

			final MutableAlgorithmConfiguration settings = geneticInitializer.mergeEod(leftSe, rightSe);
			result.addEodExecution(new EodExecution(geneticInitializer.getExecutionName(), leftSe.getAlgorithmType(), settings));
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
