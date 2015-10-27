package stsc.general.strategy.selector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.strategy.TradingStrategy;

/**
 * Can be used only with original {@link StrategySelector}. <br/>
 * Provide possibility to not add {@link TradingStrategy} in case when it is necessary to filter it out.<br/>
 * For example, if you like to not add strategies with low signals frequency you can {@link #withIntegerMinFilter(String,Integer)}
 * 
 */
public final class StrategyFilteringSelector extends BorderedStrategySelector {

	private final StrategySelector originalStrategySelector;

	private final HashMap<MetricType, Integer> integerMinFilters = new HashMap<>();
	private final HashMap<MetricType, Integer> integerMaxFilters = new HashMap<>();

	private final HashMap<MetricType, Double> doubleMinFilters = new HashMap<>();
	private final HashMap<MetricType, Double> doubleMaxFilters = new HashMap<>();

	public StrategyFilteringSelector(BorderedStrategySelector originalStrategySelector) {
		super(originalStrategySelector.maxPossibleAmount());
		this.originalStrategySelector = originalStrategySelector;
	}

	public StrategyFilteringSelector withIntegerMinFilter(final MetricType filterKey, final Integer filterValue) {
		integerMinFilters.put(filterKey, filterValue);
		return this;
	}

	public StrategyFilteringSelector withIntegerMaxFilter(final MetricType filterKey, final Integer filterValue) {
		integerMaxFilters.put(filterKey, filterValue);
		return this;
	}

	public StrategyFilteringSelector withDoubleMinFilter(final MetricType filterKey, final Double filterValue) {
		doubleMinFilters.put(filterKey, filterValue);
		return this;
	}

	public StrategyFilteringSelector withDoubleMaxFilter(final MetricType filterKey, final Double filterValue) {
		doubleMaxFilters.put(filterKey, filterValue);
		return this;
	}

	/**
	 * @return true if {@link TradingStrategy} out of filters.
	 */
	private boolean isFilteredOut(final TradingStrategy strategy) {
		final Metrics m = strategy.getMetrics();
		for (Entry<MetricType, Integer> i : integerMinFilters.entrySet()) {
			if (m.getIntegerMetric(i.getKey()) < i.getValue()) {
				return true;
			}
		}
		for (Entry<MetricType, Integer> i : integerMaxFilters.entrySet()) {
			if (m.getIntegerMetric(i.getKey()) > i.getValue()) {
				return true;
			}
		}
		for (Entry<MetricType, Double> i : doubleMinFilters.entrySet()) {
			if (m.getDoubleMetric(i.getKey()) < i.getValue()) {
				return true;
			}
		}
		for (Entry<MetricType, Double> i : doubleMaxFilters.entrySet()) {
			if (m.getDoubleMetric(i.getKey()) > i.getValue()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<TradingStrategy> addStrategy(final TradingStrategy newStrategy) {
		if (isFilteredOut(newStrategy)) {
			return Arrays.asList(newStrategy);
		}
		return originalStrategySelector.addStrategy(newStrategy);
	}

	@Override
	public boolean removeStrategy(final TradingStrategy strategy) {
		return originalStrategySelector.removeStrategy(strategy);
	}

	@Override
	public List<TradingStrategy> getStrategies() {
		return originalStrategySelector.getStrategies();
	}

	@Override
	public int currentStrategiesAmount() {
		return originalStrategySelector.currentStrategiesAmount();
	}

}
