package hep.io.root.interfaces;

import java.io.IOException;
import java.util.List;


/**
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: TFile.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface TFile extends hep.io.root.RootObject, TDirectory
{
   int getVersion();

   /**
    * Close the file.
    */
   void close() throws IOException;

   /**
    * Get the StreamerInfo
    */
   List streamerInfo() throws IOException;

   TKey streamerInfoKey();
}
