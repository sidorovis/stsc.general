package stsc.general.simulator.multistarter;

import java.util.Iterator;

public interface AlgorithmConfigurationSet {

	long size();

	int parametersSize();

	String toString();

	AlgorithmConfigurationSet clone();

	//

	public void reset();

	public ParameterList<Integer, MpNumberIterator<Integer>> getIntegers();

	public ParameterList<Double, MpNumberIterator<Double>> getDoubles();

	public ParameterList<String, MpTextIterator<String>> getStrings();

	public ParameterList<String, MpTextIterator<String>> getSubExecutions();

	public Iterator<MpTextIterator<String>> getSubExecutionIterator();

}