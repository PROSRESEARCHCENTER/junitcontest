package hep.aida.swig.test;

import hep.aida.IAxis;
import hep.aida.IHistogram1D;
import hep.aida.swig.AIDA;
import hep.aida.jni.NarSystem;
import junit.framework.TestCase;

public class TestCallFromJava extends TestCase {

	public final static native long getHistogram();

	public TestCallFromJava(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		NarSystem.loadLibrary();
	}

	public void testCallFromJava() throws Exception {
		IHistogram1D h1d = AIDA.getTestHistogram();
		assertNotNull(h1d);
		assertEquals("CHistogram", h1d.title());
		assertEquals(100.0, h1d.binMean(50), 0);
		IAxis axis = h1d.axis();
		assertNotNull(axis);
		assertEquals(4, axis.binLowerEdge(8), 0);
		IHistogram1D h1a = new JIHistogram1D();
		assertNotNull(h1a);
		assertEquals("JHistogram", h1a.title());
		h1d.add(h1a);
		assertEquals(150, h1d.binMean(25), 0);
	}
}
