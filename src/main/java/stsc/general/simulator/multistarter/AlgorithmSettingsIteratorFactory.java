package stsc.general.simulator.multistarter;

import stsc.general.simulator.multistarter.genetic.AlgorithmConfigurationSetGeneticGenerator;
import stsc.general.simulator.multistarter.grid.AlgorithmSettingsGridIterator;

/**
 * Factory for {@link AlgorithmSettingsGridIterator}, {@link AlgorithmConfigurationSetGeneticGenerator}.
 * 
 * @author isidarau
 *
 */
public final class AlgorithmSettingsIteratorFactory {

	private final AlgorithmConfigurationSetImpl parameters;

	public AlgorithmSettingsIteratorFactory() {
		this.parameters = new AlgorithmConfigurationSetImpl();
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

	public AlgorithmConfigurationSetGeneticGenerator getGeneticList() {
		return new AlgorithmConfigurationSetGeneticGenerator(parameters);
	}

}
