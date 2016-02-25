package stsc.general.simulator.multistarter.genetic.tasks;

import java.util.Optional;
import java.util.concurrent.Callable;

import stsc.general.simulator.Simulator;
import stsc.general.simulator.Execution;
import stsc.general.statistic.Metrics;
import stsc.general.strategy.TradingStrategy;

public final class SimulatorCalculatingTask implements Callable<Boolean> {

	private final GeneticTaskController controller;
	private final Execution simulatorSettings;

	public SimulatorCalculatingTask(GeneticTaskController controller, Execution settings) {
		this.controller = controller;
		this.simulatorSettings = settings;
	}

	@Override
	public Boolean call() throws Exception {
		boolean result = false;
		try {
			final Optional<Metrics> metrics = simulate();
			if (metrics.isPresent()) {
				final TradingStrategy strategy = new TradingStrategy(simulatorSettings, metrics.get());
				result = controller.addTradingStrategy(strategy);
			}
		} finally {
			this.controller.simulationCalculationFinished();
		}
		return result;
	}

	private Optional<Metrics> simulate() {
		try {
			final Simulator simulator = controller.createSimulator();
			simulator.simulateMarketTrading(simulatorSettings);
			return Optional.of(simulator.getMetrics());
		} catch (Exception e) {
			controller.getLogger().error("Error while calculating statistics: " + e.getMessage());
			return Optional.empty();
		}
	}

}