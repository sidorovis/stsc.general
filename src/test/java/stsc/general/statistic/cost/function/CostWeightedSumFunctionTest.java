package stsc.general.statistic.cost.function;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.testhelper.TestMetricsHelper;

public class CostWeightedSumFunctionTest {

	@Test
	public void testCostWeightedSumFunction() throws ParseException {
		final Metrics metrics = TestMetricsHelper.getMetrics();

		final CostWeightedSumFunction function = new CostWeightedSumFunction();
		final Double expectedResult = 1.0 * metrics.getDoubleMetric(MetricType.avGain);
		final Double result = function.calculate(metrics);
		Assert.assertEquals(expectedResult, result, Settings.doubleEpsilon);

		function.withParameter(MetricType.period, 0.5);
		final Double expectedResult2 = expectedResult + metrics.getIntegerMetric(MetricType.period) * 0.5;
		final Double result2 = function.calculate(metrics);
		Assert.assertEquals(expectedResult2, result2, Settings.doubleEpsilon);

		function.withParameter(MetricType.kelly, 0.3);
		final Double expectedResult3 = expectedResult2 + metrics.getDoubleMetric(MetricType.kelly) * 0.3;
		final Double result3 = function.calculate(metrics);
		Assert.assertEquals(expectedResult3, result3, Settings.doubleEpsilon);

		function.withParameter(MetricType.maxLoss, 0.7);
		final Double expectedResult4 = expectedResult3 + metrics.getDoubleMetric(MetricType.maxLoss) * 0.7;
		final Double result4 = function.calculate(metrics);
		Assert.assertEquals(expectedResult4, result4, Settings.doubleEpsilon);
	}
}
