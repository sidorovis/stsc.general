package stsc.general.simulator.multistarter.genetic;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.general.simulator.multistarter.StrategySearcher.IndicatorProgressListener;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StrategySelector;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.testhelper.TestGeneticSimulatorSettings;

public class StrategyGeneticSearcherTest {

	@Test
	public void testStrategyGeneticSearcher() throws InterruptedException, StrategySearcherException {
		final StrategyGeneticSearcher sgs = createSearcher();
		final StrategySelector selector = sgs.getSelector();
		Assert.assertEquals(112, selector.getStrategies().size());
		Assert.assertEquals(100.0, selector.getStrategies().get(0).getStatistics().getAvGain(), Settings.doubleEpsilon);
		Assert.assertEquals(0.666666, selector.getStrategies().get(0).getStatistics().getWinProb(), Settings.doubleEpsilon);
	}

	@Test
	public void testStrategyGeneticSearchStop() throws InterruptedException, StrategySearcherException {
		final StrategyGeneticSearcher sgs = createSearcher();
		sgs.stopSearch();
		final StrategySelector selector = sgs.getSelector();
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
		final StrategySelector selector = sgs.getSelector();

		Assert.assertEquals(112, selector.getStrategies().size());
		Assert.assertTrue(104 >= updates.size());
	}

	private StrategyGeneticSearcher createSearcher() throws InterruptedException {
		final CostWeightedSumFunction costFunction = new CostWeightedSumFunction();
		costFunction.addParameter("getWinProb", 1.2);
		costFunction.addParameter("getKelly", 0.6);
		costFunction.addParameter("getDdDurationAvGain", 0.4);
		costFunction.addParameter("getFreq", 0.3);
		costFunction.addParameter("getSharpeRatio", 0.2);
		costFunction.addParameter("getMaxLoss", -0.3);
		costFunction.addParameter("getAvLoss", -0.5);

		final StrategySelector selector = new StatisticsByCostSelector(112, costFunction);

		final SimulatorSettingsGeneticList geneticList = TestGeneticSimulatorSettings.getBigGeneticList();
		final int maxGeneticStepsAmount = 104;
		final int populationSize = 124;
		return new StrategyGeneticSearcher(geneticList, selector, 4, costFunction, maxGeneticStepsAmount, populationSize, 0.94, 0.86);
	}
}
