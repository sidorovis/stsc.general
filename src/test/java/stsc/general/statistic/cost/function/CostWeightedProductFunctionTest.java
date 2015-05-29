package stsc.general.statistic.cost.function;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.testhelper.TestMetricsHelper;

public class CostWeightedProductFunctionTest {

	@Test
	public void testCostWeightedProductFunction() throws ParseException {
		final Metrics metrics = TestMetricsHelper.getMetrics();

		final CostWeightedProductFunction function = new CostWeightedProductFunction();
		function.addParameter(MetricType.kelly, 0.8);
		final Double expectedResult = Math.signum(metrics.getDoubleMetric(MetricType.avGain))
				* Math.pow(Math.abs(metrics.getDoubleMetric(MetricType.avGain)), 1.0 / 1.8) + Math.signum(metrics.getDoubleMetric(MetricType.kelly))
				* Math.pow(Math.abs(metrics.getDoubleMetric(MetricType.kelly)), 0.8 / 1.8);
		final Double result = function.calculate(metrics);
		Assert.assertEquals(expectedResult, result, Settings.doubleEpsilon);
	}
}
