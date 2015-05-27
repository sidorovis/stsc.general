package stsc.general.statistic.cost.comparator;

import stsc.general.statistic.Metrics;

/**
 * Compare {@link Metrics} by {@link Metrics#equals()} method.
 * 
 * @return always 1
 */
public class MetricsDifferentComparator implements MetricsComparator {

	@Override
	public int compare(Metrics o1, Metrics o2) {
		return 1;
	}

}
