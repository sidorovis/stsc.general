package stsc.general.trading;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Optional;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.primitive.eod.TestingEodAlgorithm;
import stsc.algorithms.primitive.eod.TestingEodAlgorithmSignal;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.EodExecutionInstance;
import stsc.common.algorithms.MutableAlgorithmConfiguration;
import stsc.common.signals.SerieSignal;
import stsc.common.stocks.united.format.UnitedFormatStock;
import stsc.common.storage.SignalsStorage;
import stsc.common.storage.StockStorage;
import stsc.general.algorithm.AlgorithmConfigurationImpl;
import stsc.storage.ExecutionInstanceProcessor;
import stsc.storage.ExecutionInstancesStorage;
import stsc.storage.ThreadSafeStockStorage;
import stsc.storage.mocks.StockStorageMock;

public final class TradeProcessorTest {

	final private File resourceToPath(final String resourcePath) throws URISyntaxException {
		return new File(BrokerTest.class.getResource(resourcePath).toURI());
	}

	private void csvReaderHelper(final ThreadSafeStockStorage ss, final String stockName) throws IOException, ParseException, URISyntaxException {
		ss.updateStock(UnitedFormatStock.readFromCsvFile(stockName, resourceToPath("trade_processor_tests").toPath().resolve(stockName + ".csv").toString()));
	}

	@Test
	public void testTradeProcessor() throws Exception {
		final ThreadSafeStockStorage ss = new ThreadSafeStockStorage();

		csvReaderHelper(ss, "aapl");
		csvReaderHelper(ss, "gfi");
		csvReaderHelper(ss, "oldstock");
		csvReaderHelper(ss, "no30");

		final FromToPeriod period = new FromToPeriod("30-10-2013", "06-11-2013");
		final AlgorithmConfigurationImpl algoSettings = new AlgorithmConfigurationImpl();
		algoSettings.setInteger("size", 10000);

		final ExecutionInstancesStorage executionsStorage = new ExecutionInstancesStorage();
		executionsStorage.addEodExecution(new EodExecutionInstance("e1", TestingEodAlgorithm.class.getName(), algoSettings));

		final TradeProcessorInit settings = new TradeProcessorInit(ss, period, executionsStorage);

		final TradeProcessor tradeProcessor = new TradeProcessor(settings);
		tradeProcessor.simulate(period, Optional.empty());

		final ExecutionInstanceProcessor es = tradeProcessor.getExecutionStorage();
		Assert.assertEquals(1, es.getEodAlgorithmsSize());

		final TestingEodAlgorithm ta = (TestingEodAlgorithm) es.getEodAlgorithm("e1");
		Assert.assertEquals(6, ta.datafeeds.size());

		int[] expectedDatafeedSizes = { 1, 1, 2, 2, 3, 2 };

		for (int i = 0; i < expectedDatafeedSizes.length; ++i)
			Assert.assertEquals(expectedDatafeedSizes[i], ta.datafeeds.get(i).size());

		final SignalsStorage signalsStorage = tradeProcessor.getExecutionStorage().getSignalsStorage();
		final SerieSignal e1s1 = signalsStorage.getEodSignal("e1", new LocalDate(2013, 10, 30).toDate()).getContent(SerieSignal.class);
		Assert.assertTrue(e1s1.getClass() == TestingEodAlgorithmSignal.class);
		Assert.assertEquals("2013-10-30", ((TestingEodAlgorithmSignal) e1s1).dateRepresentation);

		final SerieSignal e1s2 = signalsStorage.getEodSignal("e1", new LocalDate(2013, 10, 31).toDate()).getContent(TestingEodAlgorithmSignal.class);
		Assert.assertTrue(e1s2.getClass() == TestingEodAlgorithmSignal.class);
		Assert.assertEquals("2013-10-31", ((TestingEodAlgorithmSignal) e1s2).dateRepresentation);

		final SerieSignal e1s3 = signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 01).toDate()).getContent(TestingEodAlgorithmSignal.class);
		Assert.assertTrue(e1s3.getClass() == TestingEodAlgorithmSignal.class);
		Assert.assertEquals("2013-11-01", ((TestingEodAlgorithmSignal) e1s3).dateRepresentation);

		final SerieSignal e1s6 = signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 04).toDate()).getContent(SerieSignal.class);
		Assert.assertTrue(e1s6.getClass() == TestingEodAlgorithmSignal.class);
		Assert.assertEquals("2013-11-04", ((TestingEodAlgorithmSignal) e1s6).dateRepresentation);

		Assert.assertFalse(signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 6).toDate()).isPresent());
		Assert.assertFalse(signalsStorage.getEodSignal("e2", new LocalDate(2013, 11, 3).toDate()).isPresent());
		Assert.assertFalse(signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 29).toDate()).isPresent());
	}

	@Test
	public void testTradeProcessorWithStatistics() throws Exception {
		final StockStorage ss = StockStorageMock.getStockStorage();
		final FromToPeriod period = new FromToPeriod("02-09-2013", "06-11-2013");
		final ExecutionInstancesStorage executionsStorage = new ExecutionInstancesStorage();
		final MutableAlgorithmConfiguration algoSettings = new AlgorithmConfigurationImpl();

		executionsStorage.addEodExecution(new EodExecutionInstance("e1", TestingEodAlgorithm.class.getName(), algoSettings));
		final TradeProcessorInit init = new TradeProcessorInit(ss, period, executionsStorage);
		final TradeProcessor marketSimulator = new TradeProcessor(init);
		marketSimulator.simulate(period, Optional.empty());
	}
}
