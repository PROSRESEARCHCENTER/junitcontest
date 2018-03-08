
package org.freehep.commons.sqlutils;

import org.freehep.commons.sqlutils.format.AbstractSQLFormatter;
import org.freehep.commons.sqlutils.interfaces.MaybeHasAlias;

/**
 *
 * @author bvan
 */
public class Sql implements MaybeHasAlias {
    private String sql;
    private String alias = "";
    
    public Sql(){}
    
    public Sql(String str){
        this.sql = str;
    }
    
    @Override
    public String toString(){
        return this.sql;
    }

    @Override
    public String alias() {
        return alias != null ? alias : "";
    }

    @Override
    public Sql as(String alias) {
        this.alias = alias;
        return this;
    }
    
    public <T> T as(String alias, Class<T> clazz){
        this.alias = alias;
        return (T) this;
    }
    
    @Override
    public Sql asExact(String alias){
        this.alias = '"' + alias + '"';
        return this;
    }
    
    public <T> T asExact(String alias, Class<T> clazz){
        this.alias = '"' + alias + '"';
        return (T) this;
    }
    
    @Override
    public String canonical(){
        return !alias().isEmpty() ? alias : sql != null ? sql.toString() : "";
    }

    @Override
    public String formatted(){
        return formatted(AbstractSQLFormatter.getDefault());
    }
    
    @Override
    public String formatted(AbstractSQLFormatter fmtr){
        return sql;
    }
}
