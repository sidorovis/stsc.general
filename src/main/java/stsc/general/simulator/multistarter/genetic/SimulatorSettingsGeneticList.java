package stsc.general.simulator.multistarter.genetic;

import java.util.List;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.SimulatorSettings;

/**
 * Creating {@link SimulatorSettingsGeneticList} interface to have possibility to separate genetic algorithm search from Simulation / settings and etc.
 */
public interface SimulatorSettingsGeneticList {

	public long getId();

	public FromToPeriod getPeriod();

	public long size();

	//

	public List<GeneticExecutionInitializer> getStockInitializers();

	public List<GeneticExecutionInitializer> getEodInitializers();

	//

	public SimulatorSettings generateRandom() throws BadAlgorithmException;

	public SimulatorSettings mutate(SimulatorSettings settings);

	public SimulatorSettings merge(SimulatorSettings left, SimulatorSettings right);

}
