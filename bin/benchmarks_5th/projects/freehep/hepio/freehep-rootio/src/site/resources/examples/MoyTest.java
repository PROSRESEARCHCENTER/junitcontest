import hep.io.root.*;
import hep.io.root.interfaces.*;
import java.io.IOException;

public class MoyTest
{
    public static void main(String[] argv) throws IOException
    {
        RootFileReader rfr = new RootFileReader("Moy.root");

        TKey key = rfr.getKey("MeanPedBF_0");
        Moyennes moy = (Moyennes) key.getObject();
        
        // Now we have the user define object we can call any method 
        // we like.
        int size = moy.getSize();
        System.out.println("Size="+size);
   } 
}