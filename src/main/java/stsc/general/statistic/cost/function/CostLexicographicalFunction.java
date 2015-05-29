package stsc.general.statistic.cost.function;

import java.util.ArrayList;
import java.util.List;

import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;

/**
 * CostLexicographicalFunction is a cost function that calculated by next rules:<br/>
 * there is a multiplikator and ordered set of statistics parameters M for
 * multiplikator; <br/>
 * P[x] - for N parameters; <br/>
 * <b>CF = (((P[1] * M) + P[2]) * M + P[3]) * M + ... P[N]</b>;
 */

public class CostLexicographicalFunction implements CostFunction {

	final List<MetricType> order = new ArrayList<>();
	private final double multiplikator;

	public CostLexicographicalFunction() {
		this(10.0);
	}

	public CostLexicographicalFunction(double multiplikator) {
		this.multiplikator = multiplikator;

	}

	public void addNextValue(MetricType value) {
		order.add(value);
	}

	@Override
	public Double calculate(Metrics metrics) {
		Double result = 0.0;
		for (MetricType metricName : order) {
			final Double dMetric = metrics.getMetric(metricName);
			if (dMetric != null) {
				result = result * multiplikator + dMetric;
			}
		}
		return result;
	}
}
