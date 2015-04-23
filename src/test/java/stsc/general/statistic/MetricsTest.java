package stsc.general.statistic;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

public class MetricsTest {

	@Test
	public void testMetrics() {
		final Metrics m = new Metrics(Collections.emptyMap(), Collections.emptyMap());
		Assert.assertTrue(m.getDoubleMetrics().isEmpty());
		Assert.assertTrue(m.getIntegerMetrics().isEmpty());
	}
}
