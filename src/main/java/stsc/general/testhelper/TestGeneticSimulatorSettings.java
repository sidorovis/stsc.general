package stsc.general.testhelper;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpSubExecution;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticFactory;
import stsc.general.simulator.multistarter.genetic.GeneticList;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticListImpl;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.mocks.StockStorageMock;

public final class TestGeneticSimulatorSettings {

	private static final StockStorage stockStorage = StockStorageMock.getStockStorage();

	public static SimulatorSettingsGeneticListImpl getGeneticList() {
		return getGeneticList(Arrays.asList(new String[] { "open", "high", "low", "close", "value" }), "31-12-2009");
	}

	public static GeneticList getBigGeneticList() {
		return getBigGeneticList(Arrays.asList(new String[] { "open", "high", "low", "close", "value" }), "31-12-2009");
	}

	private static String algoStockName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getStock(aname).getName();
	}

	private static String algoEodName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getEod(aname).getName();
	}

	private static SimulatorSettingsGeneticListImpl getGeneticList(final List<String> openTypes, final String periodTo) {
		return getGeneticFactory(openTypes, periodTo).getList();
	}

	private static SimulatorSettingsGeneticFactory getGeneticFactory(final List<String> openTypes, final String periodTo) {
		try {
			final FromToPeriod period = new FromToPeriod("01-01-2000", periodTo);
			final SimulatorSettingsGeneticFactory factory = new SimulatorSettingsGeneticFactory(stockStorage, period);
			fillFactory(factory, openTypes, 4.0, 10, 10, 50000.0);
			return factory;
		} catch (BadParameterException | BadAlgorithmException | ParseException e) {
		}
		return new SimulatorSettingsGeneticFactory(stockStorage, new FromToPeriod(new Date(), new Date()));
	}

	private static GeneticList getBigGeneticList(final List<String> openTypes, final String periodTo) {
		return getBigGeneticFactory(openTypes, periodTo).getList();
	}

	private static SimulatorSettingsGeneticFactory getBigGeneticFactory(final List<String> openTypes, final String periodTo) {
		try {
			final FromToPeriod period = new FromToPeriod("01-01-2000", periodTo);
			final SimulatorSettingsGeneticFactory factory = new SimulatorSettingsGeneticFactory(stockStorage, period);
			fillFactory(factory, openTypes, 0.1, 1, 1, 1.0);
			return factory;
		} catch (BadParameterException | BadAlgorithmException | ParseException e) {
		}
		return new SimulatorSettingsGeneticFactory(stockStorage, new FromToPeriod(new Date(), new Date()));
	}

	private static void fillFactory(SimulatorSettingsGeneticFactory settings, final List<String> openTypes, double fStep, int nSide, int mSide, double psSide)
			throws BadParameterException, BadAlgorithmException {
		settings.addStock("in", algoStockName("Input"), "e", openTypes);
		settings.addStock("ema", algoStockName("Ema"), new AlgorithmSettingsIteratorFactory().add(new MpDouble("P", 0.1, 0.6, 0.4)).add(new MpSubExecution("", "in")));
		settings.addStock("level", algoStockName(".Level"),
				new AlgorithmSettingsIteratorFactory().add(new MpDouble("f", 15.0, 20.0, fStep)).add(new MpSubExecution("", Arrays.asList(new String[] { "ema", "in" }))));
		settings.addEod("os", algoEodName("OneSideOpenAlgorithm"), "side", Arrays.asList(new String[] { "long", "short" }));

		final AlgorithmSettingsIteratorFactory factoryPositionSide = new AlgorithmSettingsIteratorFactory();
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "ema", "level", "in" })));
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "level", "ema" })));
		factoryPositionSide.add(new MpInteger("n", 1, 32, nSide));
		factoryPositionSide.add(new MpInteger("m", 1, 32, mSide));
		factoryPositionSide.add(new MpDouble("ps", 50000.0, 200001.0, psSide));
		settings.addEod("pnm", algoEodName("PositionNDayMStocks"), factoryPositionSide);
	}

}
