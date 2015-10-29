package stsc.general.trading;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.EodExecution;
import stsc.common.algorithms.MutableAlgorithmConfiguration;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockExecution;
import stsc.general.algorithm.AlgorithmConfigurationImpl;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.ExecutionsStorage;

/**
 * Executions Loader - load (create set of instances for algorithms) executions settings from text file / string.
 */
final class ExecutionsLoader {

	private static final class PropertyNames {
		public static String INCLUDES_LINE = "Includes";
		public static String STOCK_EXECUTIONS_LINE = "StockExecutions";
		public static String EOD_EXECUTIONS_LINE = "EodExecutions";
	}

	private static final class Regexps {
		public static final Pattern loadLine = Pattern.compile("^(\\w*.?\\w+)\\((.*)\\)$");
		public static final Pattern subAlgoParameter = Pattern.compile("^([^\\(]+)\\((.*)\\)(\\s)*$");
		public static final Pattern integerParameter = Pattern.compile("^(.+)=(.+)[iI]$");
		public static final Pattern doubleParameter = Pattern.compile("^(.+)=(.+)[dD]$");
		public static final Pattern stringParameter = Pattern.compile("^(.+)=(.+)$");
		public static final Pattern subExecutionParameter = Pattern.compile("^(.+)$");
	}

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/algorithmLoader.log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("ExecutionsLoader");

	public File configPath = new File("./config/algs.ini");
	private String configFileFolder;
	final private AlgorithmConfigurationImpl settings;
	final private AlgorithmsStorage algorithmsStorage;
	final private ExecutionsStorage executionsStorage = new ExecutionsStorage();

	final private Set<String> openedPropertyFileNames = new HashSet<>();

	final private HashMap<String, String> registeredStockExecutions = new HashMap<>();
	final private HashMap<String, String> namedStockExecutions = new HashMap<>();

	final private HashMap<String, String> registeredEodExecutions = new HashMap<>();
	final private HashMap<String, String> namedEodExecutions = new HashMap<>();

	ExecutionsLoader(FromToPeriod period, String config) throws BadAlgorithmException {
		this.settings = new AlgorithmConfigurationImpl();
		this.algorithmsStorage = AlgorithmsStorage.getInstance();
		loadAlgorithms(config);
	}

	ExecutionsLoader(File configPath) throws BadAlgorithmException {
		this.configPath = configPath;
		this.settings = new AlgorithmConfigurationImpl();
		this.algorithmsStorage = AlgorithmsStorage.getInstance();
		loadAlgorithms();
	}

	ExecutionsLoader(File configPath, String algoPackageName) throws BadAlgorithmException {
		this.configPath = configPath;
		this.settings = new AlgorithmConfigurationImpl();
		this.algorithmsStorage = AlgorithmsStorage.getInstance(algoPackageName);
		loadAlgorithms();
	}

	private void loadAlgorithms() throws BadAlgorithmException {
		logger.info("start executions loader");
		configFileFolder = new File(configPath.getParent()).toString() + File.separatorChar;
		logger.debug("configuration path: {}", configFileFolder);
		openedPropertyFileNames.add(configPath.getName());
		try (FileInputStream in = new FileInputStream(configPath)) {
			final Properties p = new Properties();
			logger.debug("main properties file '{}' opened", configFileFolder);
			p.load(in);
			processProperties(p);
		} catch (IOException e) {
			throw new BadAlgorithmException(e.getMessage());
		}
		logger.info("stop executions loader");
	}

	private void loadAlgorithms(String config) throws BadAlgorithmException {
		final Properties p = new Properties();
		final InputStream stream = new ByteArrayInputStream(config.getBytes());
		try {
			p.load(stream);
			processProperties(p);
		} catch (IOException e) {
			throw new BadAlgorithmException(e.getMessage());
		}
	}

	private void processProperties(final Properties p) throws FileNotFoundException, IOException, BadAlgorithmException {
		processIncludes(p);
		processStockLoadLines(p);
		processEodLoadLines(p);
	}

	private void processIncludes(final Properties p) throws FileNotFoundException, IOException, BadAlgorithmException {
		final String includes = p.getProperty(PropertyNames.INCLUDES_LINE);
		if (includes == null)
			return;
		final String[] includesFileNames = includes.split(",");
		for (String rawFileName : includesFileNames) {
			final String fileName = rawFileName.trim();
			if (openedPropertyFileNames.contains(fileName))
				continue;
			openedPropertyFileNames.add(fileName);
			try (FileInputStream in = new FileInputStream(configFileFolder + fileName)) {
				final Properties includeProperty = new Properties();
				logger.debug("read include property file '{}'", fileName);
				includeProperty.load(in);
				processProperties(includeProperty);
			}
		}
	}

	private void processStockLoadLines(final Properties p) throws BadAlgorithmException {
		final String stockNames = p.getProperty(PropertyNames.STOCK_EXECUTIONS_LINE);
		if (stockNames == null)
			return;
		for (String rawExecutionName : stockNames.split(",")) {
			final String executionName = rawExecutionName.trim();
			final String loadLine = p.getProperty(executionName + ".loadLine");
			if (loadLine == null)
				throw new BadAlgorithmException("bad stock execution registration, no " + executionName + ".loadLine property");
			checkNewStockExecution(executionName);
			final String generatedName = processStockExecution(executionName, loadLine);
			namedStockExecutions.put(executionName, generatedName);
			registeredStockExecutions.put(generatedName, executionName);
		}
	}

	private void checkNewStockExecution(final String executionName) throws BadAlgorithmException {
		if (namedStockExecutions.containsKey(executionName))
			throw new BadAlgorithmException("algorithm " + executionName + " already registered");
	}

	private String processStockExecution(String executionName, String loadLine) throws BadAlgorithmException {
		final Matcher loadLineMatch = Regexps.loadLine.matcher(loadLine);
		if (loadLineMatch.matches()) {
			return processStockSubExecution(executionName, loadLineMatch);
		} else
			throw new BadAlgorithmException("bad algorithm load line: " + loadLine);
	}

	private String processStockSubExecution(Matcher match) throws BadAlgorithmException {
		final List<String> params = parseParams(match.group(2).trim());
		return processStockExecution(match.group(1).trim(), params);
	}

	private String processStockSubExecution(String executionName, Matcher match) throws BadAlgorithmException {
		final List<String> params = parseParams(match.group(2).trim());
		return processStockExecution(executionName, match.group(1).trim(), params);
	}

	private String processStockExecution(String realExecutionName, String algorithmName, final List<String> params) throws BadAlgorithmException {
		final Class<? extends StockAlgorithm> stockAlgorithm = algorithmsStorage.getStock(algorithmName);
		if (stockAlgorithm == null)
			throw new BadAlgorithmException("there is no such algorithm like " + algorithmName);
		final MutableAlgorithmConfiguration algorithmSettings = generateStockAlgorithmSettings(params);
		final String executionName = algorithmName + "(" + algorithmSettings.toString() + ")";
		final String oldRealExecutionName = registeredStockExecutions.get(executionName);
		if (oldRealExecutionName != null)
			return oldRealExecutionName;
		final StockExecution execution = new StockExecution(realExecutionName, stockAlgorithm, algorithmSettings);
		executionsStorage.addStockExecution(execution);
		return executionName;
	}

	private String processStockExecution(String algorithmName, final List<String> params) throws BadAlgorithmException {
		final Class<? extends StockAlgorithm> stockAlgorithm = algorithmsStorage.getStock(algorithmName);
		if (stockAlgorithm == null)
			throw new BadAlgorithmException("there is no such algorithm like " + algorithmName);
		final MutableAlgorithmConfiguration algorithmSettings = generateStockAlgorithmSettings(params);
		final String executionName = algorithmName + "(" + algorithmSettings.toString() + ")";
		final String oldRealExecutionName = registeredStockExecutions.get(executionName);
		if (oldRealExecutionName != null)
			return oldRealExecutionName;
		final StockExecution execution = new StockExecution(executionName, stockAlgorithm, algorithmSettings);
		executionsStorage.addStockExecution(execution);
		return executionName;
	}

	private void processEodLoadLines(final Properties p) throws BadAlgorithmException {
		final String eodNames = p.getProperty(PropertyNames.EOD_EXECUTIONS_LINE);
		if (eodNames == null)
			return;
		for (String rawExecutionName : eodNames.split(",")) {
			final String executionName = rawExecutionName.trim();
			final String loadLine = p.getProperty(executionName + ".loadLine");
			if (loadLine == null)
				throw new BadAlgorithmException("bad eod algorithm execution registration, no " + executionName + ".loadLine property");
			checkNewEodExecution(executionName);
			final String generatedName = processEodExecution(executionName, loadLine);
			namedEodExecutions.put(executionName, generatedName);
			registeredEodExecutions.put(generatedName, executionName);
		}
	}

	private void checkNewEodExecution(final String executionName) throws BadAlgorithmException {
		if (namedEodExecutions.containsKey(executionName))
			throw new BadAlgorithmException("eod algorithm " + executionName + " already registered");
	}

	private String processEodExecution(final String executionName, final String loadLine) throws BadAlgorithmException {
		final Matcher loadLineMatch = Regexps.loadLine.matcher(loadLine);
		if (loadLineMatch.matches()) {
			return processEodSubExecution(executionName, loadLineMatch);
		} else
			throw new BadAlgorithmException("bad algorithm load line: " + loadLine);
	}

	private Optional<String> processEodSubExecution(final Matcher match) throws BadAlgorithmException {
		final List<String> params = parseParams(match.group(2).trim());
		return processEodExecution(match.group(1).trim(), params);
	}

	private String processEodSubExecution(final String executionName, final Matcher match) throws BadAlgorithmException {
		final List<String> params = parseParams(match.group(2).trim());
		return processEodExecution(executionName, match.group(1).trim(), params);
	}

	private String processEodExecution(String realExecutionName, String algorithmName, final List<String> params) throws BadAlgorithmException {
		final Class<? extends EodAlgorithm> eodAlgorithm = algorithmsStorage.getEod(algorithmName);
		if (eodAlgorithm == null)
			throw new BadAlgorithmException("there is no such algorithm like " + algorithmName);
		final MutableAlgorithmConfiguration algorithmSettings = generateEodAlgorithmSettings(params);
		final String executionName = algorithmName + "(" + algorithmSettings.toString() + ")";
		final String oldRealExecutionName = registeredEodExecutions.get(executionName);
		if (oldRealExecutionName != null)
			return oldRealExecutionName;
		final EodExecution execution = new EodExecution(realExecutionName, eodAlgorithm, algorithmSettings);
		executionsStorage.addEodExecution(execution);
		return executionName;
	}

	private Optional<String> processEodExecution(String algorithmName, final List<String> params) throws BadAlgorithmException {
		final Class<? extends EodAlgorithm> eodAlgorithm = algorithmsStorage.getEod(algorithmName);
		if (eodAlgorithm == null)
			return Optional.empty();
		final MutableAlgorithmConfiguration algorithmSettings = generateEodAlgorithmSettings(params);
		final String executionName = algorithmName + "(" + algorithmSettings.toString() + ")";
		final String oldRealExecutionName = registeredEodExecutions.get(executionName);
		if (oldRealExecutionName != null)
			return Optional.of(oldRealExecutionName);
		final EodExecution execution = new EodExecution(executionName, eodAlgorithm, algorithmSettings);
		executionsStorage.addEodExecution(execution);
		return Optional.of(executionName);
	}

	private MutableAlgorithmConfiguration generateStockAlgorithmSettings(final List<String> params) throws BadAlgorithmException {
		final AlgorithmConfigurationImpl algorithmSettings = settings.clone();

		for (final String parameter : params) {
			final Matcher subAlgoMatch = Regexps.subAlgoParameter.matcher(parameter);
			final Matcher integerMatch = Regexps.integerParameter.matcher(parameter);
			final Matcher doubleMatch = Regexps.doubleParameter.matcher(parameter);
			final Matcher stringMatch = Regexps.stringParameter.matcher(parameter);
			final Matcher subExecutionMatch = Regexps.subExecutionParameter.matcher(parameter);
			if (subAlgoMatch.matches()) {
				final String subName = processStockSubExecution(subAlgoMatch);
				if (!namedStockExecutions.containsKey(subName)) {
					registeredStockExecutions.put(subName, subName);
				}
				algorithmSettings.addSubExecutionName(subName);
			} else if (integerMatch.matches()) {
				algorithmSettings.setInteger(integerMatch.group(1).trim(), Integer.valueOf(integerMatch.group(2).trim()));
			} else if (doubleMatch.matches()) {
				algorithmSettings.setDouble(doubleMatch.group(1).trim(), Double.valueOf(doubleMatch.group(2).trim()));
			} else if (stringMatch.matches()) {
				algorithmSettings.setString(stringMatch.group(1).trim(), stringMatch.group(2).trim());
			} else if (subExecutionMatch.matches()) {
				final String subExecutionName = subExecutionMatch.group(1).trim();
				final String executionCode = namedStockExecutions.get(subExecutionName);
				if (executionCode != null)
					algorithmSettings.addSubExecutionName(subExecutionName);
				else
					throw new BadAlgorithmException("unknown sub execution name: " + parameter);
			} else
				throw new BadAlgorithmException("bad sub execution line: " + parameter);
		}
		return algorithmSettings;
	}

	private MutableAlgorithmConfiguration generateEodAlgorithmSettings(final List<String> params) throws BadAlgorithmException {
		final AlgorithmConfigurationImpl algorithmSettings = settings.clone();

		for (final String parameter : params) {
			final Matcher subAlgoMatch = Regexps.subAlgoParameter.matcher(parameter);
			final Matcher integerMatch = Regexps.integerParameter.matcher(parameter);
			final Matcher doubleMatch = Regexps.doubleParameter.matcher(parameter);
			final Matcher stringMatch = Regexps.stringParameter.matcher(parameter);
			final Matcher subExecutionMatch = Regexps.subExecutionParameter.matcher(parameter);
			if (subAlgoMatch.matches()) {
				final Optional<String> subEodName = processEodSubExecution(subAlgoMatch);
				if (subEodName.isPresent()) {
					final String subName = subEodName.get();
					if (!namedEodExecutions.containsKey(subName)) {
						registeredEodExecutions.put(subName, subName);
					}
					algorithmSettings.addSubExecutionName(subName);
				} else {
					final String subStockName = processStockSubExecution(subAlgoMatch);
					if (!namedStockExecutions.containsKey(subStockName)) {
						registeredStockExecutions.put(subStockName, subStockName);
					}
					algorithmSettings.addSubExecutionName(subStockName);
				}
			} else if (integerMatch.matches()) {
				algorithmSettings.setInteger(integerMatch.group(1).trim(), Integer.valueOf(integerMatch.group(2).trim()));
			} else if (doubleMatch.matches()) {
				algorithmSettings.setDouble(doubleMatch.group(1).trim(), Double.valueOf(doubleMatch.group(2).trim()));
			} else if (stringMatch.matches()) {
				algorithmSettings.setString(stringMatch.group(1).trim(), stringMatch.group(2).trim());
			} else if (subExecutionMatch.matches()) {
				final String subExecutionName = subExecutionMatch.group(1).trim();
				final String executionEodCode = namedEodExecutions.get(subExecutionName);
				if (executionEodCode != null) {
					algorithmSettings.addSubExecutionName(subExecutionName);
				} else {
					final String executionStockCode = namedStockExecutions.get(subExecutionName);
					if (executionStockCode != null)
						algorithmSettings.addSubExecutionName(subExecutionName);
					else
						throw new BadAlgorithmException("unknown sub execution name: " + parameter);
				}
			} else
				throw new BadAlgorithmException("bad sub execution line: " + parameter);
		}
		return algorithmSettings;
	}

	private List<String> parseParams(final String paramsString) {
		int inBracketsStack = 0;
		int lastParamIndex = 0;
		final ArrayList<String> params = new ArrayList<>();
		for (int i = 0; i < paramsString.length(); ++i) {
			if (paramsString.charAt(i) == '(') {
				inBracketsStack += 1;
			} else if (paramsString.charAt(i) == ')') {
				inBracketsStack -= 1;
			} else if (paramsString.charAt(i) == ',' && inBracketsStack == 0) {
				params.add(paramsString.substring(lastParamIndex, i).trim());
				lastParamIndex = i + 1;
			}
		}
		if (lastParamIndex != paramsString.length()) {
			params.add(paramsString.substring(lastParamIndex, paramsString.length()).trim());
		}
		return params;
	}

	public ExecutionsStorage getExecutionsStorage() {
		return executionsStorage;
	}

}
