/*
 * TestBasic.java
 *
 * Created on May 12, 2003, 4:41 PM
 */

package hep.aida.ref.remote.basic;

import hep.aida.IAnalysisFactory;
import hep.aida.IBaseHistogram;
import hep.aida.IHistogram1D;
import hep.aida.IHistogramFactory;
import hep.aida.IManagedObject;
import hep.aida.IPlotter;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.dev.IDevTree;
import hep.aida.ref.event.AIDAListener;
import hep.aida.ref.event.HistogramEvent;
import hep.aida.ref.event.IsObservable;
import hep.aida.ref.event.TreeEvent;
import hep.aida.ref.remote.basic.interfaces.UpdateEvent;
import hep.aida.ref.tree.Tree;

import java.util.EventObject;

/**
 * Test basic client-server interaction for the Basic classes.
 * @author  serbo
 */
public class TestBasic implements AIDAListener {
    private Tree tree;
    
    /** Creates a new instance of TestBasic */
    public TestBasic(Tree tree) {
        this.tree = tree;
        tree.addListener(this);
        tree.setFolderIsWatched("/", true);
        tree.setValid(this); 
    }
        
    public void stateChanged(EventObject ev) {
        System.out.println("TestBasic.stateChanged GOT Event: "+ev);
        if (ev instanceof TreeEvent) {
            TreeEvent tev = (TreeEvent) ev;
            //System.out.println("TestBasic.stateChanged GOT TreeEvent: id="+tev.getID()+", type="+
            //                    tev.getType()+", flags="+tev.getFlags()+
            //                    ", path="+(tev.getPath())[(tev.getPath()).length-1]);
            
        } else if (ev instanceof HistogramEvent) {
            IBaseHistogram hist = (IBaseHistogram) ev.getSource();
            int id = UpdateEvent.NODE_UPDATED;
            String pathString = tree.findPath((IManagedObject) hist);
            String nodeType = hist.getClass().getName();
            ((IsObservable) hist).setValid(this);
            //System.out.println("TestBasic.stateChanged GOT HistogramEvent id="+id+", path="+pathString+
            //                    ", type="+nodeType);
        }

    }
    

    public static void main(String[] args) { 
        java.util.Random r = new java.util.Random();
        IAnalysisFactory af = IAnalysisFactory.create();

        IPlotter plotter = af.createPlotterFactory().create("Plot");
	plotter.createRegions(2,2,0);

        ITreeFactory tf = af.createTreeFactory();
        IDevTree clientTree = (IDevTree) tf.create(); 
        ITree serverTree = tf.create();
        
        TestBasic bt = new TestBasic((Tree) serverTree);
        
        IHistogramFactory histogramFactory = af.createHistogramFactory(serverTree);
        
        int nEntries = 1000;
        int xbins = 10;
        double xLowerEdge = -10.;
        double xUpperEdge = 10.;

        serverTree.mkdir("/dir1");
        IHistogram1D h1 = histogramFactory.createHistogram1D("Hist-1",xbins,xLowerEdge,xUpperEdge);
        IHistogram1D h2 = histogramFactory.createHistogram1D("Hist-2",xbins,xLowerEdge,xUpperEdge);
        /* Fill the histogram */
        for (int i=0; i<nEntries; i++) {
            double xval = r.nextGaussian()*3+2.;
            h1.fill( xval );
            xval = r.nextGaussian()*3+2.;
            h2.fill( xval );
        }

        System.out.println("Creating TreeServer ...");
        BasicTreeServer server = new BasicTreeServer(serverTree, "Server_Tree");
/*
        try {
            System.out.println("Server is ready. To continue press ENTER");
            System.in.read();

            System.out.println("\nCreating TreeClient ...");
            BasicTreeClient client = new BasicTreeClient(clientTree, true, server);
            System.out.println("Client is ready. To connect to Server press ENTER");
            System.in.read();

            client.connect();
            client.read(clientTree, new HashMap(), true, false);

            System.out.println("Client is connected. To get  histogram 1 press ENTER");
            System.in.read();
            IHistogram1D ho = (IHistogram1D) clientTree.find("/Hist-1");
            System.out.println("Found:  name="+ho.title()+", entries="+ho.allEntries());
            
            System.out.println("To update  histogram 1 press ENTER");
            System.in.read();
            double xval = r.nextGaussian()*3+2.;
            h1.fill( xval );
            
            System.out.println("To update  histogram 2 press ENTER");
            System.in.read();
            System.out.println("Currently:  name="+ho.title()+", entries="+ho.allEntries());
            xval = r.nextGaussian()*3+2.;
            h2.fill( xval );
            
            System.out.println("To add histogram 3 to server Tree press ENTER");
            System.in.read();
            IHistogram1D h3 = histogramFactory.createHistogram1D("Hist-3",xbins,xLowerEdge,xUpperEdge);
            
            System.out.println("To update  histogram 3 press ENTER");
            System.in.read();
            xval = r.nextGaussian()*3+2.;
            h3.fill( xval );
            
            System.out.println("To make directory /dir1/dir2/dir3 press ENTER");
            System.in.read();
            serverTree.mkdirs("/dir1/dir2/dir3");
            
            System.out.println("To add histogram 4 to directory /dir1/dir2/dir3 press ENTER");
            System.in.read();
            serverTree.cd("/dir1/dir2/dir3");
            IHistogram1D h4 = histogramFactory.createHistogram1D("Hist-4",xbins,xLowerEdge,xUpperEdge);
            
            System.out.println("To update  histogram 4 press ENTER");
            System.in.read();
            xval = r.nextGaussian()*3+2.;
            h4.fill( xval );

            System.out.println("To get  histogram 4 from Client press ENTER");
            System.in.read();
            client.read(clientTree, "/dir1/dir2/dir3");
            IManagedObject mo = clientTree.find("/dir1/dir2/dir3/Hist-4");
            System.out.println("Found:  name="+mo.name());
            
            System.out.println("To update  histogram 4 press ENTER");
            System.in.read();
            xval = r.nextGaussian()*3+2.;
            h4.fill( xval );

            System.out.println("To disconnect from Server press ENTER");
            System.in.read();
            client.disconnect();

        }  catch(Exception e) {
            e.printStackTrace();
        }
*/
    }
    
}
