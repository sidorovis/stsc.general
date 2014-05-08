package stsc.algorithms.eod.privitive;

import java.util.HashMap;
import java.util.Date;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.eod.primitive.TestingEodAlgorithm;
import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.testhelper.TestHelper;
import junit.framework.TestCase;

public class SimpleTradingAlgorithmTest extends TestCase {
	public void testTestingEodAlgorithm() throws BadSignalException, BadAlgorithmException {

		final EodAlgorithm.Init init = TestHelper.getEodAlgorithmInit();

		TestingEodAlgorithm tea = new TestingEodAlgorithm(init);
		tea.process(new Date(), new HashMap<String, Day>());
	}
}