
package org.freehep.commons.sqlutils.format.db;

import java.text.SimpleDateFormat;

/**
 *
 * @author bvan
 */
public class MySQL {
    public String toTimestamp(java.util.Date date){
        SimpleDateFormat sdf = new SimpleDateFormat( "''yyyy-MM-dd HH:mm:ss.SSS''" );
        return sdf.format( date );
    }
}
