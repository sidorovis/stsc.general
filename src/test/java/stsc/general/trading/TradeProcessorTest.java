package stsc.general.trading;

import java.io.IOException;
import java.text.ParseException;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.eod.primitive.TestingEodAlgorithm;
import stsc.algorithms.eod.primitive.TestingEodAlgorithmSignal;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.AlgorithmSettings;
import stsc.common.algorithms.EodExecution;
import stsc.common.signals.SerieSignal;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.SignalsStorage;
import stsc.common.storage.StockStorage;
import stsc.storage.ExecutionStarter;
import stsc.storage.ExecutionsStorage;
import stsc.storage.StockStorageFactory;
import stsc.storage.ThreadSafeStockStorage;

import com.google.common.collect.Sets;

public final class TradeProcessorTest {

	private void csvReaderHelper(StockStorage ss, String stockName) throws IOException, ParseException {
		final String stocksFilePath = "./test_data/trade_processor_tests/";
		ss.updateStock(UnitedFormatStock.readFromCsvFile(stockName, stocksFilePath + stockName + ".csv"));
	}

	@Test
	public void testTradeProcessor() throws Exception {
		final StockStorage ss = new ThreadSafeStockStorage();

		csvReaderHelper(ss, "aapl");
		csvReaderHelper(ss, "gfi");
		csvReaderHelper(ss, "oldstock");
		csvReaderHelper(ss, "no30");

		final FromToPeriod period = new FromToPeriod("30-10-2013", "06-11-2013");
		final AlgorithmSettingsImpl algoSettings = new AlgorithmSettingsImpl(period);
		algoSettings.setInteger("size", 10000);

		final ExecutionsStorage executionsStorage = new ExecutionsStorage();
		executionsStorage.addEodExecution(new EodExecution("e1", TestingEodAlgorithm.class.getName(), algoSettings));

		final TradeProcessorInit settings = new TradeProcessorInit(ss, period, executionsStorage);

		final TradeProcessor tradeProcessor = new TradeProcessor(settings);
		tradeProcessor.simulate(period);

		final ExecutionStarter es = tradeProcessor.getExecutionStorage();
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

		final SerieSignal e1s2 = signalsStorage.getEodSignal("e1", new LocalDate(2013, 10, 31).toDate()).getContent(
				TestingEodAlgorithmSignal.class);
		Assert.assertTrue(e1s2.getClass() == TestingEodAlgorithmSignal.class);
		Assert.assertEquals("2013-10-31", ((TestingEodAlgorithmSignal) e1s2).dateRepresentation);

		final SerieSignal e1s3 = signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 01).toDate()).getContent(
				TestingEodAlgorithmSignal.class);
		Assert.assertTrue(e1s3.getClass() == TestingEodAlgorithmSignal.class);
		Assert.assertEquals("2013-11-01", ((TestingEodAlgorithmSignal) e1s3).dateRepresentation);

		final SerieSignal e1s6 = signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 04).toDate()).getContent(SerieSignal.class);
		Assert.assertTrue(e1s6.getClass() == TestingEodAlgorithmSignal.class);
		Assert.assertEquals("2013-11-04", ((TestingEodAlgorithmSignal) e1s6).dateRepresentation);

		Assert.assertNull(signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 6).toDate()));
		Assert.assertFalse(signalsStorage.getEodSignal("e2", new LocalDate(2013, 11, 3).toDate()).isPresent());
		Assert.assertNull(signalsStorage.getEodSignal("e1", new LocalDate(2013, 11, 29).toDate()));
	}

	@Test
	public void testTradeProcessorWithStatistics() throws Exception {
		final StockStorage ss = StockStorageFactory.createStockStorage(Sets.newHashSet(new String[] { "aapl", "adm", "spy" }),
				"./test_data/");
		final FromToPeriod period = new FromToPeriod("02-09-2013", "06-11-2013");
		final ExecutionsStorage executionsStorage = new ExecutionsStorage();
		final AlgorithmSettings algoSettings = new AlgorithmSettingsImpl(period);

		executionsStorage.addEodExecution(new EodExecution("e1", TestingEodAlgorithm.class.getName(), algoSettings));
		final TradeProcessorInit init = new TradeProcessorInit(ss, period, executionsStorage);
		final TradeProcessor marketSimulator = new TradeProcessor(init);
		marketSimulator.simulate(period);
	}
}
