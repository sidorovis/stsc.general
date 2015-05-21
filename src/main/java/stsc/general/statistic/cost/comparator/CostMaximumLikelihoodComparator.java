package stsc.general.statistic.cost.comparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import stsc.general.statistic.Metrics;

/**
 * Maximum Likelihood Comparator is a {@link Metrics} {@link Comparator}.<br/>
 * For each metric (from {@link Metrics}) we define weight <b>W</b> (using
 * {@link #withParameter(String, Double)} method.<br/>
 * Then for two {@link Metrics} that we plan to compare we calculate next value:<br/>
 * 1. V = Abs( W[i] - AssosiatedMetricValue ) ;<br/>
 * 2. if (V != 0):<br/>
 * 2.a. R += Log(V);<br/>
 * Result of compare function is comparing R for left {@link Metrics} and right
 * {@link Metrics}.
 */
public class CostMaximumLikelihoodComparator implements MetricsComparator {

	private final Map<String, Double> parameters = new HashMap<>();

	public CostMaximumLikelihoodComparator() {
		parameters.put("avGain", 100.0);
	}

	public CostMaximumLikelihoodComparator withParameter(String name, Double value) {
		parameters.put(name, value);
		return this;
	}

	@Override
	public int compare(Metrics s1, Metrics s2) {
		Double result1 = 0.0;
		Double result2 = 0.0;
		for (Entry<String, Double> i : parameters.entrySet()) {
			final Double w = i.getValue();
			final Double v1 = Math.abs(w - s1.getMetric(i.getKey()));
			final Double v2 = Math.abs(w - s2.getMetric(i.getKey()));
			if (Double.compare(v1, 0.0) != 0)
				result1 += Math.log(v1);
			if (Double.compare(v2, 0.0) != 0)
				result2 += Math.log(v2);
		}
		return Double.compare(result1, result2);
	}
}
