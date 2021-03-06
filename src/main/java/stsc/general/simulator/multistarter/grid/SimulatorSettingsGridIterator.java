package stsc.general.simulator.multistarter.grid;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodExecutionInstance;
import stsc.common.algorithms.StockExecutionInstance;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.ExecutionImpl;
import stsc.general.trading.TradeProcessorInit;
import stsc.storage.ExecutionInstancesStorage;

/**
 * This iterator could be created only one for each list, so all iterators will
 * iterate like singleton will do
 */
public class SimulatorSettingsGridIterator implements Iterator<ExecutionImpl> {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/simulator_settings_iterator_log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("SimulatorSettingsGridIterator");

	private boolean finished;
	private final StockStorage stockStorage;
	private final FromToPeriod period;

	private final List<GridExecutionInitializer> stockInitializers;
	private final List<GridExecutionInitializer> eodInitializers;

	private AtomicLong ssId;

	SimulatorSettingsGridIterator(StockStorage stockStorage, FromToPeriod period, List<GridExecutionInitializer> stocks, List<GridExecutionInitializer> eods, boolean finished) {
		this.finished = finished;
		this.stockStorage = stockStorage;
		this.period = period;
		this.stockInitializers = stocks;
		this.eodInitializers = eods;
		this.ssId = new AtomicLong();
	}

	@Override
	public synchronized boolean hasNext() {
		return !finished;
	}

	@Override
	public synchronized ExecutionImpl next() {
		ExecutionImpl result = null;
		try {
			result = generateSimulatorSettings();
		} catch (BadAlgorithmException e) {
			logger.error("Problem with generating SimulatorSettings: " + e.getMessage());
			result = new ExecutionImpl(-1, new TradeProcessorInit(stockStorage, period));
			finished = true;
		}
		generateNext();
		return result;
	}

	synchronized void reset() {
		for (GridExecutionInitializer i : stockInitializers) {
			i.reset();
		}
		for (GridExecutionInitializer i : eodInitializers) {
			i.reset();
		}
	}

	@Override
	public synchronized void remove() {
	}

	private void generateNext() {
		int index = generateNext(stockInitializers);
		if (index == stockInitializers.size()) {
			index = generateNext(eodInitializers);
			if (index == eodInitializers.size())
				finished = true;
		}
	}

	private int generateNext(final List<GridExecutionInitializer> initializers) {
		int index = 0;
		while (index < initializers.size()) {
			final GridExecutionInitializer ei = initializers.get(index);
			if (ei.hasNext()) {
				ei.next();
				if (ei.hasNext()) {
					return index;
				} else {
					ei.reset();
					index += 1;
				}
			} else {
				index += 1;
			}
		}
		return index;
	}

	private ExecutionImpl generateSimulatorSettings() throws BadAlgorithmException {
		ExecutionInstancesStorage executionsStorage = new ExecutionInstancesStorage();
		for (GridExecutionInitializer i : stockInitializers) {
			final StockExecutionInstance e = new StockExecutionInstance(i.executionName, i.algorithmName, i.current());
			executionsStorage.addStockExecution(e);
		}
		for (GridExecutionInitializer i : eodInitializers) {
			final EodExecutionInstance e = new EodExecutionInstance(i.executionName, i.algorithmName, i.current());
			executionsStorage.addEodExecution(e);
		}

		final TradeProcessorInit init = new TradeProcessorInit(stockStorage, period, executionsStorage);
		final ExecutionImpl ss = new ExecutionImpl(ssId.getAndIncrement(), init);
		return ss;
	}

}
