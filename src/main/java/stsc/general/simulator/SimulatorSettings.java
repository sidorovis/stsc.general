package stsc.general.simulator;

import org.apache.commons.lang3.Validate;

import stsc.general.statistic.Metrics;
import stsc.general.trading.TradeProcessor;
import stsc.general.trading.TradeProcessorInit;

/**
 * All necessary values to simulate one trading strategy.<br/>
 * If we will think about {@link TradeProcessor} as about function f, then
 * {@link Metrics} => f( {@link SimulatorSettings} ).
 */
public class SimulatorSettings implements Cloneable {

	private long id;
	private final TradeProcessorInit tradeProcessorInit;

	public SimulatorSettings(final long id, TradeProcessorInit tradeProcessorInit) {
		Validate.notNull(tradeProcessorInit);
		Validate.isTrue(id >= 0);
		this.id = id;
		this.tradeProcessorInit = tradeProcessorInit;
	}

	public TradeProcessorInit getInit() {
		return tradeProcessorInit;
	}

	public String stringHashCode() {
		return tradeProcessorInit.stringHashCode();
	}

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return tradeProcessorInit.toString();
	}

	@Override
	public SimulatorSettings clone() {
		return new SimulatorSettings(id, tradeProcessorInit.clone());
	}

}
