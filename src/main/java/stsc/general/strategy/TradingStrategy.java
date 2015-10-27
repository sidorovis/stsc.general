package stsc.general.strategy;

import org.apache.commons.lang3.Validate;

import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.SimulatorSettingsImpl;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;

/**
 * Represents pair {@link SimulatorSettingsImpl} -> {@link Metrics}. {@link SimulatorSettingsImpl} could be null Only for Tests. Please call
 * {@link #createTest(Metrics)} only for tests.
 */
public final class TradingStrategy {

	private final SimulatorSettings simulatorSettings;
	private final Metrics metrics;

	public static TradingStrategy createTest(final Metrics metrics) {
		return new TradingStrategy(metrics);
	}

	TradingStrategy(final Metrics metrics) {
		this.simulatorSettings = null;
		this.metrics = metrics;
	}

	public TradingStrategy(final SimulatorSettings simulatorSettings, final Metrics metrics) {
		Validate.notNull(simulatorSettings);
		this.simulatorSettings = simulatorSettings;
		this.metrics = metrics;
	}

	public SimulatorSettings getSettings() {
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
