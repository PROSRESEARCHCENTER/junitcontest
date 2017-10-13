
package org.freehep.commons.sqlutils.interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import org.freehep.commons.sqlutils.Param;

/**
 *
 * @author bvan
 */
public abstract class Executable implements Formattable, MaybeHasParams {

    public PreparedStatement prepare(Connection conn) throws SQLException {
        String sql = formatted();
        PreparedStatement stmt = conn.prepareStatement(sql);
        return stmt;
    }

    public void bindAll(PreparedStatement stmt) throws SQLException {
        Iterator<Param> params = getParams().iterator();
        for (int i = 1; params.hasNext(); i++) {
            // TODO: Handle Safe List
            stmt.setObject(i, params.next().getValue());
        }
    }

    public PreparedStatement prepareAndBind(Connection conn) throws SQLException {
        PreparedStatement stmt = prepare(conn);
        try {
            bindAll(stmt);
        } catch (SQLException ex){
            stmt.close();
            throw ex;
        }
        return stmt;
    }
    
}
