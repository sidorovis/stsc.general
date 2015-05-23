package stsc.general.statistic;

import java.util.Comparator;

import stsc.general.statistic.cost.comparator.MetricsComparator;
import stsc.general.strategy.TradingStrategy;

final class TradingStrategyComparator implements Comparator<TradingStrategy> {
	private MetricsComparator comparator;

	TradingStrategyComparator(MetricsComparator comparator) {
		this.comparator = comparator;
	}

	@Override
	public int compare(TradingStrategy o1, TradingStrategy o2) {
		return comparator.compare(o1.getMetrics(), o2.getMetrics());
	}

}