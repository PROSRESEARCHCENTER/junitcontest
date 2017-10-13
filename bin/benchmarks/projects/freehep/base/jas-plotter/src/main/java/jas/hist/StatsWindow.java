package jas.hist;
import jas.util.JASDialog;
import jas.util.JASState;
import jas.util.SciFormatPanel;
import jas.util.ScientificFormat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Format;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

/**This class creates a gui popup window for choosing which statistics and titles should be displayed 
 *in the StatisticsBlock, and for setting the number formatting parameters for 
 * base.jas.util.ScientificFormat. The window uses the jpane created by base.jas.util.SciFormatPanel
 * for setting the ScientificForm parameters. This window created by selecting "Stats Properties.."
 * from the popup menu. If generated, the number formatting method in Statistics Block is 
 * set to ScientificFormat.  
 * @author Paul Spence
 * @version 03/20/2000  
 * "userInterface.propertiesDialog.Statistics"
 * */

public class StatsWindow extends JASDialog implements ActionListener
{
	public  static JFrame frame;	
	private  JButton all;
	private JButton none;
	private JRadioButton alltitles;
	private JRadioButton notitles;
	private JRadioButton sometitles;
	private JRadioButton leftalign;
	private JRadioButton rightalign;
	private JRadioButton noalign;
	private JRadioButton alwaysall;
	private JRadioButton subset;
	private  String[] names;
	private JLabel selectlabel;
	private SciFormatPanel scipanel;
	private ScientificFormat f;
	private StatisticsBlock statblock;
	private JList list;
	private int showtitles;
	private int splitalign;
	
	public StatsWindow(StatisticsBlock b)
	{	
		super(frame, "Statistic Display Options", true, JASDialog.OK_BUTTON | JASDialog.CANCEL_BUTTON | JASDialog.APPLY_BUTTON | JASDialog.HELP_BUTTON);
		statblock = b;
		Format sf = b.getFormat();
		if (sf instanceof ScientificFormat) f = (ScientificFormat) sf;
		else f = new ScientificFormat();
	}
	
	/**
	 * This method will create and show the Statistics Window
	 */
	public void showStatsWindow()
	{
		
		super.setHelpTopic("userInterface.Statistics");
			
		Container c = getContentPane();
		c.removeAll();
				
		scipanel = new SciFormatPanel(f);
		
		//always all or subset buttoons
		ButtonGroup allways_subset_group = new ButtonGroup();
		alwaysall = new JRadioButton("Always show all statistics");
		alwaysall.addActionListener(this);
		alwaysall.setActionCommand("alwaysall");
		subset = new JRadioButton("Show selection of current statistics");
		subset.addActionListener(this);
		subset.setActionCommand("subset");
		allways_subset_group.add(alwaysall);
		allways_subset_group.add(subset);
		Box allways_subset_box = new Box(BoxLayout.X_AXIS);
		allways_subset_box.add(alwaysall);
		allways_subset_box.add(subset);
				
		//checkbox list
		names =  statblock.getStatNames();
		
		list = new JList( createData(names) );
		list.setVisibleRowCount(3);
		list.setCellRenderer(new CheckListRenderer());
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int index = list.locationToIndex(e.getPoint());
				if(index != -1 && list.getModel().getSize() >index){
					CheckableItem item = (CheckableItem)list.getModel().getElementAt(index);
					item.setSelected(! item.isSelected());
					Rectangle rect = list.getCellBounds(index, index);
					list.repaint(rect);
				}
			}
		});   
    					
		JScrollPane checkbuttonspanel = new JScrollPane(list);
		Box subsetbuttons = new Box(BoxLayout.Y_AXIS);
		all = new JButton("All");
		none = new JButton("None");
		all.setActionCommand("all");
		all.addActionListener(this);
		none.setActionCommand("none");
		none.addActionListener(this);
		subsetbuttons.add(all);
		subsetbuttons.add(none);
				
		JPanel statselection = new JPanel();
		statselection.setLayout(new BorderLayout());
		selectlabel = new JLabel("Choose selection of current statistics for display");
		statselection.add(selectlabel,BorderLayout.NORTH);
		statselection.add(subsetbuttons,BorderLayout.EAST);
		statselection.add(checkbuttonspanel, BorderLayout.CENTER);
		
		Box statoptionsbox = new Box(BoxLayout.Y_AXIS);
		statoptionsbox.add(allways_subset_box);
		statoptionsbox.add(statselection);
		
		JPanel statoptions = new JPanel();
		statoptions.setBorder(BorderFactory.createTitledBorder("Choose a statistics display options"));
		statoptions.add(statoptionsbox);
				
		//display title and alignment
		showtitles = statblock.getShowTitles();
		alltitles = new JRadioButton("Always show title");
		notitles = new JRadioButton("Never show title");
		sometitles = new JRadioButton("Show if multiple plots");
		alltitles.setActionCommand("alltitles");
		alltitles.addActionListener(this);
		notitles.setActionCommand("notitles");
		notitles.addActionListener(this);
		sometitles.setActionCommand("sometitles");
		sometitles.addActionListener(this);
		Box titlebox = new Box(BoxLayout.X_AXIS);
		titlebox.add(alltitles);
		titlebox.add(notitles);
		titlebox.add(sometitles);
		ButtonGroup titlegroup = new ButtonGroup();
		titlegroup.add(alltitles);
		titlegroup.add(notitles);
		titlegroup.add(sometitles);
		
		if(showtitles == statblock.SHOWTITLES_ALWAYS) alltitles.setSelected(true);
		else if(showtitles == statblock.SHOWTITLES_NEVER) notitles.setSelected(true);
		else if(showtitles == statblock.SHOWTITLES_AUTOMATIC) sometitles.setSelected(true);
		
		JPanel titleoptions = new JPanel();
		titleoptions.setBorder(BorderFactory.createTitledBorder("Select a title display option for plots with statistics"));
		titleoptions.add(titlebox);		
		
		splitalign = statblock.getSplitStringAlign();
		leftalign = new JRadioButton("Left align numbers");
		rightalign = new JRadioButton("Right align numbers");
		noalign = new JRadioButton("Do not align numbers");
		leftalign.setActionCommand("leftalign");
		leftalign.addActionListener(this);
		rightalign.setActionCommand("rightalign");
		rightalign.addActionListener(this);
		noalign.setActionCommand("noalign");
		noalign.addActionListener(this);
		ButtonGroup aligngroup = new ButtonGroup();
		Box alignbox = new Box(BoxLayout.X_AXIS);
		aligngroup.add(leftalign);
		aligngroup.add(rightalign);
		aligngroup.add(noalign);
		alignbox.add(leftalign);
		alignbox.add(rightalign);
		alignbox.add(noalign);
		JPanel alignoptions = new JPanel();
		alignoptions.setBorder(BorderFactory.createTitledBorder("Select an alignment display option for the statistics values"));
		alignoptions.add(alignbox);
		if(splitalign == statblock.LEFTALIGNSPLIT) leftalign.setSelected(true);
		else if(splitalign == statblock.RIGHTALIGNSPLIT) rightalign.setSelected(true);
		else if(splitalign == statblock.NOALIGNSPLIT) noalign.setSelected(true);
		
		JPanel scioptions = scipanel.getPanel();
		scioptions.setBorder(BorderFactory.createTitledBorder("Set number formating parameters for numerical statistics"));
		
		if(statblock.get_AllwaysAll_Subset()){
			alwaysall.setSelected(true);
			this.enableStatSelections(false);
		}else subset.setSelected(true);
		
		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(statoptions);
		box.add(titleoptions);
		box.add(alignoptions);
		box.add(scioptions);
		c.add(box);
				
		pack();
		show();
	}
	
			
	public void onOK(){
		String[] s;	
		scipanel.updateSciFormat();//sets max width, sig digits and style for doubles
		ListModel model = list.getModel();
		if(alwaysall.isSelected()){
			statblock.setSelectedEntries(s = null);
			statblock.set_AllwaysAll_Subset(true);
		}else if(model.getSize()>0){
			int n = model.getSize();
			int j = 0;
			for(int i=0;i<n;i++){
				CheckableItem item = (CheckableItem)model.getElementAt(i);
				if(item.isSelected()){
					j++;
				}
			}
			
			if( j>0 ){
				s = new String[j];
				int k = 0;			
				for(int i=0;i<n;i++){
					CheckableItem item = (CheckableItem)model.getElementAt(i);
					if(item.isSelected()){
						s[k] = item.toString();
						k++;
					}
				}
			}else{
				s = new String[1];
				s[0] = "\none";
				
			}
			
			
			statblock.setSelectedEntries(s);
			statblock.set_AllwaysAll_Subset(false);
		}else{
			statblock.setSelectedEntries(s=null);
			statblock.set_AllwaysAll_Subset(false);
		}
		
		
		statblock.setShowTitles(showtitles);
		statblock.setSplitStringAlign(splitalign);
		statblock.setFormat(f);	
		dispose();
	}
	
	protected void enableApply(JASState state)
	{
		state.setEnabled(true);
	}
	
	protected void enableHelp(JASState state)
	{
		state.setEnabled(true);
	}

	public void onCancel(){
		dispose();
	}
	
	public void onApply(){
		
		scipanel.updateSciFormat();//sets max width, sig digits and style for doubles
		String[] s;
		ListModel model = list.getModel();
		if(alwaysall.isSelected()){
			statblock.setSelectedEntries(s = null);
			statblock.set_AllwaysAll_Subset(true);
		}else if(model.getSize()>0){
			int n = model.getSize();
			int j = 0;
			for(int i=0;i<n;i++){
				CheckableItem item = (CheckableItem)model.getElementAt(i);
				if(item.isSelected()){
					j++;
				}
			}
			
			if( j>0 ){
				s = new String[j];
				int k = 0;			
				for(int i=0;i<n;i++){
					CheckableItem item = (CheckableItem)model.getElementAt(i);
					if(item.isSelected()){
						s[k] = item.toString();
						k++;
					}
				}
			}else{
				s = new String[1];
				s[0] = "\none";
				
			}
			
			
			statblock.setSelectedEntries(s);
			statblock.set_AllwaysAll_Subset(false);
		}else{
			statblock.setSelectedEntries(s=null);
			statblock.set_AllwaysAll_Subset(false);
		}
		
		statblock.setShowTitles(showtitles);
		statblock.setSplitStringAlign(splitalign);
		statblock.setFormat(f);	
	}
		
	public void actionPerformed(ActionEvent e) {
		
		if (e.getActionCommand() == "all" ){
			ListModel model = list.getModel();
			int n = model.getSize();
			for (int i=0;i<n;i++) {
				CheckableItem item = (CheckableItem)model.getElementAt(i);
				item.setSelected(true);
			}
			list.repaint();
		}else if(e.getActionCommand() == "none"){
			ListModel model = list.getModel();
			int n = model.getSize();
			for (int i=0;i<n;i++) {
				CheckableItem item = (CheckableItem)model.getElementAt(i);
				item.setSelected(false);
			}
			list.repaint();
		}else if(e.getActionCommand() == "alwaysall"){
			enableStatSelections(false);
		}else if(e.getActionCommand() == "subset"){
			enableStatSelections(true);
		}else if(e.getActionCommand()=="alltitles"){
			showtitles = statblock.SHOWTITLES_ALWAYS;
		}else if(e.getActionCommand()=="notitles"){
			showtitles = statblock.SHOWTITLES_NEVER;
		}else if(e.getActionCommand()=="sometitles"){
			showtitles = statblock.SHOWTITLES_AUTOMATIC;
		}else if(e.getActionCommand()=="leftalign"){
			splitalign = statblock.LEFTALIGNSPLIT;
		}else if(e.getActionCommand()=="rightalign"){
			splitalign = statblock.RIGHTALIGNSPLIT;
		}else if(e.getActionCommand()=="noalign"){
			splitalign = statblock.NOALIGNSPLIT;
		}
		
	}
	
	private void enableStatSelections(boolean toggleable){
		all.setEnabled(toggleable);
		none.setEnabled(toggleable);
		selectlabel.setEnabled(toggleable);
		list.setEnabled(toggleable);
	}		
	
		
	private CheckableItem[] createData(String[] strs) {
    int n = strs.length;
    CheckableItem[] items = new CheckableItem[n];
    for (int i=0;i<n;i++) {
      items[i] = new CheckableItem(strs[i]);
    }
    return items;
  }
  
  class CheckableItem {
    private String  str;
    private boolean isSelected;
    
    public CheckableItem(String str) {
      this.str = str;
      isSelected = false;
	  initializeSelection();
	  
    }
	private void initializeSelection(){
		String[] oldselections = statblock.getSelectedEntries();
		if(oldselections==null) setSelected(true);
		if(oldselections!=null)
						for (int i=0;i<oldselections.length;i++)
							if(str.equals(oldselections[i])) setSelected(true);				
      
	}
    public void setSelected(boolean b) {
      isSelected = b;
	}
    
    public boolean isSelected() {
      return isSelected;
    }
    
    public String toString() {
      return str;
    }
  }
  
  class CheckListRenderer extends JCheckBox implements ListCellRenderer {
		  
	  public Component getListCellRendererComponent(JList list, Object value,
               int index, boolean isSelected, boolean hasFocus) {
      setEnabled(list.isEnabled());
	  setSelected(((CheckableItem)value).isSelected());
      setText(value.toString());
	  setBackground(Color.white);
	  return this;
    }
  } 

	
	
}
