package stsc.general.statistic.cost.function;

import java.util.HashMap;
import java.util.Map;

import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;

/**
 * CostWeightedProductFunction is a cost function for {@link Metrics}. <br/>
 * Store amount of parameters and double weights for each: <br/>
 * <b>(P[X] -> W[X]).</b> <br/>
 * CostFunction Calculation algorithm is next: <br/>
 * 1) calculate sum = W[1] + W[2] + ... W[N]; <br/>
 * 2) calculate elements E[1] = W[1] / sum; <br/>
 * 3) get S[X] = signum(P[X]) <br/>
 * 4) get V[X] = power(abs(P[X]),E[X]); <br/>
 * 5) get ResultSum = S[X] * V[X] (for X in 1..N).
 */

public class CostWeightedProductFunction implements CostFunction {

	private final Map<MetricType, Double> parameters = new HashMap<>();

	public CostWeightedProductFunction() {
		parameters.put(MetricType.avGain, 1.0);
	}

	public void addParameter(MetricType name, Double value) {
		parameters.put(name, value);
	}

	@Override
	public double calculate(Metrics metrics) {
		Double sum = 0.0;
		for (Double d : parameters.values()) {
			sum += d;
		}
		Double result = 0.0;
		for (Map.Entry<MetricType, Double> e : parameters.entrySet()) {
			final Double power = e.getValue() / sum;
			final Double metricsValue = metrics.getMetric(e.getKey());
			final Double signum = Math.signum(metricsValue);
			final Double pow = Math.pow(Math.abs(metricsValue), power);
			result += signum * pow;
		}
		return result;
	}
}
