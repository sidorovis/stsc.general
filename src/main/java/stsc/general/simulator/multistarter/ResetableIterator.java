package stsc.general.simulator.multistarter;

import java.util.Iterator;

/**
 * {@link ResetableIterable} extends {@link Iterator} with possibility to return
 * current value. And reset iterator (so it will start from the first element).
 * 
 */
public interface ResetableIterator<E> extends Iterator<E> {

	public E current();

	public void reset();

}
