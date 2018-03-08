package hep.aida.web.taglib;

import hep.aida.IAnalysisFactory;
import hep.aida.IDataPoint;
import hep.aida.IDataPointSet;
import hep.aida.IDataPointSetFactory;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.ITuple;
import hep.aida.web.taglib.util.PlotUtils;
import jas.hist.Rebinnable1DHistogramData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;


/**
 * The implementation class for all {@link DataPointSetTag}classes.
 * 
 * @author The AIDA Team @ SLAC
 * 
 */
public class DataPointSetTagSupport implements DataPointSetTag {


    private String var;

    private String scope = "page";

    private Object tuple;

    private String xAxisColumn;

    private String yAxisColumn;

    private String title = null;

    private IAnalysisFactory analysisFactory = IAnalysisFactory.create();

    private ITreeFactory treeFactory = analysisFactory.createTreeFactory();

    public void doStartTag() throws JspException {
        if (var == null || var.length() == 0) {
            throw new JspException("var must not be null");
        }

        if (tuple == null) {
            throw new JspException("tuple must not be null");
        }

        if (xAxisColumn == null || xAxisColumn.length() == 0) {
            throw new JspException("xAxisColumn must not be null");
        }

        if (yAxisColumn == null || yAxisColumn.length() == 0) {
            throw new JspException("yAxisColumn must not be null");
        }
    }

    public void doEndTag(PageContext pageContext) throws JspException {
        String scopeName = getScope();
        if (scopeName == null) {
            scopeName = "page";
        }
        int scope = PlotUtils.getScope(scopeName);

        ITuple ntuple = null;
        if (tuple instanceof ITuple) {
            ntuple = (ITuple) tuple;
        } else if (tuple instanceof String) {
            // If tuple is a string, then search all JSP scopes for a
            // an ITuple with that name.
            String attributeName = (String) tuple;
            ntuple = findTuple(attributeName, pageContext);

            if (ntuple == null) {
                throw new JspException("Could not fined tuple " + tuple);
            }
        } else {
            // We don't know how to handle objects of this type.
            throw new JspException("don't know how to handle query " + tuple);
        }

        String dataPointSetPath = getVar();
        String dataPointSetTitle = getVar();
        if (getTitle() != null)
            dataPointSetTitle = getTitle();

        Object dataPointSet;

        if (ntuple.columnType(ntuple.findColumn(xAxisColumn)).isPrimitive())
            dataPointSet = toDataPointSet(ntuple, dataPointSetPath,
                    dataPointSetTitle, xAxisColumn, yAxisColumn);
        else
            dataPointSet = toDataSource(ntuple, dataPointSetTitle, xAxisColumn,
                    yAxisColumn);

        // Store the IDataPointSet in a JSP scope.
        pageContext.setAttribute(getVar(), dataPointSet, scope);
    }

    IDataPointSet toDataPointSet(ITuple tuple, String dataPointSetPath,
            String dataPointSetTitle, String xAxisColumn, String yAxisColumn)
            throws JspException {

        ITree tree = treeFactory.create();
        IDataPointSetFactory dataPointSetFactory = analysisFactory
                .createDataPointSetFactory(tree);

        // Create a two dimensional IDataPointSet.
        IDataPointSet dataPointSet = dataPointSetFactory.create(
                dataPointSetPath, dataPointSetTitle, 2);

        int xAxisColumnIndex = tuple.findColumn(xAxisColumn);
        int yAxisColumnIndex = tuple.findColumn(yAxisColumn);

        tuple.start();
        while (tuple.next()) {
            IDataPoint datapoint = dataPointSet.addPoint();
            datapoint.coordinate(0).setValue(
                    getTupleColumnAsDouble(tuple, xAxisColumnIndex, -1.0));
            datapoint.coordinate(1).setValue(
                    getTupleColumnAsDouble(tuple, yAxisColumnIndex, -1.0));
        }

        return dataPointSet;
    }

    /**
     * Convert the next value of the column at the specified index in the
     * {@link ITuple}to a double if the column's type is a numeric type. A
     * numeric type is defined to be boolean, byte, character, short, int, long,
     * float or double. If the column's type is any other type, then the
     * specified default value is returned. A boolean value 'true' is converted
     * to 1.0, and a boolean 'false' is converted to 0.0.
     * 
     * @param tuple
     *            the {@link ITuple}to read the next value from
     * @param columnIndex
     *            the column index (0-based) into the {@link ITuple}
     * @param defaultValue
     *            the default value to use if the column at the specifed index
     *            is not a numeric value
     * @return the value of the column at the specified index in the
     *         {@link ITuple}converted to a double
     */
    private double getTupleColumnAsDouble(ITuple tuple, int columnIndex,
            double defaultValue) {
        Class clazz = tuple.columnType(columnIndex);
        double value = defaultValue;
        if (Boolean.TYPE.equals(clazz)) {
            value = tuple.getBoolean(columnIndex) ? 1.0 : 0.0;
        } else if (Byte.TYPE.equals(clazz)) {
            value = tuple.getByte(columnIndex);
        } else if (Character.TYPE.equals(clazz)) {
            value = tuple.getChar(columnIndex);
        } else if (Short.TYPE.equals(clazz)) {
            value = tuple.getShort(columnIndex);
        } else if (Integer.TYPE.equals(clazz)) {
            value = tuple.getInt(columnIndex);
        } else if (Long.TYPE.equals(clazz)) {
            value = tuple.getLong(columnIndex);
        } else if (Float.TYPE.equals(clazz)) {
            value = tuple.getFloat(columnIndex);
        } else if (Double.TYPE.equals(clazz)) {
            value = tuple.getDouble(columnIndex);
        }

        return value;
    }

    Rebinnable1DHistogramData toDataSource(ITuple tuple,
            String dataPointSetTitle, String xAxisColumn, String yAxisColumn)
            throws JspException {

        int xAxisColumnIndex = tuple.findColumn(xAxisColumn);
        int yAxisColumnIndex = tuple.findColumn(yAxisColumn);

        int rows = tuple.rows();
        String[] labels = new String[rows];
        double[] yData = new double[rows];

        tuple.start();
        for (int i = 0; i < rows; i++) {
            tuple.next();
            labels[i] = getTupleColumnAsString(tuple, xAxisColumnIndex);
            yData[i] = getTupleColumnAsDouble(tuple, yAxisColumnIndex, -1.0);
        }

        return new DataPointSetWithString(dataPointSetTitle, labels, yData);
    }

    private String getTupleColumnAsString(ITuple tuple, int columnIndex) {
        Class clazz = tuple.columnType(columnIndex);
        String value = "null";
        if (String.class.equals(clazz)) {
            value = tuple.getString(columnIndex);
        } else if (Object.class.equals(clazz)) {
            value = tuple.getObject(columnIndex).toString();
        }
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.DataPointSetTagg#setVar(java.lang.String)
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
     * @see hep.aida.web.taglib.DataPointSetTag#setScope(java.lang.String)
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
     * @see hep.aida.web.taglib.DataPointSetTagg#setTuple(java.lang.Object)
     */
    public void setTuple(Object tuple) {
        this.tuple = tuple;
    }

    public Object getTuple() {
        return tuple;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.DataPointSetTag#setXaxisColumn(java.lang.String)
     */
    public void setXaxisColumn(String xAxisColumn) {
        this.xAxisColumn = xAxisColumn;
    }

    public String getXaxisColumn() {
        return xAxisColumn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.DataPointSetTag#setYaxisColumn(java.lang.String)
     */
    public void setYaxisColumn(String yAxisColumn) {
        this.yAxisColumn = yAxisColumn;
    }

    public String getYaxisColumn() {
        return yAxisColumn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.DataPointSetTag#setTitle(java.lang.String)
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Find a {@link ITuple}in a JSP scope under the given attribute name. If
     * nothing is found then return null.
     * 
     * @param attributeName
     *            the name of the {@link ITuple}in a JSP scope
     * @return the {@link ITuple}if it is found, otherwise null
     */
    private ITuple findTuple(String attributeName, PageContext pageContext) {
        ITuple tuple = null;

        // There is a bug in ColdFusion MX 6.1 on JRun4 whereby a
        // request scope attribute exists but its value is always null.
        // Therefore, we simply search the scopes ourselves.
        // resultSet = (ResultSet)
        // pageContext.findAttribute(attributeName);
        int[] scope = { PageContext.PAGE_SCOPE, PageContext.REQUEST_SCOPE,
                PageContext.SESSION_SCOPE, PageContext.APPLICATION_SCOPE };
        for (int i = 0; i < scope.length; ++i) {
            tuple = (ITuple) pageContext.getAttribute(attributeName, scope[i]);
            if (tuple != null) {
                break;
            }
        }

        return tuple;
    }

    /**
     * Private class for dataPointSets with string as axis.
     *  
     */

    private class DataPointSetWithString implements Rebinnable1DHistogramData {

        private String[] xLabels;

        private double[] yData;

        private String title;

        DataPointSetWithString(String title, String[] xLabels, double[] yData) {
            this.yData = yData;
            this.xLabels = xLabels;
            this.title = title;
        }

        public String[] getAxisLabels() {
            return xLabels;
        }

        public int getAxisType() {
            return Rebinnable1DHistogramData.STRING;
        }

        public int getBins() {
            return getAxisLabels().length;
        }

        public double getMax() {
            return getBins();
        }

        public double getMin() {
            return 0;
        }

        public String getTitle() {
            return title;
        }

        public boolean isRebinnable() {
            return false;
        }

        public double[][] rebin(int param, double param1, double param2,
                boolean param3, boolean param4) {
            return new double[][] { yData };
        }
    }
}