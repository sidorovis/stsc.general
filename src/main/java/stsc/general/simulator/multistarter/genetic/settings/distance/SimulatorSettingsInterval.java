package stsc.general.simulator.multistarter.genetic.settings.distance;

import stsc.general.simulator.Execution;

/**
 * Classes of this interface should calculate distance between two {@link Execution}.
 */
public interface SimulatorSettingsInterval {

	public double calculateInterval(final Execution left, final Execution right);

}