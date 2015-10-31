package stsc.general.simulator.multistarter.grid;

import stsc.general.strategy.selector.StrategySelector;

/**
 * Builder for {@link StrategyGridSearcher}.
 */
public final class StrategyGridSearcherBuilder {

	private SimulatorSettingsGridList simulatorSettingsGridList;
	private StrategySelector selector;
	private int threadAmount = 4;

	StrategyGridSearcherBuilder() {
	}

	public StrategyGridSearcherBuilder setSimulatorSettingsGridList(SimulatorSettingsGridList simulatorSettingsGridList) {
		this.simulatorSettingsGridList = simulatorSettingsGridList;
		return this;
	}

	public StrategyGridSearcherBuilder setSelector(StrategySelector selector) {
		this.selector = selector;
		return this;
	}

	public StrategyGridSearcherBuilder setThreadAmount(int threadAmount) {
		this.threadAmount = threadAmount;
		return this;
	}

	// getters

	public SimulatorSettingsGridList getSimulatorSettingsGridList() {
		return simulatorSettingsGridList;
	}

	public StrategySelector getSelector() {
		return selector;
	}

	public int getThreadAmount() {
		return threadAmount;
	}

	// build

	public StrategyGridSearcher build() {
		return new StrategyGridSearcher(this);
	}

}
