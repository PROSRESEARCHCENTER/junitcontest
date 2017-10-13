
package org.freehep.commons.sqlutils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.freehep.commons.sqlutils.format.AbstractSQLFormatter;
import org.freehep.commons.sqlutils.interfaces.MaybeHasAlias;
import org.freehep.commons.sqlutils.interfaces.Schema;
import org.freehep.commons.sqlutils.interfaces.SimpleTable;

/**
 * 
 * @author bvan
 */
public class Table implements SimpleTable<Table> {
    private String table;
    private String alias = "";

    // Column cache/list of columns defined for this table
    private Map<String,Column> columns = new LinkedHashMap<String,Column>(){
        // Override so columns returns the value just inserted
        @Override
        public Column put(String key, Column value){
            super.put(key,value);
            return value;
        }
    };

    public Table() {
        Schema tableInfo = getClass().getAnnotation( Schema.class );
        if(tableInfo != null){
            this.table = tableInfo.name();
            this.alias = tableInfo.alias();
        } else {
            this.table = getClass().getSimpleName();
        }
        initAnnotatedColumns();
    }
    
    public Table(String table){
        this();
        this.table = table;
    }
    
    public Table(String table, String alias){
        this(table);
        this.alias = alias;
    }

    /**
     * Get Table name
     * @return 
     */
    public String getTable(){
        return table;
    }
        
    /**
     * returns a column / new column
     * @param column Name of Column
     * @return 
     */
    public Column getColumn(String column) {
        return columns.containsValue(column)
                ? columns.get(column)
                : columns.put(column, new Column(column, this));
    }
    
    /**
     * Shortened/Convenience function for getColumn
     * @param column Name of Column
     * @return 
     */
    public Column _(String column) {
        return getColumn(column);
    }

    /**
     * Returns columns that have been defined
     */
    public List<Column> columns() {
        List<Column> cols = new ArrayList<>();
        for(Column c: columns.values()){
            if(!cols.contains( c )){
                cols.add( c );
            }
        }
        return cols;
    }
    
    /**
     * returns a column / new column
     * @param column Name of Column
     * @return 
     */
    @Override
    public List<Column> getColumns() {
        return columns();
    }
    
    /**
     * Construct Select statement with this table as the primary Table
     * @param columns
     * @return 
     */
    public Select select(){
        return new Select().from( this );
    }
    
    /**
     * Construct Select statement with this table as the primary Table
     * @param columns
     * @return 
     */
    public Select selectAllColumns(){
        return new Select( columns() ).from( this );
    }

    /**
     * Construct Select statement with this table as the primary Table,7
     * use the following columns for the selection.
     * @param columns
     * @return 
     */
    public Select select(MaybeHasAlias... columns){
        return new Select( columns ).from( this );
    }
    
    /**
     * Construct Select statement with this table as the primary Table
     * @param columns
     * @return 
     */
    public Select select(String... columns){
        return new Select( columns ).from( this );
    }
    
    /**
     * Construct Select statement with this table as the primary Table,
     * and all defined columns for the selection
     * @param columns
     * @return 
     */
    public Select where(Expr... exprs){
        return new Select( columns() ).from( this ).where( exprs );
    }
    
    @Override
    public Table as(String alias){
        this.alias = alias;
        return this;
    }
    
    @Override
    public Table asExact(String alias){
        this.alias = '"' + alias + '"';
        return this;
    }
    
    public <T> T as(String alias, Class<T> clazz){
        this.alias = alias;
        return (T) this;
    }

    @Override
    public String alias() {
        return alias != null ? alias : "";
    }

    @Override
    public String formatted() {
        return formatted(AbstractSQLFormatter.getDefault());
    }
    
    @Override
    public String formatted(AbstractSQLFormatter fmtr) {
        return getTable();
    }

    @Override
    public String canonical(){
        return !alias().isEmpty() ? alias : table;
    }

    
    // Initiate annotated Columns
    private void initAnnotatedColumns() {
        // Initiate Columns for this class and all parents that are of type Table
        for(Class<?> clazz = getClass(); Table.class.isAssignableFrom( clazz ); 
                clazz = clazz.getSuperclass()){
            initAC(clazz);
        }

    }
    
    private void initAC(Class<?> clazz){
        for(Field field: clazz.getDeclaredFields()){
            if(field.getAnnotation( Schema.class ) == null){
                continue;
            }
            Schema schemaColumn = field.getAnnotation( Schema.class );
            String name = schemaColumn.name();
            name = name != null && !name.isEmpty() ? name : field.getName();
            try {
                Class type = Object.class;
                if(field.getGenericType() instanceof ParameterizedType){
                    ParameterizedType t = (ParameterizedType) field.getGenericType();
                    if(t.getActualTypeArguments().length == 1){
                        Type columnType = t.getActualTypeArguments()[0];
                        if(columnType instanceof Class){
                            type = (Class) columnType;
                        } else if(columnType instanceof ParameterizedType){
                            type = (Class) ((ParameterizedType) columnType).getActualTypeArguments()[0];
                        }
                        
                    }
                }
                Column c = schemaColumn.alias().isEmpty() ? 
                        new Column(name, type, this) : 
                        new Column(name, type, this, schemaColumn.alias());
                field.setAccessible( true );
                field.set( this, c );
                field.setAccessible( false );
                columns.put(c.canonical(), c);
            } catch(Exception ex) {
                Logger.getLogger( Table.class.getName() ).warning("Unable to bind Column " + name);
            }
        }
    }
}
