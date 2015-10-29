package stsc.general.simulator;

import java.io.File;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.BadSignalException;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.SignalsStorage;
import stsc.common.storage.StockStorage;
import stsc.general.statistic.Metrics;
import stsc.general.trading.TradeProcessor;
import stsc.general.trading.TradeProcessorInit;

import com.google.common.base.Joiner;

/**
 * This class includes all necessary procedures and data for trading simulation.
 */
public final class SimulatorImpl implements Simulator {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/simulator.log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger(SimulatorImpl.class.getName());

	private Metrics metrics;
	private SignalsStorage signalsStorage;

	public SimulatorImpl() {
	}

	@Override
	public void simulateMarketTrading(SimulatorConfiguration simulatorSettings) throws BadAlgorithmException, BadSignalException {
		final Optional<Set<String>> stockNames = simulatorSettings.getStockNames();
		if (stockNames.isPresent()) {
			logger.info("Simulator starting on " + Joiner.on(",").join(stockNames.get()));
		} else {
			logger.info("Simulator starting on all possible stocks");
		}
		final TradeProcessor tradeProcessor = new TradeProcessor(simulatorSettings.getInit());
		metrics = tradeProcessor.simulate(simulatorSettings.getInit().getPeriod(), simulatorSettings.getStockNames());
		signalsStorage = tradeProcessor.getExecutionStorage().getSignalsStorage();
		logger.info("Simulated finished");
	}

	public static Simulator fromConfig(final StockStorage stockStorage, final FromToPeriod period, final String config)
			throws BadAlgorithmException, BadSignalException, Exception {
		final Simulator simulator = new SimulatorImpl();
		simulator.simulateMarketTrading(new SimulatorConfigurationImpl(0, new TradeProcessorInit(stockStorage, period, config)));
		return simulator;
	}

	public static Simulator fromFile(final File filePath) throws BadAlgorithmException, BadSignalException, Exception {
		final Simulator simulator = new SimulatorImpl();
		simulator.simulateMarketTrading(new SimulatorConfigurationImpl(0, new TradeProcessorInit(filePath)));
		return simulator;
	}

	@Override
	public Metrics getMetrics() {
		return metrics;
	}

	@Override
	public SignalsStorage getSignalsStorage() {
		return signalsStorage;
	}

}
