/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.freehep.commons.sqlutils.format;

import org.freehep.commons.sqlutils.Column;
import org.freehep.commons.sqlutils.Delete;
import org.freehep.commons.sqlutils.Expr;
import org.freehep.commons.sqlutils.Insert;
import org.freehep.commons.sqlutils.Param;
import org.freehep.commons.sqlutils.Select;
import org.freehep.commons.sqlutils.Sql;
import org.freehep.commons.sqlutils.Update;
import org.freehep.commons.sqlutils.Val;
import org.freehep.commons.sqlutils.interfaces.MaybeHasAlias;

/**
 *
 * @author bvan
 */
public abstract class AbstractSQLFormatter {
    private static AbstractSQLFormatter formatter;
    
    public static AbstractSQLFormatter getDefault(){
        if(formatter == null){
            formatter = new SQLFormatter();
        }
        return formatter;
    }

    boolean debug = false;
    public boolean getDebug(){
        return this.debug;
    }
    
    public void setDebug(boolean debug){
        this.debug = debug;
    }
    
    public String aliased(String formatted, MaybeHasAlias aliased){
        return formatted + (!aliased.alias().isEmpty() ? " " + aliased.alias() : "");
    }
    
    public abstract String format(Insert stmt);
    public abstract String format(Select stmt);
    public abstract String format(Update stmt);
    public abstract String format(Delete stmt);
    public abstract String format(Param param);
    public abstract String format(Val val);
    
    public String format(Sql sql){ return sql.toString(); }
    
    public abstract String format(Column col, boolean aliased);
    public abstract String format(Expr expr, boolean aliased);
    public abstract String formatAsSafeString(Object value);
    public abstract String formatAsSafeString(Object value, boolean aliased);
}
