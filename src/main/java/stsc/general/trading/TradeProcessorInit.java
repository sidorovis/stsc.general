package stsc.general.trading;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.UnitedFormatHelper;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.storage.ExecutionsStorage;
import stsc.storage.ThreadSafeStockStorage;

public final class TradeProcessorInit implements Cloneable {

	private final BrokerImpl broker;
	private final FromToPeriod period;
	private final ExecutionsStorage executionsStorage;

	public TradeProcessorInit(final StockStorage stockStorage, final FromToPeriod period) {
		this.broker = new BrokerImpl(stockStorage);
		this.period = period;
		this.executionsStorage = new ExecutionsStorage();
	}

	public TradeProcessorInit(final StockStorage stockStorage, final FromToPeriod period, final ExecutionsStorage executionsStorage) {
		this.broker = new BrokerImpl(stockStorage);
		this.period = period;
		this.executionsStorage = executionsStorage;
	}

	public TradeProcessorInit(final StockStorage stockStorage, final FromToPeriod period, final String config) throws BadAlgorithmException {
		this.broker = new BrokerImpl(stockStorage);
		this.period = period;
		final ExecutionsLoader executionsLoader = new ExecutionsLoader(period, config);
		this.executionsStorage = executionsLoader.getExecutionsStorage();
	}

	/**
	 * This constructor requires several configuration files on file system:
	 * <br/>
	 * 1. <Data.filter.folder> folder where stock data is placed (for
	 * StockStorage). <br/>
	 * 2. <Executions.path> (./algs.ini by default). This settings should point
	 * to the file with execution plan settings.
	 * 
	 * @param configPath
	 *            - path to the configuration file
	 * @throws BadAlgorithmException
	 *             - this exception appears when algorithm settings are not
	 *             appropriate for algorithm.
	 */
	public TradeProcessorInit(final File configPath) throws BadAlgorithmException {
		try {
			Properties p = loadProperties(configPath.getAbsolutePath());
			final Path filterDataRelativePath = resolveAbsoluteDataPath(configPath.toPath().getParent(), p.getProperty("Data.filter.folder"));
			final StockStorage stockStorage = createStockStorageForStockSet(getStockSet(p), filterDataRelativePath);

			final Path algsConfig = resolveAbsoluteDataPath(configPath.toPath().getParent(), p.getProperty("Executions.path", "./algs.ini"));
			final FromToPeriod period = new FromToPeriod(p);
			final ExecutionsLoader executionsLoader = new ExecutionsLoader(algsConfig.toFile(), period);
			final ExecutionsStorage executionsStorage = executionsLoader.getExecutionsStorage();

			this.broker = new BrokerImpl(stockStorage);
			this.period = period;
			this.executionsStorage = executionsStorage;
		} catch (ClassNotFoundException | IOException | ParseException e) {
			throw new BadAlgorithmException(e.getMessage());
		}
	}

	private StockStorage createStockStorageForStockSet(final Set<String> stockNamesSet, final Path filterDataPath) throws IOException {
		final StockStorage stockStorage = new ThreadSafeStockStorage();
		for (String name : stockNamesSet) {
			final String path = filterDataPath.resolve(UnitedFormatHelper.toFilesystem(name).getFilename()).toString();
			stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile(path));
		}
		return stockStorage;
	}

	private Path resolveAbsoluteDataPath(final Path configPath, final String path) {
		final Path absolutePath = FileSystems.getDefault().getPath(path);
		if (!FileSystems.getDefault().getPath(path).isAbsolute()) {
			return FileSystems.getDefault().getPath(configPath.toString()).resolve(path);
		}
		return absolutePath;
	}

	private TradeProcessorInit(final BrokerImpl broker, final FromToPeriod period, final ExecutionsStorage executionsStorage) {
		this.broker = new BrokerImpl(broker.getStockStorage());
		this.period = period;
		this.executionsStorage = executionsStorage;
	}

	private Set<String> getStockSet(final Properties p) {
		final String[] rawStockSet = p.getProperty("Stocks").split(",");
		Set<String> stockSet = new HashSet<>();
		for (String string : rawStockSet) {
			stockSet.add(string.trim());
		}
		return stockSet;
	}

	private Properties loadProperties(final String configPath) throws ClassNotFoundException, IOException {
		final Properties properties = new Properties();
		try (FileInputStream in = new FileInputStream(configPath)) {
			properties.load(in);
		}
		return properties;
	}

	public BrokerImpl getBrokerImpl() {
		return broker;
	}

	public FromToPeriod getPeriod() {
		return period;
	}

	public ExecutionsStorage getExecutionsStorage() {
		return executionsStorage;
	}

	public String stringHashCode() {
		return getExecutionsStorage().stringHashCode();
	}

	@Override
	public String toString() {
		return getExecutionsStorage().toString();
	}

	@Override
	public TradeProcessorInit clone() {
		return new TradeProcessorInit(broker, period, getExecutionsStorage().clone());
	}

	public List<String> generateOutForStocks() {
		return getExecutionsStorage().generateOutForStocks();
	}

	public List<String> generateOutForEods() {
		return getExecutionsStorage().generateOutForEods();
	}
}
