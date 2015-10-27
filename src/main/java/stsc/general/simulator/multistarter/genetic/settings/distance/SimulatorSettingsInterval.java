package stsc.general.simulator.multistarter.genetic.settings.distance;

import stsc.general.simulator.SimulatorSettings;

/**
 * Classes of this interface should calculate distance between two {@link SimulatorSettings}.
 */
public interface SimulatorSettingsInterval {

	public double calculateInterval(final SimulatorSettings left, final SimulatorSettings right);

}