package stsc.general.statistic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import stsc.general.statistic.EquityCurve.Element;
import stsc.general.strategy.TradingStrategy;

/**
 * {@link Metrics} is a class that store comparable (double like) values that describe {@link TradingStrategy}. <br/>
 * There is two type of Metrics: Integer and Double types.
 */
public final class Metrics {

	static public MetricsBuilder getBuilder() {
		return new MetricsBuilder();
	}

	private final Map<MetricType, Double> doubleMetrics = new HashMap<>();
	private final Map<MetricType, Integer> integerMetrics = new HashMap<>();

	private final EquityCurve equityCurveInMoney;

	Metrics(final MetricsBuilder init) {
		calculateProbabilityStatistics(init);
		calculateEquityStatistics(init);
		this.equityCurveInMoney = init.equityCurveInMoney;
	}

	public static Metrics createEmpty() {
		return Metrics.getBuilder().build();
	}

	public Metrics(Map<MetricType, Double> doubleList, Map<MetricType, Integer> integerList) {
		this.getDoubleMetrics().putAll(doubleList);
		this.getIntegerMetrics().putAll(integerList);
		this.equityCurveInMoney = new EquityCurve();
	}

	private void calculateProbabilityStatistics(MetricsBuilder init) {
		setDoubleMetric(MetricType.avGain, init.getAvGain());
		setIntegerMetric(MetricType.period, init.period);

		setDoubleMetric(MetricType.freq, divide(init.count, init.period));
		setDoubleMetric(MetricType.winProb, divide(init.winCount, init.count));

		setDoubleMetric(MetricType.avWin, divide(init.winSum, init.winCount));
		setDoubleMetric(MetricType.maxWin, init.maxWin);
		setDoubleMetric(MetricType.avLoss, Math.abs(divide(init.lossSum, init.lossCount)));
		setDoubleMetric(MetricType.maxLoss, -init.maxLoss);
		setDoubleMetric(MetricType.avWinAvLoss, divide(getDoubleMetric(MetricType.avWin), getDoubleMetric(MetricType.avLoss)));

		if (getDoubleMetric(MetricType.avWinAvLoss) == 0.0)
			setDoubleMetric(MetricType.kelly, 0.0);
		else
			setDoubleMetric(MetricType.kelly,
					getDoubleMetric(MetricType.winProb) - (1 - getDoubleMetric(MetricType.winProb)) / getDoubleMetric(MetricType.avWinAvLoss));

	}

	private void calculateEquityStatistics(MetricsBuilder init) {
		setDoubleMetric(MetricType.sharpeRatio, init.sharpeRatio);
		setDoubleMetric(MetricType.startMonthAvGain, init.startMonthAvGain);
		setDoubleMetric(MetricType.startMonthStDevGain, init.startMonthStDevGain);
		setDoubleMetric(MetricType.startMonthMax, init.startMonthMax);
		setDoubleMetric(MetricType.startMonthMin, init.startMonthMin);

		setDoubleMetric(MetricType.month12AvGain, init.month12AvGain);
		setDoubleMetric(MetricType.month12StDevGain, init.month12StDevGain);
		setDoubleMetric(MetricType.month12Max, init.month12Max);
		setDoubleMetric(MetricType.month12Min, init.month12Min);

		setDoubleMetric(MetricType.ddDurationAverage, init.ddDurationAverage);
		setDoubleMetric(MetricType.ddDurationMax, init.ddDurationMax);
		setDoubleMetric(MetricType.ddValueAverage, init.ddValueAverage);
		setDoubleMetric(MetricType.ddValueMax, init.ddValueMax);

		setDoubleMetric(MetricType.maxSpentMoney, init.getMaximumSpentMoney());
	}

	static private double divide(double a, double b) {
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
	 * Try to return by double metric; if double value is not there -> try to return integer
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

	/**
	 * Print out {@link Metrics} to a text file.
	 */
	public void print(final Path outputFilepath) throws IOException, IllegalArgumentException, IllegalAccessException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilepath.toFile()))) {
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
