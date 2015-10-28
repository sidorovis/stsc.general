package stsc.general.statistic;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.TradingStrategy;
import stsc.general.strategy.selector.StatisticsWithMetricsClusterDistanceSelector;

public class StatisticsWithMetricsClusterDistanceSelectorTest {

	TradingStrategy getStrategy(Double avGain) {
		final Map<MetricType, Double> doubleList = new HashMap<>();
		doubleList.put(MetricType.avGain, avGain);
		final Map<MetricType, Integer> integerList = new HashMap<>();
		return TradingStrategy.createTest(new Metrics(doubleList, integerList));
	}

	@Test
	public void testStatisticsWithDistanceSelector() {
		final StatisticsWithMetricsClusterDistanceSelector selector = new StatisticsWithMetricsClusterDistanceSelector(3, 3, new CostWeightedSumFunction());
		selector.withDistanceParameter(MetricType.avGain, 0.8);
		selector.addStrategy(getStrategy(1.0));
		selector.addStrategy(getStrategy(2.0));
		selector.addStrategy(getStrategy(3.1));
		selector.addStrategy(getStrategy(3.2));
		selector.addStrategy(getStrategy(3.3));
		Assert.assertEquals(3.1, selector.addStrategy(getStrategy(3.4)).get(0).getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(5, selector.getStrategies().size());

		selector.addStrategy(getStrategy(1.2));
		selector.addStrategy(getStrategy(2.1));

		Assert.assertEquals(6, selector.getStrategies().size());
		selector.addStrategy(getStrategy(8.0));
		selector.addStrategy(getStrategy(8.1));
		selector.addStrategy(getStrategy(8.2));
		selector.addStrategy(getStrategy(8.3));
		selector.addStrategy(getStrategy(9.2));
		selector.addStrategy(getStrategy(9.4));
		Assert.assertEquals(7, selector.getStrategies().size());
		selector.addStrategy(getStrategy(9.5));
		selector.addStrategy(getStrategy(9.6));
		Assert.assertEquals(9, selector.getStrategies().size());
		selector.addStrategy(getStrategy(9.45));
		Assert.assertEquals(9, selector.getStrategies().size());
	}

	@Test
	public void testStatisticsWithDistanceSelectorAposterioryTest() {
		final StatisticsWithMetricsClusterDistanceSelector selector = new StatisticsWithMetricsClusterDistanceSelector(10, 4, new CostWeightedSumFunction());
		selector.withDistanceParameter(MetricType.avGain, 0.3);
		for (int i = 0; i < 100; ++i) {
			selector.addStrategy(getStrategy(Double.valueOf(i)));
		}
		Assert.assertEquals(40, selector.currentStrategiesAmount());
	}

	@Test
	public void testStatisticsWithDistanceSelectorDeleteClusterTest() {
		final StatisticsWithMetricsClusterDistanceSelector selector = new StatisticsWithMetricsClusterDistanceSelector(1, 2, new CostWeightedSumFunction());
		selector.withDistanceParameter(MetricType.avGain, 1.0);
		selector.addStrategy(getStrategy(1.0));
		Assert.assertEquals(1.0, selector.addStrategy(getStrategy(1.0)).get(0).getAvGain(), Settings.doubleEpsilon);
		selector.addStrategy(getStrategy(1.1));
		Assert.assertEquals(1.1, selector.addStrategy(getStrategy(1.1)).get(0).getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(1.0, selector.addStrategy(getStrategy(1.2)).get(0).getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(1.1, selector.addStrategy(getStrategy(1.3)).get(0).getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(1.2, selector.addStrategy(getStrategy(1.4)).get(0).getAvGain(), Settings.doubleEpsilon);

		Assert.assertEquals(1.3, selector.addStrategy(getStrategy(2.4)).get(1).getAvGain(), Settings.doubleEpsilon);
		Assert.assertTrue(selector.addStrategy(getStrategy(2.5)).isEmpty());
	}
}
