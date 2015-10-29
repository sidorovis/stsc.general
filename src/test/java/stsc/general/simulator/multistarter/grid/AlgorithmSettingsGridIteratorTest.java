package stsc.general.simulator.multistarter.grid;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.algorithms.AlgorithmConfiguration;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.MutableAlgorithmConfiguration;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.MpSubExecution;

public class AlgorithmSettingsGridIteratorTest {

	private void testHelperNlmParameters(Integer n, String l, Integer m, MutableAlgorithmConfiguration s) {
		Assert.assertEquals(n, s.getIntegerSetting("n", Integer.MAX_VALUE));
		Assert.assertEquals(m, s.getIntegerSetting("m", Integer.MAX_VALUE));
		Assert.assertEquals(l, s.getStringSetting("l", "unknown unexpected value"));
	}

	@Test
	public void testAlgorithmSettingsGridSearcher() throws ParseException, BadParameterException, BadAlgorithmException {
		final AlgorithmSettingsIteratorFactory factory = new AlgorithmSettingsIteratorFactory();
		factory.add(new MpInteger("n", 1, 3, 1));
		factory.add(new MpInteger("m", -4, -1, 2));
		factory.add(new MpString("l", Arrays.asList(new String[] { "asd", "ibm" })));
		final AlgorithmSettingsGridIterator mas = factory.getGridIterator();

		final ArrayList<MutableAlgorithmConfiguration> settings = new ArrayList<>();

		for (MutableAlgorithmConfiguration se : mas) {
			settings.add(se);
		}
		Assert.assertEquals(8, settings.size());
		testHelperNlmParameters(1, "asd", -4, settings.get(0));
		testHelperNlmParameters(2, "asd", -4, settings.get(1));
		testHelperNlmParameters(1, "asd", -2, settings.get(2));
		testHelperNlmParameters(2, "asd", -2, settings.get(3));
		testHelperNlmParameters(1, "ibm", -4, settings.get(4));
		testHelperNlmParameters(2, "ibm", -4, settings.get(5));
		testHelperNlmParameters(1, "ibm", -2, settings.get(6));
		testHelperNlmParameters(2, "ibm", -2, settings.get(7));
	}

	@Test
	public void testStockExecutionGridSearcherALotOfParameters() throws ParseException, BadParameterException, BadAlgorithmException {
		final AlgorithmSettingsIteratorFactory factory = new AlgorithmSettingsIteratorFactory();
		factory.add(new MpInteger("q", 0, 5, 1));
		factory.add(new MpInteger("w", -4, 1, 1));
		factory.add(new MpDouble("a", 0.0, 100.0, 7.0));
		factory.add(new MpDouble("s", -100.0, 101.0, 25.0));
		factory.add(new MpString("z", Arrays.asList(new String[] { "asd", "ibm", "yhoo" })));
		factory.add(new MpString("z", Arrays.asList(new String[] { "vokrug", "fileName" })));
		factory.add(new MpSubExecution("p", Arrays.asList(new String[] { "12313-432423", "234535-23424", "35345-234234135", "24454-65462245" })));
		final AlgorithmSettingsGridIterator mas = factory.getGridIterator();

		final ArrayList<AlgorithmConfiguration> settings = new ArrayList<>();

		AlgorithmSettingsGridIterator.Element i = mas.iterator();
		int sum = 0;
		while (i.hasNext()) {
			i.next();
			sum += 1;
		}
		Assert.assertEquals(5 * 5 * 15 * 9 * 3 * 2 * 4, sum);

		i.reset();

		for (AlgorithmConfiguration se : mas) {
			Assert.assertNotNull(se);
			settings.add(se);
		}
		Assert.assertEquals(5 * 5 * 15 * 9 * 3 * 2 * 4, settings.size());
	}

	@Test
	public void testGridSearcherStockWithStrings() throws BadParameterException {
		final String[] arr = new String[] { "asd", "ibm" };
		final AlgorithmSettingsIteratorFactory factory = new AlgorithmSettingsIteratorFactory();
		factory.add(new MpString("z", Arrays.asList(arr)));
		final AlgorithmSettingsGridIterator mas = factory.getGridIterator();

		AlgorithmSettingsGridIterator.Element i = mas.iterator();
		int sum = 0;
		while (i.hasNext()) {
			MutableAlgorithmConfiguration as = i.next();
			Assert.assertEquals(as.getStringSetting("z", "unexpected"), arr[sum]);
			sum += 1;
		}
		Assert.assertEquals(2, sum);
		i.reset();

		factory.add(new MpString("y", Arrays.asList(new String[] { "asd", "ibm", "yhoo" })));
		final AlgorithmSettingsGridIterator newMas = factory.getGridIterator();
		i = newMas.iterator();

		while (i.hasNext()) {
			i.next();
			sum += 1;
		}
		Assert.assertEquals(8, sum);
	}
}
