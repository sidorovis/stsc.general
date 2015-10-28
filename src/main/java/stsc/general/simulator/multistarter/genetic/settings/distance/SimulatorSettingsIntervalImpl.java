package stsc.general.simulator.multistarter.genetic.settings.distance;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.Execution;
import stsc.common.algorithms.MutatingAlgorithmSettings;
import stsc.general.simulator.SimulatorSettings;

public final class SimulatorSettingsIntervalImpl implements SimulatorSettingsInterval {

	public static final double MAX_INTERVAL_VALUE = Double.MAX_VALUE;

	public SimulatorSettingsIntervalImpl() {
	}

	@Override
	public double calculateInterval(final SimulatorSettings left, final SimulatorSettings right) {
		final List<EodExecution> leftEods = left.getInit().getExecutionsStorage().getEodExecutions();
		final List<EodExecution> rightEods = right.getInit().getExecutionsStorage().getEodExecutions();
		if (leftEods.size() != rightEods.size()) {
			return MAX_INTERVAL_VALUE;
		}
		double result = 0.0;
		for (int i = 0; i < leftEods.size(); ++i) {
			final EodExecution leftEod = leftEods.get(i);
			final EodExecution rightEod = rightEods.get(i);
			if (compareNonSettingsFields(leftEod, rightEod)) {
				return MAX_INTERVAL_VALUE;
			}
			result += calculateExecutionInterval(leftEod.getSettings(), rightEod.getSettings());
		}
		return result;
	}

	private double calculateExecutionInterval(final MutatingAlgorithmSettings left, final MutatingAlgorithmSettings right) {
		final Map<String, Double> leftDoubles = left.getDoubles();
		final Map<String, Double> rightDoubles = right.getDoubles();
		if (leftDoubles.size() != rightDoubles.size()) {
			return MAX_INTERVAL_VALUE;
		}
		double interval = 0.0;
		for (Entry<String, Double> le : leftDoubles.entrySet()) {
			final Double rightValue = rightDoubles.get(le.getKey());
			interval += Math.abs(le.getValue() - rightValue);
		}
		final Map<String, Integer> leftIntegers = left.getIntegers();
		final Map<String, Integer> rightIntegers = right.getIntegers();
		for (Entry<String, Integer> le : leftIntegers.entrySet()) {
			final Integer rightValue = rightIntegers.get(le.getKey());
			interval += Math.abs(le.getValue() - rightValue);
		}
		return interval;
	}

	private boolean compareNonSettingsFields(Execution<?> left, Execution<?> right) {
		return !left.getAlgorithmType().equals(right.getAlgorithmType()) || //
				!left.getAlgorithmName().equals(right.getAlgorithmName()) || //
				!left.getExecutionName().equals(right.getExecutionName());
	}

}
