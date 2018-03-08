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
 * @version $Id: TestRTree.java 13419 2007-11-30 19:38:32Z serbo $
 */
public class TestRTree extends RAIDATestCase {
	private IAnalysisFactory af;
	private ITreeFactory tf;
	private IHistogramFactory hf;

    private IDevTree serverTree;
    private ITree clientTree;
    private RemoteServer treeServer;
    private RmiServer rmiTreeServer;
    private String localHost;
    private int port;
    private String serverName;

	//mounted tree;
    private ITree mntTree;
	private IHistogram1D mntHist;
	private IHistogramFactory mntFact;
    
    //Paramters adapted from AIDA test suite;
    private int nEntries = 1234;
    private int xbins = 40;
    private int ybins = 20;
    private int zbins = 10;
    
    private String histPath = "/hists";

	private String histSubPath1 = "histDir-1D";
	private String histSubPath2 = "histDir-2D";
	private String histSubPath3 = "histSubLvl3";
	private String histSubPath4 = "histSubLvl4";

	private String profPath = "/profiles";
    
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

    public TestRTree(String testName) {
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
    
    public void testCaseRTree() {
        
        // create AIDA factories and AIDA Tree
        //IAnalysisFactory af = IAnalysisFactory.create();
        //ITreeFactory tf = af.createTreeFactory();
		
		if (TestRUtils.verbose) System.out.println("\n\n\n***testCaseRTree***");

        af = IAnalysisFactory.create();
        tf = af.createTreeFactory();
        serverTree = (IDevTree) tf.create();
        hf = af.createHistogramFactory(serverTree);

		//construct a tree to be mounted on server tree;
		constructMntTree();
        
        serverTree.mkdir(histPath);
        serverTree.mkdir(profPath);
		serverTree.mkdir("/tmp");
        
        //create an instance of Histogram1D and an instance of Histogram2D on server tree;
        serverTree.cd(histPath);

		//mkdir "/hists/histSubDir-1D";
		serverTree.mkdir(histSubPath1);
		//mkdir "/hists/histSubDir-2D";
		serverTree.mkdir(histSubPath2);

		//mkdir "/hists/histSubDir-1D/histSubLvl3";
		serverTree.cd(histSubPath1);
		serverTree.mkdir(histSubPath3);
		//serverTree.mkdir("tmp");

		//mkdir "/hists/histSubDir-1D/histSubLvl3/histSubLvl4";
		serverTree.cd(histSubPath3);
		serverTree.mkdir(histSubPath4);

        serverTree.cd(histPath + "/" + histSubPath1 + "/" + histSubPath3);
        IHistogram1D h1 = hf.createHistogram1D(hist1DTitle, xbins, xLowerEdge, xUpperEdge);
        assertTrue(h1 != null);

		serverTree.cd(histPath + "/" + histSubPath2);
		IHistogram2D h2 = hf.createHistogram2D(hist2DTitle, xbins, xLowerEdge, xUpperEdge, 
			ybins, yLowerEdge, yUpperEdge);
		assertTrue(h2 != null);

		//create an instance of IProfile1D;
        serverTree.cd(profPath);
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
            rh1 = (IHistogram1D) clientTree.find(histPath + "/" + histSubPath1 + "/" + histSubPath3 + "/" + hist1DTitle);
            rh2 = (IHistogram2D) clientTree.find(histPath + "/" + histSubPath2 + "/" + hist2DTitle);

            rp1 = (IProfile1D) clientTree.find(profPath + "/" + profile1DTitle);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		assertTrue(rh1 != null);
		assertTrue(rh2 != null);
        assertTrue(rp1 != null);

		TestRUtils.waitForAWhile(timeout);
        
        //check if the remote and local IHistogram instances are equal
        assertEquals(h1, (RemoteHistogram1D) rh1);
        assertEquals(h2, (RemoteHistogram2D) rh2);

        assertEquals(p1, (RemoteProfile1D) rp1);

		//-----------------------------------------------------------
		if (TestRUtils.verbose) System.out.println("\n***Copy an IHistogram1D instance to a top-level directory:");
		serverTree.cd(histPath);
		try{
			//serverTree.cp(histPath + "/" + histSubPath1 + "/" + histSubPath3 + "/" + hist1DTitle, profPath);
			serverTree.cp(histPath + "/" + histSubPath1 + "/" + histSubPath3 + "/" + hist1DTitle, "/tmp");
		} catch (IllegalArgumentException ie){
			System.out.println("IllegalArgumentException caught!");
			ie.printStackTrace();
		}

		TestRUtils.waitForAWhile(timeout);

		IHistogram1D hh1 = null;
		//hh1 = (IHistogram1D) serverTree.find(profPath + "/" + hist1DTitle);
		hh1 = (IHistogram1D) serverTree.find("/tmp/" + hist1DTitle);
		assertTrue(hh1 != null);

		//assertEquals(h1, hh1);

		if (TestRUtils.verbose) System.out.println("---Server tree");
		prnDirectory(serverTree, "/", "server0");
		
		if (TestRUtils.verbose) System.out.println("---Client tree");
		prnDirectory(serverTree, "/", "client0");

		IHistogram1D h11 = null;
		try{
			//h11 = (IHistogram1D) clientTree.find(profPath + "/" + hist1DTitle);
			h11 = (IHistogram1D) clientTree.find("/tmp/" + hist1DTitle);
		} catch(Exception e){
			//e.printStackTrace();
		}
		assertTrue(h11 != null); 

		//-----------------------------------------------------------
		if (TestRUtils.verbose) System.out.println("\n**Copy a non-top level directory recursively on server tree:");
		try{
			serverTree.cp(histPath + "/" + histSubPath1 + "/" + histSubPath3, histPath + "/" + histSubPath2, true);
		} catch(IllegalArgumentException ie){
			ie.printStackTrace();
		}

		if (TestRUtils.verbose) System.out.println("---Server tree");
		prnDirectory(serverTree, "/", "server1");

		if (TestRUtils.verbose) System.out.println("---Client tree");
		prnDirectory(clientTree, "/", "client1");

		//-----------------------------------------------------------
		if (TestRUtils.verbose) System.out.println("\n***Copy a IHistogram2D instance to another non-top level directory on server tree:");
		try{
			serverTree.cp(histPath + "/" + histSubPath2 + "/" + hist2DTitle, histPath + "/" + histSubPath1 + 
				"/" + histSubPath3 + "/" + histSubPath4);
		} catch (IllegalArgumentException ie){
			ie.printStackTrace();
		}

		TestRUtils.waitForAWhile(timeout);
		IHistogram2D rhh2 = null;
		try{
			//rhh2 = (IHistogram2D) clientTree.find(histPath + "/" + histSubPath1 + "/" + histSubPath3 + "/" + histSubPath4 + "/" + hist2DTitle);
			rhh2 = (IHistogram2D) serverTree.find(histPath + "/" + histSubPath1 + "/" + histSubPath3 + "/" + histSubPath4 + "/" + hist2DTitle);
		} catch (IllegalArgumentException ie){
			ie.printStackTrace();
		}
		//assertTrue(rhh2 != null);
		//assertEquals(h2, (RemoteHistogram2D) rhh2);

		if (TestRUtils.verbose) System.out.println("---Server tree");
		prnDirectory(serverTree, "/", "server2");

		if (TestRUtils.verbose) System.out.println("---Client tree");
		prnDirectory(clientTree, "/", "client2");

		//-----------------------------------------------------------
		if (TestRUtils.verbose) System.out.println("\n***Remove a top level non-empty directory from server tree:");
		//serverTree.rmdir(histPath + "/" + histSubPath1 + "/tmp");
		serverTree.rmdir("/tmp");
		if (TestRUtils.verbose) System.out.println("---Server tree");
		prnDirectory(serverTree, "/", "server3");
		TestRUtils.waitForAWhile(timeout);
		if (TestRUtils.verbose) System.out.println("---Client tree");
		prnDirectory(clientTree, "/", "client3");

		//-----------------------------------------------------------
		if (TestRUtils.verbose) System.out.println("\n***Mount a new tree to server tree:");
		serverTree.mount("/mounted", mntTree, "/");
		if (TestRUtils.verbose) System.out.println("---Server tree");
		prnDirectory(serverTree, "/", "server4");
		TestRUtils.waitForAWhile(timeout);
		if (TestRUtils.verbose) System.out.println("---Client tree");
		prnDirectory(clientTree, "/", "client4");

		IHistogram1D rhh1 = null;
		try{
			rhh1 = (IHistogram1D) clientTree.find("/mounted/dir1/sub1/" + hist1DTitle);
		} catch (Exception e){
			e.printStackTrace();
		}
		assertTrue(rhh1 != null);
		assertEquals(mntHist, (RemoteHistogram1D) rhh1);

		//-----------------------------------------------------------
		if (TestRUtils.verbose) System.out.println("\n***Remove an empty directory from the mounted tree:");
		try{
			serverTree.rmdir("/mounted/dir2");
		} catch(IllegalArgumentException ie){
			System.out.println("ITree.rmdir exception caught!");
			ie.printStackTrace();
		}
		if (TestRUtils.verbose) System.out.println("---Server tree:");
		prnDirectory(serverTree, "/", "server5");
		TestRUtils.waitForAWhile(timeout);
		if (TestRUtils.verbose) System.out.println("---Client tree:");
		prnDirectory(clientTree, "/", "client5");

		//-----------------------------------------------------------
		if (TestRUtils.verbose) System.out.println("\n***Remove a non-empty directory from the mounted tree:");
		try{
			serverTree.rmdir("/mounted/dir1/sub1");
		} catch(IllegalArgumentException ie){
			System.out.println("ITree.rmdir exception caught!");
			ie.printStackTrace();
		}
		if (TestRUtils.verbose) System.out.println("---Server tree:");
		prnDirectory(serverTree, "/", "server6");
		TestRUtils.waitForAWhile(timeout);
		if (TestRUtils.verbose) System.out.println("---Client tree:");
		prnDirectory(clientTree, "/", "client7");
    }
	
	public void constructMntTree(){
		mntTree = tf.create();
        mntFact = af.createHistogramFactory(mntTree);

		mntTree.mkdir("/dir1");
		mntTree.mkdir("/dir2");
		mntTree.mkdir("/dir1/sub1");
		mntTree.cd("/dir1/sub1");

		mntHist = mntFact.createHistogram1D(hist1DTitle, xbins, xLowerEdge, xUpperEdge);
		assertTrue(mntHist != null);

		Random r = getRandomNumberGenerator();

        for (int i = 0; i < nEntries; i++) {
            double xVal = r.nextGaussian();
            double w = r.nextDouble();
            mntHist.fill(xVal, w);
		}
	}

	/**
	 * list (ls) objects and their corresponding types in the provided directory.
	 * @param tree	- the ITree instance;
	 * @param path  - the path;
	 * @param msg 	- the message to display;
	 */
	
	public void prnDirectory(ITree tree, String path, String msg){
		String[] names = tree.listObjectNames(path, true);
		String[] types = tree.listObjectTypes(path, true);
		assertEquals(names.length, types.length);
                if (!TestRUtils.verbose) return;
                
		for(int i = 0; i < names.length; i ++){
			System.out.println(msg + ": names[" + i + "] = " + names[i] + ", types[" + i + "] = " + types[i]);
		}
	}

	/**
	 * compare object names and object types in the provided directory.
	 * @param 	tree1	- the ITree instance 1;
	 * @param 	tree2	- the ITree instance 2;
	 * @param 	path  	- the path to be compared;
	 * @param 	msg 	- the message to display;
	 * @return 	true	- if tree1 and tree2 contails the same set of object names and types;
	 */
	
	public boolean cmpDirectory(ITree tree1, ITree tree2, String path, String msg){
		String[] names1 = tree1.listObjectNames(path, true);
		String[] types1 = tree1.listObjectTypes(path, true);

		String[] names2 = tree2.listObjectNames(path, true);
		String[] types2 = tree2.listObjectTypes(path, true);

		if(names1.length != types1.length) return false;
		if(names2.length != types2.length) return false;
		if(names1.length != names2.length) return false;

		for(int i = 0; i < names1.length; i ++){
                    if (TestRUtils.verbose) {
			if(!names1[i].equals(names2[i])) {
				System.out.println("---cmpDirectory failed: names1[" + i + "] = " + 
					names1[i] + ", names2[" + i + "] = " + names2[i]);
				System.out.println("---cmpDirectory failed: types1[" + i + "] = " + 
					types1[i] + ", types2[" + i + "] = " + types2[i]);
				return false;
			}
			if(!types1[i].equals(types2[i])) {
				System.out.println("---cmpDirectory failed: names1[" + i + "] = " + 
					names1[i] + ", names2[" + i + "] = " + names2[i]);
				System.out.println("---cmpDirectory failed: types1[" + i + "] = " + 
					types1[i] + ", types2[" + i + "] = " + types2[i]);
				return false;
			}
                    } else {
                        assertTrue(names1[i].equals(names2[i]));
                        assertTrue(types1[i].equals(types2[i]));
                    }
		}
		return true;
	}
}
