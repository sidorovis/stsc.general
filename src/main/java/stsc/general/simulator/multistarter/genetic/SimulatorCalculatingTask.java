package stsc.general.simulator.multistarter.genetic;

import java.util.Optional;
import java.util.concurrent.Callable;

import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorImpl;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Metrics;
import stsc.general.strategy.TradingStrategy;

final class SimulatorCalculatingTask implements Callable<Boolean> {

	private final StrategyGeneticSearcher strategyGeneticSearcher;
	private SimulatorSettings settings;

	SimulatorCalculatingTask(StrategyGeneticSearcher strategyGeneticSearcher, SimulatorSettings settings) {
		this.strategyGeneticSearcher = strategyGeneticSearcher;
		this.settings = settings;
	}

	@Override
	public Boolean call() throws Exception {
		boolean result = false;
		try {
			final Optional<Metrics> metrics = simulate();
			if (metrics.isPresent()) {
				final TradingStrategy strategy = new TradingStrategy(settings, metrics.get());
				result = strategyGeneticSearcher.addTradingStrategy(strategy);
			}
		} finally {
			this.strategyGeneticSearcher.simulationCalculationFinished();
		}
		return result;
	}

	private Optional<Metrics> simulate() {
		Simulator simulator = null;
		try {
			simulator = new SimulatorImpl(settings);
		} catch (Exception e) {
			StrategyGeneticSearcher.logger.error("Error while calculating statistics: " + e.getMessage());
			return Optional.empty();
		}
		return Optional.of(simulator.getMetrics());
	}

}