package stsc.general.statistic;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.Side;
import stsc.common.stocks.Stock;
import stsc.common.storage.StockStorage;
import stsc.general.trading.BrokerImpl;
import stsc.general.trading.TradingLog;
import stsc.storage.mocks.StockStorageMock;

public class StatisticsProcessorTest {

	private Day gd(Stock s, int i) {
		return s.getDays().get(i);
	}

	private Date gdd(Stock s, int i) {
		return gd(s, i).getDate();
	}

	@Test
	public void testStatistics() throws Exception {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
		final Stock aapl = stockStorage.getStock("aapl").get();
		final Stock adm = stockStorage.getStock("adm").get();

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		final TradingLog tradingLog = new BrokerImpl(stockStorage).getTradingLog();

		final StatisticsProcessor statistics = new StatisticsProcessor(tradingLog);

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));

		final int aaplLongSize = 100;
		final int admShortSize = 200;

		tradingLog.addBuyRecord(gdd(aapl, aaplIndex), "aapl", Side.LONG, aaplLongSize);
		tradingLog.addBuyRecord(gdd(adm, admIndex), "adm", Side.SHORT, admShortSize);

		statistics.processEod();

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));

		final double aaplLongIn = gd(aapl, aaplIndex).getPrices().getOpen();
		final double admShortIn = gd(adm, admIndex).getPrices().getOpen();

		tradingLog.addSellRecord(gdd(aapl, aaplIndex), "aapl", Side.LONG, aaplLongSize);
		tradingLog.addSellRecord(gdd(adm, admIndex), "adm", Side.SHORT, admShortSize);

		statistics.processEod();

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));

		final double aaplLongOut = gd(aapl, aaplIndex).getPrices().getOpen();
		final double admShortOut = gd(adm, admIndex).getPrices().getOpen();

		statistics.processEod();

		final Metrics metrics = statistics.calculate();

		final double aaplPriceDiff = aaplLongSize * aaplLongOut * (1.0 - statistics.getCommision()) - aaplLongIn * aaplLongSize
				* (1.0 + statistics.getCommision());
		final double admPriceDiff = admShortSize * admShortOut * (1.0 - statistics.getCommision()) - admShortSize * admShortIn
				* (1.0 + statistics.getCommision());

		Assert.assertEquals(3, metrics.getIntegerMetric(MetricType.period).intValue());
		Assert.assertEquals(aaplPriceDiff, metrics.getDoubleMetric(MetricType.maxWin), Settings.doubleEpsilon);
		Assert.assertEquals(admPriceDiff, metrics.getDoubleMetric(MetricType.maxLoss), Settings.doubleEpsilon);
		Assert.assertEquals(aaplPriceDiff - admPriceDiff, metrics.getEquityCurveInMoney().getLastElement().value, Settings.doubleEpsilon);
	}

	@Test
	public void testReverseStatistics() throws Exception {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
		final Stock aapl = stockStorage.getStock("aapl").get();
		final Stock adm = stockStorage.getStock("adm").get();

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		TradingLog tradingLog = new BrokerImpl(stockStorage).getTradingLog();

		StatisticsProcessor statistics = new StatisticsProcessor(tradingLog);

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));

		final int aaplLongSize = 100;
		final int admShortSize = 200;

		tradingLog.addBuyRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, aaplLongSize);
		tradingLog.addBuyRecord(gdd(adm, admIndex), "adm", Side.LONG, admShortSize);

		statistics.processEod();

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));

		final double aaplLongIn = gd(aapl, aaplIndex).getPrices().getOpen();
		final double admShortIn = gd(adm, admIndex).getPrices().getOpen();

		tradingLog.addSellRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, aaplLongSize);
		tradingLog.addSellRecord(gdd(adm, admIndex), "adm", Side.LONG, admShortSize);

		statistics.processEod();

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));

		final double aaplLongOut = gd(aapl, aaplIndex).getPrices().getOpen();
		final double admShortOut = gd(adm, admIndex).getPrices().getOpen();

		statistics.processEod();

		Metrics metrics = statistics.calculate();

		final double aaplPriceDiff = aaplLongSize * aaplLongOut * (1.0 - statistics.getCommision()) - aaplLongIn * aaplLongSize
				* (1.0 + statistics.getCommision());
		final double admPriceDiff = admShortSize * admShortOut * (1.0 - statistics.getCommision()) - admShortSize * admShortIn
				* (1.0 + statistics.getCommision());

		Assert.assertEquals(3.0, metrics.getMetric(MetricType.period), Settings.doubleEpsilon);
		Assert.assertEquals(aaplPriceDiff, metrics.getMetric(MetricType.maxLoss), Settings.doubleEpsilon);
		Assert.assertEquals(admPriceDiff, metrics.getMetric(MetricType.maxWin), Settings.doubleEpsilon);
		Assert.assertEquals(admPriceDiff - aaplPriceDiff, metrics.getEquityCurveInMoney().getLastElement().value, Settings.doubleEpsilon);
	}

	@Test
	public void testProbabilityStatistics() throws IOException {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
		final Stock aapl = stockStorage.getStock("aapl").get();
		final Stock adm = stockStorage.getStock("adm").get();
		final Stock spy = stockStorage.getStock("spy").get();

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int spyIndex = spy.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		final TradingLog tradingLog = new BrokerImpl(stockStorage).getTradingLog();

		final StatisticsProcessor statistics = new StatisticsProcessor(tradingLog);

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));
		statistics.setStockDay("spy", gd(spy, ++spyIndex));

		tradingLog.addBuyRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, 100);
		tradingLog.addBuyRecord(gdd(adm, admIndex), "adm", Side.LONG, 200);
		tradingLog.addBuyRecord(gdd(spy, spyIndex), "spy", Side.SHORT, 30);

		statistics.processEod();

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));

		final double aaplShortIn1 = gd(aapl, aaplIndex).getPrices().getOpen();
		final double admLongIn1 = gd(adm, admIndex).getPrices().getOpen();
		final double spyShortIn = gd(spy, spyIndex).getPrices().getOpen();

		spyIndex++;

		tradingLog.addBuyRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, 100);
		tradingLog.addBuyRecord(gdd(adm, admIndex), "adm", Side.LONG, 500);

		statistics.processEod();

		statistics.setStockDay("aapl", gd(aapl, ++aaplIndex));
		statistics.setStockDay("adm", gd(adm, ++admIndex));
		statistics.setStockDay("spy", gd(spy, ++spyIndex));

		statistics.processEod();

		final double aaplShortIn2 = gd(aapl, aaplIndex).getPrices().getOpen();
		final double admLongIn2 = gd(adm, admIndex).getPrices().getOpen();

		tradingLog.addSellRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, 200);
		tradingLog.addSellRecord(gdd(adm, admIndex), "adm", Side.LONG, 700);
		tradingLog.addSellRecord(gdd(spy, spyIndex), "spy", Side.SHORT, 30);

		statistics.processEod();

		final double aaplShortOut = gd(aapl, aaplIndex).getPrices().getOpen();
		final double admLongOut = gd(adm, admIndex).getPrices().getOpen();
		final double spyShortOut = gd(spy, spyIndex).getPrices().getOpen();

		final Metrics metrics = statistics.calculate();
		final double c = statistics.getCommision();

		final double aaplDiff = -aaplShortOut * 200 * (1 - c) + (aaplShortIn2 + aaplShortIn1) * 100 * (1 + c);
		final double admDiff = admLongOut * 700 * (1 - c) - admLongIn1 * 200 * (1 + c) - admLongIn2 * 500 * (1 + c);
		final double spyDiff = -spyShortOut * 30 * (1 - c) + spyShortIn * 30 * (1 + c);

		final double lastResult = aaplDiff + admDiff + spyDiff;
		Assert.assertEquals(4.0, metrics.getMetric(MetricType.period), Settings.doubleEpsilon);
		Assert.assertEquals(lastResult, metrics.getEquityCurveInMoney().getLastElement().value, Settings.doubleEpsilon);
		Assert.assertEquals(0.042828, metrics.getMetric(MetricType.avGain), Settings.doubleEpsilon);

		Assert.assertEquals(0.75, metrics.getMetric(MetricType.freq), Settings.doubleEpsilon);
		Assert.assertEquals(0.666666, metrics.getMetric(MetricType.winProb), Settings.doubleEpsilon);

		Assert.assertEquals(54.473079, metrics.getMetric(MetricType.avWin), Settings.doubleEpsilon);
		Assert.assertEquals(53.263523, metrics.getMetric(MetricType.avLoss), Settings.doubleEpsilon);

		Assert.assertEquals(81.913320, aaplDiff, Settings.doubleEpsilon);
		Assert.assertEquals(-spyDiff, metrics.getMetric(MetricType.maxLoss), Settings.doubleEpsilon);

		Assert.assertEquals(1.022708, metrics.getMetric(MetricType.avWinAvLoss), Settings.doubleEpsilon);
		Assert.assertEquals(0.340734, metrics.getMetric(MetricType.kelly), Settings.doubleEpsilon);
	}

	@Test
	public void testEquityCurveOn518DaysStatistics() throws IOException {
		final Metrics stats = testTradingHelper(518, true);

		Assert.assertEquals(-16.710421, stats.getMetric(MetricType.avGain), Settings.doubleEpsilon);
		Assert.assertEquals(0.301158, stats.getMetric(MetricType.freq), Settings.doubleEpsilon);

		Assert.assertEquals(430.313881, stats.getMetric(MetricType.avWin), Settings.doubleEpsilon);
		Assert.assertEquals(-0.091019, stats.getMetric(MetricType.kelly), Settings.doubleEpsilon);

		Assert.assertEquals(-0.765763, stats.getMetric(MetricType.sharpeRatio), Settings.doubleEpsilon);

		Assert.assertEquals(-0.668416, stats.getMetric(MetricType.startMonthAvGain), Settings.doubleEpsilon);
		Assert.assertEquals(4.930223, stats.getMetric(MetricType.startMonthStDevGain), Settings.doubleEpsilon);
		Assert.assertEquals(15.162020, stats.getMetric(MetricType.startMonthMax), Settings.doubleEpsilon);
		Assert.assertEquals(-7.577820, stats.getMetric(MetricType.startMonthMin), Settings.doubleEpsilon);

		Assert.assertEquals(-21.378450, stats.getMetric(MetricType.month12AvGain), Settings.doubleEpsilon);
		Assert.assertEquals(9.683782, stats.getMetric(MetricType.month12StDevGain), Settings.doubleEpsilon);
		Assert.assertEquals(0.0, stats.getMetric(MetricType.month12Max), Settings.doubleEpsilon);
		Assert.assertEquals(-34.239761, stats.getMetric(MetricType.month12Min), Settings.doubleEpsilon);

		Assert.assertEquals(145, stats.getMetric(MetricType.ddDurationAvGain), Settings.doubleEpsilon);
		Assert.assertEquals(675.0, stats.getMetric(MetricType.ddDurationMax), Settings.doubleEpsilon);
		Assert.assertEquals(12.568085, stats.getMetric(MetricType.ddValueAvGain), Settings.doubleEpsilon);
		Assert.assertEquals(48.826069, stats.getMetric(MetricType.ddValueMax), Settings.doubleEpsilon);
	}

	@Test
	public void testEquityCurveOn251DaysStatistics() throws IOException {
		final Metrics stats = testTradingHelper(251, true);

		Assert.assertEquals(-9.350847, stats.getMetric(MetricType.avGain), Settings.doubleEpsilon);
		Assert.assertEquals(0.298804, stats.getMetric(MetricType.freq), Settings.doubleEpsilon);

		Assert.assertEquals(499.964045, stats.getMetric(MetricType.avWin), Settings.doubleEpsilon);
		Assert.assertEquals(-0.077171, stats.getMetric(MetricType.kelly), Settings.doubleEpsilon);

		Assert.assertEquals(-0.611352, stats.getMetric(MetricType.sharpeRatio), Settings.doubleEpsilon);

		Assert.assertEquals(-0.779237, stats.getMetric(MetricType.startMonthAvGain), Settings.doubleEpsilon);
		Assert.assertEquals(6.554998, stats.getMetric(MetricType.startMonthStDevGain), Settings.doubleEpsilon);
		Assert.assertEquals(17.914611, stats.getMetric(MetricType.startMonthMax), Settings.doubleEpsilon);
		Assert.assertEquals(-6.845838, stats.getMetric(MetricType.startMonthMin), Settings.doubleEpsilon);

		Assert.assertEquals(-9.350847, stats.getMetric(MetricType.month12AvGain), Settings.doubleEpsilon);
		Assert.assertEquals(0.0, stats.getMetric(MetricType.month12StDevGain), Settings.doubleEpsilon);
		Assert.assertEquals(0.0, stats.getMetric(MetricType.month12Max), Settings.doubleEpsilon);
		Assert.assertEquals(-9.350847, stats.getMetric(MetricType.month12Min), Settings.doubleEpsilon);

		Assert.assertEquals(67.2, stats.getMetric(MetricType.ddDurationAvGain), Settings.doubleEpsilon);
		Assert.assertEquals(286.0, stats.getMetric(MetricType.ddDurationMax), Settings.doubleEpsilon);
		Assert.assertEquals(10.581057, stats.getMetric(MetricType.ddValueAvGain), Settings.doubleEpsilon);
		Assert.assertEquals(36.346692, stats.getMetric(MetricType.ddValueMax), Settings.doubleEpsilon);
	}

	@Test
	public void testStatisticsOnLastClose() throws IOException, IllegalArgumentException, IllegalAccessException {
		final Metrics stats = testTradingHelper(3, false);
		stats.print("./test/out.csv");

		Assert.assertEquals(0.0, stats.getMetric(MetricType.ddValueMax), Settings.doubleEpsilon);
		final File file = new File("./test/out.csv");
		Assert.assertTrue(file.exists());
		Assert.assertEquals(456.0, file.length(), Settings.doubleEpsilon);
		file.delete();
	}

	private Metrics testTradingHelper(int daysCount, boolean closeOnExit) throws IOException {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
		final Stock aapl = stockStorage.getStock("aapl").get();
		final Stock adm = stockStorage.getStock("adm").get();
		final Stock spy = stockStorage.getStock("spy").get();

		int aaplIndex = aapl.findDayIndex(new LocalDate(2008, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2008, 9, 4).toDate());
		int spyIndex = spy.findDayIndex(new LocalDate(2008, 9, 4).toDate());

		TradingLog tradingLog = new BrokerImpl(stockStorage).getTradingLog();

		StatisticsProcessor statisticsProcessor = new StatisticsProcessor(tradingLog);

		final int buySellEach = 5;
		boolean opened = false;

		for (int i = 0; i < daysCount; ++i) {

			statisticsProcessor.setStockDay("aapl", gd(aapl, ++aaplIndex));
			statisticsProcessor.setStockDay("adm", gd(adm, ++admIndex));
			statisticsProcessor.setStockDay("spy", gd(spy, ++spyIndex));

			if (i % buySellEach == 0 && i % (buySellEach * 2) == 0) {
				tradingLog.addBuyRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, 100);
				tradingLog.addBuyRecord(gdd(adm, admIndex), "adm", Side.LONG, 200);
				tradingLog.addBuyRecord(gdd(spy, spyIndex), "spy", Side.SHORT, 100);
				opened = true;
			}
			if (i % buySellEach == 0 && i % (buySellEach * 2) != 0) {
				tradingLog.addSellRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, 100);
				tradingLog.addSellRecord(gdd(adm, admIndex), "adm", Side.LONG, 200);
				tradingLog.addSellRecord(gdd(spy, spyIndex), "spy", Side.SHORT, 100);
				opened = false;
			}

			if ((i == (daysCount - 1)) && opened && closeOnExit) {
				tradingLog.addSellRecord(gdd(aapl, aaplIndex), "aapl", Side.SHORT, 100);
				tradingLog.addSellRecord(gdd(adm, admIndex), "adm", Side.LONG, 200);
				tradingLog.addSellRecord(gdd(spy, spyIndex), "spy", Side.SHORT, 100);
				opened = false;
			}

			statisticsProcessor.processEod();
		}

		Metrics metrics = statisticsProcessor.calculate();
		Assert.assertEquals(daysCount, metrics.getIntegerMetric(MetricType.period).intValue());

		return metrics;
	}
}
