package stsc.general.statistic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import stsc.general.statistic.EquityCurve.Element;
import stsc.general.strategy.TradingStrategy;

/**
 * {@link Metrics} is a class that store comparable (double like) values that
 * describe {@link TradingStrategy}. <br/>
 * There is two type of Metrics: Integer and Double types.
 */
public final class Metrics {

	static class Builder {

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

	static public Builder getBuilder() {
		return new Builder();
	}

	private final Map<MetricType, Double> doubleMetrics = new HashMap<>();
	private final Map<MetricType, Integer> integerMetrics = new HashMap<>();

	private final EquityCurve equityCurveInMoney;

	public Metrics(final Builder init) {
		calculateProbabilityStatistics(init);
		calculateEquityStatistics(init);
		this.equityCurveInMoney = init.equityCurveInMoney;
	}

	public Metrics(Map<MetricType, Double> doubleList, Map<MetricType, Integer> integerList) {
		this.getDoubleMetrics().putAll(doubleList);
		this.getIntegerMetrics().putAll(integerList);
		this.equityCurveInMoney = new EquityCurve();
	}

	private void calculateProbabilityStatistics(Builder init) {
		setDoubleMetric(MetricType.avGain, init.getAvGain());
		setIntegerMetric(MetricType.period, init.period);

		setDoubleMetric(MetricType.freq, division(init.count, init.period));
		setDoubleMetric(MetricType.winProb, division(init.winCount, init.count));

		setDoubleMetric(MetricType.avWin, division(init.winSum, init.winCount));
		setDoubleMetric(MetricType.maxWin, init.maxWin);
		setDoubleMetric(MetricType.avLoss, Math.abs(division(init.lossSum, init.lossCount)));
		setDoubleMetric(MetricType.maxLoss, -init.maxLoss);
		setDoubleMetric(MetricType.avWinAvLoss, division(getDoubleMetric(MetricType.avWin), getDoubleMetric(MetricType.avLoss)));

		if (getDoubleMetric(MetricType.avWinAvLoss) == 0.0)
			setDoubleMetric(MetricType.kelly, 0.0);
		else
			setDoubleMetric(MetricType.kelly, getDoubleMetric(MetricType.winProb) - (1 - getDoubleMetric(MetricType.winProb)) / getDoubleMetric(MetricType.avWinAvLoss));

	}

	private void calculateEquityStatistics(Builder init) {
		setDoubleMetric(MetricType.sharpeRatio, init.sharpeRatio);
		setDoubleMetric(MetricType.startMonthAvGain, init.startMonthAvGain);
		setDoubleMetric(MetricType.startMonthStDevGain, init.startMonthStDevGain);
		setDoubleMetric(MetricType.startMonthMax, init.startMonthMax);
		setDoubleMetric(MetricType.startMonthMin, init.startMonthMin);

		setDoubleMetric(MetricType.month12AvGain, init.month12AvGain);
		setDoubleMetric(MetricType.month12StDevGain, init.month12StDevGain);
		setDoubleMetric(MetricType.month12Max, init.month12Max);
		setDoubleMetric(MetricType.month12Min, init.month12Min);

		setDoubleMetric(MetricType.ddDurationAvGain, init.ddDurationAvGain);
		setDoubleMetric(MetricType.ddDurationMax, init.ddDurationMax);
		setDoubleMetric(MetricType.ddValueAvGain, init.ddValueAvGain);
		setDoubleMetric(MetricType.ddValueMax, init.ddValueMax);
	}

	static private double division(double a, double b) {
		if (b == 0.0)
			return 0.0;
		else
			return a / b;
	}

	public void setDoubleMetric(MetricType name, Double value) {
		getDoubleMetrics().put(name, value);
	}

	public Double getDoubleMetric(MetricType name) {
		return getDoubleMetrics().get(name);
	}

	/**
	 * Try to return by double metric; if double value is not there -> try to
	 * return integer
	 */
	public Double getMetric(MetricType name) {
		Double r = getDoubleMetric(name);
		if (r == null) {
			return getIntegerMetric(name).doubleValue();
		} else {
			return r;
		}
	}

	public void setIntegerMetric(MetricType name, Integer value) {
		getIntegerMetrics().put(name, value);
	}

	public EquityCurve getEquityCurveInMoney() {
		return equityCurveInMoney;
	}

	public Integer getIntegerMetric(MetricType name) {
		return getIntegerMetrics().get(name);
	}

	public Map<MetricType, Double> getDoubleMetrics() {
		return doubleMetrics;
	}

	public Map<MetricType, Integer> getIntegerMetrics() {
		return integerMetrics;
	}

	// TODO move to {@link Path} class
	public void print(final String outputFile) throws IOException, IllegalArgumentException, IllegalAccessException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
			print(writer);
		}
	}

	private void print(BufferedWriter outfile) throws IOException, IllegalArgumentException, IllegalAccessException {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		final DecimalFormat decimalFormat = new DecimalFormat("#0.000");

		for (Map.Entry<MetricType, Double> e : getDoubleMetrics().entrySet()) {
			outfile.append(e.getKey().name()).append('\t').append(decimalFormat.format(e.getValue())).append('\n');
		}
		for (Map.Entry<MetricType, Integer> e : getIntegerMetrics().entrySet()) {
			outfile.append(e.getKey().name()).append('\t').append(e.getValue().toString()).append('\n');
		}
		outfile.append('\n');

		for (int i = 0; i < equityCurveInMoney.size(); ++i) {
			final Element e = equityCurveInMoney.get(i);
			outfile.append(dateFormat.format(e.date)).append('\t').append(decimalFormat.format(e.value)).append('\n');
		}
	}

	@Override
	public String toString() {
		String result = "Metrics: \n";
		for (Map.Entry<MetricType, Double> e : getDoubleMetrics().entrySet()) {
			result += " " + e.getKey() + " " + e.getValue().toString();
		}
		for (Map.Entry<MetricType, Integer> e : getIntegerMetrics().entrySet()) {
			result += " " + e.getKey() + " " + e.getValue().toString();
		}
		result += "\n";
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((doubleMetrics == null) ? 0 : doubleMetrics.hashCode());
		result = prime * result + ((integerMetrics == null) ? 0 : integerMetrics.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Metrics other = (Metrics) obj;
		if (doubleMetrics == null) {
			if (other.doubleMetrics != null)
				return false;
		} else if (!doubleMetrics.equals(other.doubleMetrics))
			return false;
		if (integerMetrics == null) {
			if (other.integerMetrics != null)
				return false;
		} else if (!integerMetrics.equals(other.integerMetrics))
			return false;
		return true;
	}

}
