package stsc.general.simulator;

import java.io.File;
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
public class Simulator {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/simulator.log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("Simulator");

	private final Metrics metrics;
	private final SignalsStorage signalsStorage;

	public Simulator(final SimulatorSettings settings, Set<String> stockNames) throws BadAlgorithmException, BadSignalException {
		logger.info("Simulator starting on " + Joiner.on(",").join(stockNames));
		final TradeProcessor tradeProcessor = new TradeProcessor(settings.getInit());
		metrics = tradeProcessor.simulate(settings.getInit().getPeriod(), stockNames);
		signalsStorage = tradeProcessor.getExecutionStorage().getSignalsStorage();
		logger.info("Simulated finished");
	}

	public Simulator(final SimulatorSettings settings) throws BadAlgorithmException, BadSignalException {
		logger.info("Simulator starting");
		final TradeProcessor tradeProcessor = new TradeProcessor(settings.getInit());
		this.metrics = tradeProcessor.simulate(settings.getInit().getPeriod());
		signalsStorage = tradeProcessor.getExecutionStorage().getSignalsStorage();
		logger.info("Simulated finished");
	}

	public static Simulator fromConfig(final StockStorage stockStorage, final FromToPeriod period, final String config) throws BadAlgorithmException,
			BadSignalException, Exception {
		return new Simulator(new SimulatorSettings(0, new TradeProcessorInit(stockStorage, period, config)));
	}

	public static Simulator fromFile(final File filePath) throws BadAlgorithmException, BadSignalException, Exception {
		return new Simulator(new SimulatorSettings(0, new TradeProcessorInit(filePath)));
	}

	public Metrics getMetrics() {
		return metrics;
	}

	public SignalsStorage getSignalsStorage() {
		return signalsStorage;
	}

}
