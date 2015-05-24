package stsc.general.strategy.selector;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import stsc.general.statistic.Metrics;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.TradingStrategy;

public class StrategyFilteringSelectorTest {

	private TradingStrategy getTs(double freq, int period) {
		final HashMap<String, Double> dStats = new HashMap<>();
		dStats.put("freq", freq);
		dStats.put("avGain", 15.0);
		final HashMap<String, Integer> iStats = new HashMap<>();
		iStats.put("period", period);
		return TradingStrategy.createTest(new Metrics(dStats, iStats));
	}

	@Test
	public void testStrategyFilteringSelectorWithMinDouble() {
		final StrategyFilteringSelector selector = new StrategyFilteringSelector(new StatisticsByCostSelector(10000, new CostWeightedSumFunction()));
		Assert.assertEquals(10000, selector.maxPossibleAmount());
		Assert.assertEquals(0, selector.currentStrategiesAmount());
		selector.withDoubleMinFilter("freq", 0.01);
		selector.addStrategy(getTs(0.001, 10));
		Assert.assertEquals(0, selector.currentStrategiesAmount());
	}

	@Test
	public void testStrategyFilteringSelectorWithMaxDouble() {
		final StrategyFilteringSelector selector = new StrategyFilteringSelector(new StatisticsByCostSelector(10000, new CostWeightedSumFunction()));
		selector.withDoubleMaxFilter("freq", 1.00);
		selector.addStrategy(getTs(1.00001, 10));
		Assert.assertEquals(0, selector.currentStrategiesAmount());
	}

	@Test
	public void testStrategyFilteringSelectorWithMinInteger() {
		final StrategyFilteringSelector selector = new StrategyFilteringSelector(new StatisticsByCostSelector(10000, new CostWeightedSumFunction()));
		selector.withIntegerMinFilter("period", 11);
		selector.addStrategy(getTs(1.00001, 10));
		Assert.assertEquals(0, selector.currentStrategiesAmount());
	}

	@Test
	public void testStrategyFilteringSelectorWithMaxInteger() {
		final StrategyFilteringSelector selector = new StrategyFilteringSelector(new StatisticsByCostSelector(10000, new CostWeightedSumFunction()));
		selector.withIntegerMaxFilter("period", 11);
		selector.addStrategy(getTs(1.00001, 12));
		Assert.assertEquals(0, selector.currentStrategiesAmount());
	}

}
