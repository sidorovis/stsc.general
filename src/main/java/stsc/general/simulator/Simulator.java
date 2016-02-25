package stsc.general.simulator;

import stsc.common.BadSignalException;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.SignalsStorage;
import stsc.general.statistic.Metrics;

/**
 * Simulator interface - each simulator should automatically execute simulation on start and set {@link Metrics} and {@link SignalsStorage}.
 */
public interface Simulator {

	public void simulateMarketTrading(final Execution simulatorSettings) throws BadAlgorithmException, BadSignalException;

	public Metrics getMetrics();

	public SignalsStorage getSignalsStorage();

}
