package stsc.general.strategy.selector;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import stsc.general.statistic.Metrics;
import stsc.general.strategy.TradingStrategy;

/**
 * Can be used only with original {@link StrategySelector}. <br/>
 * Provide possibility to not add {@link TradingStrategy} in case when it is
 * necessary to filter it out.<br/>
 * For example, if you like to not add strategies with low signals frequency you
 * can {@link #withIntegerMinFilter(String,Integer)}
 * 
 */
public final class StrategyFilteringSelector implements StrategySelector {

	private final StrategySelector originalStrategySelector;

	private final HashMap<String, Integer> integerMinFilters = new HashMap<>();
	private final HashMap<String, Integer> integerMaxFilters = new HashMap<>();

	private final HashMap<String, Double> doubleMinFilters = new HashMap<>();
	private final HashMap<String, Double> doubleMaxFilters = new HashMap<>();

	public StrategyFilteringSelector(StrategySelector originalStrategySelector) {
		super();
		this.originalStrategySelector = originalStrategySelector;
	}

	public StrategyFilteringSelector withIntegerMinFilter(final String filterKey, final Integer filterValue) {
		integerMinFilters.put(filterKey, filterValue);
		return this;
	}

	public StrategyFilteringSelector withIntegerMaxFilter(final String filterKey, final Integer filterValue) {
		integerMaxFilters.put(filterKey, filterValue);
		return this;
	}

	public StrategyFilteringSelector withDoubleMinFilter(final String filterKey, final Double filterValue) {
		doubleMinFilters.put(filterKey, filterValue);
		return this;
	}

	public StrategyFilteringSelector withDoubleMaxFilter(final String filterKey, final Double filterValue) {
		doubleMaxFilters.put(filterKey, filterValue);
		return this;
	}

	/**
	 * @return true if {@link TradingStrategy} out of filters.
	 */
	private boolean isFilteredOut(final TradingStrategy strategy) {
		final Metrics m = strategy.getMetrics();
		for (Entry<String, Integer> i : integerMinFilters.entrySet()) {
			if (m.getIntegerMetric(i.getKey()) < i.getValue()) {
				return true;
			}
		}
		for (Entry<String, Integer> i : integerMaxFilters.entrySet()) {
			if (m.getIntegerMetric(i.getKey()) > i.getValue()) {
				return true;
			}
		}
		for (Entry<String, Double> i : doubleMinFilters.entrySet()) {
			if (m.getDoubleMetric(i.getKey()) < i.getValue()) {
				return true;
			}
		}
		for (Entry<String, Double> i : doubleMaxFilters.entrySet()) {
			if (m.getDoubleMetric(i.getKey()) > i.getValue()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Optional<TradingStrategy> addStrategy(final TradingStrategy strategy) {
		if (isFilteredOut(strategy)) {
			return Optional.of(strategy);
		}
		return originalStrategySelector.addStrategy(strategy);
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

	@Override
	public int maxPossibleAmount() {
		return originalStrategySelector.maxPossibleAmount();
	}

}
