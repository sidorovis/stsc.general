package stsc.general.statistic;

import java.util.HashMap;
import java.util.Map;

public final class Metrics {

	static class StatisticsInit {

		public EquityCurve equityCurve = new EquityCurve();
		public EquityCurve equityCurveInMoney;

		public int period = 0;

		public int count = 0;

		public int winCount = 0;
		public int lossCount = 0;

		public double winSum = 0.0;
		public double lossSum = 0.0;

		public double maxWin = 0.0;
		public double maxLoss = 0.0;

		public double sharpeRatio = 0.0;

		public double startMonthAvGain = 0.0;
		public double startMonthStDevGain = 0.0;
		public double startMonthMin = 0.0;
		public double startMonthMax = 0.0;

		public double month12AvGain = 0.0;
		public double month12StDevGain = 0.0;
		public double month12Min = 0.0;
		public double month12Max = 0.0;

		public double ddDurationAvGain = 0.0;
		public double ddDurationMax = 0.0;

		public double ddValueAvGain = 0.0;
		public double ddValueMax = 0.0;

		double getAvGain() {
			if (equityCurve.size() == 0) {
				Statistics.logger
						.warn("strange equityCurve, seems that algorithms won't trade that time");
				return 0.0;
			}
			return equityCurve.getLastElement().value;
		}

		public String toString() {
			return "curve(" + equityCurve.toString() + ")";
		}

		void copyMoneyEquityCurve() {
			equityCurveInMoney = equityCurve.clone();
		}
	}

	private final Map<String, Double> doubleMetrics = new HashMap<>();
	private final Map<String, Integer> integerMetrics = new HashMap<>();

	static public StatisticsInit createInit() {
		return new StatisticsInit();
	}

	public Metrics() {

	}

	public void addDoubleMetric(String name, Double value) {
		doubleMetrics.put(name, value);
	}

	public Double getDoubleMetric(String name) {
		return doubleMetrics.get(name);
	}

	public void addIntegerMetric(String name, Integer value) {
		integerMetrics.put(name, value);
	}

	public Integer getIntegerMetric(String name) {
		return integerMetrics.get(name);
	}

}
