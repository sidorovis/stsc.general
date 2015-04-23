package stsc.general.statistic.cost.comparator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import stsc.general.statistic.Metrics;

public class CostUniterComparator implements CostStatisticsComparator {

	final private Map<CostStatisticsComparator, Double> parameters = new HashMap<CostStatisticsComparator, Double>();

	public CostUniterComparator() {
	}

	public CostUniterComparator addComparator(CostStatisticsComparator sc, Double d) {
		parameters.put(sc, d);
		return this;
	}

	public int compare(Metrics o1, Metrics o2) {
		Double sumResult = 0.0;
		for (Entry<CostStatisticsComparator, Double> v : parameters.entrySet()) {
			final Double value = Double.valueOf(v.getKey().compare(o1, o2));
			sumResult += value * v.getValue();
		}
		return sumResult.intValue();
	}
}
