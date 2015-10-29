package stsc.general.simulator.multistarter;

import stsc.general.simulator.multistarter.genetic.AlgorithmConfigurationSetGeneticGenerator;
import stsc.general.simulator.multistarter.grid.AlgorithmConfigurationSetGridGenerator;

/**
 * Factory for {@link AlgorithmConfigurationSetGridGenerator}, {@link AlgorithmConfigurationSetGeneticGenerator}.
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

	public AlgorithmConfigurationSetGridGenerator getGridIterator() {
		return new AlgorithmConfigurationSetGridGenerator(parameters);
	}

	public AlgorithmConfigurationSetGeneticGenerator getGeneticList() {
		return new AlgorithmConfigurationSetGeneticGenerator(parameters);
	}

}
