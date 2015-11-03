package stsc.general.statistic;

import java.util.ArrayList;

import org.joda.time.LocalDate;

import stsc.general.statistic.EquityCurve.Element;

final class MetricsBuilderCalculateMonthsStatistics {

	public MetricsBuilderCalculateMonthsStatistics(final MetricsBuilder metricsBuilder) {
		int index = 0;

		LocalDate indexDate = new LocalDate(metricsBuilder.equityCurve.get(index).date);
		LocalDate monthAgo = indexDate.plusMonths(1);

		double indexValue = metricsBuilder.equityCurve.get(index).value;

		double monthsCapitalsSum = 0.0;
		final ArrayList<Double> monthsDifferents = new ArrayList<>();

		final LocalDate endDate = new LocalDate(metricsBuilder.equityCurve.getLastElement().date);

		while (monthAgo.isBefore(endDate)) {
			index = metricsBuilder.equityCurve.find(monthAgo.toDate()) - 1;
			Element element = metricsBuilder.equityCurve.get(index);

			double lastValue = element.value;
			double differentForMonth = lastValue - indexValue;

			monthsDifferents.add(differentForMonth);
			monthsCapitalsSum += differentForMonth;

			indexValue = lastValue;
			monthAgo = monthAgo.plusMonths(1);
		}

		final int REASONABLE_AMOUNT_OF_DAYS = 13;
		if (metricsBuilder.equityCurve.size() - index >= REASONABLE_AMOUNT_OF_DAYS) {
			double lastValue = metricsBuilder.equityCurve.getLastElement().value;
			double differentForMonth = lastValue - indexValue;

			monthsDifferents.add(differentForMonth);
			monthsCapitalsSum += differentForMonth;
		}

		final double RISK_PERCENTS = 5.0;
		final double MONTHS_PER_YEAR = 12.0;
		final double sharpeAnnualReturn = (MONTHS_PER_YEAR / monthsDifferents.size()) * monthsCapitalsSum;
		final double sharpeStDev = Math.sqrt(MONTHS_PER_YEAR) * StatisticsProcessor.calculateStDev(monthsCapitalsSum, monthsDifferents);

		metricsBuilder.sharpeRatio = (sharpeAnnualReturn - RISK_PERCENTS) / sharpeStDev;
	}
}
