
package org.freehep.commons.sqlutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.freehep.commons.sqlutils.format.AbstractSQLFormatter;
import org.freehep.commons.sqlutils.interfaces.Executable;
import org.freehep.commons.sqlutils.interfaces.SimpleTable;

/**
 *
 * @author bvan
 */
public class Insert extends Executable {

    private Table into;
    private List<Column> columns = new ArrayList<>();
    private Object source;  // List or Select
    
    /**
     * Construct initial INSERT statement.
     */
    public Insert() { this.into = new Table();}

    /**
     * Construct initial INSERT statement with the table to be inserted into
     * @param table
     */
    public Insert(Table table) {
        this.into = table;
    }

    public Insert(String tableName) {
        this.into = new Table(tableName);
    }
    
    public Insert into(Table into){
        this.into = into;
        return this;
    }
    
    public Insert into(String into){
        this.into = new Table(into);
        return this;
    }

    public Table getInto(){
        return into;
    }
    
    /**
     * Columns of the table that should be inserted into.
     * @param columns
     */
    public Insert columns(Column... columns){
        this.columns.addAll(Arrays.asList(columns));
        return this;
    }
    
    public Insert columns(String... columns){
        for(String s: columns){
            this.columns.add( new Column(s, into) );
        }
        return this;
    }

    public List<Column> getColumns(){
        return columns;
    }
    
    /**
     * If inserting values, source is now a list
     * @param values
     * @return 
     */
    public Insert values(Object... values){
        List l = new ArrayList();
        l.addAll(Arrays.asList(values));
        this.source = l;
        return this;
    }
    
    public void dump() {
        System.out.println(formatted());
    }
    
    @Override
    public String formatted() {
        return formatted(AbstractSQLFormatter.getDefault());
    }
    
    @Override
    public String formatted(AbstractSQLFormatter fmtr) {
        return fmtr.format( this );
    }

    @Override
    public boolean hasParams() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getSource(){
        return this.source;
    }
    
    public Insert source(Object source){
        if( !((source instanceof List) || (source instanceof SimpleTable))){
            throw new RuntimeException("Value must be list or SimpleTable (table or query)");
        }
        this.source = source;
        return this;
    }
    
    @Override
    public List<Param> getParams() {
        //for()
        return null;
    }
}
