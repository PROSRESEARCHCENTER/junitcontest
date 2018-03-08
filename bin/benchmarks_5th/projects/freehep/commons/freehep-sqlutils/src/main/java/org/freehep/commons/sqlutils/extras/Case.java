
package org.freehep.commons.sqlutils.extras;

import org.freehep.commons.sqlutils.Expr;
import org.freehep.commons.sqlutils.Sql;
import org.freehep.commons.sqlutils.format.AbstractSQLFormatter;
import org.freehep.commons.sqlutils.interfaces.Formattable;

/**
 *
 * @author bvan
 */
public class Case extends Sql implements Formattable {
    private Expr expression;
    private Formattable tClause;
    private Formattable eClause;
    
    public Case(Expr caseExpression, Formattable thenClause, Formattable elseClause){
        this.expression = caseExpression;
        this.tClause = thenClause;
        this.eClause = elseClause;
        this.expression.setWrapped( false );
    }

    @Override
    public String formatted(){
        return formatted(AbstractSQLFormatter.getDefault());
    }
    
    @Override
    public String formatted(AbstractSQLFormatter fmtr){
        StringBuilder sb = new StringBuilder();
        sb.append( "CASE WHEN ").append( expression.formatted( fmtr, true ) );
        sb.append( " THEN " ).append( tClause.formatted() );
        sb.append( " ELSE " ).append( eClause.formatted() );
        sb.append( " END");
        return sb.toString();
    }

}
