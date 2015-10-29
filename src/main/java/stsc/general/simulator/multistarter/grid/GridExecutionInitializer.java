package stsc.general.simulator.multistarter.grid;

import stsc.common.algorithms.MutableAlgorithmConfiguration;
import stsc.general.simulator.multistarter.ResetableIterable;
import stsc.general.simulator.multistarter.ResetableIterator;

public class GridExecutionInitializer implements ResetableIterator<MutableAlgorithmConfiguration>, ResetableIterable<MutableAlgorithmConfiguration>, Cloneable {
	public String executionName;
	public String algorithmName;
	public AlgorithmConfigurationSetGridGenerator iterator;

	public GridExecutionInitializer(String eName, String algorithmName, AlgorithmConfigurationSetGridGenerator mas) {
		super();
		this.executionName = eName;
		this.algorithmName = algorithmName;
		this.iterator = mas;
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
	public MutableAlgorithmConfiguration next() {
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
	public MutableAlgorithmConfiguration current() {
		return iterator.current();
	}

	@Override
	public ResetableIterator<MutableAlgorithmConfiguration> iterator() {
		return this;
	}

	public long size() {
		return iterator.size();
	}

}
