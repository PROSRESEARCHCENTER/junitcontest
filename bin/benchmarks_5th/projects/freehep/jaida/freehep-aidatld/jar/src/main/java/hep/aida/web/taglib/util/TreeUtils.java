package hep.aida.web.taglib.util;

import hep.aida.IAnalysisFactory;
import hep.aida.IManagedObject;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.web.taglib.PlotterRegistry;
import hep.aida.web.taglib.TreeTagSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.jsp.JspException;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class TreeUtils implements ServletContextListener, HttpSessionListener {
    
    private static Hashtable treeMap = new Hashtable();;
    private static ITreeFactory treeFactory = IAnalysisFactory.create().createTreeFactory();
    
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //treeMap = new Hashtable();
    }
    
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        treeMap.clear();
    }
    
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        
        System.out.println("**** Destroying session "+httpSessionEvent.getSession().getId());
        
        String sessionId = httpSessionEvent.getSession().getId();
        String context = httpSessionEvent.getSession().getServletContext().getServletContextName();
        synchronized (treeMap) {
            Enumeration keys = treeMap.keys();
            while ( keys.hasMoreElements() ) {
                String storeName = (String) keys.nextElement();
                TreeSession treeSession = (TreeSession) treeMap.get(storeName);
                if ( treeSession.containsSessionId(sessionId) ) {
                    try {
                        closeTree(storeName, sessionId);
                    } catch (IOException ioe) {
                        LogUtils.log().warn("Problems closing tree "+storeName+" for session "+sessionId+" in context "+context);
                    }
                }
            }
        }
        
        //Also clear the plotter registry
        Object obj = httpSessionEvent.getSession().getAttribute(PlotterRegistry.REGISTRY_SESSION_NAME);
        if ( obj != null ) {
            System.out.println("************* Clearing plotter registry ");
            ((PlotterRegistry)obj).clear();
        }
            
    }
    
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
    }
    
    public static ITree getTree(TreeTagSupport tag, String sessionId) throws IOException {
        return getTree(tag.getStoreName(), tag.getStoreType(), tag.getOptions(), sessionId);
    }

    public static ITree getTree(String storeName, String storeType, String options, String sessionId) throws IOException {
        ITree tree;
        synchronized (treeMap) {
            tree = getTree(storeName, sessionId);
            if (tree == null) {
                long t0 = System.currentTimeMillis();
                tree = treeFactory.create(storeName, storeType, true,false, options);
                treeMap.put(storeName, new TreeSession(tree, sessionId));
                long t1 = System.currentTimeMillis();
                LogUtils.log().warn(" Adding tree "+storeName+" to map for session "+sessionId+". *** Create ITree Time: "+(t1-t0)+" ms. Open trees: "+treeMap.size());
            }
        }
        return tree;
    }
        
    public static ITree getTree(String storeName, String sessionId) {
        ITree tree;
        synchronized (treeMap) {
            TreeSession treeSession = (TreeSession) treeMap.get(storeName);
            if ( treeSession == null )
                return null;
            treeSession.addSessionId(sessionId);
            tree = treeSession.tree();
            LogUtils.log().debug("Getting tree "+storeName+" from map for session "+sessionId+". Open trees: "+treeMap.size());
        }
        return tree;
    }
    
    public static void closeTree(String storeName, String sessionId) throws IOException {
        Object obj = treeMap.get(storeName);
        if ( obj != null ) {
            TreeSession treeSession = (TreeSession) obj;
            treeSession.removeSessionId(sessionId);
            if ( ! treeSession.hasSessionId() ) {
                ITree tree = treeSession.tree();
                tree.close();
                treeMap.remove(storeName);
                
//                LogUtils.log().debug("Removing managed objects ");
//
//                String[] objs = tree.listObjectNames();
//                for ( int i = 0; i < objs.length; i++ ) {
//                    tree.rm(objs[i]);
//                }
                
                LogUtils.log().debug("Closed tree "+storeName+" for sessionId "+sessionId+". Open trees: "+treeMap.size());
            } else
                LogUtils.log().debug("Detached tree "+storeName+" from sessionId "+sessionId+". Open trees: "+treeMap.size());
        } else
            LogUtils.log().warn("Attempt to close non existent tree "+storeName+" Open trees: "+treeMap.size());
    }
    
    private static class TreeSession {
        
        private ITree tree;
        private ArrayList sessionIds = new ArrayList();
        
        TreeSession(ITree tree, String sessionId) {
            this.tree = tree;
            sessionIds.add(sessionId);
        }
        
        ITree tree() {
            return tree;
        }
        
        void addSessionId(String sessionId) {
            if ( sessionIds.contains(sessionId) )
                return;
            sessionIds.add(sessionId);
        }
        
        void removeSessionId(String sessionId) {
            sessionIds.remove(sessionId);
        }
        
        boolean containsSessionId(String sessionId) {
            return sessionIds.contains(sessionId);
        }
        
        boolean hasSessionId() {
            return sessionIds.size() != 0;
        }
    }
    
}
