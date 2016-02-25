package stsc.general.simulator.multistarter.genetic.settings.distance;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import stsc.common.algorithms.EodExecutionInstance;
import stsc.common.algorithms.ExecutionInstance;
import stsc.common.algorithms.MutableAlgorithmConfiguration;
import stsc.general.simulator.Execution;

public final class SimulatorSettingsIntervalImpl implements SimulatorSettingsInterval {

	public static final double MAX_INTERVAL_VALUE = Double.MAX_VALUE;

	public SimulatorSettingsIntervalImpl() {
	}

	@Override
	public double calculateInterval(final Execution left, final Execution right) {
		final List<EodExecutionInstance> leftEods = left.getInit().getExecutionsStorage().getEodExecutions();
		final List<EodExecutionInstance> rightEods = right.getInit().getExecutionsStorage().getEodExecutions();
		if (leftEods.size() != rightEods.size()) {
			return MAX_INTERVAL_VALUE;
		}
		double result = 0.0;
		for (int i = 0; i < leftEods.size(); ++i) {
			final EodExecutionInstance leftEod = leftEods.get(i);
			final EodExecutionInstance rightEod = rightEods.get(i);
			if (compareNonSettingsFields(leftEod, rightEod)) {
				return MAX_INTERVAL_VALUE;
			}
			result += calculateExecutionInterval(leftEod.getSettings(), rightEod.getSettings());
		}
		return result;
	}

	private double calculateExecutionInterval(final MutableAlgorithmConfiguration left, final MutableAlgorithmConfiguration right) {
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

	private boolean compareNonSettingsFields(ExecutionInstance<?> left, ExecutionInstance<?> right) {
		return !left.getAlgorithmType().equals(right.getAlgorithmType()) || //
				!left.getAlgorithmName().equals(right.getAlgorithmName()) || //
				!left.getExecutionName().equals(right.getExecutionName());
	}

}
