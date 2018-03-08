package hep.aida.web.taglib.util;

import hep.aida.IManagedObject;
import jas.hist.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import javax.servlet.jsp.PageContext;

/**
 * Various utility functions.
 *
 * @author The AIDA Team @ SLAC
 *
 */
public abstract class AidaTLDUtils {
    
    public static List createList(SortedMap[] maps, String key) {
        return createList(maps, key, false);
    }
    
    public static List createList(SortedMap[] maps, String key, boolean unique) {
        ArrayList list = new ArrayList(maps.length);
        for (int i=0; i<maps.length; i++) {
            Object value = maps[i].get(key);
            if (unique && list.contains(value)) continue;
            if (value != null) list.add(value);
        }
        return list;
    }
    
    public static void sortList(List list, boolean ascending) {
        Comparator comp = null;
        if (!ascending) {
            comp = new Comparator() {
                public int compare(Object o1, Object o2) {
                    String o1_str = (o1 instanceof String) ? (String) o1 : o1.toString();
                    String o2_str = (o2 instanceof String) ? (String) o2 : o2.toString();
                    return ((String) o2_str).compareTo((String) o1_str);
                }
            };
        }
        Collections.sort(list, comp);
    }
    
    /**
     * Find an Object in a JSP scope under the given attribute name. If
     * nothing is found then return null.
     *
     * @param attributeName
     *            the name of the IManagedObject in a JSP scope
     * @return the IManagedObject if it is found, otherwise null
     */
    public static Object findObject(String attributeName, PageContext pageContext) {
        Object plotObject = null;
        
        // There is a bug in ColdFusion MX 6.1 on JRun4 whereby a
        // request scope attribute exists but its value is always null.
        // Therefore, we simply search the scopes ourselves.
        // plotObject = (IManagedObject)
        // pageContext.findAttribute(attributeName);
        int[] scope = { PageContext.PAGE_SCOPE, PageContext.REQUEST_SCOPE,
        PageContext.SESSION_SCOPE, PageContext.APPLICATION_SCOPE };
        for (int i = 0; i < scope.length; ++i) {
            plotObject = (Object) pageContext.getAttribute(attributeName,
                    scope[i]);
            if (plotObject != null) {
                break;
            }
        }
        
        return plotObject;
    }
    
    
    public static boolean isEmpty(String par) {
        return  par == null ||
                par.trim().equalsIgnoreCase("null") ||
                par.trim().equals("");
    }
    
    /**
     * Name of the current Object
     */
    public static String objectName(Object obj) {
        String name = "";
        if (obj != null) {
            if (obj instanceof IManagedObject) name = ((IManagedObject) obj).name();
            else if (obj instanceof DataSource) name = ((DataSource) obj).getTitle();
        }
        return name;
    }
    
    /**
     * Type of the current object
     */
    public static String objectType(Object obj) {
        String type = "";
        if (obj != null) {
            if (obj instanceof IManagedObject) type = ((IManagedObject) obj).type();
            else type = obj.getClass().getName();
        }
        return type;
    }
    
}