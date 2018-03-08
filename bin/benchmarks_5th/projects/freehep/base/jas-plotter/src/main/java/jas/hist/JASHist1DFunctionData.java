package jas.hist;

import jas.plot.CoordinateTransformation;
import jas.plot.DateCoordinateTransformation;
import jas.plot.DoubleCoordinateTransformation;
import jas.plot.MutableLegendEntry;
import jas.plot.Overlay;
import jas.plot.PlotGraphics;
import jas.plot.Transformation;

import java.awt.BasicStroke;
import java.io.IOException;
import java.io.Serializable;
import java.util.Observable;

class JASHist1DFunctionData extends JASHistData implements Serializable
{
    
    private double lowerBound = Double.NaN;
    private double upperBound = Double.NaN;
    
    JASHist1DFunctionData(DataManager parent, Basic1DFunction ds)
    {
        super(parent);
        dataSource = ds;
        
        style = new JASHist1DFunctionStyle();
        style.addObserver(this);
    }
    public void setStyle(JASHistStyle style)
    {
        if (!(style instanceof JASHist1DFunctionStyle))
            throw new IllegalArgumentException("Style is not subclass of JASHist1DFunctionStyle");
        if (this.style != null) this.style.deleteObserver(this);
        this.style = (JASHist1DFunctionStyle) style;
        style.addObserver(this);
    }
    public void update (Observable o, Object arg)
    {
        // Dragons: Likely to be called by different thread
        if (o == dataSource) ((SupportsFunctions) parent).update(this);
        else if (o == style) ((SupportsFunctions) parent).update(this); // overkill, we really just need a repaint
    }
    public void axisChanged()
    {
        ((SupportsFunctions) parent).update(this);
    }
    
    public void setXBounds(double xmin, double xmax) {
        this.lowerBound = xmin;
        this.upperBound = xmax;
    }
    
    void setXRange(double xmin, double xmax)
    {
        if ( ! Double.isNaN(lowerBound) )
            xmin = xmin < lowerBound ? lowerBound : xmin;
        if ( ! Double.isNaN(upperBound) )
            xmax = xmax > upperBound ? upperBound : xmax;
        if ( xmin > xmax )
            xmin = xmax;
        if (overlay instanceof JASHistFunctionOverlay) ((JASHistFunctionOverlay) overlay).setXRange(xmin,xmax);
    }
    public String getTitle()
    {
        return dataSource.getTitle();
    }
    Overlay createOverlay()
    {
        return new JASHistFunctionOverlay(this,style);
    }
    public JASHistStyle getStyle()
    {
        return style;
    }
    public DataSource getDataSource()
    {
        return dataSource;
    }
    void normalizationChanged(boolean now)
    {
    }
    public void writeAsXML(XMLPrintWriter pw, boolean snapshot)
    {
        pw.setAttribute("axis","y"+getYAxis());
        //TODO: getTitle is not correct, fix this
        pw.setAttribute("type",dataSource.getTitle());
        pw.openTag("function1d");
        
        String[] pNames = dataSource.getParameterNames();
        double[] pValue = dataSource.getParameterValues();
        for (int i=0; i<pNames.length; i++)
        {
            pw.setAttribute("name",pNames[i]);
            pw.setAttribute("value",pValue[i]);
            pw.printTag("functionParam");
        }
        pw.setAttribute("lineColor",jas.util.ColorConverter.colorToString(style.getLineColor()));
        pw.printTag("functionStyle1d");
        pw.closeTag();
    }
    Basic1DFunction getFunction()
    {
        return dataSource;
    }
    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
        // things in JASHistData we need to save:
        out.writeObject(parent);
        out.writeInt(yAxisIndex);
        out.writeBoolean(isVisible);
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        isVisible = false; // we aren't showing right now
        parent = (DataManager) in.readObject();
        yAxisIndex = in.readInt();
        style.addObserver(this);
        if (in.readBoolean())
        {
            if (dataSource instanceof Observable)
                ((Observable) dataSource).addObserver(this);
            show(true);
        }
    }
    public void delete()
    {
        ((SupportsFunctions) parent).removeFunction(this);
    }
    void destroy()
    {
        dataSource.destroy();
        dataSource.deleteObserver(this);
        style.deleteObserver(this);
        super.deleteNormalizationObserver();
    }
    private Basic1DFunction dataSource;
    private double xLow;
    private double xHigh;
    private double xIncrement;
    transient private Double hole;
    private JASHist1DFunctionStyle style;
    static final long serialVersionUID = -3529869583896718619L;
}
class JASHistFunctionOverlay extends OverlayWithHandles implements MutableLegendEntry
{
    private static final float[][] lineStyles = {null, { 1, 5 },{ 4, 6 },{ 6, 4, 2, 4 }};
    
    JASHistFunctionOverlay(JASHist1DFunctionData source, JASHist1DFunctionStyle style)
    {
        super(source.getDataSource());
        this.source = source;
        this.style = style;
    }
    void setXRange(double xmin, double xmax)
    {
        this.xmin = xmin;
        this.xmax = xmax;
    }
    public void paint(PlotGraphics g, boolean isPrinting)
    {
        final CoordinateTransformation xp = container.getXTransformation();
        final CoordinateTransformation yp = container.getYTransformation(source.getYAxis());
        Basic1DFunction f = (Basic1DFunction) source.getDataSource();
        
        Transformation xT = null, yT = null;
        
        if ( xp instanceof DoubleCoordinateTransformation )
            xT = (Transformation) xp;
        if ( xp instanceof DateCoordinateTransformation )
            xT = new DateTransformationConverter((DateCoordinateTransformation) xp);
        if ( yp instanceof DoubleCoordinateTransformation )
            yT = (Transformation) yp;
        if ( yp instanceof DateCoordinateTransformation )
            yT = new DateTransformationConverter((DateCoordinateTransformation) yp);

        if ( xT != null && yT != null ) {
            
            g.setTransformation(xT,yT);
            
            double xi = (xmax - xmin) / (xT.convert(xmax) - xT.convert(xmin));
            
            double yold = Double.NEGATIVE_INFINITY;
            
            g.setColor(style.getLineColor());
            BasicStroke s = new BasicStroke(style.getLineWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getLineStyle()],0);
            g.setStroke(s);
            
            
            int lpbn = 0;
            for (double x=xmin; x<xmax; x += xi)
                lpbn++;
            double[] lpbx = new double[lpbn];
            double[] lpby = new double[lpbn];
                     
            int count = 0;
            for (double x=xmin; x<xmax; x += xi)
            {
                try
                {
                    lpbx[count] = x;
                    double y = f.valueAt(x);
                    lpby[count] = y;
                    yold = y;
                }
                catch (FunctionValueUndefined xx)
                {
                    lpby[count] = yold;
                }
                count++;
            }
            g.drawPolyLine(lpbx, lpby, lpbn);            
            g.setStroke(null);
            super.paint(g);
        }
    }

    public boolean titleIsChanged() {
        return source.isLegendChanged();
    }

    public void setTitle(String newTitle) {
        source.setLegendText(newTitle);
    }
   
    public String getTitle() {
        return source.getLegendText();
    }
   
    public void paintIcon(PlotGraphics g, int width, int height) {
        g.setColor(style.getLineColor());
        float flw = (float) style.getLineWidth()*3.0f;
        if (flw > (width/2)) flw = (width > 2) ? (float) (width/2 -1) : 1;
        BasicStroke s = new BasicStroke(flw,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getLineStyle()],0);
        g.setStroke(s);
        g.setStroke(s);
        g.drawLine(1, height/2, width-2, height/2);
        g.setStroke(null);
    }
    
    private JASHist1DFunctionStyle style;
    private JASHist1DFunctionData source;
    private double xmin, xmax;
}
