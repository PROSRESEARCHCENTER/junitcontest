/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freehep.commons.sqlutils;

// Ops and their sql

import java.util.List;
import org.freehep.commons.sqlutils.interfaces.Formattable;
import org.freehep.commons.sqlutils.interfaces.MaybeHasAlias;

/**
 * Ops and their SQL equivalence. Methods to construct boolean Expressions.
 * Expr
 * @author bvan
 */
public enum Op {
    /** {@literal 'AND'}*/
    AND("AND"), 
    /** {@literal 'OR'}*/
    OR("OR"), 
    /** {@literal '='}*/
    EQ("="), 
    /** {@literal '<'}*/
    LT("<"), 
    /** {@literal '<='}*/
    LTEQ("<="), 
    /** {@literal '>'}*/
    GT(">"), 
    /** {@literal '>='}*/
    GTEQ(">="), 
    /** {@literal '!='}*/
    NOT_EQ("!="), 
    /** {@literal 'BETWEEN'}*/
    BETWEEN("BETWEEN"), 
    /** {@literal 'NOT BETWEEN'}*/
    NOT_BETWEEN("NOT BETWEEN"), 
    /** {@literal 'IN'}*/
    IN("IN"), 
    /** {@literal 'NOT IN'}*/
    NOT_IN("NOT IN"), 
    /** {@literal 'IS NULL'}*/
    IS_NULL("IS NULL"), 
    /** {@literal 'IS NOT NULL'} */
    NOT_NULL("IS NOT NULL"), 
    EXISTS("EXISTS"), 
    NOT_EXISTS("NOT EXISTS");
    
    private String sql;

    private Op(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return this.sql;
    }
    
    public Expr apply(MaybeHasAlias val1, Object val2){
        if( !(val2 instanceof Formattable || val2 instanceof MaybeHasAlias || val2 instanceof List) ){
            throw new RuntimeException("Object " + val2.getClass().getCanonicalName() + 
                    " is not of type MaybeHasParams, Formatter, or List");
        }
        
        if(this == IS_NULL || this == NOT_NULL){
            return new Expr(val1, this, null);
        }
        if(this == EXISTS || this == NOT_EXISTS){
            return new Expr(null, this, val2);
        }
        return new Expr(val1, this, val2);
    }
    
    public Expr apply(Column val1, Object val2){
        if( val1.getClass().isAssignableFrom( val2.getClass()) ){
            throw new RuntimeException("Object " + val2.getClass().getCanonicalName() + 
                    " is not of type MaybeHasParams, Formatter, or List");
        }

        if(this == IS_NULL || this == NOT_NULL){
            return new Expr(val1, this, null);
        }
        if(this == EXISTS || this == NOT_EXISTS){
            return new Expr(null, this, val2);
        }
        return new Expr(val1, this, val2);
    }
    
    public Expr apply(Expr val1, Expr val2){
        return new Expr(val1, this, val2, true);
    }

    public static Expr and(Expr... exprs) {
        return Expr.and(exprs);
    }

    public static Expr or(Expr... exprs) {
        return Expr.or(exprs);
    }

    public static Expr eq(Object from, Object val1) {
        return new Expr(from, Op.EQ, val1);
    }

    public static Expr neq(Object from, Object val1) {
        return new Expr(from, Op.NOT_EQ, val1);
    }

    public static Expr lt(Object from, Object val1) {
        return new Expr(from, Op.LT, val1);
    }

    public static Expr lteq(Object from, Object val1) {
        return new Expr(from, Op.LTEQ, val1);
    }

    public static Expr gt(Object from, Object val1) {
        return new Expr(from, Op.GT, val1);
    }

    public static Expr gteq(Object from, Object val1) {
        return new Expr(from, Op.GTEQ, val1);
    }

    public static Expr exists(Object val1) {
        if (val1 instanceof Select) {
            ((Select) val1).setWrapped(true);
        }
        return new Expr(null, Op.EXISTS, val1);
    }

    public static Expr not_exists(Object val1) {
        if (val1 instanceof Select) {
            ((Select) val1).setWrapped(true);
        }
        return new Expr(null, Op.NOT_EXISTS, val1);
    }

    public static Expr between(Object from, Object val1, Object val2) {
        return new Expr(from, Op.BETWEEN, val1, val2);
    }

    public static Expr not_between(Object from, Object val1, Object val2) {
        return new Expr(from, Op.NOT_BETWEEN, val1, val2);
    }

    public static Expr in(Object from, Object val1) {
        if (val1 instanceof Select) {
            ((Select) val1).setWrapped(true);
        }
        return new Expr(from, Op.IN, val1);
    }

    public static Expr not_in(Object from, Object val1) {
        if (val1 instanceof Select) {
            ((Select) val1).setWrapped(true);
        }
        return new Expr(from, Op.NOT_IN, val1);
    }

    public static Expr is_null(Object from) {
        return new Expr(from, Op.IS_NULL, null);
    }

    public static Expr not_null(Object from) {
        return new Expr(from, Op.NOT_NULL, null);
    }
    
}
