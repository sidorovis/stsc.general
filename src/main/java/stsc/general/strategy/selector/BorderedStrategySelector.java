package stsc.general.strategy.selector;

/**
 * {@link StrategySelector} with protected maxPossibleSize field.
 */
public abstract class BorderedStrategySelector implements StrategySelector {

	protected final int maxPossibleSize;

	protected BorderedStrategySelector(final int maxPossibleSize) {
		this.maxPossibleSize = maxPossibleSize;
	}

	@Override
	public int maxPossibleAmount() {
		return maxPossibleSize;
	}
}
