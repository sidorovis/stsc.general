package stsc.general.simulator.multistarter.genetic.tasks;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Metrics;

/**
 * This class generate initial genetic population of {@link SimulatorSettings}. Also it creates and starts {@link SimulatorCalculatingTask} (Trading Strategies
 * {@link Metrics} calculation procedures).
 */
public final class GenerateRandomPopulationsTask implements Runnable {

	private final GeneticTaskController controller;
	private final int amountOfRandomElementsOfPopulation;

	public GenerateRandomPopulationsTask(final GeneticTaskController controller, final int amountOfRandomElementsOfPopulation) {
		this.controller = controller;
		this.amountOfRandomElementsOfPopulation = amountOfRandomElementsOfPopulation;
	}

	@Override
	public void run() {
		for (int i = 0; i < amountOfRandomElementsOfPopulation; ++i) {
			boolean taskWasAdded = false;
			try {
				final SimulatorSettings simulatorSettings = controller.getRandomSimulatorSettings();
				final SimulatorCalculatingTask task = new SimulatorCalculatingTask(controller, simulatorSettings);
				controller.addTaskToExecutor(task);
				taskWasAdded = true;
			} catch (BadAlgorithmException e) {
				controller.getLogger().error("Problem while generating random simulator settings.", e);
			}
			if (!taskWasAdded) {
				controller.simulationCalculationFinished();
			}
		}
	}
}