package hep.aida.ref.plotter.style.registry;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.freehep.application.Application;


public class JELRuleEditorPanel extends JPanel {
    private String title;
    private JTextField field;
    private JPanel actionsPanel;
    private JComboBox qualifiers;
    private JComboBox logics;
    private JComboBox operations;
    private DefaultComboBoxModel noneModel;
    private DefaultComboBoxModel intModel;
    private DefaultComboBoxModel logicModel;
    private DefaultComboBoxModel stringModel;
    private DefaultComboBoxModel objectModel;
    private JButton ok;
    private JButton cancel;
    private String oldText;
    private JELRule rule;
    
    public JELRuleEditorPanel() {
        this(null);
    }
    public JELRuleEditorPanel(JELRule rule) {
        this("JEL Rule Editor", rule);
    }
    public JELRuleEditorPanel(String title, JELRule rule) {
        super();
        this.title = title;
        createActionsPanel();
        initComponents();
        if (rule != null) setRule(rule);
    }
    
    private void initComponents() {
        field = new JTextField(40);
        ok = new JButton("Set Rule");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                okAction();
            }
        });
        
        cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                cancelAction();
            }
        });
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets =  new java.awt.Insets(2, 2, 2, 2);

        JPanel rulePanel = new JPanel();
        rulePanel.setLayout(new java.awt.GridBagLayout());
        
        gbc.gridy=0; gbc.gridx=0;
        rulePanel.add(new JLabel("Rule: "), gbc);
        gbc.gridx=1;
        rulePanel.add(field, gbc);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new java.awt.GridBagLayout());
        gbc.gridy=0; gbc.gridx=0;
        buttonPanel.add(cancel, gbc);
        gbc.gridy=0; gbc.gridx=1;
        buttonPanel.add(ok, gbc);
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new TitledBorder(new EtchedBorder(), title));
        if (actionsPanel != null) this.add(actionsPanel);
        this.add(rulePanel);
        this.add(buttonPanel);
    }
    
    // Create panel with help and actions
    private void createActionsPanel() {
        String[] qual = new String[] { "[NONE]", IStyleRule.OVERLAY_INDEX, IStyleRule.OVERLAY_TOTAL, 
                                IStyleRule.REGION_INDEX, IStyleRule.REGION_TOTAL, IStyleRule.PATH,
                                IStyleRule.OBJECT, IStyleRule.OBJECT_TYPE, IStyleRule.ATTRIBUTE};
        
        String[] none = new String[] { "[NONE]" };
        String[] logic = new String[] { "[NONE]", " && ", " || ", " ( ", " ) " };
        String[] integ = new String[] { "[NONE]", " == ", " != ", " >= ", " <= ", " > ", " < " };
        String[] str  = new String[] { "[NONE]", ".equals(\"\")", ".equalsIgnoreCase(\"\")", ".startsWith(\"\")",
                                       ".endsWith(\"\")", ".contains(\"\")", 
                                       ".indexOf(\"\")", ".lastIndexOf(\"\")",
                                       ".toLowerCase(\"\")", ".toUpperCase(\"\")" };
        String[] objm = new String[] { "[NONE]", ".getClass()", ".toString()" };
        
        noneModel = new DefaultComboBoxModel(none);
        intModel = new DefaultComboBoxModel(integ);
        logicModel = new DefaultComboBoxModel(logic);
        stringModel = new DefaultComboBoxModel(str);
        objectModel = new DefaultComboBoxModel(objm);

        qualifiers = new JComboBox(qual);
        qualifiers.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                System.out.println("Qualifiers : "+e.getStateChange());
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String message = (e.getItem() instanceof String) ? (String) e.getItem() : e.getItem().toString();
                    System.out.println("Qualifiers Selected: "+message);
                    if (!message.equals("[NONE]")) {
                        if (message.equals(IStyleRule.OVERLAY_INDEX) || message.equals(IStyleRule.OVERLAY_TOTAL) ||
                            message.equals(IStyleRule.REGION_INDEX) || message.equals(IStyleRule.REGION_TOTAL)) setIntModel();
                        else if (message.equals(IStyleRule.OBJECT)) setObjectModel();
                        else setStringModel();
                        addTextToTheRule(message);
                    } else setNoneModel();
                }
            }
        });
        
        logics = new JComboBox(logicModel);
        logics.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String message = (e.getItem() instanceof String) ? (String) e.getItem() : e.getItem().toString();
                    System.out.println("Logics Selected: "+message);
                    if (!message.equals("[NONE]")) {
                        addTextToTheRule(message);
                        logics.setSelectedIndex(0);
                    }
                }
            }
        });
        
        operations = new JComboBox(intModel);
        operations.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String message = (e.getItem() instanceof String) ? (String) e.getItem() : e.getItem().toString();
                    System.out.println("Qualifiers Selected: "+message);
                    if (!message.equals("[NONE]")) {
                        addTextToTheRule(message);
                        operations.setSelectedIndex(0);
                        qualifiers.setSelectedIndex(0);
                    }
                }
            }
        });
        
        // Create panel and pack everything together
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets =  new java.awt.Insets(2, 2, 2, 2);

        actionsPanel = new JPanel();
        actionsPanel.setLayout(new java.awt.GridBagLayout());
        
        gbc.gridy=0; gbc.gridx=0;
        actionsPanel.add(new JLabel(" Qualifiers: "), gbc);
        gbc.gridx=1;
        actionsPanel.add(qualifiers, gbc);
        gbc.gridx=2;
        actionsPanel.add(new JLabel(" Logics: "), gbc);
        gbc.gridx=3;
        actionsPanel.add(logics, gbc);
        gbc.gridx=4;
        actionsPanel.add(new JLabel(" Operations: "), gbc);
        gbc.gridx=5;
        actionsPanel.add(operations, gbc);
       
    }
    
    
    public JELRule getRule() { return rule; }
    public void setRule(JELRule rule) {
        this.rule = rule;
        oldText = rule.getDescription();
        if (oldText == null) oldText = "";
        field.setText(oldText);
    }
    
    public boolean isModified() {
        if (rule.getDescription() != null) {
            if (!rule.getDescription().equals(field.getText())) return true;
            if (!rule.getDescription().equals(oldText)) return true;
        } 
        if (rule.getDescription() == null && field.getText() != null && !field.getText().trim().equals("")) return true;
        else return false;
    }
    
    void okAction() {
        try {
            String tmp = field.getText();
            rule.setDescription(tmp);
            oldText = tmp;
        } catch (Exception e) {
            handleException(e);
        }
    }
    
    void cancelAction() {
        try {
            rule.setDescription(oldText);
            field.setText(oldText);
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void setNoneModel()   { operations.setModel(noneModel); }
    private void setIntModel()    { operations.setModel(intModel); }
    private void setStringModel() { operations.setModel(stringModel); }
    private void setObjectModel() { operations.setModel(objectModel); }
    
    
    private void addTextToTheRule(String text) {
        String tmp = field.getText();
        tmp += text;
        field.setText(tmp);
    }
    
    private void handleException(Exception e) {
        handleException("", e);
    }
    private void handleException(String message, Exception e) {
        if (Application.getApplication() != null) Application.error(Application.getApplication(), message, e);
        System.out.println("ERROR: "+ message+"\n");
        if (e != null) e.printStackTrace();
    }
    
    public static void main(String[] args) {
	JFrame frame = new JFrame("TesT");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JELRuleEditorPanel panel = new JELRuleEditorPanel(new JELRule());
        frame.getContentPane().add(panel);
        
        //frame.setSize(500, 300);
        frame.pack();
        frame.setVisible(true);
    }
    
}
