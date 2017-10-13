package org.freehep.swing.images.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.freehep.swing.images.FreeHepImage;

public class ImageTest extends JFrame {

    String cursors[] = { "ParallelogramCursor", 
                         "PointCursor", 
                         "RectangularCursor", 
                         "RotatedRectangleCursor", 
                         "SquareCursor", 
                         "XSkewCursor", 
                         "YSkewCursor",
                         "RotationCursor",
                         "NotFoundCursor"
    };

    String selections[] = { "Parallelogram", 
                            "Point", 
                            "Rectangular", 
                            "RotatedRectangle", 
                            "Square", 
                            "XSkew", 
                            "YSkew",
                            "Rotation",
                            "NotFound"
    };

    public ImageTest() {
        super("Image, Cursor and Icon Test");
        Container content = getContentPane();
        content.setLayout(new BorderLayout());

        // create button panel
        JPanel buttonPanel = new JPanel();
        content.add(buttonPanel, "Center");
        buttonPanel.setLayout(new GridLayout(4,2));
        for (int i=0; i<cursors.length; i++) {
            JButton button = new JButton(cursors[i]);
            final int buttonNo = i;
            buttonPanel.add(button);
            button.setCursor(FreeHepImage.getCursor(cursors[i]));
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    ImageTest.this.setIconImage(FreeHepImage.getImage(selections[buttonNo]));
                }
            });
        }
        
        // create toolbar and menubar
        JToolBar toolBar = new JToolBar();
        JMenuBar menuBar = new JMenuBar();
        getRootPane().setJMenuBar(menuBar);
        content.add(toolBar, "North");
        JMenu selectionMenu = new JMenu("Selection");
        menuBar.add(selectionMenu);
        ButtonGroup selectionGroup = new ButtonGroup();
        for (int i=0; i<selections.length; i++) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(selections[i], FreeHepImage.getIcon(selections[i]));
            selectionMenu.add(item);
            selectionGroup.add(item);
            
            toolBar.add(new JButton(FreeHepImage.getIcon(selections[i])));
        }
        
//        addMouseMotionListener(this);
    }    

    public static void main(String[] args) {
        JFrame frame = new ImageTest();
        frame.pack();
        frame.setSize(640, 480);
        frame.setVisible(true);
    }
    
    
}
