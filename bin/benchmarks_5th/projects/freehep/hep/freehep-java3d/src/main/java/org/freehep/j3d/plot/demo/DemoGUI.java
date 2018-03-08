/*
 * DemoGUI.java
 *
 * Created on June 15, 2001, 1:30 PM
 */

package org.freehep.j3d.plot.demo;
import org.freehep.j3d.plot.*;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author  joyk
 * @version $Id: DemoGUI.java 8584 2006-08-10 23:06:37Z duns $
 */
public class DemoGUI extends JPanel
{
    private static JPanel panel;
    private static LegoPlot lego;
    private static SurfacePlot surf = null;

    /** Creates new DemoGUI */
    public DemoGUI() throws java.io.IOException
    {
        super(new BorderLayout());

        lego = new LegoPlot();
        lego.setDrawBlocks(true);
        lego.setLinesWhileAnim(true);
        lego.setSparsifyThreshold(600);
        lego.setData(new TestBinned2DData());
        LegoControlPanel controls = new LegoControlPanel(lego);

        panel = new JPanel(new BorderLayout());
        panel.add(lego,BorderLayout.CENTER);
        panel.add(controls,BorderLayout.SOUTH);
        add(panel,BorderLayout.CENTER);
        JTextPane label = new JTextPane();
        label.setContentType("text/html");
        label.setText("<html><font color=\"black\">This three-dimensional graph plots 2-dimensional binned data.  For each bin (x,y) there is a corresponding z value, plotted as the height of the bin.  There are three representations, the lego plot, which shows each bin as a box, the line plot, which shows each bin as a vertical line, and the surface plot, which makes a surface of the heights of the bins.<p>The plots can be rotated using the first mouse button.  They can be panned (moved around) using the right mouse button.  To zoom in or out, use the middle mouse button.  Alternatively, you can use the arrow keys and the page up/down keys for panning in specific directions. Use the same keys while holding the shift key down for rotations.  Use the Home key to restore the original orientation. <p>Some systems have trouble keeping up with the movements of the lego plot, so animation options are available to show a simpler version of the plot while the mouse button is depressed.  To enter a new sparsification threshold, type the number followed by the Enter key.<p>Other options inlude a log scale for the z axis and a parallel projection instead of the default perspective projection.<p>The labels for the axes can also be changed.  Type the new axis text followed by the Enter key.");
        JScrollPane scroll = new JScrollPane(label);
        scroll.setPreferredSize(new Dimension(250,600));
        add(scroll,BorderLayout.EAST);

    }
    public static SurfacePlot getSurfacePlot() throws java.io.IOException
    {
        if (surf == null) {
            surf = new SurfacePlot();
            surf.setData(new TestBinned2DData());
        }
        panel.remove(lego);
        panel.add(surf,BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();

        return surf;
    }
    public static LegoPlot getLegoPlot() {
        panel.remove(surf);
        panel.add(lego,BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
        return lego;
    }
    public static void main(String[] args) throws java.io.IOException
    {
        JFrame f = new JFrame("3D Lego Demo");
        f.setContentPane(new DemoGUI());
        //f.pack();
        f.setSize(new Dimension(600,600));
        f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
