package stsc.general.simulator.multistarter.genetic;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.SimulatorConfiguration;

/**
 * Creating {@link GeneticList} interface to have possibility to separate genetic algorithm search from Simulation / settings and etc.
 */
public interface GeneticList {

	public SimulatorConfiguration generateRandom() throws BadAlgorithmException;

	public SimulatorConfiguration mutate(SimulatorConfiguration settings);

	public SimulatorConfiguration merge(SimulatorConfiguration left, SimulatorConfiguration right);

}
