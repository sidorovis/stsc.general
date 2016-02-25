package stsc.general.simulator.multistarter.genetic;

import stsc.common.algorithms.EodExecutionInstance;
import stsc.common.algorithms.MutableAlgorithmConfiguration;
import stsc.common.algorithms.StockExecutionInstance;
import stsc.general.simulator.Execution;

public final class GeneticExecutionInitializer {

	private final String executionName;
	public String algorithmName;
	public AlgorithmConfigurationSetGeneticGenerator geneticAlgorithmSettings;

	public GeneticExecutionInitializer(String eName, String algorithmName, AlgorithmConfigurationSetGeneticGenerator algorithmSettings) {
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

	public MutableAlgorithmConfiguration generateRandom() {
		return geneticAlgorithmSettings.generateRandom();
	}

	public void mutateStock(int mutateSettingIndex, Execution copy) {
		final StockExecutionInstance execution = copy.getInit().getExecutionsStorage().getStockExecutions().get(mutateSettingIndex);
		final MutableAlgorithmConfiguration algorithmSettings = execution.getSettings();
		mutateAlgorithmSettings(algorithmSettings);
	}

	public void mutateEod(int eodIndex, Execution copy) {
		final EodExecutionInstance execution = copy.getInit().getExecutionsStorage().getEodExecutions().get(eodIndex);
		final MutableAlgorithmConfiguration algorithmSettings = execution.getSettings();
		mutateAlgorithmSettings(algorithmSettings);
	}

	private void mutateAlgorithmSettings(final MutableAlgorithmConfiguration algorithmSettings) {
		geneticAlgorithmSettings.mutate(algorithmSettings);
	}

	public MutableAlgorithmConfiguration mergeStock(StockExecutionInstance leftSe, StockExecutionInstance rightSe) {
		return geneticAlgorithmSettings.merge(leftSe.getSettings(), rightSe.getSettings());
	}

	public MutableAlgorithmConfiguration mergeEod(EodExecutionInstance leftSe, EodExecutionInstance rightSe) {
		return geneticAlgorithmSettings.merge(leftSe.getSettings(), rightSe.getSettings());
	}

	public long size() {
		return geneticAlgorithmSettings.size();
	}

}
