package stsc.general.simulator.multistarter.genetic;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Maps;

import stsc.common.BadSignalException;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.SignalsStorage;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorFactory;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.selector.StatisticsWithDistanceSelector;
import stsc.general.strategy.selector.StrategySelector;
import stsc.general.trading.TradeProcessorInit;

/**
 * This test search max of http://www.wolframalpha.com/input/?i=z+%3D+10.-%28-4+%28x%29%5E2%2Bx%5E4%29-%281%2By%29%5E2 function.
 */
public class StrategyGeneticSearcherTest {

	private static double FROM = -10.0;
	private static double TO = 10.0;

	private static final class TestSimulatorSettings implements SimulatorSettings {

		private double x;
		private double y;

		TestSimulatorSettings(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public TradeProcessorInit getInit() {
			return null;
		}

		@Override
		public String stringHashCode() {
			return String.valueOf(x) + " " + String.valueOf(y);
		}

		@Override
		public SimulatorSettings clone() {
			return new TestSimulatorSettings(x, y);
		}

		@Override
		public Optional<Set<String>> getStockNames() {
			return Optional.empty();
		}

		@Override
		public long getId() {
			return 0;
		}

		public static double getX(SimulatorSettings ss) {
			return Double.valueOf(ss.stringHashCode().split(" ")[0]);
		}

		public static double getY(SimulatorSettings ss) {
			return Double.valueOf(ss.stringHashCode().split(" ")[1]);
		}

		public String toString() {
			return Double.valueOf(x) + " " + y;
		}

	}

	private static final class TestGeneticList implements GeneticList {

		private Random r = new Random();

		private double generateRandomDouble() {
			return generateRandomDouble(FROM, TO);
		}

		private double generateRandomDouble(double f, double t) {
			double rf = Math.min(f, t);
			double rt = Math.max(f, t);
			return rf + r.nextDouble() * (rt - rf);
		}

		@Override
		public SimulatorSettings generateRandom() throws BadAlgorithmException {
			final double x = generateRandomDouble();
			final double y = generateRandomDouble();
			return new TestSimulatorSettings(x, y);
		}

		@Override
		public SimulatorSettings mutate(SimulatorSettings settings) {
			final boolean shouldMutateX = r.nextBoolean();
			if (shouldMutateX) {
				return new TestSimulatorSettings(mutate(TestSimulatorSettings.getX(settings)), TestSimulatorSettings.getY(settings));
			} else {
				return new TestSimulatorSettings(TestSimulatorSettings.getX(settings), mutate(TestSimulatorSettings.getY(settings)));
			}
		}

		private double mutate(double v) {
			final boolean hugeMutation = r.nextDouble() > 0.7;
			if (hugeMutation)
				return generateRandomDouble();
			else {
				double r = generateRandomDouble() / 10.0;
				while (r > TO || r < FROM) {
					r = generateRandomDouble() / 10.0;
				}
				return r;
			}

		}

		@Override
		public SimulatorSettings merge(SimulatorSettings left, SimulatorSettings right) {
			final double x = generateRandomDouble(TestSimulatorSettings.getX(left), TestSimulatorSettings.getX(right));
			final double y = generateRandomDouble(TestSimulatorSettings.getY(left), TestSimulatorSettings.getY(right));
			return new TestSimulatorSettings(x, y);
		}

	}

	private static final class TestSimulator implements Simulator {

		private Metrics metrics;

		@Override
		public void simulateMarketTrading(SimulatorSettings simulatorSettings) throws BadAlgorithmException, BadSignalException {
			final double x = TestSimulatorSettings.getX(simulatorSettings);
			final double y = TestSimulatorSettings.getY(simulatorSettings);
			final double v = calculate(x, y);
			final Map<MetricType, Double> d = new HashMap<>();
			d.put(MetricType.avGain, v);
			metrics = new Metrics(d, Maps.newHashMap());
		}

		private double calculate(double x, double y) {
			return 10.0 - (Math.pow(x, 4) - 4 * Math.pow(x, 2)) - Math.pow(y + 1, 2.0);
		}

		@Override
		public Metrics getMetrics() {
			return metrics;
		}

		@Override
		public SignalsStorage getSignalsStorage() {
			return null;
		}

	}

	private static final class TestSimulatorFactory implements SimulatorFactory {

		@Override
		public Simulator createSimulator() {
			return new TestSimulator();
		}

	}

	@Test
	public void testStrategyGeneticSearcher() throws StrategySearcherException {
		final StrategyGeneticSearcher searcher = StrategyGeneticSearcher.getBuilder(). //
				withPopulationCostFunction(new CostWeightedSumFunction()). //
				withGeneticList(new TestGeneticList()). //
				withStrategySelector(new StatisticsWithDistanceSelector(10, 10, new CostWeightedSumFunction())). //
				withSimulatorFactory(new TestSimulatorFactory()). //
				withMaxPopulationsAmount(250). //
				withPopulationSize(500). //
				build();
		final StrategySelector strategySelector = searcher.waitAndGetSelector();
		Assert.assertEquals(14, strategySelector.getStrategies().get(0).getAvGain(), 0.1);
		Assert.assertEquals(1.41, Math.abs(TestSimulatorSettings.getX(strategySelector.getStrategies().get(0).getSettings())), 0.1);
		Assert.assertEquals(-1, TestSimulatorSettings.getY(strategySelector.getStrategies().get(0).getSettings()), 0.1);
	}

}
