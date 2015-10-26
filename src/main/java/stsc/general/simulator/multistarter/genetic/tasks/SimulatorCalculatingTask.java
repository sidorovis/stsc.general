package stsc.general.simulator.multistarter.genetic.tasks;

import java.util.Optional;
import java.util.concurrent.Callable;

import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorImpl;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Metrics;
import stsc.general.strategy.TradingStrategy;

public final class SimulatorCalculatingTask implements Callable<Boolean> {

	private final GeneticTaskController controller;
	private final SimulatorSettings settings;

	public SimulatorCalculatingTask(GeneticTaskController controller, SimulatorSettings settings) {
		this.controller = controller;
		this.settings = settings;
	}

	@Override
	public Boolean call() throws Exception {
		boolean result = false;
		try {
			final Optional<Metrics> metrics = simulate();
			if (metrics.isPresent()) {
				final TradingStrategy strategy = new TradingStrategy(settings, metrics.get());
				result = controller.addTradingStrategy(strategy);
			}
		} finally {
			this.controller.simulationCalculationFinished();
		}
		return result;
	}

	private Optional<Metrics> simulate() {
		try {
			final Simulator simulator = new SimulatorImpl();
			simulator.simulateMarketTrading(settings);
			return Optional.of(simulator.getMetrics());
		} catch (Exception e) {
			controller.getLogger().error("Error while calculating statistics: " + e.getMessage());
			return Optional.empty();
		}
	}

}