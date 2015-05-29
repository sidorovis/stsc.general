package stsc.general.strategy.selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import stsc.common.Settings;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.statistic.SortedStrategies;
import stsc.general.statistic.cost.comparator.CostFunctionToComparator;
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
 * This selector never change center element of cluster.
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
			for (Entry<MetricType, Double> e : distanceParameters.entrySet()) {
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
			for (Entry<MetricType, Double> e : distanceParameters.entrySet()) {
				final Double lv = ls.getDoubleMetric(e.getKey()) * e.getValue();
				final Double rv = rs.getDoubleMetric(e.getKey()) * e.getValue();
				resDiff += Math.abs(lv - rv);
			}
			if (resDiff <= 1.0) {
				return 0;
			}
			return (int) (rs.getDoubleMetric(MetricType.avGain) - ls.getDoubleMetric(MetricType.avGain));
		}
	}

	private final ClusterKeyComparator clusterKeyComparator;

	private final int clustersAmount;
	private final int elementsInCluster;
	/**
	 * {@link #ratingCostFunction} is cost function to calculate rating of
	 * {@link TradingStrategy} for sort clusters by rating.<br/>
	 * We need sorted clusters to have possibility to delete strategies with
	 * smallest rating.
	 */
	private final CostFunction ratingCostFunction;
	private final CostFunctionToComparator metricsComparator;
	private final Map<MetricType, Double> distanceParameters;

	private final TreeMap<ClusterKey, SortedStrategies> clustersByKey;

	public StatisticsWithDistanceSelector(int clustersAmount, int elementsInCluster, CostFunction ratingCostFunction) {
		this.clusterKeyComparator = new ClusterKeyComparator();
		this.clustersAmount = clustersAmount;
		this.elementsInCluster = elementsInCluster;
		this.ratingCostFunction = ratingCostFunction;
		this.metricsComparator = new CostFunctionToComparator(ratingCostFunction);
		this.distanceParameters = new HashMap<>();
		this.clustersByKey = new TreeMap<ClusterKey, SortedStrategies>(clusterKeyComparator);
	}

	@Override
	public int currentStrategiesAmount() {
		int result = 0;
		for (SortedStrategies strategies : clustersByKey.values()) {
			result += strategies.size();
		}
		return result;
	}

	@Override
	public int maxPossibleAmount() {
		return clustersAmount * elementsInCluster;
	}

	public StatisticsWithDistanceSelector withDistanceParameter(MetricType key, Double value) {
		distanceParameters.put(key, value);
		return this;
	}

	private Double rating(final TradingStrategy strategy) {
		return ratingCostFunction.calculate(strategy.getMetrics());
	}

	@Override
	public synchronized List<TradingStrategy> addStrategy(final TradingStrategy newStrategy) {
		final ClusterKey clusterKey = new ClusterKey(newStrategy);
		final SortedStrategies cluster = clustersByKey.get(clusterKey);
		if (cluster == null) {
			final SortedStrategies newCluster = new SortedByRatingStrategies(metricsComparator);
			newCluster.addStrategy(rating(newStrategy), newStrategy);
			clustersByKey.put(clusterKey, newCluster);
			return checkAndFixSize(clusterKey, newStrategy);
		} else {
			return addStrategyToCluster(cluster, clusterKey, newStrategy);
		}
	}

	private List<TradingStrategy> checkAndFixSize(ClusterKey clusterKey, TradingStrategy newStrategy) {
		final List<TradingStrategy> deletedStrategies = new ArrayList<>();
		if (clustersByKey.size() > clustersAmount) {
			final Entry<ClusterKey, SortedStrategies> lastEntry = clustersByKey.lastEntry();
			clustersByKey.remove(lastEntry.getKey());
			for (Collection<TradingStrategy> i : lastEntry.getValue().getValues().values()) {
				deletedStrategies.addAll(i);
			}
		}
		if (currentStrategiesAmount() > maxPossibleAmount()) {
			final Entry<ClusterKey, SortedStrategies> lastEntry = clustersByKey.lastEntry();
			if (lastEntry.getValue().size() > 1) {
				deletedStrategies.add(lastEntry.getValue().deleteLast().get());
			} else {
				clustersByKey.remove(lastEntry.getKey());
				final Optional<TradingStrategy> tradingStrategyToDelete = lastEntry.getValue().deleteLast();
				deletedStrategies.add(tradingStrategyToDelete.get());
			}
		}
		return deletedStrategies;
	}

	@Override
	public boolean removeStrategy(TradingStrategy strategy) {
		final ClusterKey clusterKey = new ClusterKey(strategy);
		final SortedStrategies sc = clustersByKey.get(clusterKey);
		if (sc != null) {
			sc.removeStrategy(rating(strategy), strategy);
			if (sc.size() == 0) {
				clustersByKey.remove(clusterKey);
			}
			return true;
		}
		return false;
	}

	private List<TradingStrategy> addStrategyToCluster(SortedStrategies cluster, ClusterKey clusterKey, TradingStrategy newStrategy) {
		if (cluster.addStrategy(rating(newStrategy), newStrategy)) {
			if (cluster.size() > elementsInCluster) {
				return Arrays.asList(cluster.deleteLast().get());
			}
			return checkAndFixSize(clusterKey, newStrategy);
		}
		return Arrays.asList(newStrategy);
	}

	@Override
	public synchronized List<TradingStrategy> getStrategies() {
		final List<TradingStrategy> result = new ArrayList<>();
		for (SortedStrategies clusterValue : clustersByKey.values()) {
			for (Collection<TradingStrategy> i : clusterValue.getValues().values())
				result.addAll(i);
		}
		return result;
	}
}
