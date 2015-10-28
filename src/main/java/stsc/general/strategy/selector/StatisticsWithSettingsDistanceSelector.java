package stsc.general.strategy.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import stsc.general.simulator.multistarter.genetic.settings.distance.SimulatorSettingsInterval;
import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.strategy.TradingStrategy;

public final class StatisticsWithSettingsDistanceSelector extends BorderedStrategySelector {

	private final SimulatorSettingsInterval simulatorSettingsInterval;
	private final CostFunction costFunction;
	private double epsilon = 2;

	private final HashSet<TradingStrategy> strategies = new HashSet<>();
	private final TreeMap<Double, TradingStrategy> strategiesByCost = new TreeMap<>(Collections.reverseOrder());

	public StatisticsWithSettingsDistanceSelector(int maxPossibleSize, final SimulatorSettingsInterval simulatorSettingsInterval, CostFunction costFunction) {
		super(maxPossibleSize);
		this.simulatorSettingsInterval = simulatorSettingsInterval;
		this.costFunction = costFunction;
	}

	public StatisticsWithSettingsDistanceSelector setEpsilon(double epsilon) {
		this.epsilon = epsilon;
		return this;
	}

	@Override
	public synchronized List<TradingStrategy> addStrategy(TradingStrategy strategy) {
		final Double strategyCost = costFunction.calculate(strategy.getMetrics());
		final List<TradingStrategy> deletedElements = new ArrayList<>();
		for (TradingStrategy tradingStrategy : strategies) {
			final double distance = simulatorSettingsInterval.calculateInterval(tradingStrategy.getSettings(), strategy.getSettings());
			if (distance < epsilon) {
				final Double storedStrategyCost = costFunction.calculate(strategy.getMetrics());
				if (storedStrategyCost > strategyCost) {
					deletedElements.add(tradingStrategy);
					break;
				}
			}
		}
		if (!deletedElements.isEmpty()) {
			if (strategies.remove(deletedElements.get(0))) {
				final Double deletedStrategyCost = costFunction.calculate(deletedElements.get(0).getMetrics());
				strategiesByCost.remove(deletedStrategyCost);
				addStrategy(strategyCost, strategy);
			}
		} else {
			addStrategy(strategyCost, strategy);
			if (strategies.size() > maxPossibleAmount()) {
				final TradingStrategy deletingStrategy = strategiesByCost.pollLastEntry().getValue();
				strategies.remove(deletingStrategy);
				deletedElements.add(deletingStrategy);
			}
		}
		return deletedElements;
	}

	private void addStrategy(double strategyCost, TradingStrategy strategy) {
		if (!strategiesByCost.containsKey(strategyCost)) {
			strategies.add(strategy);
			strategiesByCost.put(strategyCost, strategy);
		}
	}

	@Override
	public synchronized boolean removeStrategy(TradingStrategy strategy) {
		return strategies.remove(strategies);
	}

	@Override
	public List<TradingStrategy> getStrategies() {
		return new ArrayList<>(strategiesByCost.values());
	}

	@Override
	public int currentStrategiesAmount() {
		return strategies.size();
	}

}
