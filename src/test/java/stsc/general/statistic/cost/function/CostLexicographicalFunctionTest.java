package stsc.general.statistic.cost.function;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.testhelper.TestMetricsHelper;

public class CostLexicographicalFunctionTest {

	@Test
	public void testLexicographicalCostFunction() throws ParseException {
		final Metrics metrics = TestMetricsHelper.getMetrics();

		final CostLexicographicalFunction c10 = new CostLexicographicalFunction();
		c10.addNextValue(MetricType.period);
		c10.addNextValue(MetricType.avGain);
		final Double expectedResult = metrics.getIntegerMetric(MetricType.period) * 10 + metrics.getDoubleMetric(MetricType.avGain);
		final Double c10result = c10.calculate(metrics);
		Assert.assertEquals(expectedResult, c10result, Settings.doubleEpsilon);
	}

	@Test
	public void testLexicographicalCostFunction100() throws ParseException {
		final Metrics metrics = TestMetricsHelper.getMetrics();

		final CostLexicographicalFunction c100 = new CostLexicographicalFunction(100);
		c100.addNextValue(MetricType.period);
		c100.addNextValue(MetricType.avGain);
		c100.addNextValue(MetricType.avGain);
		final Double expectedResult = (metrics.getIntegerMetric(MetricType.period) * 100 + metrics.getDoubleMetric(MetricType.avGain)) * 100
				+ metrics.getDoubleMetric(MetricType.avGain);
		final Double c100result = c100.calculate(metrics);
		Assert.assertEquals(expectedResult, c100result, Settings.doubleEpsilon);
	}

	@Test
	public void testLexicographicalCostFunctionAnotherOrder() throws ParseException {
		final Metrics metrics = TestMetricsHelper.getMetrics();

		final CostLexicographicalFunction c10 = new CostLexicographicalFunction();
		c10.addNextValue(MetricType.avGain);
		c10.addNextValue(MetricType.period);
		final Double expectedResult = metrics.getDoubleMetric(MetricType.avGain) * 10 + metrics.getIntegerMetric(MetricType.period);
		final Double c10result = c10.calculate(metrics);
		Assert.assertEquals(expectedResult, c10result, Settings.doubleEpsilon);
	}
}
