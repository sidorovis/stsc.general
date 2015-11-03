package stsc.general.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import stsc.common.Day;
import stsc.common.Settings;
import stsc.general.statistic.EquityCurve.Element;
import stsc.general.trading.TradingLog;
import stsc.general.trading.TradingRecord;

import com.google.common.math.DoubleMath;

final class EquityProcessor {

	final static double PERCENTS = 100.0;

	private final double commision;

	private Date lastDate;
	final private HashMap<String, Double> lastPrice = new HashMap<>();
	final private ArrayList<TradingRecord> tradingRecords;
	private int tradingRecordsIndex = 0;

	private double spentLongCash = 0;
	private double spentShortCash = 0;

	final private PositionCollection longPositions;
	final private PositionCollection shortPositions;

	private double maximumSpentMoney = 0.0;

	private final MetricsBuilder builder = Metrics.getBuilder();

	EquityProcessor(StatisticsProcessor statisticsProcessor, TradingLog tradingLog) {
		this.commision = statisticsProcessor.getCommision();
		this.longPositions = new PositionCollection(statisticsProcessor);
		this.shortPositions = new PositionCollection(statisticsProcessor);
		this.tradingRecords = tradingLog.getRecords();
	}

	void setStockDay(String stockName, Day stockDay) {
		lastDate = stockDay.date;
		lastPrice.put(stockName, stockDay.getPrices().getOpen());
	}

	double processEod(boolean debug) { // TODO cleanup this parameter
		tradingRecordsIndex = processLastSignals(tradingRecords.size());

		calculateMaximumSpentMoney();
		final double dayResult = calculateDayCash();
		builder.equityCurve.add(lastDate, dayResult);
		return dayResult;
	}

	private int processLastSignals(final int tradingRecordSize) {
		for (int i = tradingRecordsIndex; i < tradingRecordSize; ++i) {
			final TradingRecord record = tradingRecords.get(i);
			if (record.getDate().equals(lastDate)) {
				return i;
			}
			if (record.isPurchase()) {
				processBuying(record);
			} else {
				processSelling(record);
			}
		}
		return tradingRecordSize;
	}

	private void processBuying(final TradingRecord record) {
		final String stockName = record.getStockName();
		final double price = lastPrice.get(stockName);
		final int shares = record.getAmount();
		final double sharesPrice = shares * price * (1.0 + commision);
		if (record.isLong()) {
			spentLongCash += sharesPrice;
			longPositions.increment(stockName, shares, sharesPrice);
		} else {
			spentShortCash += sharesPrice;
			shortPositions.increment(stockName, shares, sharesPrice);
		}
	}

	private void processSelling(final TradingRecord record) {
		final String stockName = record.getStockName();
		final double price = lastPrice.get(stockName);
		final int shares = record.getAmount();
		final double sharesPrice = shares * price * (1.0 - commision);
		if (record.isLong()) {
			processSellingLong(stockName, shares, price, sharesPrice);
		} else {
			processSellingShort(stockName, shares, price, sharesPrice);
		}
	}

	private void processSellingLong(String stockName, int shares, double price, double sharesPrice) {
		final double oldPrice = longPositions.sharePrice(stockName);
		longPositions.decrement(stockName, shares, sharesPrice);
		final double priceDiff = sharesPrice - shares * oldPrice;
		spentLongCash -= sharesPrice;
		addPositionClose(priceDiff);
	}

	private void processSellingShort(String stockName, int shares, double price, double sharesPrice) {
		final double oldPrice = shortPositions.sharePrice(stockName);
		shortPositions.decrement(stockName, shares, sharesPrice);
		final double priceDiff = shares * oldPrice - sharesPrice;
		spentShortCash -= sharesPrice;
		addPositionClose(priceDiff);
	}

	private double calculateDayCash() {
		double moneyInLongs = longPositions.cost(lastPrice);
		double moneyInShorts = shortPositions.cost(lastPrice);
		return spentShortCash - spentLongCash + moneyInLongs - moneyInShorts;
	}

	private void calculateMaximumSpentMoney() {
		double spentCache = spentShortCash + spentLongCash;
		if (maximumSpentMoney < spentCache)
			maximumSpentMoney = spentCache;
	}

	private void addPositionClose(double moneyDiff) {
		if (moneyDiff >= 0)
			addWin(moneyDiff);
		else
			addLoss(moneyDiff);
	}

	private void addWin(double moneyDiff) {
		builder.count += 1;
		builder.winCount += 1;
		builder.winSum += moneyDiff;
		if (moneyDiff > builder.maxWin)
			builder.maxWin = moneyDiff;
	}

	private void addLoss(double moneyDiff) {
		builder.count += 1;
		builder.lossCount += 1;
		builder.lossSum += moneyDiff;
		if (moneyDiff < builder.maxLoss)
			builder.maxLoss = moneyDiff;
	}

	public Metrics calculate() {
		builder.period = builder.equityCurve.size();
		closeAllPositions();
		builder.copyMoneyEquityCurve();

		if (DoubleMath.fuzzyEquals(maximumSpentMoney, 0.0, Settings.doubleEpsilon))
			return new Metrics(builder);

		builder.setMaximumSpentMoney(maximumSpentMoney);
		maximumSpentMoney /= PERCENTS;
		builder.equityCurve.recalculateWithMax(maximumSpentMoney);

		calculateEquityStatistics();
		return new Metrics(builder);
	}

	private void closeAllPositions() {
		final int MINIMAL_DAY_IN_PERIOD = 2;
		if (builder.period > MINIMAL_DAY_IN_PERIOD && (longPositions.size() > 0 || shortPositions.size() > 0)) {
			while (longPositions.size() > 0) {
				final String stockName = longPositions.positions.keySet().iterator().next();
				final PositionCollection.Position p = longPositions.positions.get(stockName);

				double price = lastPrice.get(stockName);
				int shares = p.shares;
				double sharesPrice = shares * price * (1 - commision);

				processSellingLong(stockName, shares, price, sharesPrice);
			}
			while (shortPositions.size() > 0) {
				final String stockName = shortPositions.positions.keySet().iterator().next();
				final PositionCollection.Position p = shortPositions.positions.get(stockName);

				double price = lastPrice.get(stockName);
				int shares = p.shares;
				double sharesPrice = shares * price * (1 - commision);

				processSellingShort(stockName, shares, price, sharesPrice);
			}
			final double cashSum = spentShortCash - spentLongCash;
			if (maximumSpentMoney < cashSum)
				maximumSpentMoney = cashSum;
			builder.equityCurve.setLast(cashSum);
		}
	}

	private void calculateEquityStatistics() {
		final int DAYS_PER_YEAR = 250;
		if (builder.period > DAYS_PER_YEAR) {
			new MetricsBuilderCalculateMonthsStatistics(builder);
			new MetricsBuilderPeriodCalculator(builder). //
					collectAndCalculateMonthsAnd12MonthsStatistics();
		}

		calculateDrawDownStatistics();
	}

	private void calculateDrawDownStatistics() {
		final MetricsBuilder init = builder;
		final int equityCurveSize = init.equityCurve.size();

		Element ddStart = init.equityCurve.get(0);
		boolean inDrawdown = false;
		double ddSize = 0.0;
		double lastValue = ddStart.value;

		int ddCount = 0;
		double ddDurationSum = 0.0;
		double ddValueSum = 0.0;

		for (int i = 1; i < equityCurveSize; ++i) {
			Element currentElement = init.equityCurve.get(i);
			if (!inDrawdown) {
				if (currentElement.value >= lastValue)
					ddStart = currentElement;
				else {
					inDrawdown = true;
					ddSize = ddStart.value - currentElement.value;
				}
			} else {
				if (currentElement.value > lastValue) {
					if (currentElement.value >= ddStart.value) {
						final int ddLength = Days.daysBetween(new LocalDate(ddStart.date), new LocalDate(currentElement.date)).getDays();

						ddCount += 1;
						ddDurationSum += ddLength;
						ddValueSum += ddSize;

						checkDdLengthSizeOnMax(ddSize, ddLength);

						inDrawdown = false;
						ddStart = currentElement;
						ddSize = 0.0;
					}
				} else {
					final double currentDdSize = ddStart.value - currentElement.value;
					if (ddSize < currentDdSize)
						ddSize = currentDdSize;
				}
			}
			lastValue = currentElement.value;
		}
		if (inDrawdown) {
			final int ddLength = Days.daysBetween(new LocalDate(ddStart.date), new LocalDate(init.equityCurve.getLastElement().date)).getDays();
			ddCount += 1;
			ddValueSum += ddSize;
			ddDurationSum += ddLength;

			checkDdLengthSizeOnMax(ddSize, ddLength);
		}

		if (ddCount != 0) {
			init.ddDurationAvGain = ddDurationSum / ddCount;
			init.ddValueAvGain = ddValueSum / ddCount;
		}
	}

	private void checkDdLengthSizeOnMax(double ddSize, int ddLength) {
		if (ddSize > builder.ddValueMax)
			builder.ddValueMax = ddSize;
		if (ddLength > builder.ddDurationMax)
			builder.ddDurationMax = ddLength;
	}

}