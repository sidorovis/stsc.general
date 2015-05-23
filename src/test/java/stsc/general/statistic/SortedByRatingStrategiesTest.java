package stsc.general.statistic;

import org.junit.Assert;
import org.junit.Test;

import stsc.general.statistic.cost.comparator.CostWeightedSumComparator;

public class SortedByRatingStrategiesTest {

	@Test
	public void testSortedByRatingStrategies() {
		final SortedByRatingStrategies collection = new SortedByRatingStrategies(new CostWeightedSumComparator());
		Assert.assertNotNull(collection);
	}
}
