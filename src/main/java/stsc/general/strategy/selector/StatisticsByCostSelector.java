package stsc.general.strategy.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import stsc.general.statistic.Metrics;
import stsc.general.statistic.SortedStrategies;
import stsc.general.statistic.cost.comparator.MetricsSameComparator;
import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.strategy.TradingStrategy;

/**
 * This {@link StrategySelector} store best strategies by {@link CostFunction}.
 * Also it has border restrictions (max possible elements amounts).<br/>
 * So in case of adding the same element (by {@link MetricsSameComparator} we
 * will calculate the same rating and do not store 'same'
 * {@link TradingStrategy} two time.<br/>
 * In case of adding new element that has better rating, we will delete element
 * with worst rating. (In there is several of them, we will delete one of them
 * without any ordering / preferences).
 */
public final class StatisticsByCostSelector extends BorderedStrategySelector {

	private final CostFunction costFunction;
	private final SortedStrategies select;

	public StatisticsByCostSelector(final int selectLastElements, final CostFunction costFunction) {
		super(selectLastElements);
		this.costFunction = costFunction;
		this.select = new SortedByRatingStrategies(new MetricsSameComparator());
	}

	@Override
	public synchronized List<TradingStrategy> addStrategy(final TradingStrategy newStrategy) {
		final List<TradingStrategy> result = new ArrayList<>();
		final Metrics metrics = newStrategy.getMetrics();
		final Double compareValue = costFunction.calculate(metrics);
		if (select.addStrategy(compareValue, newStrategy)) {
			if (select.size() > maxPossibleSize) {
				final Optional<TradingStrategy> deletedTradingStrategy = select.deleteLast();
				if (deletedTradingStrategy.isPresent()) {
					result.add(deletedTradingStrategy.get());
				}
			}
		} else {
			result.add(newStrategy);
		}
		return result;
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
