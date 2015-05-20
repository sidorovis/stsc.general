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

	public Optional<TradingStrategy> addStrategy(final TradingStrategy strategy);

	public void removeStrategy(final TradingStrategy strategy);

	public List<TradingStrategy> getStrategies();

	int size();
}
