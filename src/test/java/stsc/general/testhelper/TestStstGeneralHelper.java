package stsc.general.testhelper;

import stsc.common.algorithms.AlgorithmConfiguration;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.storage.SignalsStorage;
import stsc.common.trading.Broker;
import stsc.general.algorithm.AlgorithmConfigurationImpl;
import stsc.general.trading.BrokerImpl;
import stsc.storage.SignalsStorageImpl;
import stsc.storage.ThreadSafeStockStorage;

public class TestStstGeneralHelper {
	public static EodAlgorithmInit getEodAlgorithmInit() {
		return getEodAlgorithmInit(new BrokerImpl(new ThreadSafeStockStorage()));
	}

	public static EodAlgorithmInit getEodAlgorithmInit(Broker broker) {
		return getEodAlgorithmInit(broker, "eName");
	}

	public static EodAlgorithmInit getEodAlgorithmInit(Broker broker, String executionName) {
		return getEodAlgorithmInit(broker, executionName, new AlgorithmConfigurationImpl());
	}

	public static EodAlgorithmInit getEodAlgorithmInit(Broker broker, String executionName, AlgorithmConfiguration settings) {
		return getEodAlgorithmInit(broker, executionName, new AlgorithmConfigurationImpl(), new SignalsStorageImpl());
	}

	public static EodAlgorithmInit getEodAlgorithmInit(Broker broker, String executionName, AlgorithmConfiguration settings, SignalsStorage signalsStorage) {
		return new EodAlgorithmInit(executionName, signalsStorage, settings, broker);
	}

	public static StockAlgorithmInit getStockAlgorithmInit(String executionName, String stockName, SignalsStorage storage, AlgorithmConfiguration settings) {
		return new StockAlgorithmInit(executionName, storage, stockName, settings);
	}

	public static StockAlgorithmInit getStockAlgorithmInit(String executionName, String stockName, AlgorithmConfiguration settings) {
		return getStockAlgorithmInit(executionName, stockName, new SignalsStorageImpl(), settings);
	}

	public static StockAlgorithmInit getStockAlgorithmInit(String executionName) {
		return getStockAlgorithmInit(executionName, "sName", new AlgorithmConfigurationImpl());
	}

	public static StockAlgorithmInit getStockAlgorithmInit() {
		return getStockAlgorithmInit("eName");
	}

}
