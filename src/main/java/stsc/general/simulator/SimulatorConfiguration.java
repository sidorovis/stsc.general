package stsc.general.simulator;

import java.util.Optional;
import java.util.Set;

import stsc.general.trading.TradeProcessorInit;

public interface SimulatorConfiguration extends Cloneable {

	long getId();

	TradeProcessorInit getInit();

	String stringHashCode();

	SimulatorConfiguration clone();

	Optional<Set<String>> getStockNames();

	String toString();

}
