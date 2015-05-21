package stsc.general.statistic;

public abstract class BorderedStrategySelector implements StrategySelector {

	protected final int maxPossibleSize;

	protected BorderedStrategySelector(final int maxPossibleSize) {
		this.maxPossibleSize = maxPossibleSize;
	}

}
