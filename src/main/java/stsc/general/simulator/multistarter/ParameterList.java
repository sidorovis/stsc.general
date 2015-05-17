package stsc.general.simulator.multistarter;

import java.util.ArrayList;
import java.util.List;

public class ParameterList<Type, MpIteratorType extends MpIterator<Type, MpIteratorType>> implements Cloneable {

	private final List<MpIteratorType> params;
	private int index;

	public ParameterList() {
		this.params = new ArrayList<MpIteratorType>();
		this.index = 0;
	}

	public ParameterList<Type, MpIteratorType> clone() {
		return new ParameterList<Type, MpIteratorType>(this.params);
	}

	private ParameterList(final List<MpIteratorType> params) {
		this.params = new ArrayList<MpIteratorType>(params.size());
		for (MpIteratorType mpIterator : params) {
			this.params.add(mpIterator.clone());
		}
		this.index = 0;
	}

	public void add(final MpIteratorType mpIterator) {
		params.add(mpIterator);
	}

	public void reset() {
		index = 0;
	}

	public void increment() {
		index += 1;
	}

	public boolean empty() {
		return params.isEmpty();
	}

	public boolean hasNext() {
		return index + 1 < params.size();
	}

	public boolean hasCurrent() {
		return index < params.size();
	}

	public MpIteratorType getCurrentParam() {
		return params.get(index);
	}

	public List<MpIteratorType> getParams() {
		return params;
	}

	@Override
	public String toString() {
		return String.valueOf(index) + ": " + params.toString();
	}

	public long size() {
		long result = 1;
		for (MpIteratorType i : params) {
			result *= i.size();
		}
		return result;
	}
}
