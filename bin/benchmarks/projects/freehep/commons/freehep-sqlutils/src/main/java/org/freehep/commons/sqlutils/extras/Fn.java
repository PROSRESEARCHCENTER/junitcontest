
package org.freehep.commons.sqlutils.extras;

import org.freehep.commons.sqlutils.Column;
import org.freehep.commons.sqlutils.Sql;
import org.freehep.commons.sqlutils.interfaces.Formattable;

/**
 *
 * @author bvan
 */
public class Fn extends Sql implements Formattable {
    private String function;
    private Column column;
    
    
    @Override
    public String formatted(){
        throw new UnsupportedOperationException( "Not supported yet." );
    }
    
    public static Fn avg(Column col){
        Fn fn = new Fn();
        fn.column = col;
        fn.function = "AVG";
        return fn;
    }

}
