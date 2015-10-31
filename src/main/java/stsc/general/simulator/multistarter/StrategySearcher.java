package stsc.general.simulator.multistarter;

import stsc.general.strategy.selector.StrategySelector;

/**
 * {@link StrategySearcher} is a multi-strategy searcher mechanism interface. Used by Grid and Genetic search mechanisms.
 */
public interface StrategySearcher {

	static public interface IndicatorProgressListener {
		void processed(double percent);
	}

	void addIndicatorProgress(final IndicatorProgressListener listener);

	StrategySelector waitAndGetSelector() throws StrategySearcherException;

	void stopSearch();

}
