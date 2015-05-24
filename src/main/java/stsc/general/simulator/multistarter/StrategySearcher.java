package stsc.general.simulator.multistarter;

import stsc.general.strategy.selector.StrategySelector;

public interface StrategySearcher {

	static public interface IndicatorProgressListener {
		void processed(double percent);
	}

	StrategySelector waitAndGetSelector() throws StrategySearcherException;

	void stopSearch();

	void addIndicatorProgress(final IndicatorProgressListener listener);
}
