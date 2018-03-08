package org.freehep.commons.sqlutils.schema;

import org.freehep.commons.sqlutils.Select;

/**
 *
 * @author bvan
 */
public class Select_t0001_t0002_named extends Select {
    Table0001Named t1 = new Table0001Named();
    Table0002Named t2 = new Table0002Named();

    public Select_t0001_t0002_named(){
        super();
        from( t1 )
                .selection( t1.getColumns() )
                .selection( t1.getColumns() )
                .join( t2, t1.pkNamed.eq( t2.table0001_pkNamed ) );
    }

}
