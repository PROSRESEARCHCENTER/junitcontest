package org.freehep.swing.graphics.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.freehep.swing.graphics.GraphicalSelectionEvent;
import org.freehep.swing.graphics.GraphicalSelectionListener;
import org.freehep.swing.graphics.ParallelogramSelectionPanel;
import org.freehep.swing.graphics.PointSelectionEvent;
import org.freehep.swing.graphics.PointSelectionPanel;
import org.freehep.swing.graphics.RectangularSelectionPanel;
import org.freehep.swing.graphics.RegionSelectionEvent;
import org.freehep.swing.graphics.RotatedRectangleSelectionPanel;
import org.freehep.swing.graphics.SquareSelectionPanel;
import org.freehep.swing.graphics.XSkewSelectionPanel;
import org.freehep.swing.graphics.XSliceSelectionPanel;
import org.freehep.swing.graphics.YSkewSelectionPanel;
import org.freehep.swing.graphics.YSliceSelectionPanel;
import org.freehep.swing.layout.StackedLayout;

public class TestGraphicalSelections 
    extends JFrame {

    JLayeredPane mainPanel;
    TestPanel drawPanel;

    PointSelectionPanel pointSelect;
    RectangularSelectionPanel rectSelect;
    SquareSelectionPanel squareSelect;
    RotatedRectangleSelectionPanel rotateSelect;
    XSkewSelectionPanel xskewSelect;
    YSkewSelectionPanel yskewSelect;
    XSliceSelectionPanel xsliceSelect;
    YSliceSelectionPanel ysliceSelect;
    ParallelogramSelectionPanel parallelogramSelect;

    public TestGraphicalSelections() {

        // Title this frame.
        super("Graphical Selections Test");

        // Make this exit when the close button is clicked.
        setDefaultCloseOperation(WindowConstants.
                                 DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {quit();}
        });

        // Make the main panel container.
        mainPanel = new JLayeredPane();
        mainPanel.setLayout(new StackedLayout());

        // Make the drawing panel.
        drawPanel = new TestPanel();
        mainPanel.add(drawPanel, new Integer(0));
        drawPanel.setPreferredSize(new Dimension(600,400));

        // Make the selection panels.
        pointSelect = new PointSelectionPanel();
        mainPanel.add(pointSelect, new Integer(10));
        pointSelect.addGraphicalSelectionListener(drawPanel);

        rectSelect = new RectangularSelectionPanel();
        mainPanel.add(rectSelect, new Integer(20));
        rectSelect.addGraphicalSelectionListener(drawPanel);
        
        squareSelect = new SquareSelectionPanel();
        mainPanel.add(squareSelect, new Integer(30));
        squareSelect.addGraphicalSelectionListener(drawPanel);

        rotateSelect = new RotatedRectangleSelectionPanel();
        mainPanel.add(rotateSelect, new Integer(40));
        rotateSelect.addGraphicalSelectionListener(drawPanel);

        xskewSelect = new XSkewSelectionPanel();
        mainPanel.add(xskewSelect, new Integer(50));
        xskewSelect.addGraphicalSelectionListener(drawPanel);

        yskewSelect = new YSkewSelectionPanel();
        mainPanel.add(yskewSelect, new Integer(60));
        yskewSelect.addGraphicalSelectionListener(drawPanel);

        xsliceSelect = new XSliceSelectionPanel();
        mainPanel.add(xsliceSelect, new Integer(70));
        xsliceSelect.addGraphicalSelectionListener(drawPanel);

        ysliceSelect = new YSliceSelectionPanel();
        mainPanel.add(ysliceSelect, new Integer(80));
        ysliceSelect.addGraphicalSelectionListener(drawPanel);

        parallelogramSelect = new ParallelogramSelectionPanel();
        mainPanel.add(parallelogramSelect, new Integer(70));
        parallelogramSelect.addGraphicalSelectionListener(drawPanel);

        // Make a menu bar and menu.
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        // Quit menu item.
        JMenuItem item = new JMenuItem("Quit");
        item.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            quit();
        }});
        menu.add(item);

        // Add the various types of selections.
        menu = new JMenu("Selections");
        menuBar.add(menu);

        item = new JMenuItem("Point Select");
        menu.add(item);
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    activePanel(pointSelect);
                }});

        item = new JMenuItem("Rectangular Selection");
        menu.add(item);
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    activePanel(rectSelect);
                }});

        item = new JMenuItem("Square Selection");
        menu.add(item);
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    activePanel(squareSelect);
                }});

        item = new JMenuItem("Rot. Rectangle Selection");
        menu.add(item);
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    activePanel(rotateSelect);
                }});

        item = new JMenuItem("X-Skew Selection");
        menu.add(item);
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    activePanel(xskewSelect);
                }});

        item = new JMenuItem("Y-Skew Selection");
        menu.add(item);
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    activePanel(yskewSelect);
                }});

        item = new JMenuItem("X-Slice Selection");
        menu.add(item);
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    activePanel(xsliceSelect);
                }});

        item = new JMenuItem("Y-Slice Selection");
        menu.add(item);
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    activePanel(ysliceSelect);
                }});

        item = new JMenuItem("Parallelogram Selection");
        menu.add(item);
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    activePanel(parallelogramSelect);
                }});

        // Choose what to check.
        menu = new JMenu("Options");
        menuBar.add(menu);

        // Reduce the size.
        item = new JMenuItem("Check Selection");
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    drawPanel.checkSelection();
                }});
        menu.add(item);

        item = new JMenuItem("Check Transform Of Graphics");
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    drawPanel.checkTransform();
                }});
        menu.add(item);

        item = new JMenuItem("Check Transform Of Shapes");
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    drawPanel.checkTransformShapes();
                }});
        menu.add(item);

        menu.addSeparator();

        item = new JMenuItem("Restore Default Zoom");
        menu.add(item);
        item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    drawPanel.defaultTransform();
                }});

        // Add this to the frame.
        setJMenuBar(menuBar);

        // Get the content pane.
        Container content = this.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(mainPanel,BorderLayout.CENTER);
        //setContentPane(mainPanel);
        
        activePanel(squareSelect);
    }

    /**
     * Just exit the JVM. */
    public void quit() {
        System.exit(0);
    }

    public void activePanel(JComponent active) {
        pointSelect.setVisible(false);
        rectSelect.setVisible(false);
        squareSelect.setVisible(false);
        rotateSelect.setVisible(false);
        xskewSelect.setVisible(false);
        yskewSelect.setVisible(false);
        xsliceSelect.setVisible(false);
        ysliceSelect.setVisible(false);
        parallelogramSelect.setVisible(false);
        active.setVisible(true);
    }


    class TestPanel
        extends JPanel 
        implements GraphicalSelectionListener {
        
        /**
         * Flag to indicate whether the transform or the selection
         * component of the GraphicalSelectionEvent should be
         * tested. */
        private boolean checkTransform;
        
        /**
         * Flag indicating whether to transform the shapes or to set
         * the transformation of the Graphics context. */
        private boolean transformShapes;

        /**
         * The transform which should be used for the zooming. */
        private AffineTransform transform = new AffineTransform();

        /**
         * The outline of the selection.  For a point, create a small
         * diamond surrounding the selected point. */
        private Polygon polygon = null;

        /**
         * List of colors for the six test figures which are drawn. */
        private Color[] colors = new Color[6];

        /**
         * The six test figures. */
        Polygon[] bkg = new Polygon[6];

        /**
         * Temporary array used to create the test
         * polygons. (x-coord.) */
        int[] xval = new int[4];

        /**
         * Temporary array used to create the test
         * polygons. (y-coord.) */
        int[] yval = new int[4];

        /**
         * GeneralPath used when transforming the shapes directly. */
        GeneralPath gp = new GeneralPath();

        /**
         * Constructor sets up the various geometrical shapes needed
         * for this test. */
        public TestPanel() {

            // Make sure that this component is opaque.
            setOpaque(true);

            // Start by checking the selections only.
            checkTransform = false;

            // Start by transforming the shapes.
            transformShapes = true;

            // Setup the colors for the six geometrical shapes.
            colors[0] = Color.red;
            colors[1] = Color.green;
            colors[2] = Color.blue;
            colors[3] = Color.cyan;
            colors[4] = Color.magenta;
            colors[5] = Color.black;

            // A square.
            xval[0] = 50;
            yval[0] = 50;
            xval[1] = 150;
            yval[1] = 50;
            xval[2] = 150;
            yval[2] = 150;
            xval[3] = 50;
            yval[3] = 150;
            bkg[0] = new Polygon(xval,yval,4);

            // A rectangle.
            xval[0] = 200+50;
            yval[0] = 20;
            xval[1] = 200+150;
            yval[1] = 20;
            xval[2] = 200+150;
            yval[2] = 180;
            xval[3] = 200+50;
            yval[3] = 180;
            bkg[1] = new Polygon(xval,yval,4);

            // A diamond.
            xval[0] = 400+100;
            yval[0] = 50;
            xval[1] = 400+180;
            yval[1] = 100;
            xval[2] = 400+100;
            yval[2] = 150;
            xval[3] = 400+20;
            yval[3] = 100;
            bkg[2] = new Polygon(xval,yval,4);

            // A y-skew parallelogram.
            xval[0] = 20;
            yval[0] = 200+20;
            xval[1] = 70;
            yval[1] = 200+20;
            xval[2] = 180;
            yval[2] = 200+180;
            xval[3] = 130;
            yval[3] = 200+180;
            bkg[3] = new Polygon(xval,yval,4);

            // An x-skew parallelogram.
            xval[0] = 200+20;
            yval[0] = 200+20;
            xval[1] = 200+20;
            yval[1] = 200+70;
            xval[2] = 200+180;
            yval[2] = 200+180;
            xval[3] = 200+180;
            yval[3] = 200+130;
            bkg[4] = new Polygon(xval,yval,4);

            // A rotated rectangle.
            xval[0] = 400+160;
            yval[0] = 200+20;
            xval[1] = 400+180;
            yval[1] = 200+40;
            xval[2] = 400+40;
            yval[2] = 200+180;
            xval[3] = 400+20;
            yval[3] = 200+160;
            bkg[5] = new Polygon(xval,yval,4);
        }

        /**
         * Paint the component. */
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();

            // Always draw the background on an untransformed graphics
            // context. 
            g2d.setColor(Color.orange);
            g2d.fillRect(0,0,getWidth(),getHeight());

            // Now draw the shapes on top.
            if (transformShapes) {
                drawTransformedShapes(g2d);
            } else {
                drawTransformedGraphics(g2d);
            }
        }

        public void drawTransformedShapes(Graphics2D g2d) {

            if (!(checkTransform && transform!=null)) {
                transform = new AffineTransform();
            }

            // Draw all of the standard shapes.
            for (int i=0; i<6; i++) {
                gp.reset();
                gp.append(bkg[i].getPathIterator(transform),false);
                g2d.setColor(colors[i]);
                g2d.fill(gp);
            }

            if (polygon!=null && !checkTransform) {
                gp.reset();
                gp.append(polygon.getPathIterator(transform),false);
                g2d.setColor(Color.black);
                g2d.draw(gp);
            }

        }

        public void drawTransformedGraphics(Graphics2D g2d) {

            if (checkTransform && transform!=null) {
                g2d.transform(transform);
            } else {
                transform = new AffineTransform();
            }

            // Draw all of the standard shapes.
            for (int i=0; i<6; i++) {
                g2d.setColor(colors[i]);
                g2d.fillPolygon(bkg[i]);
            }

            if (polygon!=null && !checkTransform) {
                g2d.setColor(Color.black);
                g2d.drawPolygon(polygon);
            }

        }

        public void checkTransformShapes() {
            transformShapes = true;
            checkTransform = true;
            transform = new AffineTransform();
            polygon = null;
            repaint();
        }

        public void checkTransform() {
            transformShapes = false;
            checkTransform = true;
            transform = new AffineTransform();
            polygon = null;
            repaint();
        }

        public void checkSelection() {
            transformShapes = true;
            checkTransform = false;
            polygon = null;
            transform = new AffineTransform();
            repaint();
        }

        public void defaultTransform() {
            transform = new AffineTransform();
            repaint();
        }

        public void graphicalSelectionMade(GraphicalSelectionEvent gsEvent) {

            AffineTransform tx = gsEvent.getTransform();
            if (transform!=null && tx!=null) {
                transform.preConcatenate(tx);
            } else {
                transform = tx;
            }
            if (gsEvent instanceof RegionSelectionEvent) {
                polygon = (Polygon) gsEvent.getSelection();
            } else if (gsEvent instanceof PointSelectionEvent) {
                Point p = (Point) gsEvent.getSelection();
                xval[0] = p.x;
                yval[0] = p.y+10;
                xval[1] = p.x+10;
                yval[1] = p.y;
                xval[2] = p.x;
                yval[2] = p.y-10;
                xval[3] = p.x-10;
                yval[3] = p.y;
                polygon = new Polygon(xval,yval,4);
            }
            repaint();
        }

    }

    public static void main(String[] args) {

        // Create a new instance of this class.
        TestGraphicalSelections test = new TestGraphicalSelections();

        // Layout the component and make it visible.
        test.pack();
        test.setVisible(true);
        test.setLocation(20,20);
        test.pack();
    }
}
