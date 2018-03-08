package org.freehep.webutil.tabs.servlet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.freehep.swing.ColorConverter;

/**
 * @author The FreeHEP team @ SLAC.
 *
 */
public class TabCorner extends JPanel {
    
    public final static int UPPER_LEFT  = 0;
    public final static int UPPER_RIGHT = 1;
    public final static int LOWER_LEFT  = 2;
    public final static int LOWER_RIGHT = 3;
    
    private int type;
    private int size;
    private Color color;
    private Color bkgColor;
    
    TabCorner(int type, int size, String color, String bkgColor) {
        super();
        this.type = type;
        this.size = size;
        try {
            this.color = ColorConverter.get(color);
        } catch (Exception e) {
            this.color = Color.green;
        }
        try {
            this.bkgColor = ColorConverter.get(bkgColor);
        } catch (Exception e) {
            this.bkgColor = Color.white;
        }
    }
    
    
    public void paint(Graphics g) {
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke((float)1.0));
        g2.setColor(bkgColor);
        g2.drawRect(0, 0, size, size);
        g2.fillRect(0, 0, size, size);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
        // draw arcs
        Arc2D arc = new Arc2D.Double(Arc2D.PIE);
        
        switch (type) {
            case LOWER_LEFT:
                arc.setFrame(0, -1*size, 2*size, 2*size);
                arc.setAngleStart(180);
                arc.setAngleExtent(90);
                break;
            case LOWER_RIGHT:
                arc.setFrame(-1*size,-1*size, 2*size, 2*size);
                arc.setAngleStart(270);
                arc.setAngleExtent(90);
                break;
            case UPPER_LEFT:
                arc.setFrame(0, 0, 2*size, 2*size);
                arc.setAngleStart(90);
                arc.setAngleExtent(90);
                break;
            case UPPER_RIGHT:
                arc.setFrame(-1*size, 0, 2*size, 2*size);
                arc.setAngleStart(0);
                arc.setAngleExtent(90);
                break;
            default:
                throw new IllegalArgumentException("Illegal TabCorner type: "+type);
        }
                        
        g2.setColor(color);
        g2.fill(arc);
        g2.draw(arc);        
    }    
}