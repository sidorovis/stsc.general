package stsc.general.strategy.selector;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import stsc.general.simulator.multistarter.genetic.StrategyGeneticSearcher;
import stsc.general.strategy.TradingStrategy;

/**
 * {@link StrategySelector} - interface for storing and <b>controlling</b>
 * collections of {@link TradingStrategy}.<br/>
 * Common interface to select strategies from different {@link TradingStrategy}
 * searchers. Be careful and implement multi-thread protected Strategy
 * Selectors.<br/>
 * This interface do not guarantee that strategies are sorted.
 */
public interface StrategySelector {

	/**
	 * Add {@link TradingStrategy} to {@link StrategySelector}.<br/>
	 * 
	 * If for any internal reason some of stored strategies should be deleted
	 * while adding, we will return deleted elements in the result.
	 * 
	 * @return {@link List} of deleted {@link TradingStrategy}.
	 */
	List<TradingStrategy> addStrategy(final TradingStrategy strategy);

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

	/**
	 * @return max possible amount of strategies that we store (required by
	 *         {@link StrategyGeneticSearcher}.
	 */
	int maxPossibleAmount();
}
