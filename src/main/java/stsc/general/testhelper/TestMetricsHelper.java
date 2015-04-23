package stsc.general.testhelper;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.LocalDate;

import stsc.common.Day;
import stsc.common.FromToPeriod;
import stsc.common.Side;
import stsc.common.stocks.Stock;
import stsc.general.statistic.Metrics;
import stsc.general.statistic.StatisticsProcessor;
import stsc.general.trading.BrokerImpl;
import stsc.general.trading.TradingLog;
import stsc.storage.mocks.StockStorageMock;

public final class TestMetricsHelper {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static FromToPeriod getPeriod() {
		try {
			return new FromToPeriod("01-01-2000", "31-12-2009");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static Metrics getMetrics() throws ParseException {
		return getMetrics(100, 200);
	}

	public static Metrics getMetrics(int applSize, int admSize) throws ParseException {
		return getMetrics(applSize, admSize, Day.createDate("04-09-2013"), false);
	}

	public static Metrics getMetrics(int applSize, int admSize, LocalDate date) {
		return getMetrics(applSize, admSize, date, false);
	}

	public static Metrics getMetrics(int applSize, int admSize, LocalDate date, boolean debug) {
		return getMetrics(applSize, admSize, date.toDate(), debug);
	}

	public static Metrics getMetrics(int applSize, int admSize, Date date) {
		return getMetrics(applSize, admSize, date, false);
	}

	public static Metrics getMetrics(int applSize, int admSize, Date date, boolean debug) {
		Metrics metrics = null;
		try {
			Stock aapl = StockStorageMock.getStockStorage().getStock("aapl").get();
			Stock adm = StockStorageMock.getStockStorage().getStock("adm").get();

			int aaplIndex = aapl.findDayIndex(date);
			int admIndex = adm.findDayIndex(date);

			TradingLog tradingLog = new BrokerImpl(StockStorageMock.getStockStorage()).getTradingLog();
			StatisticsProcessor metricProcessor = new StatisticsProcessor(tradingLog);

			metricProcessor.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
			metricProcessor.setStockDay("adm", adm.getDays().get(admIndex++));
			tradingLog.addBuyRecord(Day.createDate(), "aapl", Side.LONG, applSize);
			tradingLog.addBuyRecord(Day.createDate(), "adm", Side.SHORT, admSize);

			metricProcessor.processEod();

			metricProcessor.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
			metricProcessor.setStockDay("adm", adm.getDays().get(admIndex++));
			tradingLog.addSellRecord(Day.createDate(), "aapl", Side.LONG, applSize);
			tradingLog.addSellRecord(Day.createDate(), "adm", Side.SHORT, admSize);

			metricProcessor.processEod();
			metrics = metricProcessor.calculate();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return metrics;
	}

}
