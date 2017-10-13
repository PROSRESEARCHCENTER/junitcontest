import hep.io.root.*;
import hep.io.root.interfaces.*;
import java.io.IOException;

public class RootTest
{
    public static void main(String[] argv) throws IOException
    {
        RootFileReader rfr = new RootFileReader("Example.root");
        TKey key = rfr.getKey("mainHistogram");
        TH1 histogram = (TH1) key.getObject();

        double entries= histogram.getEntries();
        System.out.println("entries="+entries);
   }    
}