package stsc.general.simulator.multistarter.genetic;

import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.MutatingAlgorithmConfiguration;
import stsc.common.algorithms.StockExecution;
import stsc.general.simulator.SimulatorSettings;

public final class GeneticExecutionInitializer {

	public String executionName;
	public String algorithmName;
	public AlgorithmSettingsGeneticList geneticAlgorithmSettings;

	public GeneticExecutionInitializer(String eName, String algorithmName, AlgorithmSettingsGeneticList algorithmSettings) {
		super();
		this.executionName = eName;
		this.algorithmName = algorithmName;
		this.geneticAlgorithmSettings = algorithmSettings;
	}

	public String getExecutionName() {
		return executionName;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	@Override
	public String toString() {
		return executionName + "(" + algorithmName + ")\n" + geneticAlgorithmSettings + "\n";
	}

	public MutatingAlgorithmConfiguration generateRandom() {
		return geneticAlgorithmSettings.generateRandom();
	}

	public void mutateStock(int mutateSettingIndex, SimulatorSettings copy) {
		final StockExecution execution = copy.getInit().getExecutionsStorage().getStockExecutions().get(mutateSettingIndex);
		final MutatingAlgorithmConfiguration algorithmSettings = execution.getSettings();
		mutateAlgorithmSettings(algorithmSettings);
	}

	public void mutateEod(int eodIndex, SimulatorSettings copy) {
		final EodExecution execution = copy.getInit().getExecutionsStorage().getEodExecutions().get(eodIndex);
		final MutatingAlgorithmConfiguration algorithmSettings = execution.getSettings();
		mutateAlgorithmSettings(algorithmSettings);
	}

	private void mutateAlgorithmSettings(final MutatingAlgorithmConfiguration algorithmSettings) {
		geneticAlgorithmSettings.mutate(algorithmSettings);
	}

	public MutatingAlgorithmConfiguration mergeStock(StockExecution leftSe, StockExecution rightSe) {
		return geneticAlgorithmSettings.merge(leftSe.getSettings(), rightSe.getSettings());
	}

	public MutatingAlgorithmConfiguration mergeEod(EodExecution leftSe, EodExecution rightSe) {
		return geneticAlgorithmSettings.merge(leftSe.getSettings(), rightSe.getSettings());
	}

	public long size() {
		return geneticAlgorithmSettings.size();
	}

}
