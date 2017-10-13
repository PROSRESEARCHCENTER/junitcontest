
package org.freehep.commons.sqlutils;

import junit.framework.TestCase;
import org.freehep.commons.sqlutils.schema.Table0001;

/**
 *
 * @author bvan
 */
public class TableTest extends TestCase {

    
    public void testNativeColumns(){
        Table0001 t1 = new Table0001();
        t1.getColumns();
        
    }
}
