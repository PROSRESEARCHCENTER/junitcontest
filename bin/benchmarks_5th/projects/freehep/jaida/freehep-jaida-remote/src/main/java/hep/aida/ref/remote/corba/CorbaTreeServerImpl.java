/*
 * CorbaTreeServerImpl.java
 *
 * Created on June 8, 2003, 7:46 PM
 */

package hep.aida.ref.remote.corba;

import hep.aida.IAnalysisFactory;
import hep.aida.IDataPoint;
import hep.aida.IDataPointSet;
import hep.aida.IDataPointSetFactory;
import hep.aida.IHistogram1D;
import hep.aida.IHistogramFactory;
import hep.aida.ITreeFactory;
import hep.aida.ref.AnalysisFactory;
import hep.aida.ref.histogram.DataPoint;
import hep.aida.ref.remote.corba.converters.CorbaDataPointSetDConverter;
import hep.aida.ref.remote.corba.converters.CorbaHist1DConverter;
import hep.aida.ref.remote.corba.generated.TreeClient;
import hep.aida.ref.remote.corba.generated.TreeServant;
import hep.aida.ref.remote.corba.generated.TreeServantHelper;
import hep.aida.ref.remote.corba.generated.TreeServerPOA;
import hep.aida.ref.tree.Tree;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;

import org.freehep.util.FreeHEPLookup;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class CorbaTreeServerImpl extends TreeServerPOA implements Runnable {
    private Tree tree;
    private String treeName;
    private ORB orb;
    private POA rootPOA;
    private Hashtable objectHash;
    //private String iorFileName = "C:/Temp/TreeServer.ior";
    private String iorFileName = "/afs/slac.stanford.edu/u/ey/serbo/public_html/jas3/TreeServer.ior";
    public CorbaTreeServerImpl(String treeName, Tree tree) {
        System.out.println("\tStarting CorbaTreeServer with Tree Name: "+treeName);
        this.tree = tree;
        this.treeName = treeName;
        objectHash = new Hashtable();

        // Register available CORBA Converters
        FreeHEPLookup.instance().add(CorbaHist1DConverter.getInstance(), "IHistogram1D");
        FreeHEPLookup.instance().add(CorbaDataPointSetDConverter.getInstance(), "IDataPointSet");
        
        new Thread(this).start();
    }

    
    // Service methods
    
    private TreeServant connect(java.lang.Object clientRef) {
        System.out.println("New connection from Client:  "+clientRef);
        TreeServant servantRef = null;
        CorbaTreeServantImpl servant = (CorbaTreeServantImpl) objectHash.get(clientRef);
        if (servant != null) {
            
        } else {
            if (clientRef instanceof String) servant = new CorbaTreeServantImpl(orb, tree);
            else if (clientRef instanceof TreeClient) 
                servant = new CorbaTreeServantImpl(orb, tree, (TreeClient) clientRef);
            try {
                org.omg.CORBA.Object obfRef = rootPOA.servant_to_reference(servant);
                servantRef = TreeServantHelper.narrow(obfRef);
            } catch (Exception t) {
                t.printStackTrace();
                return null;
            }
            objectHash.put(clientRef, servant);
        }
        return servantRef;
    }

    private boolean disconnect (java.lang.Object clientRef) {
        System.out.println("\tDisconnecting Client: "+clientRef);
        synchronized ( objectHash ) {
            if (objectHash.containsKey(clientRef)) {
                CorbaTreeServantImpl servant = (CorbaTreeServantImpl) objectHash.get(clientRef);
                try {
                    byte[] id = rootPOA.servant_to_id(servant);
                    rootPOA.deactivate_object(id);
                } catch (Exception e) { e.printStackTrace(); }
                servant.close();
                objectHash.remove(clientRef);
                return true;
            }
        }
        return false;
    }

    
    // TreeServer methods
    
    public String treeName () { return treeName; }

    public boolean supportDuplexMode() { return true; }

    public TreeServant connectDuplex(TreeClient client) {
        return connect((java.lang.Object) client);
    }
    
    public TreeServant connectNonDuplex(String clientID) {
        return connect((java.lang.Object) clientID);
    }

    public boolean disconnectDuplex(TreeClient client) {
        return disconnect((java.lang.Object) client);
    }
    
    public boolean disconnectNonDuplex(String clientID) {
        return disconnect((java.lang.Object) clientID);
    }

    
    // Runnable methods
    
    public void run() {
        System.out.println("\tStarting ORB");
        try {
           // Create and initialize the ORB
            String[] orbArgs = {};
            orb = ORB.init(orbArgs, null);

            // Get the Root POA
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("RootPOA");
            rootPOA = POAHelper.narrow(objRef);

            // Create TreeServerImpl object
            //System.out.println("\tCreating TreeServerImpl");
            //server = new TreeServerImpl(orb, rootPOA, tree);

            // Activate POA and create a reference for the TreeServantImpl
            rootPOA.the_POAManager().activate();
            org.omg.CORBA.Object obfRef = rootPOA.servant_to_reference(this);
            String ref = orb.object_to_string(obfRef);

            // Write IOR to file
            File f = new File(iorFileName);
            PrintWriter out = new PrintWriter(new FileWriter(f));
            out.print(ref);
            out.close();
            System.out.println("Wrote TreeServer REF into file "+iorFileName);

            System.out.println("TreeServer is ready,  IOR Reference:");
            System.out.println("\n"+ref);

            orb.run();
        } catch (Exception t) {
            t.printStackTrace();
            return;
        }
    }

    public void close() {
       System.out.print("Shutting down TreeServer ... ");
        synchronized ( this ) {
            if (!objectHash.isEmpty()) {
                Iterator it = objectHash.values().iterator();
                while (it.hasNext()) {
                    CorbaTreeServantImpl servant = (CorbaTreeServantImpl) it.next();
                    servant.close();
                    try {
                        byte[] id = rootPOA.servant_to_id(servant);
                        rootPOA.deactivate_object(id);
                    } catch (Exception e) { e.printStackTrace(); }
                }
                objectHash.clear();
            }
            objectHash = null;
            tree = null;
            treeName = null;

            orb.shutdown(true);
            orb.destroy();
            orb = null;
            rootPOA = null;
        }
        System.out.print(" Done!\n");
    }

    public static void main(String[] args) {

        java.util.Random r = new java.util.Random();
        IAnalysisFactory af = new AnalysisFactory();
        ITreeFactory tf = af.createTreeFactory();
        Tree tree = (Tree) tf.create();
        IHistogramFactory histogramFactory = af.createHistogramFactory(tree);
        IDataPointSetFactory dataPointSetFactory = af.createDataPointSetFactory(tree);

        //IPlotter plotter = af.createPlotterFactory().create("Plot");
        //plotter.createRegions(2,3,0);

        int nEntries = 10;
        int xbins = 10;
        double xLowerEdge = -8.;
        double xUpperEdge = 8.;

        System.out.println("MAIN: Creating CorbaTreeServer ...");
        CorbaTreeServerImpl server = new CorbaTreeServerImpl("Test_Tree", tree);

       try {
            System.out.println("Tree is ready, name: "+tree.storeName()+". To create /dir-1, /Hist-1 and fill /Hist-1 press ENTER");
            System.in.read();

            IHistogram1D h1 = histogramFactory.createHistogram1D("Hist-1",xbins,xLowerEdge,xUpperEdge);
            tree.mkdir("dir-1");
            /* Fill the histogram */
            for (int i=0; i<nEntries; i++) {
                double xval = r.nextGaussian()*4+2.;
                h1.fill( xval );
            }

            //plotter.region(0).plot(h1);
            //plotter.show();

            String[] names = tree.listObjectNames("");
            String[] types = tree.listObjectTypes("");

            if (names != null) {
                for (int i=0; i<names.length; i++) System.out.println(i+"\t"+names[i]+"\t"+types[i]);
            }

            System.out.println("To create and fill /Points-1 press ENTER");
            System.in.read();

            IDataPointSet dps1 = dataPointSetFactory.create("Points-1", 2);
            double[] val = new double[2];
            double[] err = new double[2];
            for (int i=0; i<nEntries; i++) {
                val[0] = i*2. + 1.;
                val[1] = r.nextGaussian()*4+10.;
                err[0] = 0;
                err[1] = Math.sqrt(val[1]);
                System.out.println(i+"  x="+val[0]+",  y="+val[1]+"  err="+err[1]);
                IDataPoint point = new DataPoint(val, err);
                dps1.addPoint(point);
            }

            //plotter.region(1).plot(dps1);
            //plotter.show();


            System.out.println("Update /Points-1 press ENTER");
            System.in.read();

            for (int i=0; i<(int) nEntries/2; i++) {
                val[0] = i*2. + 1. + dps1.upperExtent(0);
                val[1] = r.nextGaussian()*4+10.;
                err[0] = 0;
                err[1] = Math.sqrt(val[1]);
                System.out.println(i+"  x="+val[0]+",  y="+val[1]+"  err="+err[1]);
                IDataPoint point = new DataPoint(val, err);
                dps1.addPoint(point);
            }

            System.out.println("Update /Points-1 press ENTER");
            System.in.read();

            for (int i=0; i<(int) nEntries/2; i++) {
                val[0] = i*2. + 1. + dps1.upperExtent(0);
                val[1] = r.nextGaussian()*4+10.;
                err[0] = 0;
                err[1] = Math.sqrt(val[1]);
                System.out.println(i+"  x="+val[0]+",  y="+val[1]+"  err="+err[1]);
                IDataPoint point = new DataPoint(val, err);
                dps1.addPoint(point);
            }

            System.out.println("To create /Hist-2 press ENTER");
            System.in.read();

            IHistogram1D h2 = histogramFactory.createHistogram1D("Hist-2",xbins,xLowerEdge-1.,xUpperEdge-1.);
            //plotter.region(2).plot(h2);
            //plotter.show();


            System.out.println("Hist-2 created. To update Hist-2 press ENTER");
            System.in.read();
            
            for (int i=0; i<nEntries; i++) {
                double xval = r.nextGaussian()*4+2.;
                h2.fill( xval );
            }

            System.out.println("Hist-2 updated. To update Hist-2 press ENTER");
            System.in.read();
            
            for (int i=0; i<nEntries; i++) {
                double xval = r.nextGaussian()*4-2.;
                h2.fill( xval );
            }

            System.out.println("Hist-2 updated. To update Hist-2 press ENTER");
            System.in.read();
            
            for (int i=0; i<nEntries; i++) {
                double xval = r.nextGaussian()*3+2.;
                h2.fill( xval );
            }

            System.out.println("Hist-2 updated. To go into update Loop Hist-2 press ENTER");
            System.in.read();
            
	while (true) {
            for (int i=0; i<nEntries; i++) {
                double xval = r.nextGaussian()*4;
                h2.fill( xval );
            }
	    try { Thread.sleep(300); }
	    catch (Exception e) { e.printStackTrace(); break; }
	}

            System.out.println("/Hist-2 updated. To create dir-2 and dir-2/Hist-3 press ENTER");
            System.in.read();

            tree.mkdir("dir-2");
            tree.cd("dir-2");

            IHistogram1D h3 = histogramFactory.createHistogram1D("Hist-3",xbins,xLowerEdge-1.,xUpperEdge-1.);
            //plotter.region(3).plot(h3);
            //plotter.show();


            System.out.println("dir-2, dir-2/Hist-3 created. To update dir-2/Hist-3 press ENTER");
            System.in.read();
            
            /* Fill the histogram */
            for (int i=0; i<nEntries; i++) {
                double yval = (r.nextDouble()-0.5)*20.;
                h3.fill( yval );
            }

            System.out.println("dir-2/Hist-3 updated. To create dir-1/Hist-4 press ENTER");
            System.in.read();
            
            tree.cd("/dir-1");

            IHistogram1D h4 = histogramFactory.createHistogram1D("Hist-4",xbins,xLowerEdge-1.,xUpperEdge-1.);
            //plotter.region(4).plot(h4);
            //plotter.show();

            System.out.println("dir-1/Hist-4 created. To update dir-2/Hist-3 press ENTER");
            System.in.read();
            
            /* Fill the histogram */
            for (int i=0; i<nEntries; i++) {
                double yval = (r.nextDouble()-0.5)*20.;
                h3.fill( yval );
            }

            System.out.println("dir-2/Hist-3 updated. To update Hist-2 press ENTER");
            System.in.read();
            
            /* Fill the histogram */
            for (int i=0; i<nEntries; i++) {
                double yval = (r.nextDouble()-0.5)*20.;
                h2.fill( yval );
            }

            System.out.println("To exit press ENTER");
            System.in.read();

        }  catch(java.io.IOException e) {
            e.printStackTrace();
        }

        System.out.println("MAIN: Exiting");
        server.close();
        System.exit(0);
    }
        
}
