package hep.aida.ref.plotter.style.registry;

import hep.aida.IPlotterStyle;
import hep.aida.ref.plotter.Style;

import java.io.File;

public class XMLStyleStore extends BaseStyleStore {
    public static String TYPE = "XMLStyleStore";
    private String commitFileName;
    
    public XMLStyleStore(String storeName) {
        this(storeName, TYPE);
    }
    public XMLStyleStore(String storeName, String storeType) {
         this(storeName, storeType, true);
    }
    public XMLStyleStore(String storeName, String storeType, boolean isReadOnly) {
        super(storeName); 
        this.storeType = storeType;
        this.isReadOnly = isReadOnly;
    }
    
    public XMLStyleStore(String storeName, IPlotterStyle[] styles, boolean isReadOnly) {
        super(storeName); 
        this.storeType = TYPE;
        this.isReadOnly = isReadOnly;
        
        for (int i =0; i<styles.length; i++) {
            String name = "Style_"+i;
            try {
                String tmp = styles[i].parameterValue(Style.PLOTTER_STYLE_NAME);
                if (tmp != null && !tmp.trim().equals("")) name = tmp;
            } catch (Exception e) { e.printStackTrace(); }
            
            addStyle(name, styles[i]);
        }
    }
    

    // Service methods
    
     public void setReadOnly(boolean isReadOnly) { 
	  this.isReadOnly = isReadOnly;
     }
	
     public String getCommitFileName() { return commitFileName; }

     public void setCommitFileName(String commitFileName) { 
        if (commitFileName != null && commitFileName.trim().equals("")) this.commitFileName = null;
        else this.commitFileName = commitFileName;
    }   
    
    // IStyleStore methods
    
    public boolean isReadOnly()  { 
        if (commitFileName == null || isReadOnly) return true;
        else return false;
    }
    
    public void commit() {
        if (isReadOnly)
            throw new UnsupportedOperationException("XMLStyleStore "+getStoreName()+" :: setup as a Read-Only store, can not commit");
        if (commitFileName == null)
            throw new UnsupportedOperationException("XMLStyleStore "+getStoreName()+" :: Commit File Name is NULL, can not commit");
        File file = new File(commitFileName);
        try {
            if (!file.exists()) file.createNewFile();
            if (!file.canWrite())
                throw new UnsupportedOperationException("XMLStyleStore "+getStoreName()+" :: Commit File \""+commitFileName+"\" is Read-Only, can not commit");
            StyleStoreXMLWriter.writeToFile(commitFileName, this, false);
        } catch (UnsupportedOperationException uoe) { 
            throw uoe;
        } catch (Exception e) { e.printStackTrace(); }
    }
           
    public void close() {  
        commitFileName = null;
        super.close();
    }
           
    
    
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
