package stsc.general.statistic;

import java.util.List;
import java.util.Optional;

import stsc.general.strategy.TradingStrategy;

/**
 * {@link StrategySelector} interface is an interface for controlling
 * collections of {@link TradingStrategy}.<br/>
 * Common interface to select strategies from different {@link TradingStrategy}
 * searchers. Be careful and implement multi-thread protected Strategy
 * Selectors.
 */
public interface StrategySelector {

	/**
	 * If max possible size was reached then we will delete one of the stored
	 * {@link TradingStrategy} and
	 * 
	 * @return {@link Optional} value in case when one of
	 *         {@link TradingStrategy} was deleted.
	 */
	Optional<TradingStrategy> addStrategy(final TradingStrategy strategy);

	/**
	 * @return true if {@link TradingStrategy} was deleted from
	 *         {@link StrategySelector}.
	 */
	boolean removeStrategy(final TradingStrategy strategy);

	/**
	 * @return list of {@link TradingStrategy} s.
	 */
	List<TradingStrategy> getStrategies();

	/**
	 * @return current size of stored {@link TradingStrategy} s.
	 */
	int currentStrategiesAmount();
}
