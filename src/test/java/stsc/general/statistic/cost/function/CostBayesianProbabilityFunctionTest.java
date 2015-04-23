package stsc.general.statistic.cost.function;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.general.statistic.Metrics;
import stsc.general.testhelper.TestMetricsHelper;

public class CostBayesianProbabilityFunctionTest {

	private Double calculateTestValue(Metrics metrics) {
		return Math.min(metrics.getIntegerMetric("period") * 6.0, metrics.getDoubleMetric("avGain") * 11.0);
	}

	@Test
	public void testBayesianProbabilityCostFunction() throws ParseException {
		final Metrics metrics = TestMetricsHelper.getMetrics();

		final CostBayesianProbabilityFunction bayesian = new CostBayesianProbabilityFunction();
		bayesian.addLayer().put("period", 6.0);
		bayesian.addLayer().put("avGain", 11.0);
		final Double bayesianResult = bayesian.calculate(metrics);
		Assert.assertEquals(calculateTestValue(metrics), bayesianResult, Settings.doubleEpsilon);
	}
}
