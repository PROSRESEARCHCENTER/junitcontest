package org.freehep.commons.sqlutils.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import org.freehep.commons.sqlutils.Column;
import org.freehep.commons.sqlutils.Delete;
import org.freehep.commons.sqlutils.Expr;
import org.freehep.commons.sqlutils.Insert;
import org.freehep.commons.sqlutils.Op;
import org.freehep.commons.sqlutils.Param;
import org.freehep.commons.sqlutils.Select;
import org.freehep.commons.sqlutils.Select.JoinExpr;
import org.freehep.commons.sqlutils.Sql;
import org.freehep.commons.sqlutils.Table;
import org.freehep.commons.sqlutils.Update;
import org.freehep.commons.sqlutils.Val;
import org.freehep.commons.sqlutils.format.db.Oracle;
import org.freehep.commons.sqlutils.interfaces.Formattable;
import org.freehep.commons.sqlutils.interfaces.MaybeHasAlias;
import org.freehep.commons.sqlutils.interfaces.SimpleTable;

/**
 *
 * @author bvan
 */
public class SQLFormatter extends AbstractSQLFormatter {
    
    public SQLFormatter(){}
    
    @Override
    public String format(Insert stmt){
        Object source = stmt.getSource();
        List<Column> columns = stmt.getColumns();
        if( source == null){
            throw new RuntimeException( "Source is null" );
        }
        StringBuilder sql = new StringBuilder();
        sql.append( "INSERT INTO " );
        sql.append( stmt.getInto().getTable() );
        
        if (!columns.isEmpty()) {
            StringBuilder clist = new StringBuilder( );
            for( Iterator<Column> i = columns.iterator(); i.hasNext(); ) {
                clist.append(i.next().getName() );
                clist.append( i.hasNext() ? ", " : "" );
            }
            sql.append( " ( " ).append( clist.toString() ).append( " )" );
        }
        
        if(source instanceof List){
            List values = (List) source;
            sql.append( " VALUES " );
            StringBuilder vl = new StringBuilder();
            for(Iterator i = values.listIterator(); i.hasNext();){
                vl.append( SQLFormatter.getDefault().formatAsSafeString( i.next() ) );
                vl.append( i.hasNext() ? ", " : "" );
            }
            sql.append( "( " ).append( vl.toString() ).append( " )" );

        } else if(source instanceof SimpleTable){
            if(source instanceof Select){
                Select values = (Select) source;
                sql.append( " " ).append( format(values) );
            } else {
                Table t = (Table) source;
                sql.append( " TABLE " ).append( t.getTable() );
            }
        } else {
            throw new RuntimeException("Source is invalid");
        }
        
        return sql.toString();
    }
    
    @Override
    public String format(Select stmt){
        StringBuilder sql = new StringBuilder();
        sql.append( formatColumns( stmt.getSelections() ,"SELECT", true) );
        sql.append( "FROM " );
        sql.append( aliased( stmt.getFrom().formatted(this), stmt.getFrom()) );
        sql.append( format( stmt.getJoins() ) );
        if(!stmt.getWhere().isEmpty()){
            sql.append( " WHERE " ).append( format(stmt.getWhere(), true ) );
        }
        sql.append( formatColumns( stmt.getGroupBys()," GROUP BY", false) );
        if(!stmt.getHaving().isEmpty()){
            sql.append( " HAVING " ).append( format(stmt.getHaving(), true ) );
        }
        sql.append( formatColumns( stmt.getOrderBys()," ORDER BY", false) );
        return stmt.getWrapped() ? "( " + sql.toString() + " )" : sql.toString();
    }
    
    @Override
    public String format(Update stmt){
        StringBuilder sql = new StringBuilder();
        sql.append( "UPDATE " );
        sql.append( stmt.getTarget().getTable() );
        
        if (stmt.getClauses().isEmpty()) {
            throw new RuntimeException("No SET clauses defined");
        }
        sql.append( " SET " );
        for(Iterator<Expr> iter =  stmt.getClauses().iterator(); iter.hasNext();){
            Expr e = iter.next();
            e.setWrapped( false );
            sql.append( format(e, false) ).append( iter.hasNext() ? ", ":"");
        }
        
        if (!stmt.getWhere().isEmpty()) {
            sql.append( " WHERE " ).append( format( stmt.getWhere(), true ) );
        }

        return sql.toString();
    }
    
    @Override
    public String format(Delete stmt){
        StringBuilder sql = new StringBuilder();
        sql.append( "DELETE FROM " );
        sql.append( stmt.getFrom().getTable() );
        if (!stmt.getWhere().isEmpty()) {
            sql.append( " WHERE "  ).append( format(stmt.getWhere(), true ) );
        } else if(stmt.getProtected()){
            throw new RuntimeException("Cannot DELETE all from an unprotected table");
        }
        return sql.toString();
    }
    
    private String formatColumns(Collection<? extends MaybeHasAlias> cols, String clause, 
            boolean aliased){
        if(cols.isEmpty()){ return ""; }
        ArrayList<String> canonicals = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append( clause ).append( " " );
        for(Iterator<? extends MaybeHasAlias> i1 = cols.iterator(); i1.hasNext();){
            MaybeHasAlias selection = i1.next();
            // Make sure we're not adding redundant columns (in the case of some blind joins)
            if(canonicals.contains( selection.canonical() )){
                if(!i1.hasNext()){
                    int commaIndex = sql.length() - 2;
                    // Handle the case that the redundant canonical is at the end of the list
                    if(",".equals( sql.substring( commaIndex, commaIndex + 1 ) )){
                        sql.delete( commaIndex, commaIndex + 1 );
                    }
                }
                continue;
            }

            canonicals.add( aliased ? selection.canonical() : ((Column) selection).getName() );
            String fmt = selection instanceof Column ? format(((Column) selection), true ) 
                    : selection.formatted(this);
            sql.append( aliased ? aliased(fmt, selection) : selection.formatted(this) );
            sql.append( i1.hasNext() ? ", " : "" );
        }
        return sql.append( " ").toString();
    }
    
    @Override
    public String format(Column col, boolean aliased){
        StringBuilder s = new StringBuilder();
        SimpleTable parent = col.getParent();
        if(aliased){
            // If there's an alias, check the parent
            s.append( parent != null && !parent.alias().isEmpty() ? parent.alias() + "." : "" );
        }
        s.append( col.getName() == null ? parent.toString() : col.getName() );
        return s.toString();

    }
    
    @Override
    public String format(Param param){
        if(!SQLFormatter.getDefault().getDebug()){ return "?"; }
        String type = "";
        if( param.getValueClass() != null ){
            type = "(" + param.getValueClass().getCanonicalName() + ")";
        }
        String val = param.getValue() != null ? param.getValue().toString()  : "null";
        System.err.println( ":" + param.getName() + "->" + type + val);
        return "?";
    }

    // joins
    public String format(LinkedHashMap<JoinExpr, SimpleTable> joinList){
        StringBuilder sql = new StringBuilder();
        Iterator<Entry<JoinExpr, SimpleTable>> iter = joinList.entrySet().iterator();

        for(Entry<JoinExpr, SimpleTable> jc; iter.hasNext();){
            jc = iter.next();
            sql.append( " " ).append( jc.getKey().op() ).append( " " );
            sql.append( aliased(jc.getValue().formatted(this), jc.getValue()) ).append( " ON " );
            sql.append( format( jc.getKey().test(), true ) );
        }
        return sql.toString();
    }
    
    @Override
    public String format(Val val){
        Object value = val.getValue();
        if(value instanceof String){
            return "'" + value.toString() + "'";
        } else if(value instanceof java.util.Date){
            return getDateAsSQLString((java.util.Date) value);
        }
        return value.toString();
    }
    
    @Override
    public String format(Expr expr, boolean aliased){
        StringBuilder s = new StringBuilder();
        if(expr.isEmpty()){ return s.toString(); }
        s.append( formatExprForSqlString( expr.getLeft(), aliased ));
        s.append( formatExprForSqlString( expr.getOp(), aliased ));
        s.append( formatExprForSqlString( expr.getRight(), aliased ));
        
        return expr.isWrapped() ? "( " + s.toString() + " )" : s.toString();
    }
    
    private String formatExprForSqlString(Object token, boolean aliased){
        if(token == null){
            return "";
        }
        if(token instanceof Op){ return " " + token.toString() + " "; }
        return formatAsSafeString(token, true);
    }
    
    @Override
    public String formatAsSafeString(Object value){
        return formatAsSafeString(value, false);
    };
    
    @Override
    public String formatAsSafeString(Object value, boolean aliased){
        if(value instanceof Expr){
            return format((Expr) value, aliased) ;
        } else if(value instanceof Column){
            return format((Column) value, aliased) ;
        } else if(value instanceof Formattable){
            return ((Formattable) value).formatted(this);
        } else if(value instanceof String){
            return "'" + value + "'" ;
        } else if(value instanceof Sql){
            return value.toString();
        } else if(value instanceof java.util.Date){
            return getDateAsSQLString((java.util.Date) value);
        }
        return value.toString();
    };
    
    public String getDateAsSQLString(java.util.Date date){
        return new Oracle().toTimestamp( date );
    }

}