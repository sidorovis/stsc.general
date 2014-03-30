package stsc.algorithms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import stsc.storage.SignalsStorage;

public class StockExecution {
	private final String executionName;
	private final String algorithmName;
	private final Class<? extends StockAlgorithm> algorithmType;

	private final AlgorithmSettings settings;

	public StockExecution(final String executionName, final String algorithmName, AlgorithmSettings settings)
			throws BadAlgorithmException {
		this.executionName = executionName;
		this.algorithmName = algorithmName;
		this.settings = settings;
		try {
			Class<?> classType = Class.forName(algorithmName);
			this.algorithmType = classType.asSubclass(StockAlgorithm.class);
		} catch (ClassNotFoundException e) {
			throw new BadAlgorithmException("Algorithm class '" + algorithmName + "' was not found: " + e.toString());
		}
	}

	public StockExecution(String executionName, Class<? extends StockAlgorithm> algorithmType,
			AlgorithmSettings settings) {
		this.executionName = executionName;
		this.algorithmName = algorithmType.getName();
		this.algorithmType = algorithmType;
		this.settings = settings;
	}

	public String getName() {
		return executionName;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public StockAlgorithm getInstance(final String stockName, final SignalsStorage signalsStorage)
			throws BadAlgorithmException {
		try {
			final Class<?>[] params = { StockAlgorithm.Init.class };
			final Constructor<? extends StockAlgorithm> constructor = algorithmType.getConstructor(params);

			final StockAlgorithm.Init init = new StockAlgorithm.Init();
			init.stockName = stockName;
			init.executionName = executionName;
			init.signalsStorage = signalsStorage;
			init.settings = settings;

			final Object[] values = { init };

			try {
				final StockAlgorithm algo = constructor.newInstance(values);
				return algo;
			} catch (InvocationTargetException e) {
				throw new BadAlgorithmException("Exception while loading algo: " + algorithmName + "( " + executionName
						+ " ) , exception: " + e.getTargetException().toString());
			}
		} catch (NoSuchMethodException e) {
			throw new BadAlgorithmException("Bad Algorithm '" + algorithmName + "', constructor was not found: "
					+ e.toString());
		} catch (SecurityException e) {
			throw new BadAlgorithmException("Bad Algorithm '" + algorithmName + "', constructor could not be called: "
					+ e.toString());
		} catch (InstantiationException e) {
			throw new BadAlgorithmException("Bad Algorithm '" + algorithmName + "', instantiation exception: "
					+ e.toString());
		} catch (IllegalAccessException e) {
			throw new BadAlgorithmException("Bad Algorithm '" + algorithmName
					+ "', instantiation impossible due to illegal access: " + e.toString());
		} catch (IllegalArgumentException e) {
			throw new BadAlgorithmException("Bad Algorithm '" + algorithmName + "', illegal arguments: " + e.toString());
		}
	}
}