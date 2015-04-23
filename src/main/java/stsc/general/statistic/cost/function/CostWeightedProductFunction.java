package stsc.general.statistic.cost.function;

import java.util.HashMap;
import java.util.Map;

import stsc.general.statistic.Metrics;

// @formatter:off
/**
 * CostWeightedProductFunction is a cost function for {@link Metrics}. Store
 * amount of parameters and double weights for each: (P[X] -> W[X] CostFunction
 * Calculation algorithm is next: 1) calculate sum = W[1] + W[2] + ... W[N]; 2)
 * calculate elements E[1] = W[1] / sum; 3) get S[X] = signum(P[X]) 4) get V[X]
 * = power(abs(P[X]),E[X]); 5) get ResultSum = S[X] * V[X] (for X in 1..N).
 */
// @formatter:on

public class CostWeightedProductFunction implements CostFunction {

	private final Map<String, Double> parameters = new HashMap<>();

	public CostWeightedProductFunction() {
		parameters.put("avGain", 1.0);
	}

	public void addParameter(String name, Double value) {
		parameters.put(name, value);
	}

	@Override
	public Double calculate(Metrics metrics) {
		Double sum = 0.0;
		for (Double d : parameters.values()) {
			sum += d;
		}
		Double result = 0.0;
		for (Map.Entry<String, Double> e : parameters.entrySet()) {
			final Double power = e.getValue() / sum;
			final Double metricsValue = metrics.getMetric(e.getKey());
			final Double signum = Math.signum(metricsValue);
			final Double pow = Math.pow(Math.abs(metricsValue), power);
			result += signum * pow;
		}
		return result;
	}
}
