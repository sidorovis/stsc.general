package stsc.general.simulator.multistarter;

import stsc.general.simulator.multistarter.genetic.AlgorithmSettingsGeneticList;
import stsc.general.simulator.multistarter.grid.AlgorithmSettingsGridIterator;

public class AlgorithmSettingsIteratorFactory {

	private final MultiAlgorithmParameters parameters;

	public AlgorithmSettingsIteratorFactory() {
		this.parameters = new MultiAlgorithmParameters();
	}

	public AlgorithmSettingsIteratorFactory add(final MpInteger parameter) {
		parameters.getIntegers().add(parameter);
		return this;
	}

	public AlgorithmSettingsIteratorFactory add(final MpDouble parameter) {
		parameters.getDoubles().add(parameter);
		return this;
	}

	public AlgorithmSettingsIteratorFactory add(final MpString parameter) {
		parameters.getStrings().add(parameter);
		return this;
	}

	public AlgorithmSettingsIteratorFactory add(final MpSubExecution parameter) {
		parameters.getSubExecutions().add(parameter);
		return this;
	}

	public AlgorithmSettingsGridIterator getGridIterator() {
		return new AlgorithmSettingsGridIterator(parameters);
	}

	public AlgorithmSettingsGeneticList getGeneticList() {
		return new AlgorithmSettingsGeneticList(parameters);
	}

}
