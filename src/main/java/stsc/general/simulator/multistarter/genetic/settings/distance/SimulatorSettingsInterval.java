package stsc.general.simulator.multistarter.genetic.settings.distance;

import stsc.general.simulator.SimulatorConfiguration;

/**
 * Classes of this interface should calculate distance between two {@link SimulatorConfiguration}.
 */
public interface SimulatorSettingsInterval {

	public double calculateInterval(final SimulatorConfiguration left, final SimulatorConfiguration right);

}