package stsc.general.simulator;

import java.util.Optional;
import java.util.Set;

import stsc.general.trading.TradeProcessorInit;

public interface SimulatorSettings {

	long getId();

	TradeProcessorInit getInit();

	String stringHashCode();

	SimulatorSettings clone();

	Optional<Set<String>> getStockNames();

	String toString();

}