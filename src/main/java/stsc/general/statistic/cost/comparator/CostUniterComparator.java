package stsc.general.statistic.cost.comparator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import stsc.general.statistic.Metrics;

/**
 * This class provide possibility to compare {@link Metrics} with several
 * {@link MetricsComparator} s using double factor for each sub-comparator.<br/>
 * So if think about {@link MetricsComparator} as about function (for example
 * f1(M), f2(M)), then {@link CostUniterComparator} is linear combination
 * function:<br/>
 * f(M) -> a1 * f1(M) + a2 * f2(M) + a3 * f3(M).<br/>
 * <b>Not thread-safe.</b> <br/>
 * Can't be used for one instance of {@link MetricsComparator} twice.
 */
public class CostUniterComparator implements MetricsComparator {

	final private Map<MetricsComparator, Double> parameters = new HashMap<MetricsComparator, Double>();

	public CostUniterComparator() {
	}

	/**
	 * @return this (simulate builder style).
	 */
	public CostUniterComparator withComparator(MetricsComparator sc, Double d) {
		parameters.put(sc, d);
		return this;
	}

	@Override
	public int compare(Metrics o1, Metrics o2) {
		Double sumResult = 0.0;
		for (Entry<MetricsComparator, Double> v : parameters.entrySet()) {
			final Double value = Double.valueOf(v.getKey().compare(o1, o2));
			sumResult += value * v.getValue();
		}
		return sumResult.intValue();
	}
}
