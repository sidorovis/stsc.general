package stsc.general.simulator.multistarter;

/**
 * This exception used for informing {@link StrategySearcher} users about possible problems at the strategy search process.
 */
public final class StrategySearcherException extends Exception {

	private static final long serialVersionUID = 1L;

	public StrategySearcherException(final String message) {
		super(message);
	}
}
