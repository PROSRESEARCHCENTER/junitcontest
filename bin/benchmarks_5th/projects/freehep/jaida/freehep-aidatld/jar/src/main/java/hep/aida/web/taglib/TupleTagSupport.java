package hep.aida.web.taglib;

import hep.aida.IAnalysisFactory;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.ITuple;
import hep.aida.ITupleFactory;
import hep.aida.web.taglib.util.LogUtils;
import hep.aida.web.taglib.util.PlotUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.sql.Result;
import javax.servlet.jsp.jstl.sql.ResultSupport;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.RowSetDynaClass;

/**
 * The implementation class for all {@link TupleTag}classes.
 * 
 * @author The AIDA Team @ SLAC
 * 
 */
public class TupleTagSupport implements TupleTag {

    private String var;

    private String scope = "page";

    private Object query;

    private ITuple tuple;

    private IAnalysisFactory analysisFactory = IAnalysisFactory.create();

    private ITreeFactory treeFactory = analysisFactory.createTreeFactory();

    // Classes supported by ITuple.
    private static final Class[] supportedClassArray = { Boolean.class,
            Byte.class, Character.class, Short.class, Integer.class,
            Long.class, Float.class, Double.class, String.class, Object.class,
            BigDecimal.class };

    // Types supported by ITuple.
    private static final Class[] supportedTypeArray = { Boolean.TYPE,
            Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE,
            Float.TYPE, Double.TYPE, String.class, Object.class, Double.TYPE };

    private static Map classTypeMap = Collections
            .synchronizedMap(new HashMap());

    static {
        for (int i = 0; i < supportedTypeArray.length; ++i) {
            classTypeMap.put(supportedClassArray[i], supportedTypeArray[i]);
        }
    }

    public void doStartTag() throws JspException {
        if (var == null || var.length() == 0) {
            throw new JspException("var must not be null");
        }

        if (query == null) {
            throw new JspException("query must not be null");
        }
    }

    public void doEndTag(PageContext pageContext) throws JspException {
        String scopeName = getScope();
        if (scopeName == null) {
            scopeName = "page";
        }
        int scope = PlotUtils.getScope(scopeName);

        Result result = null;
        if (query instanceof ResultSet) {
            ResultSet resultSet = (ResultSet) query;
            result = ResultSupport.toResult(resultSet);
        } else if (query instanceof Result) {
            result = (Result) query;
        } else if (query instanceof String) {
            // If query is a string, then search all JSP scopes for either a
            // ResultSet or a Result with that name.
            String attributeName = (String) query;
            ResultSet resultSet = findResultSet(attributeName, pageContext);
            if (resultSet != null) {
                result = ResultSupport.toResult(resultSet);
            } else {
                result = findResult(attributeName, pageContext);
            }

            if (result == null) {
                throw new JspException("don't know how to handle query "
                        + query);
            }
        } else {
            // We don't know how to handle objects of this type.
            throw new JspException("don't know how to handle query " + query);
        }

        String tuplePath = getVar();
        String tupleTitle = getVar();
        ITuple tuple = toTuple(result, tuplePath, tupleTitle);
        // Store the ITuple in a JSP scope.
        pageContext.setAttribute(getVar(), tuple, scope);
    }
    
    private Class getColumnType(SortedMap[] rows, String columnName) {
        for ( int i = 0; i < rows.length; i++ ) {
            SortedMap row = rows[i];
            if ( row != null ) {
                Object rowValue = row.get(columnName);
                if ( rowValue != null )
                    return rowValue.getClass();
            }
        }
        return null;
    }

    ITuple toTuple(Result result, String tuplePath, String tupleTitle)
            throws JspException {
        // Convert the ResultsSet to a Result.
        final int rowCount = result.getRowCount();
        if (rowCount < 0) {
            throw new JspException("query has no rows - can not continue");
        }

        List columnNameList = new ArrayList();
        List columnClassList = new ArrayList();

        // Loop over all of the columns from the query and silently ignore types
        // that aren't supported by ITuple.
        SortedMap[] rows = result.getRows();
        final String[] resultColumnNames = result.getColumnNames();
        for (int columnIndex = 0; columnIndex < resultColumnNames.length; ++columnIndex) {
            String columnName = resultColumnNames[columnIndex];
            Class clazz = getColumnType(rows,columnName);
            if ( clazz == null )
                continue;

            // Results broker in Classes. ITuples broker in primitive types
            // (except for String and Object). Therefore, we use the
            // classTypeMap which maps Classes (the keys) to primitive types
            // (the values).
            if (classTypeMap.containsKey(clazz)) {
                if (LogUtils.log().isDebugEnabled()) {
                    String message = "Found supported class "+clazz.getName();
                    LogUtils.log().debug(message);
                }
                columnNameList.add(columnName);
                columnClassList.add(classTypeMap.get(clazz));
            } else if (Date.class.isAssignableFrom(clazz)) {
                // Special case to handle dates.
                if (LogUtils.log().isDebugEnabled()) {
                    String message = "Found supported class "+clazz.getName()+" (but flagged as type Long.TYPE)";
                    LogUtils.log().debug(message);
                }
                columnNameList.add(columnName);
                // Tony says to use doubles instead of longs due
                // to AIDA limitations.
                //columnClassList.add(Long.TYPE);
                columnClassList.add(Double.TYPE);
            } else {
                LogUtils.log().debug("Ignore unsupported type " + clazz.getName());
            }
        }

        String[] columnNames = (String[]) columnNameList.toArray(new String[0]);
        Class[] columnClasses = (Class[]) columnClassList.toArray(new Class[0]);

        // Create an ITree to store the ITuple in. TODO the TupleTag should be
        // nested in a TreeTag and search its ancestry for the TreeTag to get at
        // the ITree.
        ITree tree = treeFactory.create();
        ITupleFactory tupleFactory = analysisFactory.createTupleFactory(tree);
        ITuple tuple = tupleFactory.create(tuplePath, tupleTitle, columnNames,
                columnClasses);

        // Loop over the rows of the query.
        for (int rowIndex = 0; rowIndex < rows.length; ++rowIndex) {
            SortedMap row = rows[rowIndex];
            // Loop over the columns, filling the columns with the values from
            // the query.
            for (int columnIndex = 0; columnIndex < columnNames.length; ++columnIndex) {
                String columnName = columnNames[columnIndex];
                Object columnValue = row.get(columnName);
                
                if ( columnValue == null && tuple.column(columnIndex).type() != Double.TYPE )
                    throw new JspException("A Query cannot contain a null value for a column that is not a double : "+tuple.column(columnIndex).type());
                
                Class clazz = Double.class;
                if ( columnValue != null )
                    clazz = columnValue.getClass();
                
                if (Boolean.class.equals(clazz)) {
                    Boolean value = (Boolean) columnValue;
                    tuple.fill(columnIndex, value.booleanValue());
                } else if (Byte.class.equals(clazz)) {
                    Byte value = (Byte) columnValue;
                    tuple.fill(columnIndex, value.byteValue());
                } else if (Character.class.equals(clazz)) {
                    Character value = (Character) columnValue;
                    tuple.fill(columnIndex, value.charValue());
                } else if (Short.class.equals(clazz)) {
                    Short value = (Short) columnValue;
                    tuple.fill(columnIndex, value.shortValue());
                } else if (Integer.class.equals(clazz)) {
                    Integer value = (Integer) columnValue;
                    tuple.fill(columnIndex, value.intValue());
                } else if (Long.class.equals(clazz)) {
                    Long value = (Long) columnValue;
                    tuple.fill(columnIndex, value.longValue());
                } else if (Float.class.equals(clazz)) {
                    Float value = (Float) columnValue;
                    tuple.fill(columnIndex, value.floatValue());
                } else if (Double.class.equals(clazz)) {
                    if ( columnValue != null ) {
                        Double value = (Double) columnValue;
                        tuple.fill(columnIndex, value.doubleValue());
                    } else
                        tuple.fill(columnIndex, Double.NaN);
                } else if (BigDecimal.class.equals(clazz)) {
                    BigDecimal value = (BigDecimal) columnValue;
                    tuple.fill(columnIndex, value.doubleValue());
                } else if (String.class.equals(clazz)) {
                    String value = (String) columnValue;
                    tuple.fill(columnIndex, value);
                } else if (Date.class.isAssignableFrom(clazz)) {
                    // Convert Dates to longs.
                    Date dateTime = (Date) columnValue;
                    // Tony says to use doubles instead of longs due
                    // to AIDA limitations, and to divide by 1,000 since AIDA
                    // expects seconds instead of milliseconds.
                    //tuple.fill(columnIndex, dateTime.getTime());
                    tuple.fill(columnIndex, dateTime.getTime() / 1000.0);
                } else {
                    // Fill with Object.
                    tuple.fill(columnIndex, columnValue);
                }
            }
            tuple.addRow();
        }

        return tuple;
    }

    /**
     * This is an old implementaion that uses {@link RowSetDynaClass}from
     * commons-beanutils.
     */
    ITuple toTuple(ResultSet resultSet, String tuplePath, String tupleTitle)
            throws SQLException {
        // Convert the ResultsSet to a RowSetDynaClass.
        RowSetDynaClass rsdc = null;
        rsdc = new RowSetDynaClass(resultSet);

        List columnNameList = new ArrayList();
        List columnClassList = new ArrayList();

        // Loop over all of the columns from the query and silently ignore types
        // that aren't supported by ITuple.
        DynaProperty[] properties = rsdc.getDynaProperties();
        for (int columnIndex = 0; columnIndex < properties.length; ++columnIndex) {
            DynaProperty property = properties[columnIndex];
            String name = property.getName();
            Class clazz = property.getType();

            // ResultSets broker in Classes. ITuples broker in primitive types
            // (except for String and Object). Therefore, we use the
            // classTypeMap which maps Classes (the keys) to primitive types
            // (the values).
            if (classTypeMap.containsKey(clazz)) {
                if (LogUtils.log().isDebugEnabled())
                    LogUtils.log().debug("Found supported class "+clazz.getName());

                columnNameList.add(name);
                columnClassList.add(classTypeMap.get(clazz));
            } else if (Date.class.isAssignableFrom(clazz)) {
                // Special case to handle dates.
                if (LogUtils.log().isDebugEnabled()) 
                    LogUtils.log().debug("Found supported class "+clazz.getName()+" (but flagged as type Long.TYPE)");

                columnNameList.add(name);
                columnClassList.add(Long.TYPE);
            } else {
                LogUtils.log().debug("Ignore unsupported type " + clazz.getName());
            }
        }

        String[] columnNames = (String[]) columnNameList.toArray(new String[0]);
        Class[] columnClasses = (Class[]) columnClassList.toArray(new Class[0]);

        // Create an ITree to store the ITuple in. TODO the TupleTag should be
        // nested in a TreeTag and search its ancestry for the TreeTag to get at
        // the ITree.
        ITree tree = treeFactory.create();
        ITupleFactory tupleFactory = analysisFactory.createTupleFactory(tree);
        ITuple tuple = tupleFactory.create(tuplePath, tupleTitle, columnNames,
                columnClasses);

        // Loop over the rows of the query.
        Iterator iterator = rsdc.getRows().iterator();
        while (iterator.hasNext()) {
            DynaBean dynaBean = (DynaBean) iterator.next();
            // Loop over the columns, filling the columns with the values from
            // the query.
            for (int columnIndex = 0; columnIndex < columnNames.length; ++columnIndex) {
                String propertyName = columnNames[columnIndex];
                Class clazz = dynaBean.getDynaClass().getDynaProperty(
                        propertyName).getType();

                // Convert Dates to longs.
                if (Boolean.class.equals(clazz)) {
                    Boolean value = (Boolean) dynaBean.get(propertyName);
                    tuple.fill(columnIndex, value.booleanValue());
                } else if (Byte.class.equals(clazz)) {
                    Byte value = (Byte) dynaBean.get(propertyName);
                    tuple.fill(columnIndex, value.byteValue());
                } else if (Character.class.equals(clazz)) {
                    Character value = (Character) dynaBean.get(propertyName);
                    tuple.fill(columnIndex, value.charValue());
                } else if (Short.class.equals(clazz)) {
                    Short value = (Short) dynaBean.get(propertyName);
                    tuple.fill(columnIndex, value.shortValue());
                } else if (Integer.class.equals(clazz)) {
                    Integer value = (Integer) dynaBean.get(propertyName);
                    tuple.fill(columnIndex, value.intValue());
                } else if (Long.class.equals(clazz)) {
                    Long value = (Long) dynaBean.get(propertyName);
                    tuple.fill(columnIndex, value.longValue());
                } else if (Float.class.equals(clazz)) {
                    Float value = (Float) dynaBean.get(propertyName);
                    tuple.fill(columnIndex, value.floatValue());
                } else if (Double.class.equals(clazz)) {
                    Double value = (Double) dynaBean.get(propertyName);
                    tuple.fill(columnIndex, value.doubleValue());
                } else if (String.class.equals(clazz)) {
                    String value = (String) dynaBean.get(propertyName);
                    tuple.fill(columnIndex, value);
                } else if (Date.class.isAssignableFrom(clazz)) {
                    Date dateTime = (Date) dynaBean.get(propertyName);
                    tuple.fill(columnIndex, dateTime.getTime());
                } else {
                    // Fill with Object.
                    tuple.fill(columnIndex, dynaBean.get(propertyName));
                }
            }
            tuple.addRow();
        }

        return tuple;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleTag#setVar(java.lang.String)
     */
    public void setVar(String var) {
        this.var = var;
    }

    public String getVar() {
        return var;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleTag#setScope(java.lang.String)
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TupleTag#setQuery(java.lang.Object)
     */
    public void setQuery(Object query) {
        this.query = query;
    }

    public Object getQuery() {
        return query;
    }

    /**
     * Find a {@link ResultSet}in a JSP scope under the given attribute name.
     * If nothing is found then return null.
     * 
     * @param attributeName
     *            the name of the {@link ResultSet}in a JSP scope
     * @return the {@link ResultSet}if it is found, otherwise null
     */
    private ResultSet findResultSet(String attributeName,
            PageContext pageContext) {
        ResultSet resultSet = null;

        // There is a bug in ColdFusion MX 6.1 on JRun4 whereby a
        // request scope attribute exists but its value is always null.
        // Therefore, we simply search the scopes ourselves.
        // resultSet = (ResultSet)
        // pageContext.findAttribute(attributeName);
        int[] scope = { PageContext.PAGE_SCOPE, PageContext.REQUEST_SCOPE,
                PageContext.SESSION_SCOPE, PageContext.APPLICATION_SCOPE };
        for (int i = 0; i < scope.length; ++i) {
            resultSet = (ResultSet) pageContext.getAttribute(attributeName,
                    scope[i]);
            if (resultSet != null) {
                break;
            }
        }
        return resultSet;
    }

    /**
     * Find a {@link Result}in a JSP scope under the given attribute name. If
     * nothing is found then return null.
     * 
     * @param attributeName
     *            the name of the {@link Result}in a JSP scope
     * @return the {@link Result}if it is found, otherwise null
     */
    private Result findResult(String attributeName, PageContext pageContext) {
        Result result = null;

        // There is a bug in ColdFusion MX 6.1 on JRun4 whereby a
        // request scope attribute exists but its value is always null.
        // Therefore, we simply search the scopes ourselves.
        // resultSet = (ResultSet)
        // pageContext.findAttribute(attributeName);
        int[] scope = { PageContext.PAGE_SCOPE, PageContext.REQUEST_SCOPE,
                PageContext.SESSION_SCOPE, PageContext.APPLICATION_SCOPE };
        for (int i = 0; i < scope.length; ++i) {
            result = (Result) pageContext.getAttribute(attributeName, scope[i]);
            if (result != null) {
                break;
            }
        }
        return result;
    }
}