package stsc.general.strategy.selector;

import stsc.common.collections.SortedByRating;
import stsc.general.statistic.SortedStrategies;
import stsc.general.statistic.cost.comparator.MetricsComparator;
import stsc.general.strategy.TradingStrategy;

/**
 * 
 */
class SortedByRatingStrategies extends SortedByRating<TradingStrategy> implements SortedStrategies {

	public SortedByRatingStrategies(final MetricsComparator metricsComparator) {
		super(new TradingStrategyComparator(metricsComparator));
	}

	@Override
	public boolean addStrategy(final Double rating, final TradingStrategy value) {
		return super.addElement(rating, value);
	}

	@Override
	public boolean removeStrategy(final Double rating, final TradingStrategy value) {
		return super.removeElement(rating, value);
	}

}
