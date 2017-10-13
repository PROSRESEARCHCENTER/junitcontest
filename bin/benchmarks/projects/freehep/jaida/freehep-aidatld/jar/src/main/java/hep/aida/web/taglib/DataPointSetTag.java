package hep.aida.web.taglib;

import hep.aida.IDataPointSet;
import hep.aida.ITuple;

/**
 * A top level tag which cretes an AIDA {@link IDataPointSet}.
 * 
 * @author The AIDA Team @ SLAC.
 *
 */
public interface DataPointSetTag {

    /**
     * Set the name of the output variable for the {@link IDataPointSet}. This
     * is a required attribute. If the scope is not specified (see
     * {@link #setScope(String)}) then the {@link ITuple}will be stored in
     * <code>page</code> scope.
     * 
     * @param var the name of the output variable
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
     * Set the {@link ITuple}used to fill the {@link IDataPointSet}. This can
     * be an instance of {@link IDataPointSet}, or the name of a variable in a
     * JSP scope holding an {@link IDataPointSet}.
     * 
     * @param tuple
     *            the {@link ITuple}used to fill the {@link IDataPointSet}
     */
    public void setTuple(Object tuple);

    /**
     * Set the column name from the {@link ITuple}to use for the x-axis values.
     * This is a required attribute.
     * 
     * @param xAxisColumn
     *            the column name from the {@link ITuple}to use for the x-axis
     *            values.
     * 
     * @see #setTuple(Object)
     */
    public void setXaxisColumn(String xAxisColumn);

    /**
     * Set the column name from the {@link ITuple}to use for the y-axis values.
     * This is a required attribute.
     * 
     * @param yAxisColumn
     *            the column name from the {@link ITuple}to use for the y-axis
     *            values.
     * 
     * @see #setTuple(Object)
     */
    public void setYaxisColumn(String yAxisColumn);

    /**
     * Set the title of the DataPointSet.
     * 
     * @param title
     *            the title of the IDataPointSet
     *  
     */
    public void setTitle(String title);
}