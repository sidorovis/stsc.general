package stsc.general.simulator.multistarter;

import java.util.List;

public abstract class MpTextIterator<T> extends MpIterator<T, MpTextIterator<T>> {

	protected MpTextIterator(String name) {
		super(name);
	}

	public abstract List<T> getDomen();

}
