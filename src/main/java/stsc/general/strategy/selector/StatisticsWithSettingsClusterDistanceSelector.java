package stsc.general.strategy.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import stsc.general.simulator.multistarter.genetic.settings.distance.SimulatorSettingsInterval;
import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.strategy.TradingStrategy;

public final class StatisticsWithSettingsClusterDistanceSelector extends BorderedStrategySelector {

	private final class ClusterKey implements Comparable<ClusterKey> {

		private final Double strategyCost;
		private final TradingStrategy tradingStrategy;

		public ClusterKey(final Double strategyCost, final TradingStrategy tradingStrategy) {
			this.strategyCost = strategyCost;
			this.tradingStrategy = tradingStrategy;
		}

		@Override
		public int compareTo(ClusterKey o) {
			return strategyCost.compareTo(o.strategyCost);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj.getClass().equals(this.getClass())) {
				return equals((ClusterKey) obj);
			}
			return false;
		}

		private boolean equals(ClusterKey o) {
			return strategyCost.equals(o.strategyCost) && tradingStrategy.getSettings().getInit().getExecutionsStorage().stringHashCode()
					.equals(o.tradingStrategy.getSettings().getInit().getExecutionsStorage().stringHashCode());
		}

		@Override
		public String toString() {
			return "(" + strategyCost + ")";
		}

	}

	private final class Cluster {

		private final TreeMap<Double, TradingStrategy> strategiesByCost = new TreeMap<>(Collections.reverseOrder());

		Cluster(final Double keyStrategyCost, final TradingStrategy keyStrategy) {
			strategiesByCost.put(keyStrategyCost, keyStrategy);
		}

		public ClusterKey calculateClusterKey() {
			return new ClusterKey(strategiesByCost.firstKey(), strategiesByCost.firstEntry().getValue());
		}

		public Optional<TradingStrategy> addStrategy(final Double newStrategyCost, final TradingStrategy newStrategy) {
			if ((strategiesByCost.size() == maxElementsInCluster && strategiesByCost.lastKey() > newStrategyCost) || strategiesByCost.containsKey(newStrategyCost)) {
				return Optional.of(newStrategy);
			}
			strategiesByCost.put(newStrategyCost, newStrategy);
			if (strategiesByCost.size() > maxElementsInCluster) {
				return Optional.of(strategiesByCost.pollLastEntry().getValue());
			}
			return Optional.empty();
		}

		public double getDistance(final TradingStrategy strategy) {
			double distance = 0.0;
			for (TradingStrategy ts : strategiesByCost.values()) {
				distance += simulatorSettingsInterval.calculateInterval(strategy.getSettings(), ts.getSettings());
			}
			return distance / strategiesByCost.size();
		}

		public int size() {
			return strategiesByCost.size();
		}

		@Override
		public String toString() {
			return "[" + strategiesByCost.size() + "]\n";
		}

	}

	private final SimulatorSettingsInterval simulatorSettingsInterval;
	private final CostFunction costFunction;

	private final TreeMap<ClusterKey, Cluster> clustersByCost = new TreeMap<>(Collections.reverseOrder());

	private int maxAmountOfClusters = 10;
	private int maxElementsInCluster = 10;
	private double epsilon = 2;

	public StatisticsWithSettingsClusterDistanceSelector(final int maxAmountOfClusters, final int maxElementsInCluster, final SimulatorSettingsInterval simulatorSettingsInterval,
			CostFunction costFunction) {
		super(maxAmountOfClusters * maxElementsInCluster);
		this.maxAmountOfClusters = maxAmountOfClusters;
		this.maxElementsInCluster = maxElementsInCluster;
		this.simulatorSettingsInterval = simulatorSettingsInterval;
		this.costFunction = costFunction;
	}

	public StatisticsWithSettingsClusterDistanceSelector setMaxElementsInCluster(int maxElementsInCluster) {
		this.maxElementsInCluster = maxElementsInCluster;
		return this;
	}

	public StatisticsWithSettingsClusterDistanceSelector setEpsilon(double epsilon) {
		this.epsilon = epsilon;
		return this;
	}

	@Override
	public synchronized List<TradingStrategy> addStrategy(TradingStrategy strategy) {
		final Double strategyCost = costFunction.calculate(strategy.getMetrics());
		final List<TradingStrategy> deletedElements = new ArrayList<>();
		final Optional<ClusterKey> closiestClusterKey = findClosiestCluster(strategy);
		if (!closiestClusterKey.isPresent()) {
			final Cluster newCluster = new Cluster(strategyCost, strategy);
			clustersByCost.put(newCluster.calculateClusterKey(), newCluster);
		} else {
			final Cluster clusterToAddTo = clustersByCost.remove(closiestClusterKey.get());
			final Optional<TradingStrategy> deletedStrategy = clusterToAddTo.addStrategy(strategyCost, strategy);
			clustersByCost.put(clusterToAddTo.calculateClusterKey(), clusterToAddTo);
			if (deletedStrategy.isPresent()) {
				deletedElements.add(deletedStrategy.get());
			}
		}
		if (clustersByCost.size() > maxAmountOfClusters) {
			final Cluster clusterToDelete = clustersByCost.pollLastEntry().getValue();
			deletedElements.addAll(clusterToDelete.strategiesByCost.values());
		}
		return deletedElements;
	}

	private Optional<ClusterKey> findClosiestCluster(TradingStrategy strategy) {
		ClusterKey closiestClusterKey = null;
		double distanceToClosiestCluster = Double.MAX_VALUE;
		for (Entry<ClusterKey, Cluster> e : clustersByCost.entrySet()) {
			final double distanceToCluster = e.getValue().getDistance(strategy);
			if (distanceToCluster < distanceToClosiestCluster && distanceToCluster < epsilon) {
				closiestClusterKey = e.getKey();
				distanceToClosiestCluster = distanceToCluster;
			}
		}
		return Optional.ofNullable(closiestClusterKey);
	}

	@Override
	public synchronized boolean removeStrategy(TradingStrategy strategy) {
		return false;
	}

	@Override
	public List<TradingStrategy> getStrategies() {
		final ArrayList<TradingStrategy> tradingStrategies = new ArrayList<>();
		for (Cluster c : clustersByCost.values()) {
			tradingStrategies.addAll(c.strategiesByCost.values());
		}
		Collections.sort(tradingStrategies, new Comparator<TradingStrategy>() {
			@Override
			public int compare(TradingStrategy o1, TradingStrategy o2) {
				return Double.compare(costFunction.calculate(o2.getMetrics()), costFunction.calculate(o1.getMetrics()));
			}
		});
		return tradingStrategies;
	}

	@Override
	public int currentStrategiesAmount() {
		int size = 0;
		for (Cluster c : clustersByCost.values()) {
			size += c.size();
		}
		return size;
	}

}
