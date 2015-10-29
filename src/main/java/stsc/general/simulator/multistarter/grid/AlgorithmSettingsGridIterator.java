package stsc.general.simulator.multistarter.grid;

import stsc.common.algorithms.MutableAlgorithmConfiguration;
import stsc.general.algorithm.AlgorithmConfigurationImpl;
import stsc.general.simulator.multistarter.AlgorithmConfigurationSet;
import stsc.general.simulator.multistarter.MpIterator;
import stsc.general.simulator.multistarter.ParameterList;
import stsc.general.simulator.multistarter.ResetableIterable;
import stsc.general.simulator.multistarter.ResetableIterator;

public final class AlgorithmSettingsGridIterator implements ResetableIterable<MutableAlgorithmConfiguration> {

	public class Element implements ResetableIterator<MutableAlgorithmConfiguration>, Cloneable {

		private final AlgorithmConfigurationSet parameters;
		private boolean finished;

		public Element(AlgorithmConfigurationSet parameterList, boolean finished) {
			this.parameters = parameterList;
			this.finished = finished;
		}

		public Element clone() {
			final AlgorithmConfigurationSet copyParameters = parameters.clone();
			return new Element(copyParameters, this.finished);
		}

		@Override
		public boolean hasNext() {
			if (finished)
				return false;
			if (parameters.getIntegers().hasCurrent())
				return true;
			if (parameters.getDoubles().hasCurrent())
				return true;
			if (parameters.getStrings().hasCurrent())
				return true;
			if (parameters.getSubExecutions().hasCurrent())
				return true;
			return false;
		}

		@Override
		public AlgorithmConfigurationImpl next() {
			final AlgorithmConfigurationImpl result = generateSettings();
			generateNext();
			return result;
		}

		@Override
		public void remove() {
		}

		public MutableAlgorithmConfiguration current() {
			return generateSettings();
		}

		protected void generateNext() {
			if (getNext(parameters.getIntegers()))
				return;
			if (getNext(parameters.getDoubles()))
				return;
			if (getNext(parameters.getStrings()))
				return;
			if (getNext(parameters.getSubExecutions()))
				return;
			finished = true;
			return;
		}

		private <T> boolean getNext(ParameterList<T, ?> list) {
			while (true) {
				if (list.empty()) {
					return false;
				}
				final MpIterator<T, ?> iterator = list.getCurrentParam();
				iterator.increment();
				if (iterator.hasNext()) {
					list.reset();
					return true;
				}
				iterator.reset();
				if (list.hasNext()) {
					list.increment();
				} else {
					list.reset();
					return false;
				}
			}
		}

		protected AlgorithmConfigurationImpl generateSettings() {
			final AlgorithmConfigurationImpl algoSettings = new AlgorithmConfigurationImpl();

			final ParameterList<Integer, ?> integers = parameters.getIntegers();
			for (MpIterator<Integer, ?> p : integers.getParams()) {
				final String name = p.currentParameter().getName();
				final Integer value = p.currentParameter().getValue();
				algoSettings.setInteger(name, value);
			}

			final ParameterList<Double, ?> doubles = parameters.getDoubles();
			for (MpIterator<Double, ?> p : doubles.getParams()) {
				final String name = p.currentParameter().getName();
				final Double value = p.currentParameter().getValue();
				algoSettings.setDouble(name, value);
			}

			final ParameterList<String, ?> strings = parameters.getStrings();
			for (MpIterator<String, ?> p : strings.getParams()) {
				final String name = p.currentParameter().getName();
				final String value = p.currentParameter().getValue();
				algoSettings.setString(name, value);
			}

			final ParameterList<String, ?> list = parameters.getSubExecutions();
			for (MpIterator<String, ?> p : list.getParams()) {
				final String subExecutionName = p.currentParameter().getValue();
				algoSettings.addSubExecutionName(subExecutionName);
			}
			return algoSettings;
		}

		public void reset() {
			finished = false;
			parameters.reset();
		}

		public long size() {
			return parameters.size();
		}

		public AlgorithmConfigurationSet getParameters() {
			return parameters;
		}
	}

	private final AlgorithmConfigurationSet parameters;

	private boolean finished;

	public AlgorithmSettingsGridIterator(final AlgorithmConfigurationSet parameters) {
		this.parameters = parameters.clone();
		this.finished = false;
	}

	@Override
	public Element iterator() {
		return new Element(this.parameters, this.finished);
	}

	public Element getResetIterator() {
		return new Element(this.parameters, this.finished);
	}

	public boolean getFinished() {
		return this.finished;
	}

	@Override
	public String toString() {
		return parameters.toString();
	}

	public long size() {
		return parameters.size();
	}

}
