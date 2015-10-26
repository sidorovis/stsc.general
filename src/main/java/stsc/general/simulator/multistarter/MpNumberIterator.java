package stsc.general.simulator.multistarter;

public abstract class MpNumberIterator<T> extends MpIterator<T, MpNumberIterator<T>> implements Cloneable {

	protected MpNumberIterator(String name) {
		super(name);
	}

	public abstract T getFrom();

	public abstract T getTo();

	public abstract T getStep();
}
