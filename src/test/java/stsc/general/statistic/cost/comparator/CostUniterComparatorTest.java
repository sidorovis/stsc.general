package stsc.general.statistic.cost.comparator;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.general.statistic.Metrics;
import stsc.general.testhelper.TestMetricsHelper;

public class CostUniterComparatorTest {

	@Test
	public void testCostUniterComparator() {
		final CostUniterComparator cu = new CostUniterComparator();
		cu.addComparator(new CostWeightedSumComparator(), 0.5);
		cu.addComparator(new CostWeightedSumComparator().addParameter("winProb", 0.6), 0.8);

		for (int i = 1; i < 6; ++i) {
			final Metrics leftStat = TestMetricsHelper.getMetrics(50, 150, new LocalDate(2013, 5, i));
			for (int u = i + 20; u < 25; ++u) {
				if (i != u) {
					final Metrics rightStat = TestMetricsHelper.getMetrics(50, 150, new LocalDate(2013, 5, u));
					final int r = cu.compare(leftStat, rightStat) * cu.compare(rightStat, leftStat);
					if (r != 0)
						Assert.assertEquals(-1, r);
				}
			}
		}

	}
}
