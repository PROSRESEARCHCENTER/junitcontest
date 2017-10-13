package jas.util;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


/**Creates a jpanel that allows users to set the parameters used by ScientificFormat
 * objects for number formatting. The panel allows the setting of significant digits,
 * max width of number and formatting styles. 
 * * @author Paul Spence
 * @version 03/20/2000  
 */

public class SciFormatPanel extends JPanel implements ActionListener
{
	public SciFormatPanel(ScientificFormat s)
	{
		f=s;
		createPanel();
	}
	
	private ScientificFormat f;
	private JLabel WidthLabel;
	private JLabel SigLabel;
	private JPanel DisplayStyle;
	private JRadioButton PureSci;
	private JRadioButton StandardSci;
	private SpinBox widthdigits;
	private SpinBox sigdigits;
		 
	private void createPanel()
	{	
		JPanel DisplayOptions = new JPanel(new FlowLayout(FlowLayout.CENTER));
		SigLabel = new JLabel("Significant Digits");
		WidthLabel = new JLabel("Maximum Digits");	
		sigdigits = new SpinBox(f.getSigDigits(),1,15);
		widthdigits = new SpinBox(f.getMaxWidth(),1,100);
		sigdigits.setValue(f.getSigDigits());
		widthdigits.setValue(f.getMaxWidth());
		sigdigits.addActionListener(new MySpinBoxListener());
		widthdigits.addActionListener(new MySpinBoxListener());
		if(f.getScientificNotationStyle()){
			widthdigits.setEnabled(false);
			WidthLabel.setEnabled(false);
		}
		DisplayOptions.add(SigLabel);
		DisplayOptions.add(sigdigits);
		DisplayOptions.add(WidthLabel);
		DisplayOptions.add(widthdigits);
		
		JPanel Style = new JPanel(new FlowLayout(FlowLayout.CENTER));
		PureSci = new JRadioButton("Use Pure Scientific Notation", f.getScientificNotationStyle());
		PureSci.setActionCommand("PureSci");
		PureSci.addActionListener(this);
		StandardSci = new JRadioButton("Use Standard Scientific Notation", !(f.getScientificNotationStyle()));
		StandardSci.setActionCommand("StandSci");
		StandardSci.addActionListener(this);
		Style.add(PureSci);
		Style.add(StandardSci);
		
		
		final ButtonGroup group = new ButtonGroup();
		group.add(PureSci);
		group.add(StandardSci);
				
		DisplayStyle = new JPanel();
		DisplayStyle.setLayout(new GridLayout(2,1));
		DisplayStyle.add(DisplayOptions);
		DisplayStyle.add(Style);

	}

	/**Sets the ScientificFormat objects parameters according to the SciFormatPanels values.
	 * To be called when 'ok' or 'apply' buttons int the panels' parent container is 
	 * selected. 
	*/
	public void updateSciFormat(){
			f.setMaxWidth(widthdigits.getValue());
			f.setSigDigits(sigdigits.getValue());
			f.setScientificNotationStyle(PureSci.isSelected());
	}
	
	/**Get the DisplayStyle panel */
	public JPanel getPanel(){
		return DisplayStyle;
	}
			
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "StandSci") {
			WidthLabel.setEnabled(true);
			widthdigits.setEnabled(true);
					
		}else if (e.getActionCommand() == "PureSci") {
			WidthLabel.setEnabled(false);
			widthdigits.setEnabled(false);
			
		}
		
    }
	
	class MySpinBoxListener implements ActionListener{
		public void actionPerformed(ActionEvent evt){
			if( (widthdigits.getValue() < sigdigits.getValue()) ){
				widthdigits.setValue(sigdigits.getValue());
			}
		}
	}
					
		
}	


