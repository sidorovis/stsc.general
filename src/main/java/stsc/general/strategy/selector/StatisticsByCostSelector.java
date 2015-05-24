package stsc.general.strategy.selector;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import stsc.general.statistic.Metrics;
import stsc.general.statistic.cost.comparator.MetricsSameComparator;
import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.strategy.TradingStrategy;

/**
 * This {@link StrategySelector} store best strategies by {@link CostFunction}.
 * Also it has border restrictions (max possible elements amounts).
 */
public final class StatisticsByCostSelector extends BorderedStrategySelector {

	private final CostFunction costFunction;
	private final SortedByRatingStrategies select;

	public StatisticsByCostSelector(int selectLastElements, CostFunction costFunction) {
		super(selectLastElements);
		this.costFunction = costFunction;
		this.select = new SortedByRatingStrategies(new MetricsSameComparator());
	}

	@Override
	public synchronized Optional<TradingStrategy> addStrategy(final TradingStrategy strategy) {
		final Metrics metrics = strategy.getMetrics();
		final Double compareValue = costFunction.calculate(metrics);
		if (select.addStrategy(compareValue, strategy)) {
			if (select.size() > maxPossibleSize) {
				return select.deleteLast();
			}
		}
		return Optional.empty();
	}

	@Override
	public synchronized boolean removeStrategy(final TradingStrategy strategy) {
		return select.removeStrategy(costFunction.calculate(strategy.getMetrics()), strategy);
	}

	@Override
	public synchronized List<TradingStrategy> getStrategies() {
		final List<TradingStrategy> result = new LinkedList<>();
		for (Entry<Double, Collection<TradingStrategy>> i : select.getValues().entrySet()) {
			for (TradingStrategy strategy : i.getValue()) {
				result.add(strategy);
			}
		}
		return Collections.unmodifiableList(result);
	}

	@Override
	public synchronized int currentStrategiesAmount() {
		return select.size();
	}

	@Override
	public String toString() {
		return "Size: " + select.size();
	}

}
