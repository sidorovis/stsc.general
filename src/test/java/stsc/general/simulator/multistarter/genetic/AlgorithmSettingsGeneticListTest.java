package stsc.general.simulator.multistarter.genetic;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.MutableAlgorithmConfiguration;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.MpSubExecution;

public class AlgorithmSettingsGeneticListTest {

	private AlgorithmConfigurationSetGeneticGenerator getGeneticGenerator() throws BadParameterException {
		final AlgorithmSettingsIteratorFactory factory = new AlgorithmSettingsIteratorFactory();
		factory.add(new MpInteger("q", -20, 100, 1));
		factory.add(new MpInteger("w", -40, 15, 1));
		factory.add(new MpDouble("a", -60.0, 100.0, 0.15));
		factory.add(new MpDouble("s", -100.0, 101.0, 2.0));
		factory.add(new MpString("z", Arrays.asList(new String[] { "asd", "ibm", "yhoo" })));
		factory.add(new MpString("z", Arrays.asList(new String[] { "vokrug", "fileName" })));
		factory.add(new MpSubExecution("p", Arrays.asList(new String[] { "12313-432423", "234535-23424", "35345-234234135", "24454-65462245" })));
		final AlgorithmConfigurationSetGeneticGenerator mas = factory.getGeneticList();
		return mas;
	}

	@Test
	public void testAlgorithmSettingsGeneticListGenerateRandom() throws ParseException, BadParameterException, BadAlgorithmException {
		final AlgorithmConfigurationSetGeneticGenerator mas = getGeneticGenerator();

		final Set<String> codes = new HashSet<>();
		final int TEST_SIZE = 50000;
		while (codes.size() < TEST_SIZE) {
			for (int i = 0; i < TEST_SIZE; ++i) {
				final StringBuilder b = new StringBuilder();
				mas.generateRandom().stringHashCode(b);
				codes.add(b.toString());
			}
		}
		Assert.assertEquals(true, codes.size() >= TEST_SIZE);
	}

	@Test
	public void testAlgorithmSettingsGeneticListSize() throws ParseException, BadParameterException, BadAlgorithmException {
		final AlgorithmConfigurationSetGeneticGenerator mas = getGeneticGenerator();
		Assert.assertEquals(17070292800L, mas.size());
	}

	@Test
	public void testAlgorithmSettingsGeneticListMutate() throws ParseException, BadParameterException, BadAlgorithmException {
		for (int i = 0; i < 10; ++i) {
			if (!amountOfMutations()) {
				return;
			}
		}
		Assert.fail("mutation test failed, there were no mutation");
	}

	private boolean amountOfMutations() throws BadParameterException {
		final AlgorithmConfigurationSetGeneticGenerator mas = getGeneticGenerator();
		final MutableAlgorithmConfiguration original = mas.generateRandom();
		final MutableAlgorithmConfiguration copy = original.clone();
		int i = 0;
		while (true) {
			final StringBuilder originalSb = new StringBuilder();
			final StringBuilder copySb = new StringBuilder();
			mas.mutate(copy);
			original.stringHashCode(originalSb);
			copy.stringHashCode(copySb);
			i += 1;
			if (!originalSb.toString().equals(copySb.toString()))
				break;
		}
		return (i > 2);
	}

	@Test
	public void testAlgorithmSettingsGeneticListMerge() throws ParseException, BadParameterException, BadAlgorithmException {
		final AlgorithmConfigurationSetGeneticGenerator mas = getGeneticGenerator();
		final MutableAlgorithmConfiguration original = mas.generateRandom();
		final MutableAlgorithmConfiguration copy = original.clone();

		final MutableAlgorithmConfiguration merge = mas.merge(original, copy);
		Assert.assertEquals(merge.getSubExecutions().size(), original.getSubExecutions().size());
		Assert.assertEquals(merge.getSubExecutions().get(0), original.getSubExecutions().get(0));
		Assert.assertEquals(merge.getIntegerSetting("q", 100), original.getIntegerSetting("q", 200));
		Assert.assertEquals(merge.getIntegerSetting("w", 400), original.getIntegerSetting("w", 300));
		Assert.assertEquals(merge.getDoubleSetting("a", 142.4), original.getDoubleSetting("a", 5454.6));
		Assert.assertEquals(merge.getDoubleSetting("s", 343.54), original.getDoubleSetting("s", 56.4));
		Assert.assertEquals(merge.getStringSetting("z", "vrr"), original.getStringSetting("z", "v"));
	}
}
