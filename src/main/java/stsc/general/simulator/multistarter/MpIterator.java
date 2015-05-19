package stsc.general.simulator.multistarter;

import java.util.Iterator;

/**
 * Named {@link MpIterator}. This is special Iterator concept that implements
 * {@link Iterator} interface with {@link ResetableIterable} extension.<br/>
 * MultiParameter Iterator provide possibility to get values from iterator by
 * index (like c++ random access iterator). Also you can get Random indexed
 * iterator value with {@link MpIterator#getRangom()}.
 * <hr/>
 * Also it is possible to find index by value. You can merge two values from one
 * {@link MpIterator} domen: {@link MpIterator#merge(T left, T right)}.<br/>
 * By merge we understand getting value between left and right (including left
 * and right). <b>Remember</b> result is not stable, so two merge calls with
 * same parameter could return different answers.
 */
public abstract class MpIterator<T, E extends MpIterator<T, E>> implements ResetableIterator<T>, Cloneable {

	private final String name;

	protected MpIterator(String name) {
		this.name = name;
	}

	public final String getName() {
		return name;
	}

	public final Parameter<T> currentParameter() {
		return new Parameter<T>(getName(), current());
	}

	public abstract void increment();

	public abstract long size();

	public final Parameter<T> getParameterByIndex(int index) {
		return new Parameter<T>(getName(), parameter(index));
	}

	public abstract T parameter(int index);

	public abstract E clone();

	public final T getRangom() {
		return parameter((int) (Math.random() * size()));
	}

	public abstract int getIndexByValue(String value);

	public abstract int getIndexByValue(T value);

	public final T merge(T leftValue, T rightValue) {
		final int leftIndex = getIndexByValue(leftValue);
		final int rightIndex = getIndexByValue(rightValue);
		return mergeByIndex(leftIndex, rightIndex);
	}

	protected T mergeByIndex(int leftIndex, int rightIndex) {
		int newIndex = 0;
		if (leftIndex < 0 || rightIndex < 0) {
			return parameter((int) (size() / 2));
		}
		if (leftIndex == rightIndex) {
			return parameter(leftIndex);
		}
		if (leftIndex > rightIndex) {
			final int temp = leftIndex;
			leftIndex = rightIndex;
			rightIndex = temp;
		}
		while (true) {
			newIndex = mergeIndexes(leftIndex, rightIndex);
			if (newIndex >= leftIndex && newIndex <= rightIndex)
				break;
		}
		return parameter(newIndex);
	}

	private int mergeIndexes(int leftIndex, int rightIndex) {
		int newIndex = 0;
		int binaryIndex = 0;
		while (leftIndex > 0 || rightIndex > 0) {
			final int l = (leftIndex & 1);
			final int r = (rightIndex & 1);
			final double random = Math.random();
			if (l == 1 && r == 1 && random >= 0.1) {
				newIndex |= 1 << (binaryIndex);
			} else if ((l + r) == 1 && random >= 0.5) {
				newIndex |= 1 << (binaryIndex);
			} else if ((l + r) == 0 && random >= 0.9) {
				newIndex |= 1 << (binaryIndex);
			}
			binaryIndex += 1;
			leftIndex >>= 1;
			rightIndex >>= 1;
		}
		return newIndex;
	}

}
