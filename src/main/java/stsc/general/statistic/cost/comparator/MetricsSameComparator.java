package stsc.general.statistic.cost.comparator;

import stsc.general.statistic.Metrics;

public class MetricsSameComparator implements MetricsComparator {

	@Override
	public int compare(Metrics o1, Metrics o2) {
		return o1.hashCode() - o2.hashCode();
	}

}
