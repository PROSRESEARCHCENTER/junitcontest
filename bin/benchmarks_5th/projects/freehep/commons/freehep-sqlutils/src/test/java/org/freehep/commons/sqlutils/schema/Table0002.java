
package org.freehep.commons.sqlutils.schema;

import java.sql.Timestamp;
import org.freehep.commons.sqlutils.Column;
import org.freehep.commons.sqlutils.Table;
import org.freehep.commons.sqlutils.interfaces.Schema;

/**
 *
 * @author bvan
 */
public class Table0002 extends Table {
    @Schema public Column<Long> table0001_pk;
    @Schema public Column<Timestamp> createdate;

    public Table0002(){
        super();
    }
}
