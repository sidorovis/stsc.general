package stsc.general.trading;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.testhelper.TestMetricsHelper;
import stsc.storage.ExecutionStarter;
import stsc.storage.ExecutionsStorage;
import stsc.storage.mocks.StockStorageMock;

public class ExecutionsLoaderTest {

	private final static StockStorage stockStorage = StockStorageMock.getStockStorage();

	private ExecutionsStorage helperForSuccessLoadTests(File filename) throws Exception {
		final ExecutionsLoader el = new ExecutionsLoader(filename, TestMetricsHelper.getPeriod());
		Assert.assertNotNull(el.getExecutionsStorage());
		final ExecutionsStorage executions = el.getExecutionsStorage();
		executions.initialize(new BrokerImpl(stockStorage), stockStorage.getStockNames());
		return executions;
	}

	final private File resourceToPath(final String resourcePath) throws URISyntaxException {
		return new File(BrokerTest.class.getResource(resourcePath).toURI());
	}

	@Test
	public void testAlgorithmLoader() throws Exception {
		final ExecutionsStorage executions = helperForSuccessLoadTests(resourceToPath("executions_loader_tests/algs_t1.ini"));
		final ExecutionStarter starter = executions.initialize(new BrokerImpl(stockStorage), stockStorage.getStockNames());
		Assert.assertEquals(3, starter.getStockAlgorithmsSize());
		Assert.assertEquals(0, starter.getEodAlgorithmsSize());
	}

	@Test
	public void testSeveralAlgorithmLoader() throws Exception {
		final ExecutionsStorage executions = helperForSuccessLoadTests(resourceToPath("executions_loader_tests/algs_t2.ini"));
		final ExecutionStarter starter = executions.initialize(new BrokerImpl(stockStorage), stockStorage.getStockNames());
		Assert.assertEquals(5, starter.getStockAlgorithmsSize());
		Assert.assertEquals(0, starter.getEodAlgorithmsSize());
	}

	@Test
	public void testAlgorithmLoaderWithSubExecutions() throws BadAlgorithmException {
		final String config = "StockExecutions = smcTest, ssmm\n" + //
				"smcTest.loadLine = .StockMarketCycle()\n" + //
				"ssmm.loadLine = .Sma(N=50i, .StockMarketCycle() )\n"; //
		final ExecutionsLoader el = new ExecutionsLoader(TestMetricsHelper.getPeriod(), config);
		Assert.assertEquals(2, el.getExecutionsStorage().getStockExecutions().size());
		Assert.assertEquals("smcTest", el.getExecutionsStorage().getStockExecutions().get(0).getExecutionName());
		Assert.assertEquals("ssmm", el.getExecutionsStorage().getStockExecutions().get(1).getExecutionName());
		Assert.assertEquals("smcTest", el.getExecutionsStorage().getStockExecutions().get(1).getSettings().getSubExecutions().get(0));
		el.getExecutionsStorage().initialize(new BrokerImpl(stockStorage), stockStorage.getStockNames());
	}

	@Test
	public void testAlgorithmLoaderWithEodSubExecutions() throws BadAlgorithmException {
		final String config = "EodExecutions = sma1, sma2\n" + //
				"sma1.loadLine = .Sma(.AdlAdl())\n" + //
				"sma2.loadLine = .Sma(.AdlAdl(),N=5i)\n"; //
		final ExecutionsLoader el = new ExecutionsLoader(TestMetricsHelper.getPeriod(), config);
		Assert.assertEquals(3, el.getExecutionsStorage().getEodExecutions().size());
		Assert.assertEquals(".AdlAdl()", el.getExecutionsStorage().getEodExecutions().get(0).getExecutionName());
		Assert.assertEquals("sma1", el.getExecutionsStorage().getEodExecutions().get(1).getExecutionName());
		Assert.assertEquals("sma2", el.getExecutionsStorage().getEodExecutions().get(2).getExecutionName());
		el.getExecutionsStorage().initialize(new BrokerImpl(stockStorage), stockStorage.getStockNames());
	}

	private void throwTesthelper(File file, String message) throws Exception {
		boolean throwed = false;
		try {
			ExecutionsLoader loader = new ExecutionsLoader(file, TestMetricsHelper.getPeriod());
			loader.getExecutionsStorage().initialize(new BrokerImpl(stockStorage), stockStorage.getStockNames());
		} catch (BadAlgorithmException e) {
			Assert.assertEquals(message, e.getMessage());
			throwed = true;
		}
		Assert.assertEquals(true, throwed);
	}

	@Test
	public void testBadAlgoFiles() throws Exception {
		throwTesthelper(resourceToPath("executions_loader_tests/algs_bad_repeat.ini"), "algorithm AlgDefines already registered");
		throwTesthelper(resourceToPath("executions_loader_tests/algs_no_load_line.ini"), "bad stock execution registration, no AlgDefine.loadLine property");
		throwTesthelper(resourceToPath("executions_loader_tests/algs_bad_load_line1.ini"), "bad algorithm load line: INPUT( e = close");
		throwTesthelper(resourceToPath("executions_loader_tests/algs_bad_load_line2.ini"), "bad algorithm load line: INPUT)");
		throwTesthelper(resourceToPath("executions_loader_tests/algs_bad_load_line3.ini"),
				"Exception while loading algo: stsc.algorithms.indices.primitive.stock.Sma( AlgDefine ) , exception: stsc.common.algorithms.BadAlgorithmException: Sma algorithm should receive at least one sub algorithm");
	}

	@Test
	public void testAlgorithmLoaderWithEod() throws Exception {
		final ExecutionsStorage executions = helperForSuccessLoadTests(resourceToPath("executions_loader_tests/trade_algs.ini"));
		final ExecutionStarter starter = executions.initialize(new BrokerImpl(stockStorage), stockStorage.getStockNames());
		Assert.assertEquals(4, starter.getStockAlgorithmsSize());
		Assert.assertNotNull(starter.getEodAlgorithm("a1"));
	}

	@Test
	public void testAlgorithmLoaderWithEodOnEod() throws Exception {
		final ExecutionsStorage executions = helperForSuccessLoadTests(resourceToPath("executions_loader_tests/eod_on_eod_dependency.ini"));
		final ExecutionStarter starter = executions.initialize(new BrokerImpl(stockStorage), stockStorage.getStockNames());
		Assert.assertEquals(2, starter.getEodAlgorithmsSize());
		Assert.assertNotNull(starter.getEodAlgorithm("a1"));
		Assert.assertEquals(2, starter.getStockAlgorithmsSize());
		Assert.assertTrue(starter.getStockAlgorithm(".Tma(.Input())", "aapl").isPresent());
	}

}
