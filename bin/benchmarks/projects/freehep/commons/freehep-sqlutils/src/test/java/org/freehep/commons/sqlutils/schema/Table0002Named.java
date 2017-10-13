
package org.freehep.commons.sqlutils.schema;

import java.sql.Timestamp;
import org.freehep.commons.sqlutils.Column;
import org.freehep.commons.sqlutils.Table;
import org.freehep.commons.sqlutils.interfaces.Schema;

/**
 *
 * @author bvan
 */
@Schema(name="Table0002")
public class Table0002Named extends Table {
    @Schema(name="table0001_pk") public Column<Long> table0001_pkNamed;
    @Schema(name="createdate") public Column<Timestamp> createdateNamed;

    public Table0002Named(){
        super();
    }
}
