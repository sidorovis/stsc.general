package stsc.general.trading;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.LocalDate;

import stsc.common.Side;

public final class TradingRecord {
	public enum TradingType {
		BUY, SELL;

		@Override
		public String toString() {
			if (this == BUY)
				return "BUY ";
			else
				return "SELL";
		}
	}

	private static DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	private final TradingType type;
	private final Date date;
	private final String stockName;
	private final Side side;
	private final int amount;

	static TradingRecord buy(Date when, String stockName, Side side, int amount) {
		return new TradingRecord(TradingType.BUY, when, stockName, side, amount);
	}

	static TradingRecord sell(Date when, String stockName, Side side, int amount) {
		return new TradingRecord(TradingType.SELL, when, stockName, side, amount);
	}

	private TradingRecord(TradingType type, Date when, String stockName, Side side, int amount) {
		this.type = type;
		this.date = when;
		this.stockName = stockName;
		this.side = side;
		this.amount = amount;
	}

	void printOut(Writer w) throws IOException {
		w.append(dateFormat.format(date));
		w.append("\t");
		if (type == TradingType.BUY)
			w.append("B");
		else
			w.append("S");
		w.append("\t").append(stockName).append("\t");
		if (side == Side.LONG)
			w.append("LONG");
		else
			w.append("SHORT");
		w.append("\t");
		w.write(Integer.valueOf(amount).toString());
		w.append("\n");
	}

	public TradingType getType() {
		return type;
	}

	public boolean isPurchase() {
		return type == TradingType.BUY;
	}

	public Date getDate() {
		return date;
	}

	public String getStockName() {
		return stockName;
	}

	public Side getSide() {
		return side;
	}

	public boolean isLong() {
		return side == Side.LONG;
	}

	public int getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return new LocalDate(date).toString() + ": (" + type.toString() + ") " + stockName + " | " + side.toString() + " " + String.valueOf(amount);
	}
}