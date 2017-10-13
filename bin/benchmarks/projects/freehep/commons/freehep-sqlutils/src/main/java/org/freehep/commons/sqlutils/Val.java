
package org.freehep.commons.sqlutils;

import org.freehep.commons.sqlutils.format.AbstractSQLFormatter;
import org.freehep.commons.sqlutils.interfaces.Formattable;
import org.freehep.commons.sqlutils.interfaces.MaybeHasAlias;

/**
 *
 * @author bvan
 */
public class Val<T> implements MaybeHasAlias<Val>, Formattable {
    private T value;
    private String alias = "";
    
    public Val(){}
    
    public Val(T value) { 
        this.value = value;
    }
    
    public Val(T value, String alias) { 
        this.value = value;
        this.alias = alias;
    }
    
    public T getValue(){
        return this.value;
    }

    @Override
    public String alias() {
        return alias != null ? alias : "";
    }

    @Override
    public Val as(String alias) {
        this.alias = alias;
        return this;
    }
    
    @Override
    public Val asExact(String alias){
        this.alias = '"' + alias + '"';
        return this;
    }
    
    @Override
    public String canonical(){
        return !alias().isEmpty() ? alias : value.toString();
    }

    @Override
    public String formatted(){
        return formatted(AbstractSQLFormatter.getDefault());
    }
    
    @Override
    public String formatted(AbstractSQLFormatter fmtr){
        return fmtr.format( this );
    }

}
