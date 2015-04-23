package stsc.general.statistic.cost.comparator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import stsc.general.statistic.Metrics;

public class CostWeightedSumComparator implements CostStatisticsComparator {

	private final Map<String, Double> parameters = new HashMap<>();

	public CostWeightedSumComparator() {
		parameters.put("avGain", 1.0);
	}

	public CostWeightedSumComparator addParameter(String name, Double value) {
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
