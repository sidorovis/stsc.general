package stsc.general.simulator;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.primitive.eod.OneSideOpenAlgorithm;
import stsc.common.FromToPeriod;
import stsc.common.Settings;
import stsc.common.algorithms.AlgorithmNameGenerator;
import stsc.common.algorithms.EodExecution;
import stsc.common.storage.SignalsStorage;
import stsc.common.storage.StockStorage;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.testhelper.TestMetricsHelper;
import stsc.general.trading.TradeProcessorInit;
import stsc.storage.ExecutionsStorage;
import stsc.storage.mocks.StockStorageMock;

public final class SimulatorTest {

	private final StockStorage stockStorageForAapl = StockStorageMock.getStockStorageFor("aapl");

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	final private File resourceToPath(final String resourcePath) throws URISyntaxException {
		return new File(SimulatorTest.class.getResource(resourcePath).toURI());
	}

	@Test
	public void testOneSideSimulator() throws Exception {
		final Path testOutputPath = FileSystems.getDefault().getPath(testFolder.getRoot().getAbsolutePath());
		SimulatorImpl.fromFile(resourceToPath("simulator_configs/one_side.ini")).getMetrics().print(testOutputPath.resolve("statistics.csv").toString());
		Assert.assertEquals(541, testOutputPath.resolve("statistics.csv").toFile().length());
		testOutputPath.resolve("statistics.csv").toFile().deleteOnExit();
	}

	@Test
	public void testLongSideOnAppl() throws Exception {
		final ExecutionsStorage executionsStorage = new ExecutionsStorage();
		final FromToPeriod period = new FromToPeriod("01-09-2002", "27-09-2002");
		final EodExecution execution = new EodExecution("eName", OneSideOpenAlgorithm.class, new AlgorithmSettingsImpl(period));
		executionsStorage.addEodExecution(execution);

		final TradeProcessorInit tpi = new TradeProcessorInit(stockStorageForAapl, period, executionsStorage);
		final Simulator simulator = new SimulatorImpl();
		simulator.simulateMarketTrading(new SimulatorSettingsImpl(0, tpi));
		final Metrics metrics = simulator.getMetrics();
		Assert.assertEquals(18, metrics.getIntegerMetric(MetricType.period).intValue());
		Assert.assertEquals(6.125517, metrics.getDoubleMetric(MetricType.avGain), Settings.doubleEpsilon);
	}

	@Test
	public void testLongSideOnApplForTwoMonths() throws Exception {
		final ExecutionsStorage executionsStorage = new ExecutionsStorage();
		final FromToPeriod period = new FromToPeriod("01-09-2002", "27-10-2002");
		final EodExecution execution = new EodExecution("eName", OneSideOpenAlgorithm.class, new AlgorithmSettingsImpl(period));
		executionsStorage.addEodExecution(execution);

		final TradeProcessorInit tpi = new TradeProcessorInit(stockStorageForAapl, period, executionsStorage);
		final Simulator simulator = new SimulatorImpl();
		simulator.simulateMarketTrading(new SimulatorSettingsImpl(0, tpi));
		final Metrics metrics = simulator.getMetrics();
		Assert.assertEquals(39, metrics.getIntegerMetric(MetricType.period).intValue());
		Assert.assertEquals(3.243971, metrics.getDoubleMetric(MetricType.avGain), Settings.doubleEpsilon);
	}

	@Test
	public void testShortSideOnAppl() throws Exception {
		final ExecutionsStorage executionsStorage = new ExecutionsStorage();
		final FromToPeriod period = new FromToPeriod("01-09-2002", "27-09-2002");
		final EodExecution execution = new EodExecution("eName", OneSideOpenAlgorithm.class, new AlgorithmSettingsImpl(period).setString("side", "short"));
		executionsStorage.addEodExecution(execution);

		final TradeProcessorInit tpi = new TradeProcessorInit(stockStorageForAapl, period, executionsStorage);
		final Simulator simulator = new SimulatorImpl();
		simulator.simulateMarketTrading(new SimulatorSettingsImpl(0, tpi));
		final Metrics metrics = simulator.getMetrics();
		Assert.assertEquals(18, metrics.getIntegerMetric(MetricType.period).intValue());
		Assert.assertEquals(-6.125517, metrics.getDoubleMetric(MetricType.avGain), Settings.doubleEpsilon);
	}

	@Test
	public void testSimpleSimulator() throws Exception {
		final Path testOutputPath = FileSystems.getDefault().getPath(testFolder.getRoot().getAbsolutePath());
		final Metrics metrics = SimulatorImpl.fromFile(resourceToPath("simulator_configs/simple.ini")).getMetrics();
		metrics.print(testOutputPath.resolve("statistics.csv").toString());
		Assert.assertEquals(2096, metrics.getEquityCurveInMoney().size());
		Assert.assertEquals(46132, testOutputPath.resolve("statistics.csv").toFile().length());
		testOutputPath.resolve("statistics.csv").toFile().deleteOnExit();
	}

	public void testPositiveNDaysSimulator() throws Exception {
		final Path testOutputPath = FileSystems.getDefault().getPath(testFolder.getRoot().getAbsolutePath());
		SimulatorImpl.fromFile(resourceToPath("simulator_configs/ndays.ini")).getMetrics().print(testOutputPath.resolve("statistics.csv").toString());
		Assert.assertEquals(575 * 2 + 11165, testOutputPath.resolve("statistics.csv").toFile().length());
		testOutputPath.resolve("statistics.csv").toFile().deleteOnExit();
	}

	@Test
	public void testOpenWhileSignalAlgorithmSimulator() throws Exception {
		final Path testOutputPath = FileSystems.getDefault().getPath(testFolder.getRoot().getAbsolutePath());
		SimulatorImpl.fromFile(resourceToPath("simulator_configs/open_while_signal.ini")).getMetrics().print(testOutputPath.resolve("statistics.csv").toString());
		Assert.assertEquals(59322, testOutputPath.resolve("statistics.csv").toFile().length());
		testOutputPath.resolve("statistics.csv").toFile().deleteOnExit();
	}

	@Test
	public void testFromConfigOutAlgos() throws Exception {
		final FromToPeriod period = TestMetricsHelper.getPeriod();
		final String config = "StockExecutions = Alg1\n" + "Alg1.loadLine = .Sma(n = 5, Input(e=close))";

		final TradeProcessorInit init = new TradeProcessorInit(stockStorageForAapl, period, config);
		final List<String> stockExecutions = init.generateOutForStocks();
		Assert.assertEquals(2, stockExecutions.size());
		Assert.assertEquals("Alg1", stockExecutions.get(1));
		final Simulator simulator = new SimulatorImpl();
		simulator.simulateMarketTrading(new SimulatorSettingsImpl(0, init));
		Assert.assertEquals(0.0, simulator.getMetrics().getDoubleMetric(MetricType.avGain), Settings.doubleEpsilon);
		final SignalsStorage ss = simulator.getSignalsStorage();
		final String en = AlgorithmNameGenerator.generateOutAlgorithmName("Alg1");
		Assert.assertEquals(2514, ss.getIndexSize("aapl", en));
	}

}
