package jas.plot;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class ColorMapAxis extends Axis {
    
    private ColorMap colorMap;

    public ColorMapAxis(ColorMap colorMap) {
        super(Axis.VERTICAL,false);
        this.colorMap = colorMap;
    }
    
    public void setZminZmax(double zmin, double zmax) {
        ((DoubleAxis)getType()).setMin(zmin);
        ((DoubleAxis)getType()).setMax(zmax);        
    }
    
    public void setLogarithmic(boolean isLog) {
        ((DoubleAxis)getType()).setLogarithmic(isLog);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        Dimension size = getSize();
        Insets insets = getInsets();
        double ww = 30 - insets.left - insets.right - 3 ;
        double hh = size.getHeight() - insets.top - insets.bottom - 7;
        
        double x1 = insets.left+2;
        double x2 = x1 + ww;
        double yy = insets.top+3;
        Line2D line = new Line2D.Double();
        for (int i=0; i<hh; i++) {
            line.setLine(x1, yy, x2, yy);
            g2.setPaint(colorMap.getColor(1-((double) i)/hh));
            g2.draw(line);
            yy++;
        }
    }
    
}
