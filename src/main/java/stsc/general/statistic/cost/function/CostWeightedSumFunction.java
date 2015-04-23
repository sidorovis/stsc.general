package stsc.general.statistic.cost.function;

import java.util.HashMap;
import java.util.Map;

import stsc.general.statistic.Metrics;

// @formatter:off
/**
 * {@link CostWeightedSumFunction} is a cost function for {@link Metrics}.
 * Require set of parameters: P[1]...P[N]; Result = V[1] * P[1] + V[2] * P[2]
 * ... V[N] * P[N].
 */
// @formatter:on
public class CostWeightedSumFunction implements CostFunction {

	private final Map<String, Double> parameters = new HashMap<>();

	public CostWeightedSumFunction() {
		parameters.put("avGain", 1.0);
	}

	public void addParameter(String name, Double value) {
		parameters.put(name, value);
	}

	@Override
	public Double calculate(final Metrics metrics) {
		Double result = 0.0;
		for (Map.Entry<String, Double> e : parameters.entrySet()) {
			result += e.getValue() * metrics.getMetric(e.getKey());
		}

		return result;
	}
}
