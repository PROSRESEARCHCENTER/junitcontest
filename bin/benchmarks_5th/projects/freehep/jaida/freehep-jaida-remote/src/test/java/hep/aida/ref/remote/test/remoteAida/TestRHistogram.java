package hep.aida.ref.remote.test.remoteAida;

import hep.aida.IAnalysisFactory;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogramFactory;
import hep.aida.IProfile1D;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.dev.IDevTree;
import hep.aida.ref.remote.RemoteHistogram1D;
import hep.aida.ref.remote.RemoteHistogram2D;
import hep.aida.ref.remote.RemoteProfile1D;
import hep.aida.ref.remote.RemoteServer;
import hep.aida.ref.remote.rmi.client.RmiStoreFactory;
import hep.aida.ref.remote.rmi.interfaces.RmiServer;
import hep.aida.ref.remote.rmi.server.RmiServerImpl;

import java.net.InetAddress;
import java.util.Random;

/**
 *
 * @author tonyj
 * @version $Id: TestRHistogram.java 13425 2007-12-05 00:14:57Z serbo $
 */
public class TestRHistogram extends RAIDATestCase {
    
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
    
    private String histPath = "/hists";
    private String histNewPath = "/newhists";
    private String newProfPath = "/newprofiles";
    private String profilePath = "/profiles";
    
    private double xLowerEdge = -2.3;
    private double xUpperEdge = 4.2;
    private double yLowerEdge = -6.1;
    private double yUpperEdge = 1.2;
    private double zLowerEdge = -0.6;
    private double zUpperEdge = 7.5;
    
    private String hist1DTitle = "Aida 1D Histogram";
    private String hist2DTitle = "Aida 2D Histogram";
    private String hist3DTitle = "Aida 3D Histogram";
    
    private String profile1DTitle = "Aida 1D Profile";
    private String profile2DTitle = "Aida 2D Profile";
    
    private long timeout = 2000;
    private double myscaler;
    
    public TestRHistogram(String testName) {
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
    
    public void testCaseRHistogram() {
        
        // create AIDA factories and AIDA Tree
        IAnalysisFactory af = IAnalysisFactory.create();
        ITreeFactory tf = af.createTreeFactory();
        serverTree = (IDevTree) tf.create();
        IHistogramFactory hf = af.createHistogramFactory(serverTree);
        
        serverTree.mkdir(histPath);
        serverTree.mkdir(histNewPath);
        serverTree.mkdir(newProfPath);
        serverTree.mkdir(profilePath);
        
        //create an instance of Histogram1D and an instance of Histogram2D on server tree;
        serverTree.cd(histPath);
        IHistogram1D h1 = hf.createHistogram1D(hist1DTitle, xbins, xLowerEdge, xUpperEdge);
        IHistogram2D h2 = hf.createHistogram2D(hist2DTitle, xbins, xLowerEdge, xUpperEdge, ybins, yLowerEdge, yUpperEdge);
        
        assertTrue(h1 != null);
        assertTrue(h2 != null);
        
        //create an instance of IProfile1D;
        serverTree.cd(profilePath);
        IProfile1D p1 = hf.createProfile1D(profile1DTitle, xbins, xLowerEdge, xUpperEdge);
        
        assertTrue(p1 != null);
        
        Random r = getRandomNumberGenerator();
        
        for (int i = 0; i < 10 * nEntries; i++) {
            double xVal = r.nextGaussian();
            double yVal = r.nextGaussian();
            double w = r.nextDouble();
            h1.fill(xVal, w);
            h2.fill(xVal, yVal, w);
            
            p1.fill(xVal, yVal, w);
            
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
        IHistogram1D rh1 = null;
        IHistogram2D rh2 = null;
        
        IProfile1D rp1 = null;
        
        try {
            rh1 = (IHistogram1D) clientTree.find(histPath + "/" + hist1DTitle);
            rh2 = (IHistogram2D) clientTree.find(histPath + "/" + hist2DTitle);
            
            rp1 = (IProfile1D) clientTree.find(profilePath + "/" + profile1DTitle);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(rh1 != null);
        assertTrue(rh2 != null);
        
        assertTrue(rp1 != null);
        
        //check if the remote and local IHistogram instances are equal
        assertEquals(h1, (RemoteHistogram1D) rh1);
        assertEquals(h2, (RemoteHistogram2D) rh2);
        
        //check if the remote and local IProfile instnaces are equal
        assertEquals(p1, (RemoteProfile1D) rp1);
        
        //-------------------------------------------------------
        //create a copy of IHistogram1D/2D instances on server tree;
        serverTree.cd(histNewPath);
        serverTree.cp(histPath + "/" + hist1DTitle, histNewPath);
        serverTree.cp(histPath + "/" + hist2DTitle, histNewPath);
        
        TestRUtils.waitForAWhile(timeout);
        
        IHistogram1D h11 = null;
        IHistogram2D h21 = null;
        h11 = (IHistogram1D) serverTree.find(histNewPath + "/" + hist1DTitle);
        h21 = (IHistogram2D) serverTree.find(histNewPath + "/" + hist2DTitle);
        assert(h11 != null);
        assert(h21 != null);
        
        IHistogram1D rh11 = null;
        IHistogram2D rh21 = null;
        try {
            rh11 = (IHistogram1D) clientTree.find(histNewPath + "/" + hist1DTitle);
            rh21 = (IHistogram2D) clientTree.find(histNewPath + "/" + hist2DTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        assertTrue(rh11 != null);
        assertTrue(rh21 != null);
        
        //assertEquals(h1, h11);
        //assertEquals(h2, h21);
        
        assertEquals(h11, (RemoteHistogram1D)rh11);
        assertEquals(h21, (RemoteHistogram2D)rh21);
        
        //-------------------------------------------------------
        //scale the IHistogram1D instance;
        myscaler = r.nextDouble();
        if (TestRUtils.verbose) System.out.println("Scale the Histogram1D instance on server tree with scaler: " + myscaler);
        h1.scale(myscaler);
        TestRUtils.waitForAWhile(timeout);
        //The update of h1 on server side should be reflected on client tree (DuplexMode);
        assertEquals(h1, (RemoteHistogram1D)rh1);
        
        //---------------------------------------------------------
        //add h11 to h1;
        //h1.add(h11);
        //h2.add(h21);
                /* IHistogram1D h1Add = null;
                String newName = "Add";
                try{
                        h1Add = hf.add(newName, h1, h11);
                } catch (IllegalArgumentException ie){
                        ie.printStackTrace();
                }
                assertTrue(h1Add != null);
                System.out.println("h1Add.allEntries() = " + h1Add.entries());
                System.out.println("h1.allEntries() = " + h1.entries());
                System.out.println("h11.allEntries() = " + h11.entries());
                 */
        //h1.add(h11);
        //assertEquals(h1, h1Add);
        
                /* IHistogram1D rh1Add = null;
                try{
            rh1Add = (IHistogram1D) clientTree.find(histPath + "/" + "Add");
                } catch (Exception e){
                        System.out.println("Caught exception when trying to find rh1Add!");
                        e.printStackTrace();
                }
                assertTrue(rh1Add != null); */
        
        //Sleep for a while before call the assertion;
        //TestRUtils.waitForAWhile(timeout);
        //the addition should be relected automatically to client side (duplexMode);
        //assertEquals(h1Add, (RemoteHistogram1D) rh1Add);
        
        //update IHistogram1D on server side;
        for(int i = 0; i < nEntries; i ++){
            double xVal = r.nextGaussian();
            double w = r.nextDouble();
            h1.fill(xVal, w);
        }
        
        //h1.setTitle("new title for Histogram1D instance");
        //Sleep for a while before call the assertion;
        TestRUtils.waitForAWhile(timeout);
        
        //The update of h1 on server side should be reflected on client tree (DuplexMode);
        assertEquals(h1, (RemoteHistogram1D)rh1);
        
        //A possible bug here. Change name of AIDA instance on server,
        //which is not updated to client as it's supposed to;
        //Happens on IHistogram1D/2D, ICloud1D/2D and etc..;
                /* 
                 h1.setTitle("new name");
                TestRUtils.waitForAWhile(timeout);
                assertEquals(h1, (RemoteHistogram1D)rh1);
                */
        //---------------------------------------------------------
        //update IHistogram2D on server side;
        for(int i = 0; i < nEntries; i ++){
            double xVal = r.nextGaussian();
            double yVal = r.nextGaussian();
            double w = r.nextDouble();
            h2.fill(xVal, yVal, w);
        }
        
        //Sleep for a while before call the assertion;
        TestRUtils.waitForAWhile(timeout);
        
        //The update of h2 on server side should updated on client tree (DuplexMode);
        assertEquals(h2, (RemoteHistogram2D)rh2);
        
        myscaler = r.nextDouble();
        if (TestRUtils.verbose) System.out.println("Scale the Histogram2D instance on server tree with scaler: " + myscaler);
        h2.scale(myscaler);
        TestRUtils.waitForAWhile(timeout);
        //The update of h1 on server side should be reflected on client tree (DuplexMode);
        assertEquals(h2, (RemoteHistogram2D)rh2);
        
        //testing the add(IHistogram) method;
        h2.add(h21);
        TestRUtils.waitForAWhile(timeout);
        assertEquals(h2, (RemoteHistogram2D)rh2);
        
        //Remove the IHistogram1D instance from serverTree;
                /* serverTree.rm(histNewPath + "/" + hist1DTitle);
                TestRUtils.waitForAWhile(timeout);
                 
                //Test if the removal of IHistogram1D instance from serverTree is reflected here;
                String[] snames1 = serverTree.listObjectNames(histNewPath);
                String[] cnames1 = clientTree.listObjectNames(histNewPath);
                String[] stypes1 = serverTree.listObjectTypes(histNewPath);
                String[] ctypes1 = clientTree.listObjectTypes(histNewPath);
                 
                if(cnames1 != null) {
                        for(int i = 0; i < cnames1.length; i ++) {
                                assertEquals(cnames1[i], snames1[i]);
                                assertEquals(ctypes1[i], stypes1[i]);
                        }
                } */
        
                /* serverTree.rm(histNewPath + "/" + hist2DTitle);
                TestRUtils.waitForAWhile(timeout);
                //Test if the removal of IHistogram1D instance from serverTree is reflected here;
                String[] snames2 = serverTree.listObjectNames(histNewPath);
                String[] cnames2 = clientTree.listObjectNames(histNewPath);
                String[] stypes2 = serverTree.listObjectTypes(histNewPath);
                String[] ctypes2 = clientTree.listObjectTypes(histNewPath);
                 
                //There should be no instances left in this directory;
                assertEquals(snames2.length, 0);
                assertEquals(stypes2.length, 0);
                assertEquals(cnames2.length, 0);
                assertEquals(ctypes2.length, 0); */
        
        //Remove the directory from the serverTree completely;
        serverTree.rmdir(histNewPath);
        TestRUtils.waitForAWhile(timeout);
        
        //Check if the removal has been reflected on clientTree;
        String[] dirs = clientTree.listObjectNames("/");
        assertTrue(!TestRUtils.contains(dirs, histNewPath));
        
        //-------------------------------------------------------
        //create a copy of the IProfile1D instance on server side;
        serverTree.cd(newProfPath);
        serverTree.cp(profilePath + "/" + profile1DTitle, newProfPath + "/" + profile1DTitle);
        IProfile1D p11 = null;
        p11 = (IProfile1D) serverTree.find(newProfPath + "/" + profile1DTitle);
        assertTrue(p11 != null);
        
        //find the copied instance from client tree;
        IProfile1D rp11 = null;
        try{
            rp11 = (IProfile1D) clientTree.find(newProfPath + "/" + profile1DTitle);
        } catch (Exception e){
            e.printStackTrace();
        }
        assertTrue(rp11 != null);
        assertEquals(p11, (RemoteProfile1D) rp11);
        
        //update the IProfile1D instance on server side;
                /* for(int i = 0; i < nEntries; i ++){
                        double xVal = r.nextGaussian();
                        double yVal = r.nextGaussian();
                        double w = r.nextDouble();
                        p1.fill(xVal, yVal, w);
                }
                 
                //wait for a while before comparing the local and remote IProfile1D instances;
                TestRUtils.waitForAWhile(timeout);
                assertEquals(p1, (RemoteProfile1D)rp1);  */
        
        //add p11 to p1;
                /* assertEquals(p1, p11);
                System.out.println("p1 == p11");
                try{
                        p1.add(p11);
                } catch (IllegalArgumentException ie){
                        System.out.println("IllegalArgumentException caught (p1.add(p2))!");
                        ie.printStackTrace();
                }
                 
                //wait for a while before comparing the local and remote IProfile1D instances;
                TestRUtils.waitForAWhile(timeout);
                assertEquals(p1, (RemoteProfile1D)rp1);
                 */
    }
    
}
