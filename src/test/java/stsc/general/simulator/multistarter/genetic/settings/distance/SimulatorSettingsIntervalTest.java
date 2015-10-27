package stsc.general.simulator.multistarter.genetic.settings.distance;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;

public class SimulatorSettingsIntervalTest {

	@Test
	public void testCompareDoubleMaxValue() {
		Assert.assertEquals(Double.MAX_VALUE, Double.MAX_VALUE + 100, Settings.doubleEpsilon);
	}

	public void testSimulatorSettingsInterval() {
		final SimulatorSettingsInterval interval = new SimulatorSettingsIntervalImpl();
		// TODO accomplish test
		Assert.assertNotNull(interval);
	}

}
