package stsc.general.statistic.cost.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;

/**
 * Calculate Cost Function for {@link Metrics} using Bayesian Probability
 * methodology. <br/>
 * Parameters are divided by layers with different coefficients for example: <br/>
 * we could have layer with next coefficients: <br/>
 * (getPeriod -> 2.0, getAvGain -> 4.0) <br/>
 * which provide us with information that cost function for such layer will be:
 * <b>max(Period * 2.0 and AvGain * 4.0)</b>; <br/>
 * Result of function is minimum between all layers. <br/>
 * If there is no layers result is Double.MAX_VALUE layer with no fields lead to
 * get -Double.MAX_VALUE as layer value.
 */
public class CostBayesianProbabilityFunction implements CostFunction {

	private final List<Map<MetricType, Double>> parameters = new ArrayList<Map<MetricType, Double>>();

	public CostBayesianProbabilityFunction() {
		super();
	}

	public Map<MetricType, Double> addLayer() {
		final Map<MetricType, Double> result = new HashMap<MetricType, Double>();
		parameters.add(result);
		return result;
	}

	@Override
	public Double calculate(Metrics metrics) {
		Double min = Double.MAX_VALUE;
		for (Map<MetricType, Double> layer : parameters) {
			Double max = -Double.MAX_VALUE;
			for (Entry<MetricType, Double> e : layer.entrySet()) {
				final Double sValue = metrics.getMetric(e.getKey());
				final Double pValue = sValue * e.getValue();
				if (max < pValue)
					max = pValue;
			}
			if (max < min)
				min = max;
		}
		return min;
	}

}
