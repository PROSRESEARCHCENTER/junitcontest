package hep.aida.web.taglib;

import hep.aida.ITuple;

import java.sql.ResultSet;

import javax.servlet.jsp.jstl.sql.Result;

/**
 * A top level tag which cretes an AIDA {@link ITuple}.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public interface TupleTag {

    /**
     * Set the name of the output variable for the {@link ITuple}. This is a
     * required attribute. If the scope is not specified (see
     * {@link #setScope(String)}) then the {@link ITuple}will be stored in
     * <code>page</code> scope.
     * 
     * @param var
     *            the name of the output variable
     * 
     * @see #setScope(String)
     */
    public void setVar(String var);

    /**
     * Set the scope of the output variable. This is an optional attribute, and
     * can be one of <code>page</code>,<code>request</code>,
     * <code>session</code> or <code>application</code>. The default is
     * <code>page</code>.
     * 
     * @param scope scope of the output variable
     * 
     * @see #setVar(String)
     */
    public void setScope(String scope);

    /**
     * Set the query to transform into an {@link ITuple}. This can be an
     * instance of {@link ResultSet},{@link Result}or the name of a variable
     * in a JSP scope holding an {@link ResultSet}.
     * 
     * @param query
     *            the query to transform into an {@link ITuple}
     */
    public void setQuery(Object query);
}