
package org.freehep.commons.lang.bool;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 *
 * @author Brian Van Klaveren<bvan@slac.stanford.edu>
 */
public class SymUtils {
    
    private static final HashMap<Integer, String> symMap = new HashMap<Integer, String>() {
        {
            for ( Field f : sym.class.getDeclaredFields() ) {
                // Will just throw an illegal argument exception if not there
                try {
                    put( f.getInt( null ), f.getName() );
                } catch (Exception ex) {}
            }
        }
    };
    
    public static String getSymName(int type){
        return symMap.get( type );
    }

}
