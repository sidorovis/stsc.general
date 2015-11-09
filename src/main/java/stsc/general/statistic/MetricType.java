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
	 * Maximum spent money during trading.
	 */
	maxSpentMoney,
	/**
	 * Percentage of signals that were closed with positive value of trading.
	 */
	winProb,
	/**
	 * Average money value from all positions that was closed with win.
	 */
	avWin,
	/**
	 * Average money value from all positions that was closed with loss.
	 */
	avLoss,
	/**
	 * Average value of drawdown length in time units.
	 */
	ddDurationAverage,
	/**
	 * Average value of drawndown (peak-to-trough decline).
	 */
	ddValueAverage,
	/**
	 * Amount of signals per time unit.
	 */
	freq,
	/**
	 * Average win divide to Average loss.
	 */
	avWinAvLoss,
	/**
	 * Kelly metric: http://www.investopedia.com/articles/trading/04/091504.asp <br/>
	 * {@link #winProb} - (1.0 - {@link #winProb}) / {@link #avWinAvLoss}.
	 */
	kelly,
	/**
	 * Sharpe Ratio metric: http://www.investopedia.com/articles/07/sharpe_ratio.asp
	 */
	sharpeRatio,
	/**
	 * Maximum money value from all positions that was closed with win.
	 */
	maxWin,
	/**
	 * Maximum money (absolute) value from all positions that was closed with loss.
	 */
	maxLoss,
	/**
	 * Start of Month Average Gain deviation equity gain difference. <br/>
	 * Calculates average value for equity value difference on month distance (calculated by begin date of months).
	 */
	startMonthAvGain,
	/**
	 * Start of Month Standard deviation equity gain difference. <br/>
	 * Calculates equity value difference on month distance (calculated by begin date of months).
	 */
	startMonthStDevGain,
	/**
	 * Maximum months equity difference. (Maximum gain month by month).
	 */
	startMonthMax,
	/**
	 * Minimum months equity difference. (Minimum gain month by month).
	 */
	startMonthMin,
	/**
	 * Average value for 12 months windows. This value calculated with 12 months rolling window by equity diff.
	 */
	month12AvGain,
	/**
	 * Average Standard Deviation for 12 months windows. This value calculated with 12 months rolling window by equity diff.
	 */
	month12StDevGain,
	/**
	 * Maximum value for equity differences on 12 month rolling window.
	 */
	month12Max,
	/**
	 * Minimum value for equity differences on 12 month rolling window.
	 */
	month12Min,
	/**
	 * Maximum value of drawdown length in time units.
	 */
	ddDurationMax,
	/**
	 * Maximum value of drawdown (peak-to-trough decline).
	 */
	ddValueMax,
	/**
	 * Amount of time units (days / minutes).
	 */
	period(Integer.class),
	//
	;

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
