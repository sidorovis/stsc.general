package stsc.general.statistic;

/**
 * Metric Type.
 * 
 * @Remark: equity curve is a value of money during the trading process.
 */
public enum MetricType {

	/**
	 * Average Gain, last value of equity curve.
	 */
	avGain,
	/**
	 * Amount of time units (days / minutes).
	 */
	period(Integer.class),
	/**
	 * Amount of signals per time unit.
	 */
	freq,
	/**
	 * Percentage of signals that were closed with positive value of trading.
	 */
	winProb,
	/**
	 * Average money value from all positions that was closed with win.
	 */
	avWin,
	/**
	 * Maximum money value from all positions that was closed with win.
	 */
	maxWin,
	/**
	 * Average money value from all positions that was closed with loss.
	 */
	avLoss,
	/**
	 * Maximum money (absolute) value from all positions that was closed with loss.
	 */
	maxLoss,
	/**
	 * Average win divide to Average loss.
	 */
	avWinAvLoss, //
	/**
	 * Kelly metric: http://www.investopedia.com/articles/trading/04/091504.asp <br/>
	 * {@link #winProb} - (1.0 - {@link #winProb}) / {@link #avWinAvLoss}.
	 */
	kelly,
	/**
	 * Sharpe Ratio metric: http://www.investopedia.com/articles/07/sharpe_ratio.asp
	 */
	sharpeRatio, //
	startMonthAvGain, //
	startMonthStDevGain, //
	startMonthMax, //
	startMonthMin, //
	month12AvGain, //
	month12StDevGain, //
	month12Max, //
	month12Min, //
	/**
	 * Average value of drawdown length in time units.
	 */
	ddDurationAverage,
	/**
	 * Maximum value of drawdown length in time units.
	 */
	ddDurationMax,
	/**
	 * Average value of drawndown (peak-to-trough decline).
	 */
	ddValueAverage,
	/**
	 * Maximum value of drawdown (peak-to-trough decline).
	 */
	ddValueMax,
	/**
	 * Maximum spent money during trading.
	 */
	maxSpentMoney, //
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
