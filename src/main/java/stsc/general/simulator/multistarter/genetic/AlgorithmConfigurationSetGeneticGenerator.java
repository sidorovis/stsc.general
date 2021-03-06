package stsc.general.simulator.multistarter.genetic;

import java.util.Iterator;
import java.util.Random;

import stsc.common.algorithms.AlgorithmConfiguration;
import stsc.common.algorithms.MutableAlgorithmConfiguration;
import stsc.general.algorithm.AlgorithmConfigurationImpl;
import stsc.general.simulator.multistarter.AlgorithmConfigurationSet;
import stsc.general.simulator.multistarter.MpIterator;
import stsc.general.simulator.multistarter.MpTextIterator;
import stsc.general.simulator.multistarter.ParameterList;

/**
 * This class implement list of algorithm settings for genetic search. So we can:<br/>
 * 1. Generate new population of algorithm setting {@link AlgorithmCGeneticList#generateRandom()};<br/>
 * 2. Mutate {@link AlgorithmConfiguration};<br/>
 * 3. Merge {@link AlgorithmConfiguration} (left with right);<br/>
 * We need {@link AlgorithmCGeneticList} for mutate / merge operations because we require algorithm setting domens.
 */
public final class AlgorithmConfigurationSetGeneticGenerator {

	private static final int MUTATING_FINISHED = -1;

	private final AlgorithmConfigurationSet algorithmConfigurationSet;

	private final Random random = new Random();

	public AlgorithmConfigurationSetGeneticGenerator(final AlgorithmConfigurationSet parameters) {
		this.algorithmConfigurationSet = parameters.clone();
	}

	@Override
	public String toString() {
		return algorithmConfigurationSet.toString();
	}

	// Random generate methods

	MutableAlgorithmConfiguration generateRandom() {
		final AlgorithmConfigurationImpl algoSettings = new AlgorithmConfigurationImpl();
		generateRandomIntegers(algoSettings);
		generateRandomDoubles(algoSettings);
		generateRandomStrings(algoSettings);
		generateRandomSubExecutions(algoSettings);
		return algoSettings;
	}

	private void generateRandomIntegers(final AlgorithmConfigurationImpl algoSettings) {
		final ParameterList<Integer, ?> list = algorithmConfigurationSet.getIntegers();
		for (MpIterator<Integer, ?> p : list.getParams()) {
			algoSettings.setInteger(p.getName(), p.getRangom());
		}
	}

	private void generateRandomDoubles(final AlgorithmConfigurationImpl algoSettings) {
		final ParameterList<Double, ?> list = algorithmConfigurationSet.getDoubles();
		for (MpIterator<Double, ?> p : list.getParams()) {
			algoSettings.setDouble(p.getName(), p.getRangom());
		}
	}

	private void generateRandomStrings(final AlgorithmConfigurationImpl algoSettings) {
		final ParameterList<String, ?> list = algorithmConfigurationSet.getStrings();
		for (MpIterator<String, ?> p : list.getParams()) {
			algoSettings.setString(p.getName(), p.getRangom());
		}
	}

	private void generateRandomSubExecutions(final AlgorithmConfigurationImpl algoSettings) {
		final ParameterList<String, ?> list = algorithmConfigurationSet.getSubExecutions();
		for (MpIterator<String, ?> p : list.getParams()) {
			algoSettings.addSubExecutionName(p.getRangom());
		}
	}

	// Mutate methods

	void mutate(final MutableAlgorithmConfiguration settings) {
		final int parametersAmount = algorithmConfigurationSet.parametersSize();
		if (parametersAmount > 0) {
			int indexOfMutatingParameter = random.nextInt(parametersAmount);
			indexOfMutatingParameter = mutateIntegers(settings, indexOfMutatingParameter);
			if (elementMutated(indexOfMutatingParameter))
				return;
			indexOfMutatingParameter = mutateDoubles(settings, indexOfMutatingParameter);
			if (elementMutated(indexOfMutatingParameter))
				return;
			indexOfMutatingParameter = mutateStrings(settings, indexOfMutatingParameter);
			if (elementMutated(indexOfMutatingParameter))
				return;
			mutateSubExecutions(settings, indexOfMutatingParameter);
		}
	}

	private boolean elementMutated(final int indexOfMutatingParameter) {
		return indexOfMutatingParameter < 0;
	}

	private int mutateIntegers(final MutableAlgorithmConfiguration settings, final int index) {
		if (index < 0)
			return index;
		final ParameterList<Integer, ?> list = algorithmConfigurationSet.getIntegers();
		final int size = list.getParams().size();
		if (size != 0 && size > index) {
			final MpIterator<Integer, ?> mutatingParameter = list.getParams().get(index);
			settings.setInteger(mutatingParameter.getName(), mutate(index, mutatingParameter));
			return MUTATING_FINISHED;
		}
		return index - size;
	}

	private int mutateDoubles(final MutableAlgorithmConfiguration settings, final int index) {
		if (index < 0)
			return index;
		final ParameterList<Double, ?> list = algorithmConfigurationSet.getDoubles();
		final int size = list.getParams().size();
		if (size != 0 && size > index) {
			final MpIterator<Double, ?> mutatingParameter = list.getParams().get(index);
			settings.setDouble(mutatingParameter.getName(), mutate(index, mutatingParameter));
			return MUTATING_FINISHED;
		}
		return index - size;
	}

	private int mutateStrings(final MutableAlgorithmConfiguration settings, final int index) {
		if (index < 0)
			return index;
		final ParameterList<String, ?> list = algorithmConfigurationSet.getStrings();
		final int size = list.getParams().size();
		if (size != 0 && size > index) {
			final MpIterator<String, ?> mutatingParameter = list.getParams().get(index);
			settings.setString(mutatingParameter.getName(), mutate(index, mutatingParameter));
			return MUTATING_FINISHED;
		}
		return index - size;
	}

	private void mutateSubExecutions(final MutableAlgorithmConfiguration settings, final int index) {
		if (index < 0)
			return;
		final ParameterList<String, ?> list = algorithmConfigurationSet.getSubExecutions();
		final int size = list.getParams().size();
		if (size != 0 && size > index) {
			final MpIterator<String, ?> mutatingParameter = list.getParams().get(index);
			settings.setSubExecutionName(index, mutate(index, mutatingParameter));
		}
	}

	private <T> T mutate(int index, MpIterator<T, ?> iterator) {
		final int sizeOfValues = (int) iterator.size();
		final int mutatedIndex = random.nextInt(sizeOfValues);
		final T mutatedValue = iterator.parameter(mutatedIndex);
		return mutatedValue;
	}

	// Merge methods

	MutableAlgorithmConfiguration merge(MutableAlgorithmConfiguration leftSe, MutableAlgorithmConfiguration rightSe) {
		return mergeParameters(leftSe, rightSe);
	}

	private MutableAlgorithmConfiguration mergeParameters(MutableAlgorithmConfiguration leftSe, MutableAlgorithmConfiguration rightSe) {
		final AlgorithmConfigurationImpl result = new AlgorithmConfigurationImpl();
		mergeIntegers(result, leftSe, rightSe);
		mergeDouble(result, leftSe, rightSe);
		mergeStrings(result, leftSe, rightSe);
		mergeSubExecutions(result, leftSe, rightSe);
		return result;
	}

	private void mergeIntegers(final AlgorithmConfigurationImpl result, final MutableAlgorithmConfiguration leftSe,
			final MutableAlgorithmConfiguration rightSe) {
		for (MpIterator<Integer, ?> p : algorithmConfigurationSet.getIntegers().getParams()) {
			final String settingName = p.getName();
			final Integer resultValue = p.merge(leftSe.getIntegerSetting(settingName, Integer.MAX_VALUE),
					rightSe.getIntegerSetting(settingName, Integer.MAX_VALUE));
			result.setInteger(settingName, resultValue);
		}
	}

	private void mergeDouble(AlgorithmConfigurationImpl result, MutableAlgorithmConfiguration leftSe, MutableAlgorithmConfiguration rightSe) {
		for (MpIterator<Double, ?> p : algorithmConfigurationSet.getDoubles().getParams()) {
			final String settingName = p.getName();
			final Double resultValue = mutate(p, leftSe.getDoubleSetting(settingName, Double.MAX_VALUE),
					rightSe.getDoubleSetting(settingName, Double.MAX_VALUE));
			result.setDouble(settingName, resultValue);
		}
	}

	private void mergeStrings(AlgorithmConfigurationImpl result, MutableAlgorithmConfiguration leftSe, MutableAlgorithmConfiguration rightSe) {
		for (MpIterator<String, ?> p : algorithmConfigurationSet.getStrings().getParams()) {
			final String settingName = p.getName();
			final String mutatedValue = p.merge(leftSe.getStringSetting(settingName, ""), rightSe.getStringSetting(settingName, ""));
			result.setString(settingName, mutatedValue);
		}
	}

	private void mergeSubExecutions(AlgorithmConfigurationImpl result, AlgorithmConfiguration leftSe, AlgorithmConfiguration rightSe) {
		final Iterator<MpTextIterator<String>> subExecutionIterator = algorithmConfigurationSet.getSubExecutionIterator();
		final Iterator<String> lv = leftSe.getSubExecutions().iterator();
		final Iterator<String> rv = rightSe.getSubExecutions().iterator();
		while (subExecutionIterator.hasNext() && lv.hasNext() && rv.hasNext()) {
			final MpIterator<String, ?> p = subExecutionIterator.next();
			final String mutatedValue = p.merge(lv.next(), rv.next());
			result.addSubExecutionName(mutatedValue);
		}
	}

	private <Type> Type mutate(MpIterator<Type, ?> p, Type leftValue, Type rightValue) {
		final Type mutatedValue = p.merge(leftValue, rightValue);
		return mutatedValue;
	}

	public AlgorithmConfigurationSet getParameters() {
		return algorithmConfigurationSet;
	}

	public long size() {
		return algorithmConfigurationSet.size();
	}

}
