package hep.aida.ref.test.jaida;

import hep.aida.IAnalysisFactory;
import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.ICloud3D;
import hep.aida.IDataPoint;
import hep.aida.IDataPointSet;
import hep.aida.IDataPointSetFactory;
import hep.aida.IFunction;
import hep.aida.IFunctionFactory;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.IHistogramFactory;
import hep.aida.IProfile1D;
import hep.aida.IProfile2D;
import hep.aida.ITree;
import hep.aida.ITuple;
import hep.aida.ITupleFactory;
import java.io.IOException;
import java.util.Random;

/*
 * Creates AIDA tree with all standard AIDA types
 * and writes it to disk
 */

/**
 *
 * @author serbo
 */
public class CreateAidaFile {
    
    private static long randomSeed = 123456789L;
            
    /** option for creating tree
     *  possible option key and values are:
     *  key: compress,  values: no, false, yes, true, gzip, zip,  default: no
     *  key: binary,    values: no, false, yes, true,   default: false
     *  key: skip,      values: list of AIDA types to skip when writing to disk, default: null
     */
    private String option;
    
    /**
     *  Name of file where AIDA tree is to be written
     */
    private String fileName;
    
    private ITree tree;
    
    public CreateAidaFile(String fileName, String option) {
        this.fileName = fileName;
        this.option = option;
    }
    
    public String getFileName() { return fileName; }
    
    public String getOptions() { return option; }
    
    public void commit() throws IOException {
        if (tree == null) 
            System.out.println("\nCan not commit: AIDA tree has not been created yet. Exiting.");
            
        if (fileName != null && !fileName.trim().equals("")) {
            System.out.println("\nCommitting AIDA tree");
            tree.commit();
        } else {
            System.out.println("\nCan not commit AIDA Tree: File Name is not defined");
        }        
    }
    
    private void createAndFillTree() throws IOException {
        int nBins = 10;
        int nEntries = 15;
        double xMin = -2;
        double xMax = 2;
        double yMin = -2.5;
        double yMax = 2.5;
        double zMin = -3;
        double zMax = 3;
        
            System.out.println("Creating AIDA tree with");
            System.out.println("\t\tFile Name: "+fileName);
            System.out.println("\t\tOptions:   "+option);
                
        IAnalysisFactory af = IAnalysisFactory.create();
        tree = af.createTreeFactory().create(fileName, "xml", false, true, option);
        IDataPointSetFactory df = af.createDataPointSetFactory(tree);
        IHistogramFactory hf = af.createHistogramFactory(tree);
        ITupleFactory tf = af.createTupleFactory(tree);
        IFunctionFactory ff = af.createFunctionFactory(tree);
        
        Random r = new Random(randomSeed);
        
        tree.mkdirs("/DataPointSets");
        tree.cd("/DataPointSets");
        IDataPointSet d1 = df.create("DataPointSet 1D", "DataPointSet 1D Title", 1);
        IDataPointSet d2 = df.create("DataPointSet 2D", "DataPointSet 2D Title", 2);
        IDataPointSet d3 = df.create("DataPointSet 3D", "DataPointSet 3D Title", 3);
        IDataPointSet d4 = df.create("DataPointSet 4D", "DataPointSet 4D Title", 4);
        
        tree.mkdirs("/Functions");
        tree.cd("/Functions");
        IFunction f1 = ff.createFunctionByName("P1 Function","p1");
        f1.setParameter("p0", -2);
        f1.setParameter("p1", 1);
        
        IFunction f2 = ff.createFunctionFromScript("Script Function", 1, "a*(x[0] - b)", "a,b", "simple a*(x[0] - b) function");
        f2.setParameter("a", 1);
        f2.setParameter("b", 2);
        
        tree.mkdirs("/Histograms");
        tree.cd("/Histograms");
        IHistogram1D h1 = hf.createHistogram1D("Histogram 1D", "Histogram 1D - normal", nBins, xMin, xMax);
        IHistogram2D h2 = hf.createHistogram2D("Histogram 2D", "Histogram 2D - normal", nBins, xMin, xMax, nBins, yMin, yMax);
        IHistogram3D h3 = hf.createHistogram3D("Histogram 3D", "Histogram 3D - normal", nBins, xMin, xMax, nBins, yMin, yMax, nBins, zMin, zMax);
        
        tree.mkdirs("/Clouds");
        tree.cd("/Clouds");
        ICloud1D c11 = hf.createCloud1D("Cloud 1D conv", "Cloud1D - converted", 10);
        ICloud2D c21 = hf.createCloud2D("Cloud 2D conv", "Cloud2D - converted", 10);
        ICloud3D c31 = hf.createCloud3D("Cloud 3D conv", "Cloud3D - converted", 10);
        
        ICloud1D c12 = hf.createCloud1D("Cloud 1D", "Cloud1D - not converted", -1);
        ICloud2D c22 = hf.createCloud2D("Cloud 2D", "Cloud2D - not converted", -1);
        ICloud3D c32 = hf.createCloud3D("Cloud 3D", "Cloud3D - not converted", -1);
        
        tree.mkdirs("/Profiles");
        tree.cd("/Profiles");
        IProfile1D p1 = hf.createProfile1D("Profile 1D", "Profile 1D - normal", nBins, xMin, xMax);
        IProfile2D p2 = hf.createProfile2D("Profile 2D", "Profile 2D - normal", nBins, xMin, xMax, nBins, yMin, yMax);
        
        tree.mkdirs("/Tuples");
        tree.cd("/Tuples");
        
        String numberColumns = "boolean bool_col =false, byte byte_col =0, short short_col =0, int int_col =0, long long_col =0, float float_col =0,  double double_col =0";
        ITuple numberTuple = tf.create("Number Tuple", "Tuple with Numbers Only", numberColumns);
        
        String stringColumns = "char char_col, string string_col";
        ITuple stringTuple = tf.create("String Tuple", "Tuple with Strings and Characters", stringColumns);
        
        String subtupleColumns = "int event_number=0, int nTracks=0, ITuple tracks  = { double px = 0., py = 0., pz = 0., int nHits=0, ITuple hits = {int x,y,z} }";
        ITuple subtupleTuple = tf.create("Structure Tuple", "Tuple with Internal Structure", subtupleColumns);
        
        
        // Fill in some funny names and titles
        
        tree.mkdirs("/Funny_Names");
        tree.cd("/Funny_Names");
        String title = "<HTML>TITLE<sub>subscripted</sub> <i>can also</i> <b>do</b> text<sup>superscripted</sup> and &Psi;, &lambda;, &pi;";
        IHistogram1D h111 = hf.createHistogram1D("FH 1", title, nBins, xMin, xMax);
        IHistogram1D h112 = hf.createHistogram1D("FH 2 part\\/All", "Ratio of part/All", nBins, xMin, xMax);
        
        ITuple fsTuple = tf.create("Funny String Tuple", "Tuple with Funny Strings", "string funny_string");
        
        // Fill AIDA objects with data
        
        String abc = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
        for (int i=0; i<nEntries; i++) {
            double x = r.nextGaussian();
            double y = r.nextGaussian();
            double z = r.nextGaussian();
            double w = r.nextDouble();
            
            d1.addPoint();
            IDataPoint p = d1.point(i);
            p.coordinate(0).setValue(x);
            
            d2.addPoint();
            p = d2.point(i);
            p.coordinate(0).setValue(x);
            p.coordinate(1).setValue(y);
            p.coordinate(1).setErrorPlus(0.6);
            p.coordinate(1).setErrorMinus(0.4);
            
            d3.addPoint();
            p = d3.point(i);
            p.coordinate(0).setValue(x);
            p.coordinate(1).setValue(y);
            p.coordinate(2).setValue(z);
            
            d4.addPoint();
            p = d4.point(i);
            p.coordinate(0).setValue(x);
            p.coordinate(1).setValue(y);
            p.coordinate(2).setValue(z);
            p.coordinate(3).setValue(w);
            
            h1.fill(x, w);
            h111.fill(x, w);
            h112.fill(w);
            h2.fill(x, y, w);
            h3.fill(x, y, z, w);
            
            c11.fill(x, w);
            c21.fill(x, y, w);
            c31.fill(x, y, z, w);
            
            c12.fill(x, w);
            c22.fill(x, y, w);
            c32.fill(x, y, z, w);
            
            p1.fill(x, y, w);
            p2.fill(x, y, z, w);
            
            numberTuple.fill(0, r.nextBoolean());
            numberTuple.fill(1, (byte) (r.nextInt(8) & 0x7) );
            numberTuple.fill(2, (short) (r.nextInt(16) & 0xF) );
            numberTuple.fill(3, r.nextInt());
            numberTuple.fill(4, r.nextLong());
            numberTuple.fill(5, r.nextFloat());
            numberTuple.fill(6, r.nextDouble());
            numberTuple.addRow();
            
            
            // Fill Tuple with characters and Strings
            
            int index = i % abc.length();
            String str = "String_Entry_" + String.valueOf(i);
            
            stringTuple.fill(0, abc.charAt(index));
            stringTuple.fill(1, str);
            stringTuple.addRow();
            
            String funnyString = "<HTML>&#" + String.valueOf(913+index) + ";";
            fsTuple.fill(0, funnyString);
            fsTuple.addRow();
            
            int nTracks = r.nextInt(nEntries);
            subtupleTuple.fill(0, i);
            subtupleTuple.fill(1, nTracks);
            ITuple track = subtupleTuple.getTuple( 2 );
            for (int j=0; j<nTracks; j++) {
                track.fill(0,r.nextGaussian());
                track.fill(1,r.nextGaussian());
                track.fill(2,r.nextGaussian());
                
                int nHits = r.nextInt(10);
                track.fill(3, nHits);
                ITuple hits = track.getTuple( 4 );
                for ( int k = 0; k<nHits; k++ ) {
                    hits.fill(0,r.nextInt(10));
                    hits.fill(1,r.nextInt(10));
                    hits.fill(2,r.nextInt(10));
                    hits.addRow();
                }
                track.addRow();
            }
            subtupleTuple.addRow();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        if (args == null || args.length == 0 || args.length > 2) {
            System.out.println("\nWrong arguments. Usage:");
            System.out.println("\tjava CreateAidaFile <file_name> <options>");
            System.exit(1);
        }
        
        String defaultOptions = "compress=no";
        String defaultFileName = null;
        
        if (args != null && args.length > 0) 
            defaultFileName = args[0];
        
        if (args != null && args.length > 1) 
            defaultOptions = args[1];
        
        CreateAidaFile caf = new CreateAidaFile(defaultFileName, defaultOptions);
        caf.createAndFillTree();
        caf.commit();
    }
    
}
