package stsc.general.statistic;

/**
 * Metric Type
 */
public enum MetricType {
	avGain, // Average Gain
	period(Integer.class), // Amount of Days
	freq, // Amount of signals per Day.
	winProb, //
	avWin, //
	maxWin, //
	avLoss, //
	maxLoss, //
	avWinAvLoss, //
	kelly, //
	sharpeRatio, //
	startMonthAvGain, //
	startMonthStDevGain, //
	startMonthMax, //
	startMonthMin, //
	month12AvGain, //
	month12StDevGain, //
	month12Max, //
	month12Min, //
	ddDurationAvGain, //
	ddDurationMax, //
	ddValueAvGain, //
	ddValueMax, //
	; // TODO descriptions

	private final Class<?> metricType;

	private MetricType() {
		this.metricType = Double.class;
	}

	private MetricType(Class<?> classType) {
		this.metricType = classType;
	}

	public Class<?> getMetricType() {
		return metricType;
	}
}
