package stsc.general.strategy;

import org.apache.commons.lang3.Validate;

import stsc.general.simulator.SimulatorConfiguration;
import stsc.general.simulator.SimulatorConfigurationImpl;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;

/**
 * Represents pair {@link SimulatorConfigurationImpl} -> {@link Metrics}. {@link SimulatorConfigurationImpl} could be null Only for Tests. Please call
 * {@link #createTest(Metrics)} only for tests.
 */
public final class TradingStrategy {

	private final SimulatorConfiguration simulatorSettings;
	private final Metrics metrics;

	public static TradingStrategy createTest(final Metrics metrics) {
		return new TradingStrategy(metrics);
	}

	TradingStrategy(final Metrics metrics) {
		this.simulatorSettings = null;
		this.metrics = metrics;
	}

	public TradingStrategy(final SimulatorConfiguration simulatorSettings, final Metrics metrics) {
		Validate.notNull(simulatorSettings);
		this.simulatorSettings = simulatorSettings;
		this.metrics = metrics;
	}

	public SimulatorConfiguration getSettings() {
		return simulatorSettings;
	}

	public Metrics getMetrics() {
		return metrics;
	}

	public double getAvGain() {
		return metrics.getDoubleMetric(MetricType.avGain);
	}

	@Override
	public String toString() {
		if (simulatorSettings == null) {
			return "TEST: " + getAvGain();
		}
		return simulatorSettings.toString() + "\n" + metrics.toString();
	}
}
