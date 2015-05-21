package stsc.general.statistic;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import stsc.general.statistic.cost.comparator.MetricsComparator;
import stsc.general.strategy.TradingStrategy;

public class StatisticsCompareSelector extends BorderedStrategySelector {

	private final class StrategyComparator implements Comparator<TradingStrategy> {
		private MetricsComparator comparator;

		StrategyComparator(MetricsComparator comparator) {
			this.comparator = comparator;
		}

		@Override
		public int compare(TradingStrategy o1, TradingStrategy o2) {
			return comparator.compare(o1.getMetrics(), o2.getMetrics());
		}

	}

	private final StrategyComparator strategyComparator;
	private final TreeSet<TradingStrategy> select;

	public StatisticsCompareSelector(int selectLastElements, MetricsComparator comparator) {
		super(selectLastElements);
		this.strategyComparator = new StrategyComparator(comparator);
		this.select = new TreeSet<TradingStrategy>(strategyComparator);
	}

	@Override
	public synchronized Optional<TradingStrategy> addStrategy(final TradingStrategy strategy) {
		select.add(strategy);
		if (select.size() > maxPossibleSize) {
			return Optional.of(select.pollLast());
		}
		return Optional.empty();
	}

	@Override
	public synchronized boolean removeStrategy(final TradingStrategy strategy) {
		return select.remove(strategy);
	}

	@Override
	public synchronized List<TradingStrategy> getStrategies() {
		final List<TradingStrategy> result = new LinkedList<>();
		for (TradingStrategy i : select) {
			result.add(i);
		}
		return Collections.unmodifiableList(result);
	}

	@Override
	public synchronized int currentStrategiesAmount() {
		return select.size();
	}
}
