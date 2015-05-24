package stsc.general.strategy.selector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import stsc.common.Settings;
import stsc.common.collections.SortedByRating;
import stsc.general.statistic.Metrics;
import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.strategy.TradingStrategy;

/**
 * This class implement complex {@link StrategySelector} storage that was
 * created for {@link TradingStrategy} Genetic Search.<br/>
 * Synopsis : the goal of this {@link StrategySelector} to store
 * {@link TradingStrategy} into clusters (using distance).
 * <hr/>
 * Implementation:<br/>
 * a. <b>ClusterKey</b> - store head strategy and implement equals method (for
 * all distanceParameters we calculate summary of absolute value for linear
 * combination: <b>R = abs(a1 * m11 - a1 * m12) + abs(a2 * m21 - a2 * m22) ...
 * </b>; if R < {@link Settings#doubleEpsilon} then equals returns true.<br/>
 * b. <b>ClusterKeyComparator</b> implements {@link Comparator} interface for
 * ClusterKey. (for compare ClusterKeyComparator use algorithm that we just
 * described).<br/>
 * 
 */
public class StatisticsWithDistanceSelector implements StrategySelector {

	private final class ClusterKey {
		private final TradingStrategy headStrategy;

		public ClusterKey(TradingStrategy headStrategy) {
			this.headStrategy = headStrategy;
		}

		public TradingStrategy getStrategy() {
			return headStrategy;
		}

		@Override
		public String toString() {
			return String.valueOf(headStrategy.getAvGain());
		}

		@Override
		public boolean equals(Object other) {
			if (!ClusterKey.class.isInstance(other))
				return false;
			final Metrics ls = this.getStrategy().getMetrics();
			final Metrics rs = ((ClusterKey) other).getStrategy().getMetrics();
			Double resDiff = 0.0;
			for (Entry<String, Double> e : distanceParameters.entrySet()) {
				final Double lv = ls.getDoubleMetric(e.getKey()) * e.getValue();
				final Double rv = rs.getDoubleMetric(e.getKey()) * e.getValue();
				resDiff += Math.abs(lv - rv);
			}
			return resDiff < Settings.doubleEpsilon;
		}
	}

	private final class ClusterKeyComparator implements Comparator<ClusterKey> {
		@Override
		public int compare(ClusterKey left, ClusterKey right) {
			final Metrics ls = left.getStrategy().getMetrics();
			final Metrics rs = right.getStrategy().getMetrics();
			Double resDiff = 0.0;
			for (Entry<String, Double> e : distanceParameters.entrySet()) {
				final Double lv = ls.getDoubleMetric(e.getKey()) * e.getValue();
				final Double rv = rs.getDoubleMetric(e.getKey()) * e.getValue();
				resDiff += Math.abs(lv - rv);
			}
			if (resDiff <= 1.0) {
				return 0;
			}
			return (int) (rs.getDoubleMetric("avGain") - ls.getDoubleMetric("avGain"));
		}
	}

	final int clustersAmount;
	final int elementsInCluster;
	final CostFunction costFunction;
	final private Map<String, Double> distanceParameters = new HashMap<>();

	final private Map<ClusterKey, StatisticsByCostSelector> clusters;
	final private SortedByRating<ClusterKey> clustersByRating = new SortedByRating<ClusterKey>(new ClusterKeyComparator());

	public StatisticsWithDistanceSelector(int clustersAmount, int elementsInCluster, CostFunction costFunction) {
		this.clustersAmount = clustersAmount;
		this.elementsInCluster = elementsInCluster;
		this.costFunction = costFunction;
		this.clusters = new TreeMap<ClusterKey, StatisticsByCostSelector>(new ClusterKeyComparator());
	}

	@Override
	public int currentStrategiesAmount() {
		int result = 0;
		for (StatisticsByCostSelector statisticsByCostSelector : clusters.values()) {
			result += statisticsByCostSelector.currentStrategiesAmount();
		}
		return result;
	}

	@Override
	public int maxPossibleAmount() {
		return clustersAmount * elementsInCluster;
	}

	public StatisticsWithDistanceSelector withDistanceParameter(String key, Double value) {
		distanceParameters.put(key, value);
		return this;
	}

	private Double rating(final TradingStrategy strategy) {
		return costFunction.calculate(strategy.getMetrics());
	}

	@Override
	public synchronized Optional<TradingStrategy> addStrategy(final TradingStrategy strategy) {
		final ClusterKey clusterKey = new ClusterKey(strategy);
		final StatisticsByCostSelector sc = clusters.get(clusterKey);
		if (sc == null) {
			final StatisticsByCostSelector selector = new StatisticsByCostSelector(elementsInCluster, costFunction);
			final Optional<TradingStrategy> ts = selector.addStrategy(strategy);
			if (!ts.isPresent() || strategy != ts.get()) {
				clustersByRating.addElement(rating(strategy), clusterKey);
				clusters.put(clusterKey, selector);
				checkAndRemoveCluster();
				return ts;
			} else
				return Optional.of(strategy);
		} else {
			return addStrategyToCluster(sc, clusterKey, strategy);
		}
	}

	@Override
	public boolean removeStrategy(TradingStrategy strategy) {
		final ClusterKey clusterKey = new ClusterKey(strategy);
		final StatisticsByCostSelector sc = clusters.get(clusterKey);
		if (sc != null) {
			return sc.removeStrategy(strategy);
		}
		return false;
	}

	private void checkAndRemoveCluster() {
		if (clusters.size() > clustersAmount) {
			final Optional<ClusterKey> deletedKeyPtr = clustersByRating.deleteLast();
			if (deletedKeyPtr.isPresent()) {
				final StatisticsByCostSelector cluster = clusters.remove(deletedKeyPtr.get());
				if (cluster != null) {
					for (TradingStrategy ts : cluster.getStrategies()) {
						clustersByRating.removeElement(rating(ts), new ClusterKey(ts));
					}
				}
			}
		}
	}

	private Optional<TradingStrategy> addStrategyToCluster(StatisticsByCostSelector sc, ClusterKey clusterKey, TradingStrategy strategy) {
		final Optional<TradingStrategy> ts = sc.addStrategy(strategy);
		if (ts.isPresent() && strategy != ts.get()) {
			clustersByRating.addElement(rating(strategy), clusterKey);
			findClusterAndDelete(ts.get());
		}
		return ts;
	}

	private void findClusterAndDelete(TradingStrategy ts) {
		clustersByRating.removeElement(rating(ts), new ClusterKey(ts));
		final StatisticsByCostSelector scToDelete = clusters.get(new ClusterKey(ts));
		if (scToDelete != null) {
			scToDelete.removeStrategy(ts);
		}
	}

	@Override
	public synchronized List<TradingStrategy> getStrategies() {
		final List<TradingStrategy> result = new ArrayList<>();
		for (Entry<ClusterKey, StatisticsByCostSelector> clusterValue : clusters.entrySet()) {
			result.addAll(clusterValue.getValue().getStrategies());
		}
		return result;
	}

}
