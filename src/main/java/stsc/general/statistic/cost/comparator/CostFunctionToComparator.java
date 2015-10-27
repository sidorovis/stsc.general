package stsc.general.statistic.cost.comparator;

import stsc.general.statistic.Metrics;
import stsc.general.statistic.cost.function.CostFunction;

/**
 * This comparator use one {@link CostFunction} to compare metrics.
 */
public class CostFunctionToComparator implements MetricsComparator {

	private final CostFunction costFunction;

	public CostFunctionToComparator(final CostFunction costFunction) {
		this.costFunction = costFunction;
	}

	@Override
	public int compare(Metrics o1, Metrics o2) {
		return Double.compare(costFunction.calculate(o1), costFunction.calculate(o2));
	}

}
