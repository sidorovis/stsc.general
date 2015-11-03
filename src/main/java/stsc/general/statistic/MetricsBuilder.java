package stsc.general.statistic;

class MetricsBuilder {

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

	private double maximumSpentMoney;

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

	//

	public Metrics build() {
		return new Metrics(this);
	}

	public MetricsBuilder setMaximumSpentMoney(double maximumSpentMoney) {
		this.maximumSpentMoney = maximumSpentMoney;
		return this;
	}

	public double getMaximumSpentMoney() {
		return this.maximumSpentMoney;
	}

}