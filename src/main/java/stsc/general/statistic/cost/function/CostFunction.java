package stsc.general.statistic.cost.function;

import stsc.general.statistic.Metrics;

public interface CostFunction {
	public Double calculate(Metrics metrics);
}
