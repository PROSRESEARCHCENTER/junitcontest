package org.freehep.commons.sqlutils.schema;

import org.freehep.commons.sqlutils.Column;
import org.freehep.commons.sqlutils.Table;
import org.freehep.commons.sqlutils.interfaces.Schema;

/**
 *
 * @author bvan
 */
public class Table0001 extends Table {

    @Schema public Column<Long> pk;
    @Schema public Column<String> name;

    public Table0001(){
        super();
    }

}
