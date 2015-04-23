package stsc.general.statistic.cost.function;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.general.statistic.Metrics;
import stsc.general.testhelper.TestMetricsHelper;

public class CostLexicographicalFunctionTest {

	@Test
	public void testLexicographicalCostFunction() throws ParseException {
		final Metrics metrics = TestMetricsHelper.getMetrics();

		final CostLexicographicalFunction c10 = new CostLexicographicalFunction();
		c10.addNextValue("period");
		c10.addNextValue("avGain");
		final Double expectedResult = metrics.getIntegerMetric("period") * 10 + metrics.getDoubleMetric("avGain");
		final Double c10result = c10.calculate(metrics);
		Assert.assertEquals(expectedResult, c10result, Settings.doubleEpsilon);
	}

	@Test
	public void testLexicographicalCostFunction100() throws ParseException {
		final Metrics metrics = TestMetricsHelper.getMetrics();

		final CostLexicographicalFunction c100 = new CostLexicographicalFunction(100);
		c100.addNextValue("period");
		c100.addNextValue("avGain");
		c100.addNextValue("avGain");
		final Double expectedResult = (metrics.getIntegerMetric("period") * 100 + metrics.getDoubleMetric("avGain")) * 100 + metrics.getDoubleMetric("avGain");
		final Double c100result = c100.calculate(metrics);
		Assert.assertEquals(expectedResult, c100result, Settings.doubleEpsilon);
	}

	@Test
	public void testLexicographicalCostFunctionAnotherOrder() throws ParseException {
		final Metrics metrics = TestMetricsHelper.getMetrics();

		final CostLexicographicalFunction c10 = new CostLexicographicalFunction();
		c10.addNextValue("avGain");
		c10.addNextValue("period");
		final Double expectedResult = metrics.getDoubleMetric("avGain") * 10 + metrics.getIntegerMetric("period");
		final Double c10result = c10.calculate(metrics);
		Assert.assertEquals(expectedResult, c10result, Settings.doubleEpsilon);
	}
}
