package stsc.general.simulator.multistarter.grid;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.Execution;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.testhelper.TestGridSimulatorSettings;
import stsc.general.testhelper.TestMetricsHelper;
import stsc.general.trading.BrokerImpl;
import stsc.storage.ExecutionInstanceProcessor;
import stsc.storage.ExecutionInstancesStorage;
import stsc.storage.mocks.StockStorageMock;

public class SimulatorSettingsGridIteratorTest {

	private static final StockStorage stockStorage = StockStorageMock.getStockStorage();

	@Test
	public void testEmptySimulatorSettingsGridIterator() throws BadAlgorithmException, BadParameterException {
		final FromToPeriod period = TestMetricsHelper.getPeriod();

		final SimulatorSettingsGridFactory ssFactory = new SimulatorSettingsGridFactory(stockStorage, period);

		int count = 0;
		for (Execution simulatorSettings : ssFactory.getList()) {
			count += 1;
			Assert.assertNotNull(simulatorSettings);
		}
		Assert.assertEquals(0, count);
	}

	@Test
	public void testSimulatorSettingsGridIterator() throws BadAlgorithmException, BadParameterException {
		final SimulatorSettingsGridList settings = TestGridSimulatorSettings.getGridList();
		int count = 0;
		for (Execution simulatorSettings : settings) {
			count += 1;
			final ExecutionInstancesStorage executionsStorage = simulatorSettings.getInit().getExecutionsStorage();
			final ExecutionInstanceProcessor executionStarter = executionsStorage.initialize(new BrokerImpl(stockStorage), stockStorage.getStockNames());
			final StockAlgorithm sain = executionStarter.getStockAlgorithm("in", "aapl").get();
			final StockAlgorithm saema = executionStarter.getStockAlgorithm("ema", "aapl").get();
			final StockAlgorithm salevel = executionStarter.getStockAlgorithm("level", "aapl").get();
			final EodAlgorithm saone = executionStarter.getEodAlgorithm("os");
			Assert.assertNotNull(sain);
			Assert.assertNotNull(saema);
			Assert.assertNotNull(salevel);
			Assert.assertNotNull(saone);
		}
		Assert.assertEquals(6144, count);
	}

	@Test
	public void testSimulatorSettingsGridIteratorHashCode() throws BadParameterException, BadAlgorithmException {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
		final SimulatorSettingsGridFactory ssFactory = new SimulatorSettingsGridFactory(stockStorage, TestMetricsHelper.getPeriod());
		AlgorithmSettingsIteratorFactory f1 = new AlgorithmSettingsIteratorFactory();
		f1.add(new MpInteger("a", 1, 3, 1));
		f1.add(new MpDouble("b", 0.1, 0.3, 0.1));
		f1.add(new MpDouble("c", 0.1, 0.2, 0.1));
		f1.add(new MpString("side2", Arrays.asList(new String[] { "long", "long" })));

		ssFactory.addStock("a1", TestGridSimulatorSettings.algoStockName("Input"), f1.getGridIterator());
		ssFactory.addStock("a2", TestGridSimulatorSettings.algoStockName("Input"), f1.getGridIterator());
		ssFactory.addEod("a3", TestGridSimulatorSettings.algoEodName("OneSideOpenAlgorithm"), f1.getGridIterator());
		ssFactory.addEod("a4", TestGridSimulatorSettings.algoEodName("OneSideOpenAlgorithm"), f1.getGridIterator());

		final Set<String> hashes = new HashSet<>();
		int allSize = 0;
		for (Execution simulatorSettings : ssFactory.getList()) {
			hashes.add(simulatorSettings.stringHashCode());
			allSize += 1;
		}

		Assert.assertEquals(4096, allSize);
		Assert.assertEquals(256, hashes.size());
	}
}
