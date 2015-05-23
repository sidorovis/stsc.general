package stsc.general.statistic;

import java.util.Collection;
import java.util.Optional;
import java.util.SortedMap;

import stsc.general.strategy.TradingStrategy;

public interface SortedStrategies {

	public boolean addStrategy(Double rating, TradingStrategy value);

	public boolean removeStrategy(Double rating, TradingStrategy value);

	public Optional<TradingStrategy> deleteLast();

	public int size();

	public SortedMap<Double, Collection<TradingStrategy>> getValues();

}
