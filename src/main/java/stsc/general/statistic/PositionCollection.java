package stsc.general.statistic;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Collection of positions used into {@link EquityProcessor}. <br/>
 * To store opened positions for calculating equity and {@link Metrics} for trading strategy.
 */
final class PositionCollection {

	private final double commision;

	PositionCollection(final StatisticsProcessor statisticsProcessor) {
		this.commision = statisticsProcessor.getCommision();
	}

	final class Position {
		int shares = 0;
		private double spentMoney = 0.0;
		private int tradesAmount = 1;

		public Position(int shares, double spentMoney) {
			super();
			this.shares = shares;
			this.spentMoney = spentMoney;
		}

		public void increment(int shares, double spentMoney) {
			this.shares += shares;
			this.spentMoney += spentMoney;
			this.tradesAmount += 1;
		}

		public boolean decrement(int shares, double spentMoney) {
			this.shares -= shares;
			this.spentMoney -= spentMoney;
			this.tradesAmount += 1;
			return this.shares == 0;
		}

		public double sharePrice() {
			return spentMoney / shares;
		}

		public int getTradesAmount() {
			return tradesAmount;
		}

		@Override
		public String toString() {
			return Double.toString(shares);
		}
	}

	public HashMap<String, Position> positions = new HashMap<>();

	/**
	 * Create or increment already created position for selected stock with selected by algorithm shares amount according to selected shares price.
	 */
	void increment(String stockName, int shares, double sharesPrice) {
		Position position = positions.get(stockName);
		if (position != null)
			position.increment(shares, sharesPrice);
		else
			positions.put(stockName, new Position(shares, sharesPrice));
	}

	/**
	 * Decrement and if necessary delete fully - closed position (if remainder part is zero). Returns zero if position is still not closed, and amount of trades
	 * that was created during this position.
	 */
	public int decrement(String stockName, int shares, double sharesPrice) {
		Position position = positions.get(stockName);
		if (position.decrement(shares, sharesPrice)) {
			positions.remove(stockName);
			return position.getTradesAmount();
		}
		return 0;
	}

	public double sharePrice(String stockName) {
		Position position = positions.get(stockName);
		return position.sharePrice();
	}

	public double cost(HashMap<String, Double> prices) {
		double result = 0.0;
		for (Entry<String, Position> i : positions.entrySet()) {
			final int tradesAmount = i.getValue().getTradesAmount();
			final double price = prices.get(i.getKey());
			double sharesPrices = price * i.getValue().shares;
			sharesPrices -= sharesPrices * commision * tradesAmount;
			result += sharesPrices;
		}
		return result;
	}

	public int size() {
		return positions.size();
	}

	@Override
	public String toString() {
		return "(" + Integer.toString(positions.size()) + "): " + positions.toString();
	}

}