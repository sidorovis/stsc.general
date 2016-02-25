package stsc.general.simulator.multistarter.genetic;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.Execution;

/**
 * Creating {@link GeneticList} interface to have possibility to separate genetic algorithm search from Simulation / settings and etc.
 */
public interface GeneticList {

	public Execution generateRandom() throws BadAlgorithmException;

	public Execution mutate(Execution settings);

	public Execution merge(Execution left, Execution right);

}
