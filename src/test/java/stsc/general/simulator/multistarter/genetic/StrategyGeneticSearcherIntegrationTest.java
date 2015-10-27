package stsc.general.simulator.multistarter.genetic;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.general.simulator.SimulatorFactoryImpl;
import stsc.general.simulator.multistarter.StrategySearcher.IndicatorProgressListener;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.statistic.cost.comparator.MetricsSameComparator;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.selector.StatisticsByCostSelector;
import stsc.general.strategy.selector.StrategySelector;
import stsc.general.testhelper.TestGeneticSimulatorSettings;

public class StrategyGeneticSearcherIntegrationTest {

	@Test
	public void testStrategyGeneticSearcher() throws InterruptedException, StrategySearcherException {
		final StrategyGeneticSearcher sgs = createSearcher();
		final StrategySelector selector = sgs.waitAndGetSelector();
		Assert.assertEquals(112, selector.getStrategies().size());
		final Metrics metrics = selector.getStrategies().get(0).getMetrics();
		Assert.assertEquals(30.492558, metrics.getDoubleMetric(MetricType.avGain), Settings.doubleEpsilon);
		Assert.assertEquals(0.666666, metrics.getDoubleMetric(MetricType.winProb), Settings.doubleEpsilon);
	}

	@Test
	public void testStrategyGeneticSearchStop() throws InterruptedException, StrategySearcherException {
		final StrategyGeneticSearcher sgs = createSearcher();
		sgs.stopSearch();
		final StrategySelector selector = sgs.waitAndGetSelector();
		Assert.assertTrue(100 > selector.getStrategies().size());
	}

	@Test
	public void testStrategySearchProcessingListener() throws InterruptedException, StrategySearcherException {
		final StrategyGeneticSearcher sgs = createSearcher();
		final List<Double> updates = new ArrayList<>();
		sgs.addIndicatorProgress(new IndicatorProgressListener() {
			@Override
			public void processed(double percent) {
				updates.add(percent);
			}
		});
		final StrategySelector selector = sgs.waitAndGetSelector();

		Assert.assertEquals(112, selector.getStrategies().size());
		Assert.assertTrue(104 >= updates.size());
	}

	private StrategyGeneticSearcher createSearcher() throws InterruptedException {
		final CostWeightedSumFunction costFunction = new CostWeightedSumFunction();
		costFunction.withParameter(MetricType.winProb, 1.2);
		costFunction.withParameter(MetricType.kelly, 0.6);
		costFunction.withParameter(MetricType.ddDurationAvGain, 0.4);
		costFunction.withParameter(MetricType.freq, 0.3);
		costFunction.withParameter(MetricType.sharpeRatio, 0.2);
		costFunction.withParameter(MetricType.maxLoss, -0.3);
		costFunction.withParameter(MetricType.avLoss, -0.5);

		final StrategySelector selector = new StatisticsByCostSelector(112, costFunction, new MetricsSameComparator());

		final SimulatorSettingsGeneticListImpl geneticList = TestGeneticSimulatorSettings.getGeneticList();
		final int maxGeneticStepsAmount = 104;
		final int populationSize = 124;

		final StrategyGeneticSearcherBuilder builder = StrategyGeneticSearcher.getBuilder(). //
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
}
