package stsc.general.simulator.multistarter.genetic;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.general.simulator.multistarter.StrategySearcher.IndicatorProgressListener;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.selector.StatisticsByCostSelector;
import stsc.general.strategy.selector.StrategySelector;
import stsc.general.testhelper.TestGeneticSimulatorSettings;

public class StrategyGeneticSearcherTest {

	@Test
	public void testStrategyGeneticSearcher() throws InterruptedException, StrategySearcherException {
		final StrategyGeneticSearcher sgs = createSearcher();
		final StrategySelector selector = sgs.waitAndGetSelector();
		Assert.assertEquals(112, selector.getStrategies().size());
		Assert.assertEquals(100.0, selector.getStrategies().get(0).getMetrics().getDoubleMetric("avGain"), Settings.doubleEpsilon);
		Assert.assertEquals(0.666666, selector.getStrategies().get(0).getMetrics().getDoubleMetric("winProb"), Settings.doubleEpsilon);
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
		costFunction.withParameter("winProb", 1.2);
		costFunction.withParameter("kelly", 0.6);
		costFunction.withParameter("ddDurationAvGain", 0.4);
		costFunction.withParameter("freq", 0.3);
		costFunction.withParameter("sharpeRatio", 0.2);
		costFunction.withParameter("maxLoss", -0.3);
		costFunction.withParameter("avLoss", -0.5);

		final StrategySelector selector = new StatisticsByCostSelector(112, costFunction);

		final SimulatorSettingsGeneticList geneticList = TestGeneticSimulatorSettings.getGeneticList();
		final int maxGeneticStepsAmount = 104;
		final int populationSize = 124;

		final StrategyGeneticSearcherBuilder builder = StrategyGeneticSearcher.getBuilder().withPopulationCostFunction(costFunction)
				.withStrategySelector(selector).withSimulatorSettings(geneticList).withPopulationSize(populationSize)
				.withMaxPopulationsAmount(maxGeneticStepsAmount).withThreadAmount(8).withBestPart(0.94).withCrossoverPart(0.86);

		return new StrategyGeneticSearcher(builder);
	}
}
