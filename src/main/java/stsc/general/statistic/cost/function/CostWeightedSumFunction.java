package stsc.general.statistic.cost.function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import stsc.general.statistic.PublicMethod;
import stsc.general.statistic.Statistics;

// @formatter:off
/**
 * {@link CostWeightedSumFunction} is a cost function for {@link Statistics}.
 * Require set of parameters: P[1]...P[N];
 * Result = V[1] * P[1] + V[2] * P[2] ... V[N] * P[N].
 */
//@formatter:on
public class CostWeightedSumFunction implements CostFunction {

	private final Map<String, Double> parameters = new HashMap<>();
	private final Object[] emptyValues = {};

	public CostWeightedSumFunction() {
		parameters.put("getAvGain", 1.0);
	}

	public void addParameter(String name, Double value) {
		parameters.put(name, value);
	}

	@Override
	public Double calculate(final Statistics statistics) {
		Double result = 0.0;
		final Method[] methods = statistics.getClass().getMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(PublicMethod.class)) {
				if (parameters.containsKey(method.getName())) {
					final Double value = parameters.get(method.getName());
					try {
						result += value * (Double) method.invoke(statistics, emptyValues);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					}
				}
			}
		}
		return result;
	}
}
