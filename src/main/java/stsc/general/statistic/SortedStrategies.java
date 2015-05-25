package stsc.general.statistic;

import java.util.Collection;
import java.util.Optional;
import java.util.SortedMap;

import com.google.common.collect.TreeMultimap;

import stsc.general.strategy.TradingStrategy;
import stsc.general.strategy.selector.StrategySelector;

/**
 * Store sorted {@link TradingStrategy}'s by Double value (Rating). Can return
 * SortedMap Double -> Collection of {@link TradingStrategy}.<br/>
 * The main difference from {@link StrategySelector} is no logic for adding /
 * deleting. No any restrictions. You can think about {@link SortedStrategies}
 * as adaptation of {@link TreeMultimap} for storing pairs: Rating(double) ->
 * tradingStrategy.
 */
public interface SortedStrategies {

	/**
	 * @return true if {@link TradingStrategy} was added.
	 */
	boolean addStrategy(Double rating, TradingStrategy value);

	boolean removeStrategy(Double rating, TradingStrategy value);

	/**
	 * 
	 * @return deleted {@link TradingStrategy} (or {@link Optional#empty()}.
	 */
	Optional<TradingStrategy> deleteLast();

	int size();

	SortedMap<Double, Collection<TradingStrategy>> getValues();

}
