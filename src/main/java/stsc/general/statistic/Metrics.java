package stsc.general.statistic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import stsc.general.statistic.EquityCurve.Element;

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

	private EquityCurve equityCurveInMoney;

	static public StatisticsInit createInit() {
		return new StatisticsInit();
	}

	public Metrics(Metrics.StatisticsInit init) {
		calculateProbabilityStatistics(init);
		calculateEquityStatistics(init);
		this.equityCurveInMoney = init.equityCurveInMoney;
	}

	public Metrics(Map<String, Double> doubleList, Map<String, Integer> integerList) {
		this.getDoubleMetrics().putAll(doubleList);
		this.getIntegerMetrics().putAll(integerList);
	}

	private void calculateProbabilityStatistics(StatisticsInit init) {
		setDoubleMetric("avGain", init.getAvGain());
		setIntegerMetric("period", init.period);

		setDoubleMetric("freq", division(init.count, init.period));
		setDoubleMetric("winProb", division(init.winCount, init.count));

		setDoubleMetric("avWin", division(init.winSum, init.winCount));
		setDoubleMetric("maxWin", init.maxWin);
		setDoubleMetric("avLoss", Math.abs(division(init.lossSum, init.lossCount)));
		setDoubleMetric("maxLoss", -init.maxLoss);
		setDoubleMetric("avWinAvLoss", division(getDoubleMetric("avWin"), getDoubleMetric("avLoss")));

		if (getDoubleMetric("avWinAvLoss") == 0.0)
			setDoubleMetric("kelly", 0.0);
		else
			setDoubleMetric("kelly", getDoubleMetric("winProb") - (1 - getDoubleMetric("winProb")) / getDoubleMetric("avWinAvLoss"));

	}

	private void calculateEquityStatistics(StatisticsInit init) {
		setDoubleMetric("sharpeRatio", init.sharpeRatio);
		setDoubleMetric("startMonthAvGain", init.startMonthAvGain);
		setDoubleMetric("startMonthStDevGain", init.startMonthStDevGain);
		setDoubleMetric("startMonthMax", init.startMonthMax);
		setDoubleMetric("startMonthMin", init.startMonthMin);

		setDoubleMetric("month12AvGain", init.month12AvGain);
		setDoubleMetric("month12StDevGain", init.month12StDevGain);
		setDoubleMetric("month12Max", init.month12Max);
		setDoubleMetric("month12Min", init.month12Min);

		setDoubleMetric("ddDurationAvGain", init.ddDurationAvGain);
		setDoubleMetric("ddDurationMax", init.ddDurationMax);
		setDoubleMetric("ddValueAvGain", init.ddValueAvGain);
		setDoubleMetric("ddValueMax", init.ddValueMax);
	}

	static private double division(double a, double b) {
		if (b == 0.0)
			return 0.0;
		else
			return a / b;
	}

	public void setDoubleMetric(String name, Double value) {
		getDoubleMetrics().put(name, value);
	}

	public Double getDoubleMetric(String name) {
		return getDoubleMetrics().get(name);
	}

	/**
	 * Try to return by double first, if not by integer
	 */
	public Double getMetric(String name) {
		Double r = getDoubleMetric(name);
		if (r == null) {
			return getIntegerMetric(name).doubleValue();
		} else {
			return r;
		}
	}

	public void setIntegerMetric(String name, Integer value) {
		getIntegerMetrics().put(name, value);
	}

	public EquityCurve getEquityCurveInMoney() {
		return equityCurveInMoney;
	}

	public Integer getIntegerMetric(String name) {
		return getIntegerMetrics().get(name);
	}

	public Map<String, Double> getDoubleMetrics() {
		return doubleMetrics;
	}

	public Map<String, Integer> getIntegerMetrics() {
		return integerMetrics;
	}

	public void print(final String outputFile) throws IOException, IllegalArgumentException, IllegalAccessException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
			print(writer);
		}
	}

	private void print(BufferedWriter outfile) throws IOException, IllegalArgumentException, IllegalAccessException {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		final DecimalFormat decimalFormat = new DecimalFormat("#0.000");

		for (Map.Entry<String, Double> e : getDoubleMetrics().entrySet()) {
			outfile.append(e.getKey()).append('\t').append(decimalFormat.format(e.getValue())).append('\n');
		}
		for (Map.Entry<String, Integer> e : getIntegerMetrics().entrySet()) {
			outfile.append(e.getKey()).append('\t').append(e.getValue().toString()).append('\n');
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
		for (Map.Entry<String, Double> e : getDoubleMetrics().entrySet()) {
			result += " " + e.getKey() + " " + e.getValue().toString();
		}
		for (Map.Entry<String, Integer> e : getIntegerMetrics().entrySet()) {
			result += " " + e.getKey() + " " + e.getValue().toString();
		}
		return result;
	}

}
