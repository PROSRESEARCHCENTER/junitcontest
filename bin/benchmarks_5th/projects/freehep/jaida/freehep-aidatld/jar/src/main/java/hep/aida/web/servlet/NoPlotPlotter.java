package hep.aida.web.servlet;

import hep.aida.ref.plotter.Plotter;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class NoPlotPlotter extends Plotter {
    
    private JPanel panel;
    
    protected NoPlotPlotter() {        
        super("NoPlotPlotter");
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.white);
        
        Font font = new Font("Times New Roman", 1, 14);
        
        GridBagConstraints gridBagConstraints;
        
        JLabel jLabel1 = new JLabel();
        jLabel1.setFont(font);
        jLabel1.setText("No Plot Available");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        panel.add(jLabel1, gridBagConstraints);
        
        JLabel jLabel2 = new JLabel();
        jLabel2.setFont(font);
        jLabel2.setText("Please Reload the page");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        panel.add(jLabel2, gridBagConstraints);
        
    }

    public JPanel panel() {
        return panel;
    }    
    
}
