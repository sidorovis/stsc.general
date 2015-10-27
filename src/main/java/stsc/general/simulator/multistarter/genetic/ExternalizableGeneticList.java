package stsc.general.simulator.multistarter.genetic;

import java.util.List;

import stsc.common.FromToPeriod;

public interface ExternalizableGeneticList extends GeneticList {

	public long getId();

	public FromToPeriod getPeriod();

	public long size();

	//

	public List<GeneticExecutionInitializer> getStockInitializers();

	public List<GeneticExecutionInitializer> getEodInitializers();

	//

}
