package stsc.general.simulator;

import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import stsc.general.statistic.Metrics;
import stsc.general.trading.TradeProcessor;
import stsc.general.trading.TradeProcessorInit;

/**
 * All necessary values to simulate one trading strategy.<br/>
 * If we will think about {@link TradeProcessor} as about function f, then {@link Metrics} => f( {@link SimulatorSettingsImpl} ).
 */
public final class SimulatorSettingsImpl implements Cloneable, SimulatorSettings {

	private long id;
	private final TradeProcessorInit tradeProcessorInit;
	private final Optional<Set<String>> stockNames;

	public SimulatorSettingsImpl(final long id, TradeProcessorInit tradeProcessorInit) {
		this(id, tradeProcessorInit, Optional.empty());
	}

	public SimulatorSettingsImpl(final long id, TradeProcessorInit tradeProcessorInit, final Set<String> stockNames) {
		this(id, tradeProcessorInit, Optional.of(stockNames));
	}

	private SimulatorSettingsImpl(long id, TradeProcessorInit tradeProcessorInit, Optional<Set<String>> stockNames) {
		Validate.notNull(tradeProcessorInit);
		Validate.isTrue(id >= 0);
		Validate.notNull(stockNames);
		this.id = id;
		this.tradeProcessorInit = tradeProcessorInit;
		this.stockNames = stockNames;
	}

	@Override
	public TradeProcessorInit getInit() {
		return tradeProcessorInit;
	}

	@Override
	public String stringHashCode() {
		return tradeProcessorInit.stringHashCode();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return tradeProcessorInit.toString();
	}

	@Override
	public SimulatorSettingsImpl clone() {
		return new SimulatorSettingsImpl(id, tradeProcessorInit.clone(), stockNames);
	}

	@Override
	public Optional<Set<String>> getStockNames() {
		return stockNames;
	}

}
