package stsc.general.simulator.multistarter.genetic;

import org.apache.commons.lang3.Validate;

import stsc.general.strategy.TradingStrategy;

/**
 * Internal (package-private) class / structure that used for {@link StrategyGeneticSearcher} to store settings of the algorithms.
 */
final class GeneticSearchSettings {

	private final int maxPopulationsAmount;
	private final int sizeOfBest;
	private final int populationSize;
	private final int crossoverSize;
	private final int mutationSize;
	private final int tasksSize;

	GeneticSearchSettings(final StrategyGeneticSearcherBuilder builder) {
		this(builder.maxPopulationsAmount, builder.populationSize, builder.bestPart, builder.crossoverPart, builder.strategySelector.maxPossibleAmount());
	}

	private GeneticSearchSettings(int maxPopulationsAmount, int populationSize, double bestPart, double crossoverPart, int selectorSize) {
		this.maxPopulationsAmount = maxPopulationsAmount;
		this.populationSize = populationSize;
		this.sizeOfBest = Math.min((int) (bestPart * populationSize), selectorSize);
		Validate.isTrue(getSizeOfBest() > 0, "size of 'best' population in genetic algorithm should be not zero");
		this.crossoverSize = (int) ((populationSize - this.getSizeOfBest()) * crossoverPart);
		this.mutationSize = populationSize - getCrossoverSize() - this.getSizeOfBest();
		this.tasksSize = getCrossoverSize() + getMutationSize();
	}

	/**
	 * This method returns tasks size ({@link TradingStrategy} simulations count) per genetic iteration. <br/>
	 * For the first simulation we have to calculate all population ( {@link #populationSize} ).
	 */
	int getTasksSize() {
		return tasksSize;
	}

	public int getMaxPopulationsAmount() {
		return maxPopulationsAmount;
	}

	public int getSizeOfBest() {
		return sizeOfBest;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public int getCrossoverSize() {
		return crossoverSize;
	}

	public int getMutationSize() {
		return mutationSize;
	}

}