package hep.aida.ref.remote.test.remoteAida;

import hep.aida.AlreadyConvertedException;
import hep.aida.IAnalysisFactory;
import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.IHistogramFactory;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.dev.IDevTree;
import hep.aida.ref.remote.RemoteCloud1D;
import hep.aida.ref.remote.RemoteCloud2D;
import hep.aida.ref.remote.RemoteServer;
import hep.aida.ref.remote.rmi.client.RmiStoreFactory;
import hep.aida.ref.remote.rmi.interfaces.RmiServer;
import hep.aida.ref.remote.rmi.server.RmiServerImpl;

import java.net.InetAddress;
import java.util.Random;

/**
 *
 * @author tonyj
 * @version $Id: TestRCloud.java 13419 2007-11-30 19:38:32Z serbo $
 */
public class TestRCloud extends RAIDATestCase {
    
    private IDevTree serverTree;
    private ITree clientTree;
    private RemoteServer treeServer;
    private RmiServer rmiTreeServer;
    private String localHost;
    private int port;
    private String serverName;
    
    //Paramters adapted from AIDA test suite;
    private int nEntries = 1234;
    private int xbins = 40;
    private int ybins = 20;
    private int zbins = 10;
    
    private String cloudPath= "/clouds";
    
    private double xLowerEdge = -2.3;
    private double xUpperEdge = 4.2;
    private double yLowerEdge = -6.1;
    private double yUpperEdge = 1.2;
    private double zLowerEdge = -0.6;
    private double zUpperEdge = 7.5;
    
    private String cloud1DTitle = "Aida 1D Cloud";
    private String cloud2DTitle = "Aida 2D Cloud";
    
	private long timeout = 2000;
	private double myscaler;
    
    //private String cloud1DOpts  = "Some, options=false , for=true, cloud1D=maybe ; autoConvert = false";
    private String cloud1DOpts  = "autoConvert = false";
    private String cloud2DOpts  = "autoConvert = false";
    
    public TestRCloud(String testName) {
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
    
    public void testCaseRCloud() {
        
        // create AIDA factories and AIDA Tree
        IAnalysisFactory af = IAnalysisFactory.create();
        ITreeFactory tf = af.createTreeFactory();
        serverTree = (IDevTree) tf.create();
        IHistogramFactory hf = af.createHistogramFactory(serverTree);
        
        serverTree.mkdir(cloudPath);
        
		//create an instance of ICloud1D and an instance of ICloud2D;
        serverTree.cd(cloudPath);
        //ICloud1D c1 = hf.createCloud1D(cloud1DTitle);
        ICloud1D c1 = hf.createCloud1D(cloudPath + "/" + cloud1DTitle, cloud1DTitle, 10 * nEntries, cloud1DOpts);
        ICloud2D c2 = hf.createCloud2D(cloud2DTitle);
        assertTrue(c1 != null);
        assertTrue(c2 != null);
		assertTrue(!c1.isConverted());

        Random r = getRandomNumberGenerator();
        
        for (int i = 0; i < 10 * nEntries; i++) {
            double xVal = r.nextGaussian();
            double yVal = r.nextGaussian();
            double w = r.nextDouble();

            c1.fill(xVal);
            c2.fill(xVal, yVal);
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
        ICloud1D rc1 = null;
        ICloud2D rc2 = null;
        
        try {
            rc1 = (ICloud1D) clientTree.find(cloudPath + "/" + cloud1DTitle);
            rc2 = (ICloud2D) clientTree.find(cloudPath + "/" + cloud2DTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(rc1 != null);
        assertTrue(rc2 != null);
        
        //check if two clouds are equal
        assertEquals(c1, (RemoteCloud1D) rc1);
        assertEquals(c2, (RemoteCloud2D) rc2);
        
		//-------------------------------------------------------
		try{
			c1.convertToHistogram();
		} catch (AlreadyConvertedException ae){
			ae.printStackTrace();
		}
		assertTrue(c1.isConverted());
		TestRUtils.waitForAWhile(timeout);

		//The change of state should be updated to client tree automatically; 
		//It's possiblely a bug here.
		assertTrue(((RemoteCloud1D)rc1).isConverted());

		//c1.setTitle("new " + cloud1DTitle);
		myscaler = r.nextDouble();
		if (TestRUtils.verbose) System.out.println("Scale the ICloud1D instance on server tree with scaler: " + myscaler);
		c1.scale(myscaler);
		TestRUtils.waitForAWhile(timeout);
		//The update of c1 on server side should be reflected on client tree (DuplexMode);
		assertEquals(c1, (RemoteCloud1D)rc1);

		//update ICloud1D on server side;
		for(int i = 0; i < nEntries; i ++){
            double xVal = r.nextGaussian();
            double w = r.nextDouble();
			c1.fill(xVal, w);
		}

		TestRUtils.waitForAWhile(timeout);
		assertEquals(c1, (RemoteCloud1D)rc1);

		try{
			c2.convertToHistogram();
		} catch (AlreadyConvertedException ae){
			ae.printStackTrace();
		}
		assertTrue(c2.isConverted());
		TestRUtils.waitForAWhile(timeout);

		//The change of state should be updated to client tree automatically; 
		//It's currently a bug here.
		assertTrue(((RemoteCloud2D)rc2).isConverted());

		myscaler = r.nextDouble();
		if (TestRUtils.verbose) System.out.println("Scale the ICloud2D instance on server tree with scaler: " + myscaler);
		c2.scale(myscaler);
		TestRUtils.waitForAWhile(timeout);
		//The update of c2 on server side should be reflected on client tree (DuplexMode);
		assertEquals(c2, (RemoteCloud2D)rc2);

		//update ICloud2D on server side;
		for(int i = 0; i < nEntries; i ++){
            double xVal = r.nextGaussian();
            double yVal = r.nextGaussian();
            double w = r.nextDouble();
			c2.fill(xVal, yVal, w);
		}

		TestRUtils.waitForAWhile(timeout);
		assertEquals(c2, (RemoteCloud2D)rc2);
    }

}
