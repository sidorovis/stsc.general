package stsc.general.statistic;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.common.Day;
import stsc.common.Settings;
import stsc.general.statistic.cost.comparator.CostWeightedSumComparator;
import stsc.general.strategy.TradingStrategy;
import stsc.general.strategy.selector.StatisticsCompareSelector;
import stsc.general.testhelper.TestMetricsHelper;

public class StatisticsCompareSelectorTest {

	@Test
	public void testStatisticsCompareSelector() throws ParseException {
		final CostWeightedSumComparator c = new CostWeightedSumComparator();
		c.withParameter(MetricType.winProb, 5.0);
		c.withParameter(MetricType.avLoss, 14.0);
		c.withParameter(MetricType.avWin, 1.5);
		final StatisticsCompareSelector sel = new StatisticsCompareSelector(3, c);

		final ArrayList<TradingStrategy> tses = new ArrayList<>();
		tses.add(TradingStrategy.createTest(TestMetricsHelper.getMetrics(50, 150, Day.createDate("08-05-2013"))));
		tses.add(TradingStrategy.createTest(TestMetricsHelper.getMetrics(50, 150, new LocalDate(2013, 5, 4))));
		tses.add(TradingStrategy.createTest(TestMetricsHelper.getMetrics(50, 150, new LocalDate(2013, 5, 16))));
		tses.add(TradingStrategy.createTest(TestMetricsHelper.getMetrics(50, 150, new LocalDate(2013, 5, 12))));

		for (TradingStrategy ts : tses) {
			sel.addStrategy(ts);
		}

		Assert.assertEquals(3, sel.getStrategies().size());
		final Iterator<TradingStrategy> si = sel.getStrategies().iterator();
		Assert.assertEquals(0.358820, si.next().getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(1.582514, si.next().getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(-0.201986, si.next().getAvGain(), Settings.doubleEpsilon);
	}
}
