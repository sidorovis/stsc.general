package stsc.general.simulator.multistarter.genetic;

import org.apache.commons.lang3.Validate;

import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.strategy.TradingStrategy;
import stsc.general.strategy.selector.StrategySelector;

public final class StrategyGeneticSearcherBuilder {

	public final static int POPULATION_DEFAULT_SIZE = 100;

	// empirical by StrategyGeneticSearcherTest
	// (see commit # a43c64c01d765f266b8bfae5bb3c3a1a58e4bf24)
	public final static double BEST_DEFAULT_PART = 0.94;

	// empirical by StrategyGeneticSearcherTest
	// (see commit # a43c64c01d765f266b8bfae5bb3c3a1a58e4bf24)
	public final static double CROSSOVER_DEFAULT_PART = 0.86;

	SimulatorSettingsGeneticList simulatorSettingsGeneticList;
	StrategySelector strategySelector;
	int threadAmount = 4;
	CostFunction populationCostFunction;
	// maximum possible populations iterations
	int maxPopulationsAmount = POPULATION_DEFAULT_SIZE;
	int populationSize = POPULATION_DEFAULT_SIZE;
	double bestPart = BEST_DEFAULT_PART;
	double crossoverPart = CROSSOVER_DEFAULT_PART;

	StrategyGeneticSearcherBuilder() {
	}

	public StrategyGeneticSearcherBuilder withSimulatorSettings(final SimulatorSettingsGeneticList simulatorSettingsGeneticList) {
		this.simulatorSettingsGeneticList = simulatorSettingsGeneticList;
		return this;
	}

	/**
	 * {@link StrategySelector} this strategy selector will be used to select {@link TradingStrategy}'s.
	 */
	public StrategyGeneticSearcherBuilder withStrategySelector(final StrategySelector strategySelector) {
		this.strategySelector = strategySelector;
		return this;
	}

	/**
	 * Amount of threads that we will use to simulate strategies in parallel. By default is 4;
	 */
	public StrategyGeneticSearcherBuilder withThreadAmount(int threadAmount) {
		Validate.isTrue(threadAmount > 0, "thread amount for genetic search should be bigger then 0");
		this.threadAmount = threadAmount;
		return this;
	}

	/**
	 * This cost function is used for calculate summary population cost.
	 */
	public StrategyGeneticSearcherBuilder withPopulationCostFunction(final CostFunction populationCostFunction) {
		this.populationCostFunction = populationCostFunction;
		return this;
	}

	/**
	 * maximum possible populations iterations by default is 100
	 */
	public StrategyGeneticSearcherBuilder withMaxPopulationsAmount(int maxPopulationAmount) {
		this.maxPopulationsAmount = maxPopulationAmount;
		return this;
	}

	public StrategyGeneticSearcherBuilder withPopulationSize(int populationSize) {
		this.populationSize = populationSize;
		return this;
	}

	/**
	 * This part of population will be copied from previous (values that {@link StrategySelector} returns first).
	 */
	public StrategyGeneticSearcherBuilder withBestPart(double bestPart) {
		Validate.isTrue(bestPart >= 0.0 && bestPart <= 1.0, "best part for genetic algorithm should be in [0.0, 1.0] interval");
		this.bestPart = bestPart;
		return this;
	}

	/**
	 * This part of population will be generated from crossover algorithm.
	 */
	public StrategyGeneticSearcherBuilder withCrossoverPart(double crossoverPart) {
		Validate.isTrue(crossoverPart >= 0.0 && crossoverPart <= 1.0, "crossover part for genetic algorithm should be in [0.0, 1.0] interval");
		this.crossoverPart = crossoverPart;
		return this;
	}

	public StrategyGeneticSearcher build() {
		return new StrategyGeneticSearcher(this);
	}

}
