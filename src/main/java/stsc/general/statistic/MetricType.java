package stsc.general.statistic;

/**
 * Metric Type.
 * 
 * @Remark: equity curve is a value of money during the trading
 */
public enum MetricType {

	avGain, // Average Gain, last value of equity curve*.
	period(Integer.class), // Amount of time units.
	freq, // Amount of signals per time unit.
	winProb, // Percentage of signals that were closed with positive value of trading.
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
	maxSpentMoney, // Maximum spent money during trading.
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
