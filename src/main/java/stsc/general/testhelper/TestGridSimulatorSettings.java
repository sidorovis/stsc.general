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
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.mocks.StockStorageMock;

public class TestGridSimulatorSettings {

	public static String algoStockName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getStock(aname).getName();
	}

	public static String algoEodName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getEod(aname).getName();
	}

	public static void fillFactory(SimulatorSettingsGridFactory settings, final List<String> openTypes, double fStep, int nSide, int mSide, double psSide)
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

	public static void fillSmallFactory(SimulatorSettingsGridFactory settings, final List<String> openTypes, double fStep, int nSide, int mSide, double psSide)
			throws BadParameterException, BadAlgorithmException {
		settings.addStock("in", algoStockName("Input"), "e", openTypes);
		settings.addStock("ema", algoStockName("Ema"), new AlgorithmSettingsIteratorFactory().add(new MpDouble("P", 0.1, 0.6, 0.7)).add(new MpSubExecution("", "in")));
		settings.addStock("level", algoStockName(".Level"),
				new AlgorithmSettingsIteratorFactory().add(new MpDouble("f", 15.0, 20.0, fStep)).add(new MpSubExecution("", Arrays.asList(new String[] { "ema", "in" }))));
		settings.addEod("os", algoEodName("OneSideOpenAlgorithm"), "side", Arrays.asList(new String[] { "long", "short" }));

		final AlgorithmSettingsIteratorFactory factoryPositionSide = new AlgorithmSettingsIteratorFactory();
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "ema" })));
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "level" })));
		factoryPositionSide.add(new MpInteger("n", 1, 32, nSide));
		factoryPositionSide.add(new MpInteger("m", 1, 32, mSide));
		factoryPositionSide.add(new MpDouble("ps", 50000.0, 200001.0, psSide));
		settings.addEod("pnm", algoEodName("PositionNDayMStocks"), factoryPositionSide);
	}

	public static SimulatorSettingsGridList getBidGridList() {
		return getBigGridFactory(StockStorageMock.getStockStorage(), Arrays.asList("open", "high", "low", "close", "value"), "31-12-2002").getList();
	}

	public static SimulatorSettingsGridList getGridList() {
		return getGridList(StockStorageMock.getStockStorage(), Arrays.asList("open"), "31-12-2002");
	}

	public static SimulatorSettingsGridList getSmallGridList() {
		return getSmallGridFactory(StockStorageMock.getStockStorage(), Arrays.asList("open"), "31-12-2002").getList();
	}

	public static SimulatorSettingsGridList getGridList(final StockStorage stockStorage, final List<String> openTypes, final String periodTo) {
		return getGridFactory(stockStorage, openTypes, periodTo).getList();
	}

	public static SimulatorSettingsGridFactory getBigGridFactory(final StockStorage stockStorage, final List<String> openTypes, final String periodTo) {
		try {
			final FromToPeriod period = new FromToPeriod("01-01-2000", periodTo);
			final SimulatorSettingsGridFactory factory = new SimulatorSettingsGridFactory(stockStorage, period);
			fillFactory(factory, openTypes, 4.0, 5, 5, 50000.0);
			return factory;
		} catch (BadParameterException | BadAlgorithmException | ParseException e) {
		}
		return new SimulatorSettingsGridFactory(stockStorage, new FromToPeriod(new Date(), new Date()));
	}

	public static SimulatorSettingsGridFactory getGridFactory(final StockStorage stockStorage, final List<String> openTypes, final String periodTo) {
		try {
			final FromToPeriod period = new FromToPeriod("01-01-2000", periodTo);
			final SimulatorSettingsGridFactory factory = new SimulatorSettingsGridFactory(stockStorage, period);
			fillFactory(factory, openTypes, 4.0, 10, 10, 50000.0);
			return factory;
		} catch (BadParameterException | BadAlgorithmException | ParseException e) {
		}
		return new SimulatorSettingsGridFactory(stockStorage, new FromToPeriod(new Date(), new Date()));
	}

	public static SimulatorSettingsGridFactory getSmallGridFactory(final StockStorage stockStorage, final List<String> openTypes, final String periodTo) {
		try {
			final FromToPeriod period = new FromToPeriod("01-01-2000", periodTo);
			final SimulatorSettingsGridFactory factory = new SimulatorSettingsGridFactory(stockStorage, period);
			fillSmallFactory(factory, openTypes, 6.0, 32, 32, 150001.0);
			return factory;
		} catch (BadParameterException | BadAlgorithmException | ParseException e) {
		}
		return new SimulatorSettingsGridFactory(stockStorage, new FromToPeriod(new Date(), new Date()));
	}
}
