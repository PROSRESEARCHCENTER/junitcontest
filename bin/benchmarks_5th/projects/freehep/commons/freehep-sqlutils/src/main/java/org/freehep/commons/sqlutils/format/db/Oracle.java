
package org.freehep.commons.sqlutils.format.db;

import java.text.SimpleDateFormat;

/**
 *
 * @author bvan
 */
public class Oracle {
    
    public String toTimestamp(java.util.Date date){
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddHHmmss.SSS" );
        return "to_timestamp('" + sdf.format( date ) + "', 'YYYYMMDDHH24MISS.FF')";
    }

}
