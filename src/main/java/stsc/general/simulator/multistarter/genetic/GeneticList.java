package stsc.general.simulator.multistarter.genetic;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.SimulatorSettings;

/**
 * Creating {@link GeneticList} interface to have possibility to separate genetic algorithm search from Simulation / settings and etc.
 */
public interface GeneticList {

	public SimulatorSettings generateRandom() throws BadAlgorithmException;

	public SimulatorSettings mutate(SimulatorSettings settings);

	public SimulatorSettings merge(SimulatorSettings left, SimulatorSettings right);

}
