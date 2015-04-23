package stsc.general.statistic.cost.function;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.general.statistic.Metrics;
import stsc.general.testhelper.TestMetricsHelper;

public class CostWeightedProductFunctionTest {

	@Test
	public void testCostWeightedProductFunction() throws ParseException {
		final Metrics metrics = TestMetricsHelper.getMetrics();

		final CostWeightedProductFunction function = new CostWeightedProductFunction();
		function.addParameter("kelly", 0.8);
		final Double expectedResult = Math.signum(metrics.getDoubleMetric("avGain")) * Math.pow(Math.abs(metrics.getDoubleMetric("avGain")), 1.0 / 1.8)
				+ Math.signum(metrics.getDoubleMetric("kelly")) * Math.pow(Math.abs(metrics.getDoubleMetric("kelly")), 0.8 / 1.8);
		final Double result = function.calculate(metrics);
		Assert.assertEquals(expectedResult, result, Settings.doubleEpsilon);
	}
}
