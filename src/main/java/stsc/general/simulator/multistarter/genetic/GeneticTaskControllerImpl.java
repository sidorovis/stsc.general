package stsc.general.simulator.multistarter.genetic;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorImpl;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.genetic.tasks.GeneticTaskController;
import stsc.general.strategy.TradingStrategy;

final class GeneticTaskControllerImpl implements GeneticTaskController {

	private final StrategyGeneticSearcher strategyGeneticSearcher;

	GeneticTaskControllerImpl(final StrategyGeneticSearcher strategyGeneticSearcher) {
		this.strategyGeneticSearcher = strategyGeneticSearcher;
	}

	@Override
	public SimulatorSettings getRandomSimulatorSettings() throws BadAlgorithmException {
		return strategyGeneticSearcher.getRandomSimulatorSettings();
	}

	@Override
	public void addTaskToExecutor(Callable<Boolean> callableTask) {
		strategyGeneticSearcher.addTaskToExecutor(callableTask);
	}

	@Override
	public void simulationCalculationFinished() {
		strategyGeneticSearcher.simulationCalculationFinished();
	}

	@Override
	public boolean addTradingStrategy(TradingStrategy strategy) {
		return strategyGeneticSearcher.addTradingStrategy(strategy);
	}

	@Override
	public Simulator getSimulator() {
		return new SimulatorImpl();
	}

	@Override
	public Logger getLogger() {
		return strategyGeneticSearcher.getLogger();
	}

}
