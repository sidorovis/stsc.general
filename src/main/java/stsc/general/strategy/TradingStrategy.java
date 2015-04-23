package stsc.general.strategy;

import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Metrics;

public final class TradingStrategy {

	private final SimulatorSettings simulatorSettings;
	private final Metrics metrics;

	public static TradingStrategy createTest(final Metrics metrics) {
		return new TradingStrategy(null, metrics);
	}

	public TradingStrategy(final SimulatorSettings simulatorSettings, final Metrics metrics) {
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
		return metrics.getDoubleMetric("avGain");
	}

	@Override
	public String toString() {
		if (simulatorSettings == null) {
			return "TEST: " + getAvGain();
		}
		return simulatorSettings.toString() + "\n" + metrics.toString();
	}
}
