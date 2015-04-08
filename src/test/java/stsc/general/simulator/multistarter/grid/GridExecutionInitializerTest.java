package stsc.general.simulator.multistarter.grid;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.algorithms.AlgorithmSettings;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.MpSubExecution;
import stsc.general.testhelper.TestStatisticsHelper;

public class GridExecutionInitializerTest {

	@Test
	public void testExecutionInitializer() throws BadParameterException {
		AlgorithmSettingsIteratorFactory factory = new AlgorithmSettingsIteratorFactory(TestStatisticsHelper.getPeriod());
		factory.add(new MpInteger("n", 1, 10, 2));
		factory.add(new MpDouble("d", 0.1, 1.0, 0.2));
		final AlgorithmSettingsGridIterator mas = factory.getGridIterator();
		final GridExecutionInitializer ei = new GridExecutionInitializer("e", "a", mas);
		int count = 0;
		for (AlgorithmSettings algorithmSettings : ei) {
			Assert.assertNotNull(algorithmSettings);
			count += 1;
		}
		Assert.assertEquals(25, count);
		ei.reset();
		for (AlgorithmSettings algorithmSettings : ei) {
			Assert.assertNotNull(algorithmSettings);
			count += 1;
		}
		Assert.assertEquals(50, count);
		ei.reset();
		while (ei.hasNext()) {
			count += 1;
			ei.next();
		}
		Assert.assertEquals(75, count);
		while (ei.hasNext()) {
			count += 1;
			ei.next();
		}
		Assert.assertEquals(75, count);
	}

	@Test
	public void testExecutionInitializerWithStrings() throws BadParameterException {
		final AlgorithmSettingsIteratorFactory factory = new AlgorithmSettingsIteratorFactory(TestStatisticsHelper.getPeriod());
		factory.add(new MpString("n", Arrays.asList(new String[] { "asd" })));
		factory.add(new MpString("d", Arrays.asList(new String[] { "asd", "dfg", "rty" })));
		factory.add(new MpString("o", Arrays.asList(new String[] { "hello", "world", "my", "dear" })));
		final AlgorithmSettingsGridIterator mas = factory.getGridIterator();
		final GridExecutionInitializer ei = new GridExecutionInitializer("e", "a", mas);
		int count = 0;
		while (ei.hasNext()) {
			count += 1;
			ei.next();
		}
		Assert.assertEquals(12, count);
	}

	@Test
	public void testExecutionInitializerWithEverything() throws BadParameterException {
		final AlgorithmSettingsIteratorFactory factory = new AlgorithmSettingsIteratorFactory(TestStatisticsHelper.getPeriod());
		factory.add(new MpString("n", Arrays.asList(new String[] { "asd" })));
		factory.add(new MpString("d", Arrays.asList(new String[] { "asd", "dfg", "rty" })));
		factory.add(new MpString("o", Arrays.asList(new String[] { "hello", "world", "my", "dear" })));
		factory.add(new MpSubExecution("execution", Arrays.asList(new String[] { "name1", "name2", "name3", "name4" })));
		final AlgorithmSettingsGridIterator mas = factory.getGridIterator();
		final GridExecutionInitializer ei = new GridExecutionInitializer("e", "a", mas);
		int count = 0;
		while (ei.hasNext()) {
			count += 1;
			ei.next();
		}
		Assert.assertEquals(48, count);
	}
}
