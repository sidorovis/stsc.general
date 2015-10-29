package stsc.general.simulator.multistarter.grid;

import stsc.common.algorithms.MutatingAlgorithmSettings;
import stsc.general.simulator.multistarter.ResetableIterable;
import stsc.general.simulator.multistarter.ResetableIterator;

public class GridExecutionInitializer implements ResetableIterator<MutatingAlgorithmSettings>, ResetableIterable<MutatingAlgorithmSettings>, Cloneable {
	public String executionName;
	public String algorithmName;
	public AlgorithmSettingsGridIterator.Element iterator;

	public GridExecutionInitializer(String eName, String algorithmName, AlgorithmSettingsGridIterator mas) {
		super();
		this.executionName = eName;
		this.algorithmName = algorithmName;
		this.iterator = mas.iterator();
	}

	private GridExecutionInitializer(String eName, String algorithmName, AlgorithmSettingsGridIterator.Element iterator) {
		super();
		this.executionName = eName;
		this.algorithmName = algorithmName;
		this.iterator = iterator;
	}

	public GridExecutionInitializer clone() {
		return new GridExecutionInitializer(executionName, algorithmName, iterator.clone());
	}

	public void reset() {
		iterator.reset();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public MutatingAlgorithmSettings next() {
		return iterator.next();
	}

	@Override
	public void remove() {
		iterator.remove();
	}

	@Override
	public String toString() {
		return executionName + "(" + algorithmName + ")\n" + iterator.current() + "\n";
	}

	@Override
	public MutatingAlgorithmSettings current() {
		return iterator.current();
	}

	@Override
	public ResetableIterator<MutatingAlgorithmSettings> iterator() {
		return this;
	}

	public long size() {
		return iterator.size();
	}

}
