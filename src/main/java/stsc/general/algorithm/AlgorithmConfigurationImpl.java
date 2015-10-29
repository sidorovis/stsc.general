package stsc.general.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import stsc.common.algorithms.MutatingAlgorithmConfiguration;

/**
 * This is an implementation for collection of {@link AlgorithmSetting} for typed elements. Supported / expected types are: <br/>
 * 1. integer; <br/>
 * 2. double; <br/>
 * 3. string; <br/>
 * 4. sub-execution (string like but store order). <br/>
 * Also implements writeExternal / read (external).
 */
public final class AlgorithmConfigurationImpl implements MutatingAlgorithmConfiguration {

	private final HashMap<String, Integer> integers;
	private final HashMap<String, Double> doubles;
	private final HashMap<String, String> strings;
	private final ArrayList<String> subExecutions;

	public AlgorithmConfigurationImpl() {
		this.integers = new HashMap<>();
		this.doubles = new HashMap<>();
		this.strings = new HashMap<>();
		this.subExecutions = new ArrayList<>();
	}

	private AlgorithmConfigurationImpl(final AlgorithmConfigurationImpl cloneFrom) {
		this.integers = new HashMap<String, Integer>(cloneFrom.integers);
		this.doubles = new HashMap<String, Double>(cloneFrom.doubles);
		this.strings = new HashMap<String, String>(cloneFrom.strings);
		this.subExecutions = new ArrayList<String>(cloneFrom.subExecutions);
	}

	public AlgorithmConfigurationImpl createAlgorithmConfiguration() {
		return new AlgorithmConfigurationImpl();

	}

	@Override
	public AlgorithmConfigurationImpl setString(final String key, final String value) {
		strings.put(key, value);
		return this;
	}

	@Override
	public AlgorithmConfigurationImpl setInteger(final String key, final Integer value) {
		integers.put(key, value);
		return this;
	}

	@Override
	public AlgorithmConfigurationImpl setDouble(final String key, final Double value) {
		doubles.put(key, value);
		return this;
	}

	@Override
	public AlgorithmConfigurationImpl addSubExecutionName(final String subExecutionName) {
		subExecutions.add(subExecutionName);
		return this;
	}

	@Override
	public AlgorithmConfigurationImpl setSubExecutionName(int index, String value) {
		subExecutions.set(index, value);
		return this;
	}

	@Override
	public Map<String, Integer> getIntegers() {
		return integers;
	}

	@Override
	public Map<String, Double> getDoubles() {
		return doubles;
	}

	@Override
	public Map<String, String> getStrings() {
		return strings;
	}

	@Override
	public List<String> getSubExecutions() {
		return subExecutions;
	}

	@Override
	public Integer getIntegerSetting(final String key, final Integer defaultValue) {
		final Integer value = integers.get(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	@Override
	public Double getDoubleSetting(final String key, final Double defaultValue) {
		final Double value = doubles.get(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	@Override
	public String getStringSetting(final String key, final String defaultValue) {
		final String value = strings.get(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	@Override
	public AlgorithmConfigurationImpl clone() {
		return new AlgorithmConfigurationImpl(this);
	}

	@Override
	public void stringHashCode(StringBuilder sb) {
		for (Map.Entry<String, Integer> i : integers.entrySet()) {
			sb.append(i.getKey()).append(i.getValue());
		}
		for (Map.Entry<String, Double> i : doubles.entrySet()) {
			sb.append(i.getKey()).append(i.getValue());
		}
		for (Map.Entry<String, String> i : strings.entrySet()) {
			sb.append(i.getKey()).append(i.getValue());
		}
		for (String string : subExecutions) {
			sb.append(string);
		}
	}

	// common methods

	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < subExecutions.size(); ++i) {
			result += subExecutions.get(i);
			if (i + 1 < subExecutions.size()) {
				result += ", ";
			}
		}
		result = addParameters(result, integers, "I");
		result = addParameters(result, doubles, "D");
		result = addParameters(result, strings, "");

		return result;
	}

	private <T> String addParameters(String result, HashMap<String, T> data, String postfix) {
		if (!result.isEmpty() && !data.isEmpty())
			result += ", ";
		final Iterator<Entry<String, T>> i = data.entrySet().iterator();
		while (i.hasNext()) {
			final Entry<String, T> e = i.next();
			result += e.getKey() + " = " + String.valueOf(e.getValue()) + postfix;
			if (i.hasNext())
				result += ", ";
		}
		return result;
	}

}
