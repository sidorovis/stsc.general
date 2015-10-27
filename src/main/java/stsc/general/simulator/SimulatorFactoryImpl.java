package stsc.general.simulator;

public final class SimulatorFactoryImpl implements SimulatorFactory {

	@Override
	public Simulator createSimulator() {
		return new SimulatorImpl();
	}

}
