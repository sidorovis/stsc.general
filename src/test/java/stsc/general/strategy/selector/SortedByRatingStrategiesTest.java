package stsc.general.strategy.selector;

import org.junit.Assert;
import org.junit.Test;

import stsc.general.statistic.SortedStrategies;
import stsc.general.statistic.cost.comparator.CostWeightedSumComparator;
import stsc.general.strategy.selector.SortedByRatingStrategies;

public class SortedByRatingStrategiesTest {

	@Test
	public void testSortedByRatingStrategies() {
		final SortedStrategies collection = new SortedByRatingStrategies(new CostWeightedSumComparator());
		Assert.assertNotNull(collection);
	}
}
