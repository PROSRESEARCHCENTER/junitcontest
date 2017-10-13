package hep.aida.web.taglib;

import hep.aida.ITree;
import hep.aida.ITuple;
import hep.aida.web.taglib.util.TreeUtils;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import org.freehep.webutil.tree.DefaultTreeNode;
import org.freehep.webutil.tree.Tree;
import de.schlichtherle.io.File;
import hep.aida.web.taglib.util.LogUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * The implementation class for all TreeDisplayTag classes.
 *
 * @author The AIDA Team @ SLAC
 *
 */
public class DisplayTreeTagSupport implements DisplayTreeTag {
    
    private String storeName;
    private String leafHref;
    private String folderHref;
    private boolean rootVisible;
    private boolean showItemCount;
    private static String rootLabel = "/";
    private boolean showFolderHrefForNodesWithLeavesOnly = false;
    
    
    public void doStartTag() throws JspException {
        if (storeName == null) {
            throw new JspException("storeName is a required attribute for the displayTree tag");
        }
    }
    
    public void doEndTag(PageContext pageContext) throws JspException, IOException  {
        long t0 = System.currentTimeMillis();
        ITree itree = TreeUtils.getTree(getStoreName(), pageContext.getSession().getId());
        if ( itree == null )
            throw new JspException( "Cannot find ITree with name "+getStoreName() );
        
        long t1 = System.currentTimeMillis();
        String[] names = null;
        String[] types = null;
        //File file = new File(getStoreName());
        boolean zipped = false; //file.isArchive() && file.isDirectory();
        if (zipped) {
            File root = new File(getStoreName());
            List list = listContent(root, "/", null);
            names = new String[list.size()];
            names = (String[]) list.toArray(names);
        } else {
            names = itree.listObjectNames("/",true);
            types = itree.listObjectTypes("/",true);
        }
        
        long t2 = System.currentTimeMillis();
        DefaultTreeNode root = new DefaultTreeNode(rootLabel);
        
        AidaDefaultTreeNode child = null;
        for ( int i = 0; i < names.length; i++ ) {
            String fullPath = null;
            String type = null;
            if (zipped) {
                if (names[i].endsWith("/")) {
                    fullPath = names[i];
                    type = "dir";
                } else {
                    int index = names[i].lastIndexOf(".");
                    if (index >= 0) {
                        fullPath = names[i].substring(0, index);
                        type = names[i].substring(index+1);
                    } else {
                        fullPath = names[i];
                        type = "IManagedObject";
                    }
                }
            } else {
                fullPath = names[i];
                type = types[i];
            }
            if ( ! fullPath.endsWith("/") ) {
                String name;
                String path;
                if ( fullPath.indexOf("/") != -1 ) {
                    name = fullPath.substring( fullPath.lastIndexOf("/") +1 );
                    path = fullPath.substring(0, fullPath.lastIndexOf("/"));
                } else {
                    name = fullPath;
                    path = "";
                }
                if (type.toLowerCase().indexOf("ituple") >= 0)
                    child = new AidaTupleTreeNode((ITuple) itree.find(fullPath));
                else
                    child = new AidaDefaultTreeNode(name, type);
                
                root.addNodeAtPath(child,path);
            }
        }
        
        String path = ((HttpServletRequest) (pageContext.getRequest())).getContextPath();
        Tree tree = new Tree(path);
        tree.setRootVisible(rootVisible);
        tree.setShowItemCount(showItemCount);
        tree.setLeafHref(leafHref);
        tree.setFolderHref(folderHref);
        tree.setShowFolderHrefForNodesWithLeavesOnly(showFolderHrefForNodesWithLeavesOnly);
        
        long t3 = System.currentTimeMillis();
        JspWriter out = pageContext.getOut();
        
        if (pageContext.getAttribute("scriptIncluded",PageContext.PAGE_SCOPE) == null) {
            tree.printStyle(out);
            tree.printScript(out);
            pageContext.setAttribute("scriptIncluded",Boolean.TRUE,PageContext.PAGE_SCOPE);
        }
        tree.printTree(out,root,null);
        long t4 = System.currentTimeMillis();
        LogUtils.log().warn(" DisplayTreeTagSupport zipped="+zipped+
                ", name="+getStoreName()+
                ", *** Total:     "+(t4-t0)+
                ", Getting ITree:     "+(t1-t0)+
                ", Getting Directory: "+(t2-t1)+
                ", Creating Tree:     "+(t3-t2)+
                ", Printing Tree:     "+(t4-t3));
    }
    
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    
    public String getStoreName() {
        return storeName;
    }
    
    public void setLeafHref(String leafHref) {
        this.leafHref = leafHref;
    }
    
    public String getLeafHref() {
        return leafHref;
    }
    
    public void setFolderHref(String folderHref) {
        this.folderHref = folderHref;
    }
    
    public String getFolderHref() {
        return folderHref;
    }
    
    public void setRootVisible(boolean rootVisible) {
        this.rootVisible = rootVisible;
    }
    
    public boolean getRootVisible() {
        return rootVisible;
    }
    
    public void setRootLabel(String rootLabel) {
        this.rootLabel = rootLabel;
    }
    
    public String getRootLabel() {
        return rootLabel;
    }
    
    public boolean isShowFolderHrefForNodesWithLeavesOnly() {
        return showFolderHrefForNodesWithLeavesOnly;
    }
    
    public boolean getShowFolderHrefForNodesWithLeavesOnly() {
        return showFolderHrefForNodesWithLeavesOnly;
    }
    
    public void setShowFolderHrefForNodesWithLeavesOnly(boolean visible) {
        showFolderHrefForNodesWithLeavesOnly = visible;
    }
    
    public void setShowItemCount(boolean showItemCount) {
        this.showItemCount = showItemCount;
    }
    
    public boolean getShowItemCount() {
        return showItemCount;
    }
    
    private List listContent(File root, String path, List list) throws IllegalArgumentException, IOException {
        int n=0;
        path = path.startsWith("/") ? path.substring(1) : path;
        
        File file = new File(root, path);
        
        if (list == null) list = new ArrayList();
        if (file.isDirectory()) {
            File[] files = (File[]) file.listFiles();
            int rootLength = root.getPath().length()+1;
            for (int i = 0; i < files.length; i++) {
                String tmpPath = files[i].getInnerEntryName();
                if (tmpPath == null) tmpPath = files[i].getPath().substring(rootLength);
                if (files[i].isDirectory()) {
                    list.add("/"+tmpPath+"/");
                    listContent(files[i], "/", list);
                } else {
                    list.add("/"+tmpPath);
                }
            }
        }
        return list;
    }
    
}