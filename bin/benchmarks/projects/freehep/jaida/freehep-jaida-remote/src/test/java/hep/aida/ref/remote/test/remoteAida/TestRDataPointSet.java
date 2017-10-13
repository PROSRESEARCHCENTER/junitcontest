package hep.aida.ref.remote.test.remoteAida;

import hep.aida.IAnalysisFactory;
import hep.aida.IDataPointSet;
import hep.aida.IDataPointSetFactory;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.dev.IDevTree;
import hep.aida.ref.remote.RemoteDataPointSet;
import hep.aida.ref.remote.RemoteServer;
import hep.aida.ref.remote.rmi.client.RmiStoreFactory;
import hep.aida.ref.remote.rmi.interfaces.RmiServer;
import hep.aida.ref.remote.rmi.server.RmiServerImpl;

import java.net.InetAddress;
import java.util.Random;

/**
 *
 * @author tonyj
 * @version $Id: TestRDataPointSet.java 13419 2007-11-30 19:38:32Z serbo $
 */
public class TestRDataPointSet extends RAIDATestCase {
    
    private IDevTree serverTree;
    private ITree clientTree;
    private RemoteServer treeServer;
    private RmiServer rmiTreeServer;
    private String localHost;
    private int port;
    private String serverName;
    
    private String pntsetPath0 = "/pointset";
	//copy directory;
    private String pntsetPath1 = "/newpntset";
    
    private String dataPntSetTitle1 = "Aida Data Point Set 1D";
    private String dataPntSetTitle2 = "Aida Data Point Set 2D";
    private String dataPntSetTitle3 = "Aida Data Point Set 3D";

    //private String dataPntSetTitle11= "Aida DPS 1D Copy";
    //private String dataPntSetTitle22= "Aida DPS 2D Copy";
    //private String dataPntSetTitle33= "Aida DPS 3D Copy";

	private int nEntries = 1234;
	private long timeout = 100;
	private double myscaler;
	
	private Random r;
    
    public TestRDataPointSet(String testName) {
        super(testName);
    }
    
    // is run by JUnit framework before the test
    protected void setUp() throws Exception {
        super.setUp();
        
        // Set host name, port, and server name
        localHost = null;
        try {
            localHost = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(localHost != null);
        
        port = 2001;
        serverName = "RmiAidaServer";
    }
    
    // is run by JUnit framework after the test
    protected void tearDown() throws Exception {
        super.tearDown();
        
        // disconnect client
        clientTree.close();
        
        // disconnect and shut down server
        ((RmiServerImpl) rmiTreeServer).disconnect();
        rmiTreeServer = null;
        
        treeServer.close();
        treeServer = null;
        
        serverTree.close();
        serverTree = null;
    }
    
    public void testCaseRDataPointSet() {
        
		if (TestRUtils.verbose) System.out.println("\n\n\n***testCaseRDataPointSet***");
        // create AIDA factories and AIDA Tree
        IAnalysisFactory af = IAnalysisFactory.create();
        ITreeFactory tf = af.createTreeFactory();
        serverTree = (IDevTree) tf.create();
        
        IDataPointSetFactory dpsf = af.createDataPointSetFactory(serverTree);
        
        serverTree.mkdir(pntsetPath0);
        serverTree.mkdir(pntsetPath1);
        
		//create an instance of IDataPointSet;
        serverTree.cd(pntsetPath0);
        IDataPointSet d1 = dpsf.create(dataPntSetTitle1, 1);
        IDataPointSet d2 = dpsf.create(dataPntSetTitle2, 2);
        IDataPointSet d3 = dpsf.create(dataPntSetTitle3, 3);
        assertTrue(d1 != null);
        assertTrue(d2 != null);
        assertTrue(d3 != null);
        
        //fill histograms, clouds, profile and datapointset;
        r = getRandomNumberGenerator();
        
		//fill the data point set with one-dimensional points;
        for (int i = 0; i < nEntries; i++) {
            double xVal = r.nextDouble();
            double e1 = r.nextDouble() / 10;
            double e2 = r.nextDouble() / 10;
            
            d1.addPoint();
            d1.point(i).coordinate(0).setValue(xVal);
            d1.point(i).coordinate(0).setErrorPlus(Math.abs(e1));
        }

		//fill the data point set with two-dimensional points;
		for(int i = 0; i < nEntries; i ++) {
			double xVal = r.nextDouble();
			double yVal = r.nextDouble();
			double e1   = r.nextDouble() / 10;
			double e2	= r.nextDouble() / 10;
			
			d2.addPoint();
			d2.point(i).coordinate(0).setValue(xVal);
			d2.point(i).coordinate(0).setErrorPlus(Math.abs(e1));
			d2.point(i).coordinate(1).setValue(yVal);
			d2.point(i).coordinate(1).setErrorPlus(Math.abs(e1));
		}

		//fill the data point set with three-dimensional points;
		for(int i = 0; i < nEntries; i ++) {
			double xVal = r.nextDouble();
			double yVal = r.nextDouble();
			double zVal = r.nextDouble();
			double e1   = r.nextDouble() / 10;
			double e2	= r.nextDouble() / 10;

			d3.addPoint();
			d3.point(i).coordinate(0).setValue(xVal);
			d3.point(i).coordinate(0).setErrorPlus(Math.abs(e1));
			d3.point(i).coordinate(1).setValue(yVal);
			d3.point(i).coordinate(1).setErrorPlus(Math.abs(e1));
			d3.point(i).coordinate(2).setValue(zVal);
			d3.point(i).coordinate(2).setErrorPlus(Math.abs(e1));
		}

        ////////////////////////////////
        // Now create RMI Server
        ////////////////////////////////
        
        // RMI bind name for server
        String treeBindName = "//"+localHost+":"+port+"/"+serverName;
        try {
            // General server that uses Remote AIDA interfaces (hep.aida.ref.remote.interfaces)
            boolean serverDuplex = true;
            treeServer = new RemoteServer(serverTree, serverDuplex);
            
            // Transport-layer RMI server that talks Remote AIDA to treeServer and RMI to the client
            rmiTreeServer = new RmiServerImpl(treeServer, treeBindName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(treeServer != null);
        assertTrue(rmiTreeServer != null);
        
        ////////////////////////////////
        // Now create RMI Client
        // Use RMIStoreFactory for that
        ////////////////////////////////
        
        // Create Rmi Client Tree
        boolean clientDuplex = true;
        boolean hurry = false;
        String options = "duplex=\""+clientDuplex+"\",RmiServerName=\"rmi:"+treeBindName+"\",hurry=\""+hurry+"\"";
        try {
            clientTree = tf.create(localHost, RmiStoreFactory.storeType, true, false, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        assertTrue(clientTree != null);
        
        //retrieve AIDA instances using the RMI
        IDataPointSet rd1 = null;
        IDataPointSet rd2 = null;
        IDataPointSet rd3 = null;
        
        try {
            rd1 = (IDataPointSet) clientTree.find(pntsetPath0 + "/" + dataPntSetTitle1);
            rd2 = (IDataPointSet) clientTree.find(pntsetPath0 + "/" + dataPntSetTitle2);
            rd3 = (IDataPointSet) clientTree.find(pntsetPath0 + "/" + dataPntSetTitle3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(rd1 != null);
        assertTrue(rd2 != null);
        assertTrue(rd3 != null);
        
        //check of two data point sets are equal;
        assertEquals(d1, (RemoteDataPointSet) rd1);
        assertEquals(d2, (RemoteDataPointSet) rd2);
        assertEquals(d3, (RemoteDataPointSet) rd3);

		//-------------------------------------------------------
		//Scale the DataPointSet on server side;
		myscaler = r.nextDouble();
		if (TestRUtils.verbose) System.out.println("\n***Scale the IDataPointSet(1D) instance on server tree with scaler: " + myscaler);
		d1.scale(myscaler);
		myscaler = r.nextDouble();
		if (TestRUtils.verbose) System.out.println("\n***Scale the IDataPointSet(2D) instance on server tree with scaler: " + myscaler);
		d2.scale(myscaler);
		myscaler = r.nextDouble();
		if (TestRUtils.verbose) System.out.println("\n***Scale the IDataPointSet(3D) instance on server tree with scaler: " + myscaler);
		d3.scale(myscaler);
		TestRUtils.waitForAWhile(timeout);
		assertEquals(d1, (RemoteDataPointSet) rd1);
		assertEquals(d2, (RemoteDataPointSet) rd2);
		assertEquals(d3, (RemoteDataPointSet) rd3);

		//-------------------------------------------------------
		//d1.setTitle("yet another new title");
		//remove or insert point from/to the DataPointSet randomly;
		if (TestRUtils.verbose) System.out.println("\n***Remove/Insert Data Point randomly from the data point set:");
		for(int i = 0 ; i < nEntries; i++){
			if(r.nextBoolean()) {
				//remote point from the data point set;
				rmvPntFrmDataPntSet(d1);
				rmvPntFrmDataPntSet(d2);
				rmvPntFrmDataPntSet(d3);
			} else {
				//insert a point to the data point set;
				addPntForDataPntSet(d1);
				addPntForDataPntSet(d2);
				addPntForDataPntSet(d3);
			}
		}
		TestRUtils.waitForAWhile(timeout);
		assertEquals(d1, (RemoteDataPointSet) rd1);

		//-------------------------------------------------------
		//Making copies of existing IDataPointSets;
		if (TestRUtils.verbose) System.out.println("\n***Copy IDataPointSet instances on server tree:");
		IDataPointSet dd1 = null;
		IDataPointSet dd2 = null;
		IDataPointSet dd3 = null;

		dd1 = dpsf.createCopy(pntsetPath1 + "/" + dataPntSetTitle1, d1);
		dd2 = dpsf.createCopy(pntsetPath1 + "/" + dataPntSetTitle2, d2);
		dd3 = dpsf.createCopy(pntsetPath1 + "/" + dataPntSetTitle3, d3);

		assertTrue(dd1 != null);
		assertTrue(dd2 != null);
		assertTrue(dd3 != null);

		IDataPointSet rdd1 = null;
		IDataPointSet rdd2 = null;
		IDataPointSet rdd3 = null;

		try{
            rdd1 = (IDataPointSet) clientTree.find(pntsetPath1 + "/" + dataPntSetTitle1);
            rdd2 = (IDataPointSet) clientTree.find(pntsetPath1 + "/" + dataPntSetTitle2);
            rdd3 = (IDataPointSet) clientTree.find(pntsetPath1 + "/" + dataPntSetTitle3);
		} catch(Exception e){
			e.printStackTrace();
		}
		assertTrue(rdd1 != null);
		assertTrue(rdd2 != null);
		assertTrue(rdd3 != null);
		assertEquals(dd1, (RemoteDataPointSet) rdd1);
		assertEquals(dd2, (RemoteDataPointSet) rdd2);
		assertEquals(dd3, (RemoteDataPointSet) rdd3);
		
		//-------------------------------------------------------
		//update the coordinate of data point set on serverTree (test setCoordinate method);
		if (TestRUtils.verbose) System.out.println("\n***Update data point (1D) on server tree:");
		uptPntForDataPntSet(d1);
		if (TestRUtils.verbose) System.out.println("\n***Update data point (2D) on server tree:");
		uptPntForDataPntSet(d2);
		if (TestRUtils.verbose) System.out.println("\n***Update data point (3D) on server tree:");
		uptPntForDataPntSet(d3);

		TestRUtils.waitForAWhile(timeout);
		assertEquals(d1, (RemoteDataPointSet) rd1);

		if (TestRUtils.verbose) System.out.println("\n***Add two IDataPointSet instances together on server tree:");
		try{
			d1 = dpsf.add(pntsetPath0 + "/" + dataPntSetTitle1, d1, dd1);
		} catch (Exception e){
			System.out.println("Exception cautht when adding to DataPointSet instances together!");
			e.printStackTrace();
		}
		//d2 = dpsf.add(pntsetPath0 + "/" + dataPntSetTitle2, d2, dd2);
		//d3 = dpsf.add(pntsetPath0 + "/" + dataPntSetTitle3, d3, dd3);

		assertTrue(d1 != null);
		//assertTrue(d2 != null);
		//assertTrue(d3 != null);

		TestRUtils.waitForAWhile(3 * timeout);
		//assertEquals(d1, (RemoteDataPointSet) rd1);
		//assertEquals(d2, (RemoteDataPointSet) rd2);
		//assertEquals(d3, (RemoteDataPointSet) rd3);
    }

	/**
	 * remove points from given data point set randomly;
	 * @param ps	the data point set to be updated;
	 */

	public void rmvPntFrmDataPntSet(IDataPointSet ps) {
		assertTrue(ps != null);
		int max = ps.size();
		int index = r.nextInt(max - 1);
		ps.removePoint(index);
	}
	
	/**
	 * Add points in the given data point set;
	 * The method will overwrite the existing values;
	 * This is to the test setCoordinate method;
	 * @param ps	the data point set to be updated;
	 */
	public void uptPntForDataPntSet(IDataPointSet ps){
		assertTrue(ps != null);
		int size = ps.size();	
		int dimension = ps.dimension();

		double[] xVal = new double[size];
		double[] yVal = new double[size];
		double[] zVal = new double[size];

		double[] xErr = new double[size];
		double[] yErr = new double[size];
		double[] zErr = new double[size];

		double[] mErr = new double[size];

		//scaler to scale errors;
		double scaleErr = r.nextDouble();
		//scaler to scale values;
		double scaleVal = r.nextDouble();

		if (TestRUtils.verbose) System.out.println("uptPntForDataPontSet.scaleErr = " + scaleErr);
		if (TestRUtils.verbose) System.out.println("uptPntForDataPontSet.scaleVal = " + scaleVal);

		switch(dimension){
			case 1:
				for(int i = 0; i < size; i ++) {
					xVal[i] = r.nextDouble();
					xErr[i] = r.nextDouble()/10.0;
					mErr[i] = r.nextDouble()/10.0;
				}
				ps.setCoordinate(0, xVal, xErr, mErr);

				ps.scaleErrors(scaleErr);
				ps.scaleValues(scaleVal);
				break;
			case 2:
				for(int i = 0; i < size; i ++) {
					xVal[i] = r.nextDouble();
					yVal[i] = r.nextDouble();

					xErr[i] = r.nextDouble()/10.0;
					yErr[i] = r.nextDouble()/10.0;
				}
				ps.setCoordinate(0, xVal, xErr);
				ps.setCoordinate(1, yVal, yErr);

				ps.scaleErrors(scaleErr);
				ps.scaleValues(scaleVal);

				break;
			case 3:
				for(int i = 0; i < size; i ++) {
					xVal[i] = r.nextDouble();
					yVal[i] = r.nextDouble();
					zVal[i] = r.nextDouble();

					xErr[i] = r.nextDouble()/10.0;
					yErr[i] = r.nextDouble()/10.0;
					zErr[i] = r.nextDouble()/10.0;
				}
				ps.setCoordinate(0, xVal, xErr);
				ps.setCoordinate(1, yVal, yErr);
				ps.setCoordinate(2, zVal, zErr);

				ps.scaleErrors(scaleErr);
				ps.scaleValues(scaleVal);

				break;
			default:
				if (TestRUtils.verbose) System.out.println("uptPntForDataPntSet.dimension = " + dimension);
				break;
		}
	}
	
	/**
	 * add points in given data point set randomly;
	 * @param ps	the data point set to be updated;
	 */
	public void addPntForDataPntSet(IDataPointSet ps) {
		double xVal, yVal, zVal;
		double e1, e2;
		assertTrue(ps != null);
		ps.addPoint();
		int index = ps.size();
		int dimension = ps.dimension();
		switch(dimension){
			case 1:
				xVal = r.nextDouble();
				e1 = r.nextDouble() / 10.0;
				e2 = r.nextDouble() / 10.0;
				ps.point(index - 1).coordinate(0).setValue(xVal);
				ps.point(index - 1).coordinate(0).setErrorPlus(Math.abs(e1));
				ps.point(index - 1).coordinate(0).setErrorMinus(Math.abs(e2));
				break;
			case 2:
				xVal = r.nextDouble();
				yVal = r.nextDouble();
				e1 = r.nextDouble() / 10.0;
				e2 = r.nextDouble() / 10.0;

				ps.point(index - 1).coordinate(0).setValue(xVal);
				ps.point(index - 1).coordinate(1).setValue(yVal);

				ps.point(index - 1).coordinate(0).setErrorPlus(Math.abs(e1));
				ps.point(index - 1).coordinate(0).setErrorMinus(Math.abs(e2));
				ps.point(index - 1).coordinate(1).setErrorPlus(Math.abs(e1));
				ps.point(index - 1).coordinate(1).setErrorMinus(Math.abs(e2));
				break;
			case 3:
				xVal = r.nextDouble();
				yVal = r.nextDouble();
				zVal = r.nextDouble();
				e1 = r.nextDouble() / 10.0;
				e2 = r.nextDouble() / 10.0;

				ps.point(index - 1).coordinate(0).setValue(xVal);
				ps.point(index - 1).coordinate(1).setValue(yVal);
				ps.point(index - 1).coordinate(2).setValue(yVal);

				ps.point(index - 1).coordinate(0).setErrorPlus(Math.abs(e1));
				ps.point(index - 1).coordinate(0).setErrorMinus(Math.abs(e2));
				ps.point(index - 1).coordinate(1).setErrorPlus(Math.abs(e1));
				ps.point(index - 1).coordinate(1).setErrorMinus(Math.abs(e2));
				ps.point(index - 1).coordinate(2).setErrorPlus(Math.abs(e1));
				ps.point(index - 1).coordinate(2).setErrorMinus(Math.abs(e2));
				break;
			default:
				if (TestRUtils.verbose) System.out.println("dimension error in addPntForDataPntSet!");
				break;
		}
	}
}
