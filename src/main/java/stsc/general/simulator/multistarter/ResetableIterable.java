package stsc.general.simulator.multistarter;

public interface ResetableIterable<T> extends Iterable<T> {
	public ResetableIterator<T> iterator();
}
