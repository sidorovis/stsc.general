package stsc.general.strategy.selector;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import stsc.general.statistic.Metrics;
import stsc.general.statistic.cost.comparator.MetricsComparator;
import stsc.general.strategy.TradingStrategy;

/**
 * StatisticsSelector compare two {@link TradingStrategy} by {@link Metrics}.
 */
public class StatisticsCompareSelector extends BorderedStrategySelector {

	private final TradingStrategyComparator strategyComparator;
	private final TreeSet<TradingStrategy> select;

	public StatisticsCompareSelector(int selectLastElements, MetricsComparator comparator) {
		super(selectLastElements);
		this.strategyComparator = new TradingStrategyComparator(comparator);
		this.select = new TreeSet<TradingStrategy>(strategyComparator);
	}

	@Override
	public synchronized List<TradingStrategy> addStrategy(final TradingStrategy newStrategy) {
		if (select.add(newStrategy)) {
			if (select.size() > maxPossibleSize) {
				return Arrays.asList(select.pollLast());
			}
		} else {
			return Arrays.asList(newStrategy);
		}
		return Collections.emptyList();
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
