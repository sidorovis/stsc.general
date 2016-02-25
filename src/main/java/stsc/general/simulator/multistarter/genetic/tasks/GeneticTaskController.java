package stsc.general.simulator.multistarter.genetic.tasks;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.Execution;
import stsc.general.strategy.TradingStrategy;

/**
 * Callback interface for {@link SimulatorCalculatingTask}, {@link GenerateRandomPopulationsTask}
 */
public interface GeneticTaskController {

	// generate random

	public Execution getRandomSimulatorSettings() throws BadAlgorithmException;

	public void addTaskToExecutor(Callable<Boolean> callableTask);

	/**
	 * This method should be called in case if task was not added for Simulation or simulation failed.
	 */
	public void simulationCalculationFinished();

	// simulation

	public boolean addTradingStrategy(final TradingStrategy strategy);

	public Simulator createSimulator();

	public Logger getLogger();

}
