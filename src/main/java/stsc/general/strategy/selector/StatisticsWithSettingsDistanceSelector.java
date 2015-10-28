package stsc.general.strategy.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import stsc.general.simulator.multistarter.genetic.settings.distance.SimulatorSettingsInterval;
import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.strategy.TradingStrategy;

/**
 * This algorithm require domen for each parameter.
 */
public final class StatisticsWithSettingsDistanceSelector extends BorderedStrategySelector {

	private final SimulatorSettingsInterval simulatorSettingsInterval;
	private final CostFunction costFunction;

	private final TreeMap<Double, TradingStrategy> strategiesByCost = new TreeMap<>(Collections.reverseOrder());

	private double epsilon = 2;

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
		boolean shouldWeAddStrategy = true;
		for (TradingStrategy tradingStrategy : strategiesByCost.values()) {
			final double distance = simulatorSettingsInterval.calculateInterval(tradingStrategy.getSettings(), strategy.getSettings());
			if (distance < epsilon) {
				final Double storedStrategyCost = costFunction.calculate(strategy.getMetrics());
				if (storedStrategyCost > strategyCost) {
					deletedElements.add(tradingStrategy);
				} else {
					shouldWeAddStrategy = false;
				}
				break;
			}
		}
		if (!deletedElements.isEmpty()) {
			final Double deletingElementCost = costFunction.calculate(strategy.getMetrics());
			if (strategiesByCost.remove(deletingElementCost) != null) {
				addStrategy(strategyCost, strategy);
			}
		} else {
			if (shouldWeAddStrategy) {
				addStrategy(strategyCost, strategy);
				if (strategiesByCost.size() > maxPossibleAmount()) {
					deletedElements.add(strategiesByCost.pollLastEntry().getValue());
				}
			}
		}
		return deletedElements;
	}

	private void addStrategy(double strategyCost, TradingStrategy strategy) {
		if (!strategiesByCost.containsKey(strategyCost)) {
			strategiesByCost.put(strategyCost, strategy);
		}
	}

	@Override
	public synchronized boolean removeStrategy(TradingStrategy strategy) {
		final Double deletingElementCost = costFunction.calculate(strategy.getMetrics());
		return strategiesByCost.remove(deletingElementCost) != null;
	}

	@Override
	public List<TradingStrategy> getStrategies() {
		return new ArrayList<>(strategiesByCost.values());
	}

	@Override
	public int currentStrategiesAmount() {
		return strategiesByCost.size();
	}

}
