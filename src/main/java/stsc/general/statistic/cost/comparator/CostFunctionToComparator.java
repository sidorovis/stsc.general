package stsc.general.statistic.cost.comparator;

import stsc.general.statistic.Metrics;
import stsc.general.statistic.cost.function.CostFunction;

public class CostFunctionToComparator implements CostStatisticsComparator {

	private final CostFunction costFunction;

	public CostFunctionToComparator(final CostFunction costFunction) {
		this.costFunction = costFunction;
	}

	public int compare(Metrics o1, Metrics o2) {
		return costFunction.calculate(o1).compareTo(costFunction.calculate(o2));
	}

}
