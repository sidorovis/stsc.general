package stsc.general.statistic.cost.comparator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Ordering;

import stsc.common.Day;
import stsc.common.Settings;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.strategy.TradingStrategy;
import stsc.general.strategy.selector.StatisticsCompareSelector;
import stsc.general.testhelper.TestMetricsHelper;

public class CostMaximumLikelihoodComparatorTest {

	@Test
	public void testCostMaximumLikelihoodComparatorOnSeveral() {
		final CostMaximumLikelihoodComparator comparator = new CostMaximumLikelihoodComparator();
		comparator.withParameter(MetricType.kelly, 0.8);
		comparator.withParameter(MetricType.winProb, 0.4);
		comparator.withParameter(MetricType.maxWin, 0.9);
		for (int i = 1; i < 6; ++i) {
			final Metrics leftStat = TestMetricsHelper.getMetrics(50, 150, new LocalDate(2013, 5, i));
			for (int u = i + 20; u < 25; ++u) {
				if (i != u) {
					final Metrics rightStat = TestMetricsHelper.getMetrics(50, 150, new LocalDate(2013, 5, u));
					final int r = comparator.compare(leftStat, rightStat) * comparator.compare(rightStat, leftStat);
					if (r != 0)
						Assert.assertEquals(-1, r);
				}
			}
		}
	}

	@Test
	public void testCostStatisticsCompareSelectorWithLikelihood() throws ParseException {
		final CostMaximumLikelihoodComparator c = new CostMaximumLikelihoodComparator();
		final StatisticsCompareSelector sel = new StatisticsCompareSelector(3, c);

		final ArrayList<TradingStrategy> tradingStrategies = new ArrayList<>();
		tradingStrategies.add(TradingStrategy.createTest(TestMetricsHelper.getMetrics(50, 150, Day.createDate("08-05-2013"))));
		tradingStrategies.add(TradingStrategy.createTest(TestMetricsHelper.getMetrics(50, 150, Day.createDate("04-05-2013"))));
		tradingStrategies.add(TradingStrategy.createTest(TestMetricsHelper.getMetrics(50, 150, Day.createDate("16-05-2013"))));
		tradingStrategies.add(TradingStrategy.createTest(TestMetricsHelper.getMetrics(50, 150, Day.createDate("12-05-2013"))));

		final ArrayList<Double> avGains = new ArrayList<>();
		for (TradingStrategy ts : tradingStrategies) {
			sel.addStrategy(ts);
			avGains.add(ts.getAvGain());
		}
		avGains.sort(Collections.reverseOrder());

		Assert.assertEquals(3, sel.getStrategies().size());
		final Iterator<TradingStrategy> si = sel.getStrategies().iterator();

		Assert.assertEquals(avGains.get(0), si.next().getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(avGains.get(1), si.next().getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(avGains.get(2), si.next().getAvGain(), Settings.doubleEpsilon);
	}

	@Test
	public void testCostStatisticsCompareSelectorWithLikelihoodWithKelly() {
		final CostMaximumLikelihoodComparator c = new CostMaximumLikelihoodComparator();
		c.withParameter(MetricType.maxLoss, 100.0);
		c.withParameter(MetricType.avGain, -50.0);
		c.withParameter(MetricType.freq, 15.0);
		final StatisticsCompareSelector sel = new StatisticsCompareSelector(3, c);

		final ArrayList<TradingStrategy> tradingStrategies = new ArrayList<>();
		tradingStrategies.add(TradingStrategy.createTest(TestMetricsHelper.getMetrics(50, 150, new LocalDate(2013, 5, 8))));
		tradingStrategies.add(TradingStrategy.createTest(TestMetricsHelper.getMetrics(50, 150, new LocalDate(2013, 5, 4))));
		tradingStrategies.add(TradingStrategy.createTest(TestMetricsHelper.getMetrics(50, 150, new LocalDate(2013, 5, 16))));
		tradingStrategies.add(TradingStrategy.createTest(TestMetricsHelper.getMetrics(50, 150, new LocalDate(2013, 5, 12))));

		final ArrayList<Double> avGains = new ArrayList<>();
		for (TradingStrategy ts : tradingStrategies) {
			sel.addStrategy(ts);
			avGains.add(ts.getAvGain());
		}
		avGains.sort(Ordering.natural());

		Assert.assertEquals(3, sel.getStrategies().size());
		final Iterator<TradingStrategy> si = sel.getStrategies().iterator();
		Assert.assertEquals(avGains.get(0), si.next().getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(avGains.get(1), si.next().getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(avGains.get(2), si.next().getAvGain(), Settings.doubleEpsilon);
	}
}
