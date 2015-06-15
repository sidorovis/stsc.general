package stsc.general.trading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import stsc.common.Day;
import stsc.common.stocks.Stock;

/**
 * This class provide possibility to store days and last (current) day. <br/>
 * Such storage helps with looking for next day for stock. So we can easily
 * check / return required day from storage without locating by O(1). (extremely
 * fast).
 */
final class DayIterator {
	private final Date from;

	private ArrayList<Day> days;
	private int currentIterator;

	DayIterator(Stock stock, Date from) {
		this.from = from;
		this.days = stock.getDays();
		reset();
	}

	void reset() {
		if (days.size() > 0 && days.get(0).date.compareTo(from) >= 0)
			currentIterator = 0;
		else {
			currentIterator = Collections.binarySearch(days, new Day(from));
			if (currentIterator < 0) {
				if (-currentIterator >= days.size())
					currentIterator = days.size();
				else
					currentIterator = -currentIterator - 1;
			}
		}
	}

	boolean dataFound() {
		return currentIterator < days.size();
	}

	Optional<Day> getCurrentDayAndNext(Day currentDay) {
		if (currentIterator < days.size()) {
			final Day day = days.get(currentIterator);
			final int dayCompare = day.compareTo(currentDay);
			if (dayCompare == 0) {
				currentIterator++;
				return Optional.of(day);
			} else if (dayCompare < 0) {
				currentIterator = Collections.binarySearch(days, currentDay);
				if (currentIterator < 0) {
					if (-currentIterator < days.size())
						currentIterator = -currentIterator;
					else
						currentIterator = days.size();
					return Optional.empty();
				}
				if (currentIterator >= 0) {
					return Optional.of(days.get(currentIterator));
				}
			}
		}
		return Optional.empty();
	}

	@Override
	public String toString() {
		return Integer.toString(currentIterator) + "/" + Integer.toString(days.size());
	}
}
