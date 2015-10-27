package stsc.general.strategy.selector;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import stsc.general.simulator.multistarter.genetic.settings.distance.SimulatorSettingsIntervalImpl;
import stsc.general.statistic.MetricType;
import stsc.general.statistic.Metrics;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.TradingStrategy;

public class StatisticsWithSettingsDistanceSelectorTest {

	private TradingStrategy getTs(double avGain, double winProb) {
		final HashMap<MetricType, Double> dh = new HashMap<>();
		dh.put(MetricType.avGain, avGain);
		dh.put(MetricType.winProb, winProb);
		return TradingStrategy.createTest(new Metrics(dh, new HashMap<>()));
	}

	@Test
	public void testStatisticsWithSettingsDistanceSelector() {
		final StatisticsWithSettingsDistanceSelector selector = new StatisticsWithSettingsDistanceSelector(5, new SimulatorSettingsIntervalImpl(), new CostWeightedSumFunction());
		selector.addStrategy(getTs(10.5, 6.9));
		fail("Not yet implemented");
	}

}
