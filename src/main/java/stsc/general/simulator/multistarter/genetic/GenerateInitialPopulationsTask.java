package stsc.general.simulator.multistarter.genetic;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Metrics;

/**
 * This class generate initial genetic population of {@link SimulatorSettings}
 * Also it creates and starts {@link SimulatorCalulatingTask} (Trading
 * Strategies {@link Metrics} calculation procedures).
 */
final class GenerateInitialPopulationsTask implements Runnable {

	private final StrategyGeneticSearcher strategyGeneticSearcher;
	private final int populationSize;

	GenerateInitialPopulationsTask(final StrategyGeneticSearcher strategyGeneticSearcher, final int populationSize) {
		this.strategyGeneticSearcher = strategyGeneticSearcher;
		this.populationSize = populationSize;
	}

	@Override
	public void run() {
		for (int i = 0; i < populationSize; ++i) {
			boolean taskWasNotAdded = true;
			try {
				final SimulatorSettings ss = strategyGeneticSearcher.getRandomSimulatorSettings();
				final SimulatorCalulatingTask task = new SimulatorCalulatingTask(this.strategyGeneticSearcher, ss);
				this.strategyGeneticSearcher.addTaskToExecutor(task);
				taskWasNotAdded = false;
			} catch (BadAlgorithmException e) {
				StrategyGeneticSearcher.logger.error("Problem while generating random simulator settings: " + e.getMessage());
			}
			if (taskWasNotAdded) {
				strategyGeneticSearcher.simulationCalculationFinished();
			}
		}
	}
}