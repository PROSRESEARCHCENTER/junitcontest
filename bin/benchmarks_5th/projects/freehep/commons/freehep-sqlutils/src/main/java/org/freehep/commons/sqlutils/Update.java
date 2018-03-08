
package org.freehep.commons.sqlutils;

import java.util.ArrayList;
import java.util.List;
import org.freehep.commons.sqlutils.format.AbstractSQLFormatter;
import org.freehep.commons.sqlutils.interfaces.Executable;

/**
 *
 * @author bvan
 */
public class Update extends Executable {
    
    private Table target;
    private List<Expr> clauses = new ArrayList<>();
    private Expr where = new Expr();
    
    /**
     * Construct initial UPDATE statement.
     */
    public Update() { }

    /**
     * Construct initial UPDATE statement with the table to be updated
     * @param table
     */
    public Update(Table table) {
        this.target = table;
    }

    public Update(String tableName) {
        this.target = new Table(tableName);
    }
    
    public Update update(Table targetTable) {
        this.target = targetTable;
        return this;
    }

    public Update update(String targetTableName) {
        return update(new Table(targetTableName));
    }
    
    public Table getTarget(){
        return this.target;
    }
    
    /**
     * SET statement.
     * @param clauses
     * @return 
     */
    public Update set(Expr... clauses) {
        for(Expr e: clauses){
            if(e.isEmpty() || e.getOp() != Op.EQ  || 
                    (e.getLeft() == null && e.getRight() == null) ){
                throw new RuntimeException("Illegal SET clause: " + e.formatted());
            }
            this.clauses.add( e );
        }
        return this;
    }
    
    public List<Expr> getClauses(){
        return this.clauses;
    }
    
    /**
     * WHERE statement.
     * @param predicates
     * @return 
     */
    public Update where(Expr... predicates){
        if(predicates.length == 1 && predicates[0] != null){
            if(where.isEmpty()){
                where = Expr.and( predicates );
            } else {
                where = Expr.and( where, Expr.and(predicates));
            }
            where.setWrapped( false );
        }
        return this;
    }
    
    public Expr getWhere(){
        return this.where;
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
        return !getParams().isEmpty();
    }

    @Override
    public List<Param> getParams() {
        List<Param> params = new ArrayList<Param>();

        for( Expr e: clauses ) {
            params.addAll( e.getParams() );
        }

        params.addAll( where.getParams() );
        return params;
    }

}
