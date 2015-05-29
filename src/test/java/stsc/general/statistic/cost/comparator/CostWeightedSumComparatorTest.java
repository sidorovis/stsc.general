package stsc.general.statistic.cost.comparator;

import java.text.ParseException;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.testhelper.TestMetricsHelper;

public class CostWeightedSumComparatorTest {

	@Test
	public void testCostWeightedSumComparator() throws ParseException {
		final Metrics stat = TestMetricsHelper.getMetrics();

		final CostWeightedSumComparator comparator = new CostWeightedSumComparator();
		comparator.withParameter(MetricType.kelly, 0.8);

		Assert.assertEquals(0, comparator.compare(stat, stat));

		final Metrics newStat = TestMetricsHelper.getMetrics(50, 150, new LocalDate(2013, 5, 1));
		Assert.assertEquals(0, comparator.compare(newStat, newStat));

		Assert.assertEquals(-1, comparator.compare(stat, newStat));
		Assert.assertEquals(1, comparator.compare(newStat, stat));
	}

	@Test
	public void testCostWeightedSumComparatorOnSeveralStatistics() {
		final CostWeightedSumComparator comparator = new CostWeightedSumComparator();
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
}
