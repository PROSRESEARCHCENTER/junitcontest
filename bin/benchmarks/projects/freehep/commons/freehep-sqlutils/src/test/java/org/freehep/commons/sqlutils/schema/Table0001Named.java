
package org.freehep.commons.sqlutils.schema;

import org.freehep.commons.sqlutils.Column;
import org.freehep.commons.sqlutils.Table;
import org.freehep.commons.sqlutils.interfaces.Schema;

/**
 *
 * @author bvan
 */
@Schema(name="Table0001")
public class Table0001Named extends Table {

    @Schema(name="pk") public Column<Long> pkNamed;
    @Schema(name="named") public Column<String> nameNamed;

    public Table0001Named(){
        super();
    }
}
