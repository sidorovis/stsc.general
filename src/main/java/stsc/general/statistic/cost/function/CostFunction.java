package stsc.general.statistic.cost.function;

import stsc.general.statistic.Metrics;
import stsc.general.strategy.TradingStrategy;

/**
 * Cost Function is a map {@link Metrics} -> {@link Double} value. <br/>
 * Use it to compare / somehow mark / define value of {@link Metrics}.<br/>
 * Double value represents "rating" of the {@link TradingStrategy}.
 */
public interface CostFunction {

	public double calculate(Metrics metrics);

}
