package stsc.general.statistic;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.cost.comparator.MetricsSameComparator;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.TradingStrategy;
import stsc.general.strategy.selector.StatisticsByCostSelector;
import stsc.general.strategy.selector.StrategySelector;
import stsc.general.testhelper.TestGridSimulatorSettings;
import stsc.general.testhelper.TestMetricsHelper;

public class StatisticsSelectorTest {

	@Test
	public void testStatisticsSelector() throws ParseException {
		final CostWeightedSumFunction compareMethod = new CostWeightedSumFunction();
		final StrategySelector statisticsSelector = new StatisticsByCostSelector(2, compareMethod, new MetricsSameComparator());

		final List<Double> values = new ArrayList<>();
		values.add(compareMethod.calculate(TestMetricsHelper.getMetrics(100, 200)));
		values.add(compareMethod.calculate(TestMetricsHelper.getMetrics(200, 250)));
		values.add(compareMethod.calculate(TestMetricsHelper.getMetrics(150, 210)));

		Iterator<SimulatorSettings> testSettings = TestGridSimulatorSettings.getGridList().iterator();

		statisticsSelector.addStrategy(new TradingStrategy(testSettings.next(), TestMetricsHelper.getMetrics(100, 200)));
		statisticsSelector.addStrategy(new TradingStrategy(testSettings.next(), TestMetricsHelper.getMetrics(200, 250)));
		statisticsSelector.addStrategy(new TradingStrategy(testSettings.next(), TestMetricsHelper.getMetrics(150, 210)));

		final List<TradingStrategy> strategies = statisticsSelector.getStrategies();
		Assert.assertEquals(2, strategies.size());
		Assert.assertEquals(compareMethod.calculate(strategies.get(0).getMetrics()), values.get(0));
		Assert.assertEquals(compareMethod.calculate(strategies.get(1).getMetrics()), values.get(2));
	}
}
