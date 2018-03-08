package hep.aida.ref.remote.test.remoteAida;

import hep.aida.IAnnotation;
import hep.aida.IAxis;
import hep.aida.IBaseHistogram;
import hep.aida.ICloud;
import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.IDataPointSet;
import hep.aida.IHistogram;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IManagedObject;
import hep.aida.IProfile;
import hep.aida.IProfile1D;
import hep.aida.ref.remote.RemoteCloud1D;
import hep.aida.ref.remote.RemoteCloud2D;
import hep.aida.ref.remote.RemoteDataPointSet;
import hep.aida.ref.remote.RemoteHistogram1D;
import hep.aida.ref.remote.RemoteHistogram2D;
import hep.aida.ref.remote.RemoteManagedObject;
import hep.aida.ref.remote.RemoteProfile1D;
import hep.aida.test.AidaTestCase;


/**
 *
 * @author serbo
 * @version $Id: RAIDATestCase.java 13787 2010-11-19 02:03:52Z turri $
 */
public abstract class RAIDATestCase extends AidaTestCase {

    /**
     * The default constructor.
     *
     */
    public RAIDATestCase(java.lang.String name) {
        super( name );
    }
    
    // don't want to compare annotations directly, because entries can be
    // modified during the transport

    
    // Here first annotation is from original histogram and secont is from 
    // histogram we got through RMI
    public static void assertEqualsRemote( IAnnotation a1, IAnnotation a2 ) {
        if ( a1 != null && a2 != null ) {
            int size = a1.size();
             for ( int i = 0; i<size; i++ ) {
                String key = a1.key(i);
                assertEquals( a1.value(key), a2.value(key) );
            }
        } else {
            assertTrue( false );
        }
    }
    
    
    public static void assertEqualsNoAnnotation( IBaseHistogram h1, IBaseHistogram h2 ) {
        if ( h1 instanceof IManagedObject && h2 instanceof IManagedObject )
            assertEquals( (IManagedObject)h1, (IManagedObject)h2 );

        assertEquals(h1.dimension(), h2.dimension());
        assertEquals(h1.title(), h2.title());

        assertEquals(h1.entries(), h2.entries());
		
        //assertEqualsRemote(h1.annotation(), h2.annotation());
    }  
     
	public static void assertEqualsNoAnnotation( IHistogram h1, IHistogram h2 ) {
       	assertEqualsNoAnnotation( (IBaseHistogram)h1, (IBaseHistogram)h2 );
        
       	assertEquals(h1.allEntries(), h2.allEntries());
       	assertEquals(h1.extraEntries(), h2.extraEntries());

       	assertEqualsDouble(h1.sumBinHeights(), h2.sumBinHeights(), h1.sumBinHeights());
       	assertEqualsDouble(h1.sumExtraBinHeights(), h2.sumExtraBinHeights(), h1.sumExtraBinHeights());
       	assertEqualsDouble(h1.sumAllBinHeights(), h2.sumAllBinHeights(), h1.sumAllBinHeights());

       	assertEqualsDouble(h1.minBinHeight(),h2.minBinHeight(),h1.minBinHeight());
       	assertEqualsDouble(h1.maxBinHeight(),h2.maxBinHeight(),h1.maxBinHeight());

		//not checking equvalentBinEntries() method due to rounding and precision problems.
       	//assertEquals( h1.equivalentBinEntries(), h2.equivalentBinEntries(), relPrec);
       	//assertEquals( h1.equivalentBinEntries(), h2.equivalentBinEntries(), 0);
   	}
    
	public static void assertEquals( IHistogram1D h1, RemoteHistogram1D h2 ) {
       	assertEqualsNoAnnotation( (IHistogram)h1, (IHistogram)h2 );
       	assertEquals(h1.axis(), h2.axis());
        
       	int bins = h1.axis().bins();

       	for ( int i=IAxis.UNDERFLOW_BIN; i<bins; i++ ) {
       		double orderX;
       		if ( i == IAxis.UNDERFLOW_BIN ) orderX = Math.abs(h1.axis().binUpperEdge(i));
       		else orderX = Math.abs(h1.axis().binLowerEdge(i));
       		if ( orderX == 0 ) orderX = h1.axis().binWidth(i);

          	assertEqualsDouble(h1.binMean(i), h2.binMean(i), orderX, h1.binEntries(i));
           	assertEqualsDouble(h1.binHeight(i),h2.binHeight(i), h1.binHeight(i));
           	assertEqualsDouble(h1.binError(i), h2.binError(i), h1.binError(i));
           	assertEquals(h1.binEntries(i), h2.binEntries(i));
       	}
        
       	assertEqualsDouble(h1.mean(), h2.mean(), h1.rms(), h1.entries());
       	assertEqualsDouble(h1.rms(), h2.rms(), h1.rms(), h1.entries());
   	}

	public static void assertEquals(IHistogram2D h1, RemoteHistogram2D h2){
		assertEqualsNoAnnotation((IHistogram)h1, (IHistogram)h2);
		assertEquals(h1.xAxis(), h2.xAxis());
		assertEquals(h1.yAxis(), h2.yAxis());

        int xbins = h1.xAxis().bins();
       	int ybins = h1.yAxis().bins();

       	for ( int i = IAxis.UNDERFLOW_BIN; i < xbins; i++ ) {

       		assertEquals(h1.binEntriesX(i), h2.binEntriesX(i));

       		//assertEqualsDouble(h1.binHeightX(i), h2.binHeightX(i), h1.binHeightX(i));

       		/* for ( int j=IAxis.UNDERFLOW_BIN; j<ybins; j++ ) {
           		if ( i == 0 ) {
           			assertTrue(h1.binEntriesY(j) == h2.binEntriesY(j));
           			assertEqualsDouble(h1.binHeightY(j), h2.binHeightY(j), h1.binHeightY(j));
           		}

           		double orderX;
           		if ( i == IAxis.UNDERFLOW_BIN ) orderX = Math.abs(h1.xAxis().binUpperEdge(i));
           		else orderX = Math.abs(h1.xAxis().binLowerEdge(i));
           		if ( orderX == 0 ) orderX = h1.xAxis().binWidth(i);

           		double orderY;
           		if ( j == IAxis.UNDERFLOW_BIN ) orderY = Math.abs(h1.yAxis().binUpperEdge(j));
              	else orderY = Math.abs(h1.yAxis().binLowerEdge(j));
               	if ( orderY == 0 ) orderY = h1.yAxis().binWidth(j);

              	assertEqualsDouble(h1.binMeanX(i,j), h2.binMeanX(i,j), orderX, h1.binEntries(i,j));
               	assertEqualsDouble(h1.binMeanY(i,j), h2.binMeanY(i,j), orderY, h1.binEntries(i,j));
               	assertEqualsDouble(h1.binHeight(i,j), h2.binHeight(i,j), h1.binHeight(i,j));
               	assertEqualsDouble(h1.binError(i,j), h2.binError(i,j), h1.binError(i,j));
               	assertTrue(h1.binEntries(i,j) == h2.binEntries(i,j));
           	} */
       	}

       	assertEqualsDouble(h1.meanX(), h2.meanX(), h1.rmsX(), h1.entries());
       	assertEqualsDouble(h1.rmsX(), h2.rmsX(), h1.rmsX(), h1.entries());
       	assertEqualsDouble(h1.meanY(), h2.meanY(), h1.rmsY(), h1.entries());
       	assertEqualsDouble(h1.rmsY(), h2.rmsY(), h1.rmsY(), h1.entries());
	}

	static void assertEqualsNoAnnotation(ICloud c1, ICloud c2){
       	assertEqualsNoAnnotation( (IBaseHistogram)c1, (IBaseHistogram)c2 );
      	assertEqualsDouble(c1.sumOfWeights(), c2.sumOfWeights(), 1, c1.entries());
		assertEquals(c1.isConverted(), c2.isConverted());
	}

	static void assertEquals(ICloud1D c1, RemoteCloud1D c2){
		assertEqualsNoAnnotation((ICloud) c1, (ICloud) c2);

        assertEqualsDouble(c1.lowerEdge(), c2.lowerEdge(), c1.lowerEdge());
        assertEqualsDouble(c1.upperEdge(), c2.upperEdge(), c1.upperEdge());

        assertEqualsDouble(c1.mean(), c2.mean(), c1.rms(), c1.entries() );
        assertEqualsDouble(c1.rms(), c2.rms(), c1.rms(), c1.entries() );
	}

	static void assertEquals(ICloud2D c1, RemoteCloud2D c2){
		assertEqualsNoAnnotation((ICloud) c1, (ICloud) c2);

        assertTrue( c1.lowerEdgeX() == c2.lowerEdgeX() );
        assertTrue( c1.upperEdgeX() == c2.upperEdgeX() );
        assertTrue( c1.lowerEdgeY() == c2.lowerEdgeY() );
        assertTrue( c1.upperEdgeY() == c2.upperEdgeY() );

        assertEqualsDouble( c1.meanX(), c2.meanX(), c1.rmsX(), c1.entries() );
        assertEqualsDouble( c1.rmsX(), c2.rmsX(), c1.rmsX(), c1.entries() );
        assertEqualsDouble( c1.meanY(), c2.meanY(), c1.rmsY(), c1.entries() );
        assertEqualsDouble( c1.rmsY(), c2.rmsY(), c1.rmsY(), c1.entries() );

        /* IHistogram2D h1 = null;
        IHistogram2D h2 = null;
        try {
            h1 = c1.histogram();
        } catch (RuntimeException re) {
			re.printStackTrace();
		}
        try {
            h2 = c2.histogram();
        } catch (RuntimeException re) {
			re.printStackTrace();
		}

        if ( h1 != null && h2 != null ) {
            assertEquals( h1, h2 );
        } else if ( h1 == null && h2 == null ) {
            int entries = c1.entries();
            for ( int i=0; i<entries; i++ ) {
                assertTrue( c1.valueX(i) == c2.valueX(i) );
                assertTrue( c1.valueY(i) == c2.valueY(i) );
                assertTrue( c1.weight(i) == c2.weight(i) );
            }
        } else {
            assertTrue( false );
        } */
	}
	
	static void assertEqualsNoAnnotation(IProfile p1, IProfile p2){
	   	assertEqualsNoAnnotation( (IBaseHistogram)p1, (IBaseHistogram)p2 );
		
        assertEquals(p1.allEntries(), p2.allEntries());
        assertEquals(p1.extraEntries(), p2.extraEntries());
        assertEqualsDouble(p1.sumBinHeights(), p2.sumBinHeights(), p1.sumBinHeights());
        assertEqualsDouble(p1.sumExtraBinHeights(), p2.sumExtraBinHeights(), p1.sumExtraBinHeights());
        assertEqualsDouble(p1.sumAllBinHeights(), p2.sumAllBinHeights(), p1.sumAllBinHeights());
        assertEqualsDouble(p1.minBinHeight(),p2.minBinHeight(),p2.minBinHeight());
        assertEqualsDouble(p1.maxBinHeight(),p2.maxBinHeight(),p2.maxBinHeight());

	}

	static void assertEquals(IProfile1D p1, RemoteProfile1D p2){
		assertEqualsNoAnnotation((IProfile) p1, (IProfile) p2);
		
	    assertEquals(p1.axis(), p2.axis());

        int bins = p1.axis().bins();

        for ( int i=IAxis.UNDERFLOW_BIN + 2; i<bins; i++ ) {
            assertEqualsDouble(p1.binMean(i), p2.binMean(i), p1.binRms(i), p1.binEntries(i));
            assertEqualsDouble(p1.binHeight(i),p2.binHeight(i), p1.binHeight(i));
            assertEqualsDouble(p1.binError(i), p2.binError(i), p1.binError(i));
            assertEqualsDouble(p1.binRms(i), p2.binRms(i)/*, p1.axis().binWidth(i), p1.binEntries(i)*/);
            assertEquals(p1.binEntries(i), p2.binEntries(i));
        }

        assertEqualsDouble(p1.mean(), p2.mean(), p1.rms(), p1.entries());
        assertEqualsDouble(p1.rms(), p2.rms(), p1.rms(), p1.entries());
	}
	
	static void assertEquals(IManagedObject m1, RemoteManagedObject m2){
		assertEquals(m1.name(), m2.name());
	}

	static void assertEquals(IDataPointSet d1, RemoteDataPointSet d2){
		if(d1 instanceof IManagedObject && d2 instanceof RemoteManagedObject) 
			assertEquals((IManagedObject) d1, (RemoteManagedObject) d2);

		assertEquals(d1.title(), d2.title());
		assertEquals(d1.dimension(), d2.dimension());
		assertEquals(d1.size(), d2.size());

		for(int i = 0; i < d1.size(); i += 50) {
			assertEquals(d1.point(i), d2.point(i));
			for(int j = 0; j < d1.dimension(); j ++) {
				assertEqualsDouble(d1.lowerExtent(j), d2.lowerExtent(j), d1.lowerExtent(j));
				assertEqualsDouble(d1.upperExtent(j), d2.upperExtent(j), d1.lowerExtent(j));
			}
		}
	}
}
