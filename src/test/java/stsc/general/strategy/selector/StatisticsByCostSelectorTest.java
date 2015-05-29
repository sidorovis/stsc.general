package stsc.general.strategy.selector;

import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.TradingStrategy;

public class StatisticsByCostSelectorTest {

	private TradingStrategy getTs(double avGain, double winProb) {
		final HashMap<MetricType, Double> dh = new HashMap<>();
		dh.put(MetricType.avGain, avGain);
		dh.put(MetricType.winProb, winProb);
		return TradingStrategy.createTest(new Metrics(dh, new HashMap<>()));
	}

	@Test
	public void testStatisticsByCostSelector() {
		final StatisticsByCostSelector selector = new StatisticsByCostSelector(10, new CostWeightedSumFunction());
		Assert.assertTrue(selector.addStrategy(getTs(10.5, 6.9)).isEmpty());
		Assert.assertTrue(selector.addStrategy(getTs(10.8, 4.2)).isEmpty());
		Assert.assertTrue(selector.addStrategy(getTs(11.1, 3.3)).isEmpty());
		Assert.assertTrue(selector.addStrategy(getTs(10.4, 2.5)).isEmpty());
		Assert.assertTrue(selector.addStrategy(getTs(10.6, 3.7)).isEmpty());

		Assert.assertTrue(selector.addStrategy(getTs(10.5, 2.0)).isEmpty());
		Assert.assertTrue(selector.addStrategy(getTs(10.1, 1.1)).isEmpty());
		Assert.assertTrue(selector.addStrategy(getTs(10.2, 4.0)).isEmpty());
		Assert.assertTrue(selector.addStrategy(getTs(10.3, 1.4)).isEmpty());
		Assert.assertTrue(selector.addStrategy(getTs(9.6, 3.0)).isEmpty());

		Assert.assertEquals(10, selector.currentStrategiesAmount());

		List<TradingStrategy> justDeleted = selector.addStrategy(getTs(10.3, 2.4));
		Assert.assertEquals(9.6, justDeleted.get(0).getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(10, selector.currentStrategiesAmount());

		justDeleted = selector.addStrategy(getTs(10.3, 2.4));
		Assert.assertEquals(10.3, justDeleted.get(0).getAvGain(), Settings.doubleEpsilon);

		justDeleted = selector.addStrategy(getTs(10.7, 4.5));
		Assert.assertEquals(10.1, justDeleted.get(0).getAvGain(), Settings.doubleEpsilon);

		justDeleted = selector.addStrategy(getTs(10.4, 2.5));
		Assert.assertEquals(10.4, justDeleted.get(0).getAvGain(), Settings.doubleEpsilon);

		justDeleted = selector.addStrategy(getTs(12.3, 1.5));
		Assert.assertEquals(10.2, justDeleted.get(0).getAvGain(), Settings.doubleEpsilon);

		justDeleted = selector.addStrategy(getTs(11.9, 1.73));
		Assert.assertEquals(10.3, justDeleted.get(0).getAvGain(), Settings.doubleEpsilon);

		justDeleted = selector.addStrategy(getTs(11.9, 1.72));
		Assert.assertEquals(10.3, justDeleted.get(0).getAvGain(), Settings.doubleEpsilon);
	}
}
