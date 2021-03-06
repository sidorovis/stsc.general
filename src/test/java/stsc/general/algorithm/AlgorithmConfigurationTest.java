package stsc.general.algorithm;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;

public final class AlgorithmConfigurationTest {

	@Test
	public void testAlgorithmConfiguration() {
		final AlgorithmConfigurationImpl as = new AlgorithmConfigurationImpl();
		Assert.assertNull(as.getStrings().get("a"));
		Assert.assertEquals(as.getStringSetting("a", ""), "");
		Assert.assertNotNull(as.setDouble("a", new Double(14.05)));
		Assert.assertNotNull(as.setDouble("b", 14.05));

		Assert.assertEquals(as.getDoubleSetting("b", 1.345), as.getDoubleSetting("a", 67.8), Settings.doubleEpsilon);
	}

	@Test
	public void testGetIntegerDoubleTypes() throws BadAlgorithmException {
		final AlgorithmConfigurationImpl as = new AlgorithmConfigurationImpl();
		as.setInteger("asd", Integer.valueOf(15));
		as.setInteger("4asd", Integer.valueOf(1231));
		as.setDouble("param", Double.valueOf(1231.0));
		as.setDouble("para3m", Double.valueOf(125.454));
		Assert.assertEquals(Integer.valueOf(15), as.getIntegerSetting("asd", 767));
		Assert.assertEquals(Integer.valueOf(1231), as.getIntegerSetting("4asd", 54));
		Assert.assertEquals(Double.valueOf(1231.0), as.getDoubleSetting("param", 454.9));
		Assert.assertEquals(Double.valueOf(125.454), as.getDoubleSetting("para3m", 12332.4));

		as.setDouble("kill", 15.343);
		final Double d = as.getDoubleSetting("kill", 0.0);
		Assert.assertEquals(15.343, d, Settings.doubleEpsilon);
	}
}
