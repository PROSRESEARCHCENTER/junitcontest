
package org.freehep.commons.sqlutils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.freehep.commons.sqlutils.format.AbstractSQLFormatter;
import org.freehep.commons.sqlutils.interfaces.Formattable;
import org.freehep.commons.sqlutils.interfaces.MaybeHasParams;

/**
 *
 * @author bvan
 */
public class Expr implements MaybeHasParams, Formattable {
    
    static class SafeList extends Param<List> implements MaybeHasParams, Formattable {
        List checkedList;
        String name;
        public SafeList(List list){
            Class<?> type = list.get( 0 ) instanceof Number 
                    ? Number.class : list.get( 0 ).getClass();
            init(list, type);
        }
        
        public SafeList(List list, Class<?> type){
            init(list, type);
        }
        
        public SafeList(String name, List list, Class<?> type){
            this.name = null;
            init(list, type);
        }
        
        private void init(List list, Class<?> type){
            checkedList = Collections.checkedList( new ArrayList(), type );
            String name = "a0";
            for(int i = 0; i < list.size(); i++, name = "a" + i){
                checkedList.add( list.get( i ) );
            }
        }
        
        @Override
        public boolean hasParams(){
            return !checkedList.isEmpty();
        }

        @Override
        public List<Param> getParams(){
            List<Param> paramList = new ArrayList<>();
            String pre = name == null ? "a" : name;
            String pname = null;
            for(int i = 0; i < checkedList.size(); i++, pname = pre + i){
                paramList.add(new Param(pname, checkedList.get( i ) ));
            }
            return paramList;
        }
        
        @Override
        public String formatted(){
            return formatted(AbstractSQLFormatter.getDefault());
        }

        @Override
        public String formatted(AbstractSQLFormatter fmtr){
            StringBuilder format = new StringBuilder("(");
            for(Iterator i = checkedList.iterator(); i.hasNext(); ){
                format.append( new Param(name, i.next() ).formatted( fmtr ) );
                format.append( i.hasNext() ? "," : "");
            }
            return format.append( ")").toString();
        }
        
    }
    
    // Ordered list of tokens
    private boolean wrapped = false;
    private Object tLeft;
    private Object tRight;
    private Op oper;
    
    /**
     * Construct Empty expression
     */
    protected Expr(){ }
    
    private Expr(Object identifier){
        this.tLeft = identifier;
    }
    
    private Expr(Object object, Op op){
        this(object);
        this.oper = op;
    }
    
    /**
     * Create a new expression. This expression will be be rendered wrapped 
     * with parens.
     * @param object
     * @param op
     * @param right
     */
    protected Expr(Object object, Op op, Object right){
        this(object, op);
        if(right instanceof List){
            tRight = new SafeList((List)right);
        } else {
            this.tRight = right;
        }
        this.wrapped = true;
    }
    
    /**
     * Create a new expression. If wrap is true, the expression will 
     * be rendered wrapped with parens.
     * @param object
     * @param op
     * @param right
     * @param wrap 
     */
    public Expr(Object object, Op op, Object right, boolean wrap){
        this(object,op,right);
        this.wrapped = wrap;
    }
    
    public Expr(Object object, Op op, Object val1, Object val2){
        this(object, op);
        this.tRight = new Expr(val1, Op.AND, val2);
    }
        
    public static Expr or(Expr... exprs) {
        Expr reduced = exprs[0];
        for(int i = 1; i < exprs.length; i++){
            reduced = new Expr(reduced, Op.OR, exprs[i], false);
        }
        reduced.wrapped = true;
        return reduced;
    }

    public static Expr and(Expr... exprs) {
        Expr reduced = exprs[0];
        for(int i = 1; i < exprs.length; i++){
            reduced = new Expr(reduced, Op.AND, exprs[i], false);
        }
        reduced.wrapped = true;
        return reduced;
    }
    
    public static Expr collapse(Expr expr1, Expr... exprs){
        if(exprs.length == 1 && exprs[0] != null){
            if(expr1.isEmpty() || expr1 == null){
                expr1 = Expr.and( exprs );
            } else {
                expr1 = Expr.and( expr1, Expr.and( exprs ) );
            }
            expr1.setWrapped( false );
        }
        return expr1;
    }
    
    public List getValues(){
        ArrayList vals = new ArrayList();
        if(tLeft != null){
            if(tLeft instanceof Expr){
                vals.addAll( ((Expr) tLeft).getValues());
            } else {
                vals.add(tLeft);
            }
        }
        if(tRight != null){
            if(tRight instanceof Expr){
                vals.addAll( ((Expr) tRight).getValues());
            } else {
                vals.add(tRight);
            }
        }
        return vals;
    }
    
    public Object getLeft(){
        return this.tLeft;
    }
    
    public Op getOp(){
        return this.oper;
    }
    
    public Object getRight(){
        return this.tRight;
    }
        
    public boolean isEmpty(){
        return oper == null && tLeft == null && tRight == null;
    }

    @Override
    public boolean hasParams() {
        return !getParams().isEmpty();
    }
    
    @Override
    public List<Param> getParams() {
        List<Param> params = new ArrayList<>();
        getParams(tLeft, params);
        getParams(tRight, params);
        return params;
    }
    
    private void getParams(Object token, List<Param> params){
        if ( token != null ) {
            if (token instanceof MaybeHasParams) {
                params.addAll( ((MaybeHasParams) token).getParams() );
            } else if (token instanceof Param) {
                params.add( (Param) token );
            }
        }
    }
    
    @Override
    public String toString(){
        return formatted();
    }
    
    @Override
    public String formatted(){
        return formatted(AbstractSQLFormatter.getDefault(), false);
    }
    
    public String formatted(boolean aliased) {
        return formatted(AbstractSQLFormatter.getDefault(), aliased);
    }
    
    @Override
    public String formatted(AbstractSQLFormatter fmtr) {
        return fmtr.format( this, false );
    }
    
    public String formatted(AbstractSQLFormatter fmtr, boolean aliased) {
        return fmtr.format( this, aliased );
    }
    
    public boolean isWrapped(){
        return wrapped;
    }
    
    public void setWrapped(boolean encapsulated){
        this.wrapped = encapsulated;
    }
}
