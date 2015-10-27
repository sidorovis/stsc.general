package stsc.general.simulator.multistarter.genetic;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorFactory;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.genetic.tasks.GeneticTaskController;
import stsc.general.strategy.TradingStrategy;

final class GeneticTaskControllerImpl implements GeneticTaskController {

	private final StrategyGeneticSearcher strategyGeneticSearcher;
	private final GeneticList simulatorSettingsGeneticList;
	private final SimulatorFactory simulatorFactory;

	GeneticTaskControllerImpl(final StrategyGeneticSearcher strategyGeneticSearcher, GeneticList simulatorSettingsGeneticList, SimulatorFactory simulatorFactory) {
		this.strategyGeneticSearcher = strategyGeneticSearcher;
		this.simulatorSettingsGeneticList = simulatorSettingsGeneticList;
		this.simulatorFactory = simulatorFactory;
	}

	@Override
	public SimulatorSettings getRandomSimulatorSettings() throws BadAlgorithmException {
		return simulatorSettingsGeneticList.generateRandom();
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
	public Simulator createSimulator() {
		return simulatorFactory.createSimulator();
	}

	@Override
	public Logger getLogger() {
		return strategyGeneticSearcher.getLogger();
	}

}
