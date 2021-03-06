package stsc.general.simulator.multistarter;

import java.util.Iterator;

public final class AlgorithmConfigurationSetImpl implements AlgorithmConfigurationSet {

	private final ParameterList<Integer, MpNumberIterator<Integer>> integers;
	private final ParameterList<Double, MpNumberIterator<Double>> doubles;
	private final ParameterList<String, MpTextIterator<String>> strings;
	private final ParameterList<String, MpTextIterator<String>> subExecutions;

	private final ParameterList<?, ?>[] parameters;

	private AlgorithmConfigurationSetImpl(final AlgorithmConfigurationSetImpl copy) {
		this.integers = copy.integers.clone();
		this.doubles = copy.doubles.clone();
		this.strings = copy.strings.clone();
		this.subExecutions = copy.subExecutions.clone();
		this.parameters = new ParameterList<?, ?>[] { integers, doubles, strings, subExecutions };
	}

	public AlgorithmConfigurationSetImpl() {
		this.integers = new ParameterList<Integer, MpNumberIterator<Integer>>();
		this.doubles = new ParameterList<Double, MpNumberIterator<Double>>();
		this.strings = new ParameterList<String, MpTextIterator<String>>();
		this.subExecutions = new ParameterList<String, MpTextIterator<String>>();
		this.parameters = new ParameterList<?, ?>[] { integers, doubles, strings, subExecutions };
	}

	public void reset() {
		for (ParameterList<?, ?> list : parameters) {
			list.reset();
		}
	}

	@Override
	public long size() {
		long result = 1;
		for (ParameterList<?, ?> pl : parameters) {
			result *= pl.size();
		}
		return result;
	}

	@Override
	public int parametersSize() {
		int size = 0;
		for (ParameterList<?, ?> i : parameters) {
			size += i.getParams().size();
		}
		return size;
	}

	@Override
	public String toString() {
		String result = "";
		for (ParameterList<?, ?> p : parameters) {
			result += "\n" + p.toString();
		}
		return result;
	}

	public ParameterList<Integer, MpNumberIterator<Integer>> getIntegers() {
		return integers;
	}

	public ParameterList<Double, MpNumberIterator<Double>> getDoubles() {
		return doubles;
	}

	public ParameterList<String, MpTextIterator<String>> getStrings() {
		return strings;
	}

	public ParameterList<String, MpTextIterator<String>> getSubExecutions() {
		return subExecutions;
	}

	public Iterator<MpTextIterator<String>> getSubExecutionIterator() {
		return getSubExecutions().getParams().iterator();
	}

	@Override
	public AlgorithmConfigurationSetImpl clone() {
		return new AlgorithmConfigurationSetImpl(this);
	}

}
