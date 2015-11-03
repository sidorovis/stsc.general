package stsc.general.statistic;

import java.util.ArrayList;

import org.joda.time.LocalDate;

final class MetricsBuilderPeriodCalculator {

	private final MetricsBuilder metricsBuilder;

	private double sumOfStartMonths = 0.0;

	private final ArrayList<Double> elementsInStartMonths = new ArrayList<>();
	private final ArrayList<Integer> startMonthsIndexes = new ArrayList<>();

	public MetricsBuilderPeriodCalculator(final MetricsBuilder metricsBuilder) {
		this.metricsBuilder = metricsBuilder;
	}

	public void collectAndCalculateMonthsAnd12MonthsStatistics() {
		collectElementsInStartMonths();
		calculateStartMonthsStatistics();
		calculate12MonthsStatistics();
	}

	private void collectElementsInStartMonths() {
		LocalDate nextMonthBegin = new LocalDate(metricsBuilder.equityCurve.get(0).date).plusMonths(1).withDayOfMonth(1);
		final int firstMonthIndex = metricsBuilder.equityCurve.find(nextMonthBegin.toDate());

		final int REASONABLE_AMOUNT_OF_DAYS = 15;
		if (firstMonthIndex >= REASONABLE_AMOUNT_OF_DAYS) {
			startMonthsIndexes.add(0);
		}

		final LocalDate endDate = new LocalDate(metricsBuilder.equityCurve.getLastElement().date);

		int nextIndex = metricsBuilder.equityCurve.size();
		while (nextMonthBegin.isBefore(endDate)) {
			nextIndex = metricsBuilder.equityCurve.find(nextMonthBegin.toDate());
			startMonthsIndexes.add(nextIndex);
			nextMonthBegin = nextMonthBegin.plusMonths(1);
		}
		if (metricsBuilder.equityCurve.size() - nextIndex >= REASONABLE_AMOUNT_OF_DAYS) {
			startMonthsIndexes.add(metricsBuilder.equityCurve.size() - 1);
		}

	}

	private void calculateStartMonthsStatistics() {
		final int startMonthsIndexesSize = startMonthsIndexes.size();

		double lastValue = metricsBuilder.equityCurve.get(0).value;
		for (int i = 1; i < startMonthsIndexesSize; ++i) {
			double nextValue = metricsBuilder.equityCurve.get(startMonthsIndexes.get(i)).value;
			double differentForMonth = nextValue - lastValue;
			processMonthInStartMonths(differentForMonth);
			lastValue = nextValue;
		}
		metricsBuilder.startMonthAvGain = sumOfStartMonths / elementsInStartMonths.size();
		metricsBuilder.startMonthStDevGain = StatisticsProcessor.calculateStDev(sumOfStartMonths, elementsInStartMonths);
	}

	private void calculate12MonthsStatistics() {
		final int MONTHS_PER_YEAR = 12;
		final int startMonthsIndexesSize = startMonthsIndexes.size() - MONTHS_PER_YEAR;

		ArrayList<Double> rollingWindow12Month = new ArrayList<>();
		double rollingWindow12MonthSum = 0.0;

		for (int i = 0; i < startMonthsIndexesSize; ++i) {
			final double beginPeriodValue = metricsBuilder.equityCurve.get(startMonthsIndexes.get(i)).value;
			final double endPeriodValue = metricsBuilder.equityCurve.get(startMonthsIndexes.get(i + MONTHS_PER_YEAR)).value;
			final double diff = endPeriodValue - beginPeriodValue;
			rollingWindow12Month.add(diff);
			rollingWindow12MonthSum += diff;
			if (diff > metricsBuilder.month12Max)
				metricsBuilder.month12Max = diff;
			if (diff < metricsBuilder.month12Min)
				metricsBuilder.month12Min = diff;
		}
		metricsBuilder.month12AvGain = rollingWindow12MonthSum / rollingWindow12Month.size();
		metricsBuilder.month12StDevGain = StatisticsProcessor.calculateStDev(rollingWindow12MonthSum, rollingWindow12Month);
	}

	private void processMonthInStartMonths(double moneyDiff) {
		elementsInStartMonths.add(moneyDiff);
		sumOfStartMonths += moneyDiff;
		if (moneyDiff > metricsBuilder.startMonthMax)
			metricsBuilder.startMonthMax = moneyDiff;
		if (moneyDiff < metricsBuilder.startMonthMin)
			metricsBuilder.startMonthMin = moneyDiff;
	}

}
