package stsc.general.statistic.cost.comparator;

import stsc.general.statistic.Metrics;

/**
 * Compare {@link Metrics} by {@link Metrics#equals()} method.
 * 
 * @return 0 if Metrics#equals() == true, left {@link Metrics#hashCode()} -
 *         right {@link Metrics#hashCode()} otherwise.
 */
public class MetricsSameComparator implements MetricsComparator {

	@Override
	public int compare(Metrics o1, Metrics o2) {
		return o1.equals(o2) ? 0 : o1.hashCode() - o2.hashCode();
	}

}
