package stsc.general.statistic.cost.function;

import stsc.general.statistic.Metrics;

/**
 * Cost Function is a map {@link Metrics} -> {@link Double} value. <br/>
 * Use it to compare / somehow mark / define value of {@link Metrics}.
 */
public interface CostFunction {

	public Double calculate(Metrics metrics);

}
