
package org.freehep.commons.sqlutils;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freehep.commons.sqlutils.format.AbstractSQLFormatter;
import org.freehep.commons.sqlutils.interfaces.Formattable;

/**
 *
 * @author bvan
 */
public class Param<T> implements Formattable {
    private String name;
    private T value;
    private Class<?> valueClass;
    private Method valueOf;
    private Method parse;
    
    public Param(){ }
    
    public Param(String name){
        this.name = name;
        init();
    }
    
    public <T> Param(Class<T> valueClass){
        this.valueClass = valueClass;
        init();
    }
    
    public Param(String name, T value){
        this.name = name;
        this.value = value;
        init();
    }
    
    public <T> Param(String name, Class<T> valueClass){
        this.name = name;
        this.valueClass = valueClass;
        init();
    }
    
    public static <S> Param<S> checkedParam(String name, Class<S> clazz) {
        return new Param<>( name, clazz );
    }
    
    public static <S> Param<S> checkedParam(String name, Class<S> clazz, Object value) {
        Param p = new Param<>( name, clazz );
        p.setValue( value );
        return p;
    }
    
    private void init(){
        if( value != null && valueClass == null ){
            valueClass = value.getClass();
        }
        
        if ( valueClass != null ){
            // Set parse and valueOf methods if they take one string
            for(Method m: valueClass.getMethods()) {
                Class[] types = m.getParameterTypes();
                if( types.length == 1 && types[0].equals( String.class ) ){
                    if("valueOf".equals(m.getName())){
                        this.valueOf = m;
                    }
                    if("parse".equals(m.getName())){
                        this.parse = m;
                    }
                }
            }
        }
    }
    
    public String getName(){
        return this.name;
    }
    
    public T getValue(){
        return this.value;
    }

    public Class<?> getValueClass(){
        return valueClass;
    }
    
    private T str2val(Method m, String val){
        if( m != null){
            try {
                Object o = m.invoke(null, val);
                T rval = (T) o;
                return rval;
            } catch (Exception ex) {
                Logger.getLogger(Param.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    
    public T valueOf(String value){
        return str2val(valueOf, value);
    }
    
    public T parse(String value){
        return str2val(parse, value);
    }
    
    public void setName(String name){
        this.name = name;
    }

    public void setValue(T value) {
        Class<?> clz = value.getClass();
        if( valueClass != null && !valueClass.isAssignableFrom( clz ) && !clz.equals(valueClass) ){
            throw new RuntimeException("Unable to set Parameter value: " + value.toString() + 
                    " as class " + valueClass.getCanonicalName());
        }
        this.value = value;
    }
        
    public String sqlString(){
        return name;
    }
    
    @Override
    public String toString(){
        return formatted();
    }

    @Override
    public String formatted() {
        return formatted(AbstractSQLFormatter.getDefault());
    }
    
    @Override
    public String formatted(AbstractSQLFormatter fmtr) {
        return fmtr.format( this );
    }

}