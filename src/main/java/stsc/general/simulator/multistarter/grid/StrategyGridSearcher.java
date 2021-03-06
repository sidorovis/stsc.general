package stsc.general.simulator.multistarter.grid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import com.google.common.util.concurrent.AtomicDouble;

import stsc.common.BadSignalException;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorImpl;
import stsc.general.simulator.ExecutionImpl;
import stsc.general.simulator.multistarter.StrategySearcher;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.strategy.TradingStrategy;
import stsc.general.strategy.selector.StrategySelector;

/**
 * Multi-thread Strategy Grid Searcher.
 */
public final class StrategyGridSearcher implements StrategySearcher {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/mt_strategy_grid_searcher_log4j2.xml");
	}

	private static class IteratorProxy implements Iterator<ExecutionImpl> {
		private final Iterator<ExecutionImpl> value;

		IteratorProxy(Iterator<ExecutionImpl> value) {
			this.value = value;
		}

		@Override
		public synchronized boolean hasNext() {
			return value.hasNext();
		}

		@Override
		public synchronized ExecutionImpl next() {
			return value.next();
		}
	}

	private static Logger logger = LogManager.getLogger("StrategyGridSearcher");

	private final Set<String> processedSettings = new HashSet<>();
	private final StrategySelector selector;
	private final double fullSize;
	private final AtomicDouble processedSize = new AtomicDouble(1.0);

	private final class StatisticsCalculationThread extends Thread {

		private IndicatorProgressListener progressListener = null;

		private final double fullSize;
		private final AtomicDouble processedSize;

		private final IteratorProxy iterator;
		private final StrategySelector selector;
		private boolean stoppedByRequest;

		StatisticsCalculationThread(double fullSize, AtomicDouble processedSize, final IteratorProxy iterator, final StrategySelector selector) {
			this.fullSize = fullSize;
			this.processedSize = processedSize;
			this.iterator = iterator;
			this.selector = selector;
			this.stoppedByRequest = false;
		}

		@Override
		public void run() {
			Optional<ExecutionImpl> settings = getNextSimulatorSettings();

			while (settings.isPresent()) {
				try {
					final Simulator simulator = new SimulatorImpl();
					simulator.simulateMarketTrading(settings.get());
					final TradingStrategy strategy = new TradingStrategy(settings.get(), simulator.getMetrics());
					selector.addStrategy(strategy);
					settings = getNextSimulatorSettings();
				} catch (BadAlgorithmException | BadSignalException e) {
					logger.error("Error while calculating statistics: " + e.getMessage());
				}
			}
		}

		private Optional<ExecutionImpl> getNextSimulatorSettings() {
			synchronized (iterator) {
				while (!stoppedByRequest && iterator.hasNext()) {
					final ExecutionImpl nextValue = iterator.next();
					if (nextValue == null)
						return Optional.empty();
					final String hashCode = nextValue.stringHashCode();
					if (processedSettings.contains(hashCode)) {
						logger.debug("Already resolved: " + hashCode);
						continue;
					} else {
						processedSettings.add(hashCode);
					}
					final double processedSize = this.processedSize.getAndAdd(1.0);
					if (progressListener != null) {
						progressListener.processed(processedSize / fullSize);
					}
					return Optional.of(nextValue);
				}
			}
			return Optional.empty();
		}

		public void stopSearch() {
			this.stoppedByRequest = true;
		}

		public void addIndicatorProgress(IndicatorProgressListener listener) {
			progressListener = listener;
		}
	}

	final List<StatisticsCalculationThread> threads = new ArrayList<>();

	public static StrategyGridSearcherBuilder getBuilder() {
		return new StrategyGridSearcherBuilder();
	}

	StrategyGridSearcher(StrategyGridSearcherBuilder builder) {
		Validate.notNull(builder.getSimulatorSettingsGridList(), "SimulatorSettingsGridList should not be null");

		this.selector = builder.getSelector();
		this.fullSize = (double) builder.getSimulatorSettingsGridList().size();
		final IteratorProxy iteratorProxy = new IteratorProxy(builder.getSimulatorSettingsGridList().iterator());
		logger.debug("Starting");

		for (int i = 0; i < builder.getThreadAmount(); ++i) {
			threads.add(new StatisticsCalculationThread(fullSize, processedSize, iteratorProxy, selector));
		}
		for (Thread t : threads) {
			t.start();
		}
		logger.debug("Finishing");

	}

	@Override
	public void stopSearch() {
		for (StatisticsCalculationThread t : threads) {
			t.stopSearch();
		}
	}

	@Override
	public StrategySelector waitAndGetSelector() throws StrategySearcherException {
		try {
			for (Thread t : threads) {
				t.join();
			}
		} catch (InterruptedException e) {
			throw new StrategySearcherException(e.getMessage());
		}
		return selector;
	}

	@Override
	public synchronized void addIndicatorProgress(IndicatorProgressListener listener) {
		for (StatisticsCalculationThread t : threads) {
			t.addIndicatorProgress(listener);
		}
	}
}
