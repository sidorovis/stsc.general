package stsc.general.simulator;

import java.util.Optional;
import java.util.Set;

import stsc.general.trading.TradeProcessorInit;

public interface Execution extends Cloneable {

	long getId();

	TradeProcessorInit getInit();

	String stringHashCode();

	Execution clone();

	Optional<Set<String>> getStockNames();

	String toString();

}
