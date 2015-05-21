package stsc.general.statistic.cost.comparator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import stsc.general.statistic.Metrics;

/**
 * Compare({@link Metrics} left, {@link Metrics} right) method algorithm: <br/>
 * 1. accumulate associative pairs "Metric" -> Double value (Weight) using
 * {@link #withParameter(String, Double)} method.<br/>
 * 2. calculate sum of Weights from associative pairs;<br/>
 * 3. accumulate result for all associative pairs:<br/>
 * 3.a. <b>R += {@link Math#signum(double)}(leftMetricValue - rightMetricValue)
 * * {@link Math#abs(double)}(leftMetricValue - rightMetricValue) ^ Weight; 4.
 * Result of compare -> {@link Double#compare(Double, Double)} (R, 0.0).
 */
public class CostWeightedSumComparator implements MetricsComparator {

	private final Map<String, Double> parameters = new HashMap<>();

	public CostWeightedSumComparator() {
		parameters.put("avGain", 1.0);
	}

	public CostWeightedSumComparator withParameter(String name, Double value) {
		parameters.put(name, value);
		return this;
	}

	@Override
	public int compare(Metrics s1, Metrics s2) {
		Double sum = 0.0;
		for (Double d : parameters.values()) {
			sum += d;
		}
		Double result = 0.0;
		for (Entry<String, Double> i : parameters.entrySet()) {
			Double v1 = s1.getMetric(i.getKey());
			Double v2 = s2.getMetric(i.getKey());
			final Double w = i.getValue() / sum;
			result += Math.signum(v1 - v2) * Math.pow(Math.abs(v1 - v2), w);
		}
		return Double.compare(result, 0.0);
	}
}
