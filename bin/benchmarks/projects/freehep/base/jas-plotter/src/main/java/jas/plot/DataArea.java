package jas.plot;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JComponent;

public class DataArea extends MovableObject
{
    public DataArea(final Axis x, final Axis y)
    {
        this();
        //xAxis = x;
        //yAxis[0] = y;
        add(x,DataAreaLayout.X_AXIS);
        add(y,DataAreaLayout.Y_AXIS_LEFT);
    }
    public DataArea()
    {
        super("Data Area");
        normal = new NormalDataArea();
        setLayout(cardLayout = new CardLayout());
        super.add(normal,"Normal");
        
        // Use "jas.plot.usePG2" property to switch between PG2 settings
        // Default is PG2_ALWAYS
        String usePG2Property = System.getProperty("jas.plot.usePG2", "true");
        if (usePG2Property.equalsIgnoreCase("false") || usePG2Property.equalsIgnoreCase("never") ) usePG2(PG2_NEVER);
        else if (usePG2Property.equalsIgnoreCase("printing") ) usePG2(PG2_PRINTING);
        else usePG2(PG2_ALWAYS);
    }
    public void add(final Axis a, final Object constraint)
    {
        if (a == null) return;
        
        // safe to use == instead of .equals() ?
        if      (constraint == DataAreaLayout.X_AXIS)       { if (xAxis    != null) remove(xAxis);    xAxis    = a; }
        else if (constraint == DataAreaLayout.Y_AXIS_LEFT)  { if (yAxis[0] != null) remove(yAxis[0]); yAxis[0] = a; }
        else if (constraint == DataAreaLayout.Y_AXIS_RIGHT) { if (yAxis[1] != null) remove(yAxis[1]); yAxis[1] = a; }
        
        normal.add(a, constraint);
    }
    public void add(final EditableLabel a, final Object constraint)
    {
        if (a == null) return;
        
        // safe to use == instead of .equals() ?
        if      (constraint == DataAreaLayout.X_AXIS_LABEL)       { if (xAxisLabel    != null) remove(xAxisLabel);    xAxisLabel    = a; }
        else if (constraint == DataAreaLayout.Y_AXIS_LEFT_LABEL)  { if (yAxisLabel[0] != null) remove(yAxisLabel[0]); yAxisLabel[0] = a; }
        else if (constraint == DataAreaLayout.Y_AXIS_RIGHT_LABEL) { if (yAxisLabel[1] != null) remove(yAxisLabel[1]); yAxisLabel[1] = a; }

        normal.add(a, constraint);
    }
    public void add(final Overlay o)
    {
        overlays.addElement(o);
        o.containerNotify(normal);
    }
    public void setSpecialComponent(Component special)
    {
        if (special == this.special) return;
        if (special == null)
        {
            cardLayout.first(this);
            super.remove(this.special);
            this.special = null;
            
        }
        else
        {
            super.add(special,"Special");
            cardLayout.last(this);
            if (this.special != null) super.remove(this.special);
            this.special = special;
        }
    }
    public void remove(final Component c)
    {
        if      (c == xAxis)         xAxis         = null;
        else if (c == yAxis[0])      yAxis[0]      = null;
        else if (c == yAxis[1])      yAxis[1]      = null;
        else if (c == xAxisLabel)    xAxisLabel    = null;
        else if (c == yAxisLabel[0]) yAxisLabel[0] = null;
        else if (c == yAxisLabel[1]) yAxisLabel[1] = null;
        
        normal.remove(c);
    }
    public Axis getXAxis()
    {
        return xAxis;
    }
    public Axis getYAxis()
    {
        return yAxis[0];
    }
    public Axis getYAxis(final int index)
    {
        return yAxis[index];
    }
    public void remove(final Overlay o)
    {
        overlays.removeElement(o);
        o.containerNotify(null);
    }
    
    public EditableLabel getLabel(final Axis a)
    {
        if (a == xAxis) return xAxisLabel;
        if (a == yAxis[0]) return yAxisLabel[0];
        if (a == yAxis[1]) return yAxisLabel[1];
        return null;
    }
    public void setLabel(final Axis a, final EditableLabel l)
    {
        if      (a == xAxis   ) add(l,DataAreaLayout.X_AXIS_LABEL);
        else if (a == yAxis[0]) add(l,DataAreaLayout.Y_AXIS_LEFT_LABEL);
        else if (a == yAxis[1]) add(l,DataAreaLayout.Y_AXIS_RIGHT_LABEL);
    }
        /**
         * Set the mode for using JDK 1.2 graphics (call has no effect if running under JDK 1.1)
         * @param mode One of PG2_NEVER, PG2_PRINTING, PG2_ALWAYS
         */
    public void usePG2(int mode)
    {
        if (mode != PG2_NEVER && pg2 == null)
        {
            try
            {
                Class c = Class.forName("jas.plot.java2.PlotGraphics12");
                pg2 = (SetablePlotGraphics) c.newInstance();
            }
            catch (Throwable x)
            {
                //x.printStackTrace();
                usepg2 = PG2_NEVER;
                return;
            }
        }
        if (usepg2 != mode)
        {
            usepg2 = mode;
            repaint();
        }
    }
    
    private final Vector overlays = new Vector();
    
        /** Never use JDK 1.2 graphics */
    public final static int PG2_NEVER = 0;
        /** Use JDK 1.2 graphics only when printing */
    public final static int PG2_PRINTING = 1;
        /** Use JDK 1.2 graphics always */
    public final static int PG2_ALWAYS = 2;
    
    private int usepg2 = PG2_NEVER;
    private NormalDataArea normal;
    private Component special;
    private CardLayout cardLayout;
    private SetablePlotGraphics pg2;
    private SetablePlotGraphics pg1 = new jas.plot.java1.PlotGraphics11();
    private Axis xAxis;
    private final Axis[] yAxis = new Axis[2];
    private EditableLabel xAxisLabel;
    private final EditableLabel[] yAxisLabel = new EditableLabel[2];
    
    private class NormalDataArea extends JComponent implements OverlayContainer
    {
        NormalDataArea()
        {
            setLayout(new DataAreaLayout());
        }
        public void print(Graphics g)
        {
           try
           {
              printing = true;
              super.print(g);
              super.invalidate();
           }
           finally
           {
              printing = false;
           }
        }
        public void paintChildren(final Graphics g)
        {
            // Painting the contents of a plot can occasionally throw an exception
            // (due to bugs!) which has the undesirable effect of causing painting
            // of the entire UI to get confused, so here we will catch any exceptions
            // and continue with painting the rest of the UI
            boolean pg2now = (usepg2 == PG2_ALWAYS) || (usepg2 == PG2_PRINTING && isPrinting());    
            SetablePlotGraphics pg = pg2now ? pg2 : pg1;
            try
            {
                //long start = System.currentTimeMillis();
                super.paintChildren(g);
                pg.setGraphics(g);
                xAxis.paint(pg);
                if (yAxis[0] != null) yAxis[0].paint(pg);
                if (yAxis[1] != null) yAxis[1].paint(pg);
                pg.setClip(xAxis.getMinLocation(),xAxis.getMaxLocation(),yAxis[0].getMinLocation(),yAxis[0].getMaxLocation());
                
                for (int i=0,s=overlays.size(); i<s; i++)
                {
                    pg.clearTransformation();
                    Overlay o = (Overlay) overlays.elementAt(i);
                    o.paint(pg, isPrinting());
                }

                //long stop = System.currentTimeMillis();
                //System.out.println("paint ["+nPaint+++"] took "+(stop-start)+"ms clip="+g.getClip());
            }
            catch (Throwable t)
            {
                System.err.println("Exception while painting DataArea");
                t.printStackTrace();
            }
            finally
            {
                pg.setGraphics(null); // protect against memory leak
            }
        }
        public CoordinateTransformation getXTransformation()
        {
            return xAxis.type.getCoordinateTransformation();
        }
        public CoordinateTransformation getYTransformation()
        {
            return yAxis[0].type.getCoordinateTransformation();
        }
        public CoordinateTransformation getYTransformation(final int index)
        {
            return yAxis[index].type.getCoordinateTransformation();
        }
        private boolean isPrinting()
        {
           return printing || PrintHelper.isPrinting();
        }
        private boolean printing;
    }
    //private static int nPaint = 0;
}
