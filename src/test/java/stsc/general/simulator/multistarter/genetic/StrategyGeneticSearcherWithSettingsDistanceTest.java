package stsc.general.simulator.multistarter.genetic;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.general.simulator.SimulatorFactoryImpl;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.simulator.multistarter.genetic.settings.distance.SimulatorSettingsIntervalImpl;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.selector.StatisticsWithSettingsDistanceSelector;
import stsc.general.strategy.selector.StrategySelector;
import stsc.general.testhelper.TestGeneticSimulatorSettings;

public class StrategyGeneticSearcherWithSettingsDistanceTest {

	private static final int maxGeneticStepsAmount = 100;
	private static final int populationSize = 25;

	@Test
	public void testStrategyGeneticSearcherWithDistance() throws InterruptedException, StrategySearcherException {
		final StrategyGeneticSearcher sgs = createSearcherWithDistance();
		final StrategySelector selector = sgs.waitAndGetSelector();
		final Metrics metrics = selector.getStrategies().get(0).getMetrics();
		final Double costValue = getCostFunction().calculate(metrics);
		Assert.assertEquals(-541.798602, costValue, Settings.doubleEpsilon);
	}

	private StrategyGeneticSearcher createSearcherWithDistance() throws InterruptedException {
		final CostWeightedSumFunction costFunction = getCostFunction();

		final SimulatorSettingsGeneticListImpl geneticList = TestGeneticSimulatorSettings.getGeneticList();

		final StrategySelector selector = new StatisticsWithSettingsDistanceSelector( //
				populationSize, //
				new SimulatorSettingsIntervalImpl(), //
				costFunction). //
						setEpsilon(50010.0);

		final StrategyGeneticSearcherBuilder builder = StrategyGeneticSearcher. //
				getBuilder(). //
				withPopulationCostFunction(costFunction). //
				withStrategySelector(selector). //
				withGeneticList(geneticList). //
				withPopulationSize(populationSize). //
				withMaxPopulationsAmount(maxGeneticStepsAmount). //
				withSimulatorFactory(new SimulatorFactoryImpl()). //
				withThreadAmount(8). //
				withBestPart(0.94). //
				withCrossoverPart(0.86);

		return builder.build();
	}

	private CostWeightedSumFunction getCostFunction() {
		final CostWeightedSumFunction costFunction = new CostWeightedSumFunction();
		costFunction.withParameter(MetricType.winProb, 1.2);
		costFunction.withParameter(MetricType.kelly, 0.6);
		costFunction.withParameter(MetricType.ddDurationAverage, 0.4);
		costFunction.withParameter(MetricType.freq, 0.3);
		costFunction.withParameter(MetricType.sharpeRatio, 0.2);
		costFunction.withParameter(MetricType.maxLoss, -0.3);
		costFunction.withParameter(MetricType.avLoss, -0.5);
		return costFunction;
	}

}
