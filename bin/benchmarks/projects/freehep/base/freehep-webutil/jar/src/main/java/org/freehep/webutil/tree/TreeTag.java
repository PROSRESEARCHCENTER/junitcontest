package org.freehep.webutil.tree;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Enumeration;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * A tag for generating trees.
 * @author The FreeHEP team @ SLAC
 */
public class TreeTag extends SimpleTagSupport {
    
    private TreeNode root = null;
    private boolean showRootNode = true;
    private boolean showItemCount = false;
    private String leafHref;
    private String folderHref;
    private int folderHrefLimit;
    private String rootNodeName = "/";
    private String target = null;
    private String onclick = null;
    private boolean useCookies = true;
    private boolean showEmptyFolders = false;
    private boolean showFolderHrefForNodesWithLeavesOnly = false;
    
    public void doTag() throws JspException, IOException {
        
        if ( root == null )
            throw new JspException("The model could not be loaded.");
        
        StringWriter title = new StringWriter();
        if (getJspBody() != null) getJspBody().invoke(title);
        
        HttpServletRequest request = (HttpServletRequest) ((PageContext) getJspContext()).getRequest();
        HttpServletResponse response = (HttpServletResponse) ((PageContext) getJspContext()).getResponse();
        String path = request.getContextPath();
        Tree tree = new Tree(path);
        tree.setRootVisible(showRootNode);
        tree.setShowItemCount(showItemCount);
        tree.setLeafHref(leafHref);
        tree.setFolderHref(folderHref);
        tree.setFolderHrefLimit(folderHrefLimit);
        tree.setTarget(target);
        tree.setOnclick(onclick);
        tree.setShowEmptyFolders(showEmptyFolders);
        tree.setShowFolderHrefForNodesWithLeavesOnly(showFolderHrefForNodesWithLeavesOnly);
        
        Cookie[] cookies = request.getCookies();
        for ( int i = 0; i < cookies.length; i++ ) {
            Cookie c = cookies[i];
            String name = URLDecoder.decode(c.getName(),"UTF-8");
            if ( name.startsWith("tree.folder.") ) {
                String folderName = name.replace("tree.folder.","");
                TreeNode folder = TreeUtils.findNode(root,folderName);
                if ( folder != null && useCookies ) {
                    tree.setNodeStatus(folder,c.getValue().equals("block"));
                } else {
                    c.setMaxAge(0);
                    response.addCookie(c);
                }                                    
            }
        }
        
        JspWriter out = getJspContext().getOut();
        
        if (getJspContext().getAttribute("scriptIncluded",PageContext.PAGE_SCOPE) == null) {
            tree.printStyle(out);
            tree.printScript(out);
            getJspContext().setAttribute("scriptIncluded",Boolean.TRUE,PageContext.PAGE_SCOPE);
        }
        
        tree.printTree(out,root,title.toString());
        
    }
    
    public void setModel(TreeNode model) {
        this.root = model;
    }
    public void setRootVisible(boolean show) {
        this.showRootNode = show;
    }
    public void setshowItemCount(boolean show) {
        this.showItemCount = show;
    }
    public void setLeafHref(String href) {
        this.leafHref = href;
    }
    public void setFolderHref(String href) {
        this.folderHref = href;
    }
    public void setFolderHrefLimit(int limit) {
        this.folderHrefLimit = limit;
    }
    public void setRootNodeName(String name) {
        this.rootNodeName = name;
    }
    public void setTarget(String target) {
        this.target = target;
    }
    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }
    public void setUseCookies(boolean useCookies) {
        this.useCookies = useCookies;
    }
    public void setShowEmptyFolders(boolean showEmptyFolders) {
        this.showEmptyFolders = showEmptyFolders;
    }
    public void setShowFolderHrefForNodesWithLeavesOnly(boolean visible) {
        showFolderHrefForNodesWithLeavesOnly = visible;
    }
    
}