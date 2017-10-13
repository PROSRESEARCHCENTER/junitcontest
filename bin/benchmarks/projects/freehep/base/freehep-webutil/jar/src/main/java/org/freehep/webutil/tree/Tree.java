package org.freehep.webutil.tree;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.servlet.jsp.JspWriter;
import org.freehep.webutil.util.properties.PropertiesLoader;


/**
 * A class for rendering Trees as HTML
 * @author The FreeHEP team @ SLAC
 *
 */
public class Tree {
    
    private int n = 0;
    private IconSet iconSet;
    private boolean showRootNode = true;
    private boolean showItemCount = false;
    private String leafHref;
    private String folderHref;
    private int folderHrefLimit = -1;
    private String treeTarget = null;
    private String onclick = null;
    private Hashtable nodeStatus = new Hashtable();
    private boolean useCookies = true;
    private boolean showEmptyFolders = false;
    private boolean showFolderHrefForNodesWithLeavesOnly = false;
    private String fhiconPath;
    
    // If leafHref or folderHref has this value, href=null is used
    public static String noHref = "none";
            
    /**
     * Creates a Tree renderer using the standard IconSet and with the
     * icons located at the given path.
     */
    public Tree(String path) {
        this.fhiconPath = path;
        iconSet = new DefaultIconSet();
    }
    public Tree(IconSet icons) {
        iconSet = icons;
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
    public boolean isRootVisible() {
        return showRootNode;
    }
    public void setRootVisible(boolean visible) {
        showRootNode = visible;
    }
    public void setShowItemCount(boolean visible) {
        showItemCount = visible;
    }
    public void setLeafHref(String href) {
        this.leafHref = href;
    }
    public String getLeafHref() {
        return leafHref;
    }
    public void setFolderHrefLimit(int limit) {
        this.folderHrefLimit = limit;
    }
    public int getFolderHrefLimit() {
        return folderHrefLimit;
    }
    public void setFolderHref(String href) {
        this.folderHref = href;
    }
    public String getFolderHref() {
        return folderHref;
    }
    public void setTarget(String target) {
        this.treeTarget = target;
    }
    public String getTarget() {
        return treeTarget;
    }
    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }
    public String getOnclick() {
        return onclick;
    }
    public void setUseCookies(boolean useCookies) {
        this.useCookies = useCookies;
    }
    public boolean getUseCookies() {
        return useCookies;
    }
    boolean getShowEmptyFolders() {
        return showEmptyFolders;
    }
    void setShowEmptyFolders(boolean showEmptyFolders) {
        this.showEmptyFolders = showEmptyFolders;
    }
    void setNodeStatus(TreeNode node, boolean isExpanded) {
        nodeStatus.put(node, new Boolean(isExpanded));
    }
    
    private boolean isNodeExpanded(TreeNode node) {
        Object status = nodeStatus.get(node);
        if ( status == null )
            return node.isExpanded();
        else
            return ((Boolean) status).booleanValue();
    }
    
    public void printTree(JspWriter out, TreeNode root, String title) throws IOException {
        out.println("<div class=\"directory\">");
        if ((title == null || title.length() == 0) && !showRootNode) title = root.getLabel();
        if (title != null && title.length()>0) out.println("<h3>"+title+"</h3>");
        out.println("<div style=\"display: block;\">");
        n = 0;
        BitSet isLast = new BitSet();
        
        List children;
        if ( showRootNode ) {
            children = new ArrayList();
            children.add(root);
        } else
            children = root.children();
        
        if ( children.size() > 0 ) {
            int childrenLastNodeIndex = children.size() - 1;
            TreeNode lastNode = (TreeNode) children.get(childrenLastNodeIndex);
            if ( ! getShowEmptyFolders() ) {
                for ( int i = childrenLastNodeIndex; i > -1; i-- ) {
                    TreeNode bottomNode = (TreeNode) children.get(i);
                    boolean isBottomNode = bottomNode.isLeaf() ? true : TreeUtils.nodeHasInnerLeaves(bottomNode);
                    if ( isBottomNode ) {
                        lastNode = bottomNode;
                        break;
                    } 
                }
            }                    
            for (Iterator i = children.iterator(); i.hasNext(); ) {
                TreeNode child = (TreeNode) i.next();
                isLast.set(0,child == lastNode);
                listDirectory(out,0,child,"/"+child.getLabel(),isLast);
            }
        }
        out.println("</div>");
        out.println("</div>");
    }
    
    private int countInnerLeaves(TreeNode node) {
        return countInnerLeaves(node, true);
    }
    
    private int countInnerLeaves(TreeNode node, boolean recursive) {
        List children = node.children();
        int count = 0;
        for ( int k = 0; k < children.size(); k++ ) {
            TreeNode child = (TreeNode) children.get(k);
            if ( child.isLeaf() )
                count++;
            else if (recursive)
                count += countInnerLeaves(child);
        }
        return count;
    }
    
    
    private void listDirectory(JspWriter out, int depth, TreeNode node, String path, BitSet isLast) throws IOException {
        if ( ! node.isLeaf() && ! getShowEmptyFolders() && ! TreeUtils.nodeHasInnerLeaves(node) )
            return;
        if ( node.isLeaf() )
            out.print("<p class=\"leaf\">");
        else
            out.print("<p class=\"folder\">");
        
        for (int j=0; j<depth; j++) printIcon(out, isLast.get(j) ? iconSet.getBlank() : iconSet.getVerticalLine(), null);
        
        String label = node.getLabel();
        int selfInnerLeaves = countInnerLeaves(node, false);
        int innerLeaves = countInnerLeaves(node, true);
        if (showItemCount && !node.isLeaf()) {
            if (selfInnerLeaves == innerLeaves) label += " ( "+innerLeaves+" )";
            else label += " ( "+selfInnerLeaves+" / "+innerLeaves+" )";
        }
        Icon icon = node.getIcon();
        String href = node.isLeaf() ? leafHref : folderHref;
        if (href == null) href = node.getHref();
        if ( (folderHrefLimit > 0 && innerLeaves > folderHrefLimit) ||
             (!node.isLeaf() && showFolderHrefForNodesWithLeavesOnly && !TreeUtils.nodeHasInnerLeaves(node, false)) )
            href = null;
        
        // Overwrite href for a special case
        if (node.getHref() == Tree.noHref) href = null;
        
        if (node.isLeaf()) {
            printIcon(out, isLast.get(depth) ? iconSet.getLastNode() : iconSet.getNode(), null);
            printIcon(out, icon == null ? iconSet.getDocument() : icon, null);
        } else {
            if ( isNodeExpanded(node) ) {
                printIcon(out, isLast.get(depth) ? iconSet.getLastMinus() : iconSet.getMinus(), "this");
                printIcon(out, icon == null ? iconSet.getFolderOpen() : icon, "this.previousSibling");
            } else {
                printIcon(out, isLast.get(depth) ? iconSet.getLastPlus() : iconSet.getPlus(), "this");
                printIcon(out, icon == null ? iconSet.getFolderClosed() : icon, "this.previousSibling");
            }
        }
        
        String title = node.getTitle();
        title = title == null ? "" : "title=\""+title+"\"";
        //System.out.println(title+" :: label="+label+", node.getHref()="+node.getHref()+", href="+href);
        
        if (href != null) {
            href = href.replaceAll("%p", URLEncoder.encode(path,"UTF-8"));
            href = href.replaceAll("%l", URLEncoder.encode(node.getLabel(),"UTF-8"));
            href = node.processHref(href);
            String target = node.getTarget();
            if ( target == null )
                target = treeTarget;
            target = target == null ? "" : "target=\""+target+"\" ";
            
            out.println("<a href=\""+href+"\" "+target+" "+title+">"+label+"</a></p>");
        } else out.println(label);
        
        if (!node.isLeaf()) {
            
            String folderDiv = "<div id=\"folder"+n+"\" title=\""+path+"\" ";
            
            if ( isNodeExpanded(node) )
                folderDiv += " style=\"display: block;\"";
            folderDiv += ">";
            out.println(folderDiv);
            n++;
            
            List children = node.children();
            for (Iterator i = children.iterator(); i.hasNext(); ) {
                TreeNode child = (TreeNode) i.next();
                isLast.set(depth+1,!i.hasNext());
                String newPath = path+"/"+child.getLabel();
                if ( newPath.startsWith("//") )
                    newPath = newPath.substring(1);
                listDirectory(out,depth+1,child,newPath,isLast);
            }
            out.println("</div>");
        }
    }
    private void printIcon(JspWriter out, Icon icon, String toggle) throws IOException {
        if (icon != null) {
            out.print("<img src=\"");
            out.print(fhiconPath+"/"+icon.getSourceURL());
            out.print("\" alt=\"");
            out.print(icon.getAlt());
            out.print("\" width=\"");
            out.print(icon.getWidth());
            out.print("\" height=\"");
            out.print(icon.getHeight());
            if (toggle != null) out.print("\" onclick=\"toggleFolder('folder"+n+"',"+toggle+")");
            out.print("\">");
        }
    }
    public void printStyle(JspWriter out) throws IOException {
        out.println("<style type=\"text/css\">");
        out.println("<!--");
        out.println(".folder  { "+PropertiesLoader.treeFolderStyle()+" }");
        out.println(".leaf    { "+PropertiesLoader.treeLeafStyle()+" }");
        out.println(".directory h3 { margin: 0px; margin-top: 1em; font-size: 11pt; }");
        out.println(".directory p { margin: 0px; white-space: nowrap; }");
        out.println(".directory div { display: none; margin: 0px; }");
        out.println(".directory img { vertical-align: middle; }");
        out.println("-->");
        out.println("</style>");
    }
    public void printScript(JspWriter out) throws IOException {
        out.println("<script type=\"text/javascript\">");
        out.println("<!-- // Hide script from old browsers");
        out.println("function createFolderCookie(name,value,days) {");
        out.println("   if (days != 0) {");
        out.println("      var date = new Date();");
        out.println("      date.setTime(date.getTime()+(days*24*60*60*1000));");
        out.println("      expires = \"; expires=\"+date.toGMTString();");
        out.println("   } else var expires = \"\";");
        out.println("   document.cookie = \"tree.folder.\"+escape(name)+\"=\"+value+expires+\"; path=/\";");
        out.println("}");
        out.println("");
        out.println("function eraseFolderCookie(name) {");
        out.println("   createFolderCookie(name,\"\",0);");
        out.println("}");
        out.println("");
        
        out.println("function toggleFolder(id, imageNode) {");
        out.println("   var folder = document.getElementById(id);");
        out.println("   var kkk = 0;");
        out.println("   var l = 0;");
        out.println("   if (folder == null) {");
        out.println("   } ");
        out.println("   else if (folder.style.display == \"block\") {");
        out.println("      if (imageNode != null) {");
        out.println("         imageNode.nextSibling.src = \""+fhiconPath+"/"+iconSet.getFolderClosed().getSourceURL()+"\";");
        out.println("         l = imageNode.src.length;");
        out.println("         if (imageNode.src.substring(l-"+(fhiconPath+"/"+iconSet.getMinus().getSourceURL()).length()+",l) == \""+fhiconPath+"/"+iconSet.getMinus().getSourceURL()+"\") {");
        out.println("            imageNode.src = \""+fhiconPath+"/"+iconSet.getPlus().getSourceURL()+"\";");
        out.println("         } else {");
        out.println("            imageNode.src = \""+fhiconPath+"/"+iconSet.getLastPlus().getSourceURL()+"\";");
        out.println("         }");
        out.println("      }");
        out.println("      eraseFolderCookie(folder.title);");
        out.println("      folder.style.display = \"none\";");
        out.println("   } else {");
        out.println("      if (imageNode != null) {");
        out.println("         imageNode.nextSibling.src = \""+fhiconPath+"/"+iconSet.getFolderOpen().getSourceURL()+"\";");
        out.println("         l = imageNode.src.length;");
        out.println("         if (imageNode.src.substring(l-"+(fhiconPath+"/"+iconSet.getPlus().getSourceURL()).length()+",l) == \""+fhiconPath+"/"+iconSet.getPlus().getSourceURL()+"\") {");
        out.println("            imageNode.src = \""+fhiconPath+"/"+iconSet.getMinus().getSourceURL()+"\";");
        out.println("         } else {");
        out.println("            imageNode.src = \""+fhiconPath+"/"+iconSet.getLastMinus().getSourceURL()+"\";");
        out.println("         }");
        out.println("      }");
        out.println("      folder.style.display = \"block\";");
        out.println("      createFolderCookie(folder.title,folder.style.display,15);");
        out.println("   }");
        
        // If a function is added to onclick it gets added here.
        if ( getOnclick() != null )
            out.println("   "+getOnclick());
        
        out.println("}");
        out.println("// End script hiding -->");
        out.println("</script>");
    }
}
