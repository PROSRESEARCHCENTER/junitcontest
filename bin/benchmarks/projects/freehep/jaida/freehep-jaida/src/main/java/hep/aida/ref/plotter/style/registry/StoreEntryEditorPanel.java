package hep.aida.ref.plotter.style.registry;

import hep.aida.ref.plotter.style.editor.ConfigurePreviewPanel;
import hep.aida.ref.plotter.style.editor.StyleEditorPanel;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

public class StoreEntryEditorPanel extends JSplitPane {
    private StyleStoreEntry entry;
    private StyleEditorPanel stylePanel;
    private JELRuleEditorPanel rulePanel;
    private boolean modified = false;
    
    public StoreEntryEditorPanel(StyleStoreEntry entry) {
        this(entry, false);
    }
    public StoreEntryEditorPanel(StyleStoreEntry entry, boolean showPreview) {
        //super();
        super(JSplitPane.VERTICAL_SPLIT);
        this.entry = entry;
        initComponents(showPreview);
    }
    
    void initComponents(boolean showPreview) {
        if (entry.getRule() != null) rulePanel = new JELRuleEditorPanel((JELRule) entry.getRule());
        ConfigurePreviewPanel cpp = new ConfigurePreviewPanel();
        Class previewType = entry.getPreviewType();
        if (previewType == null) stylePanel = new StyleEditorPanel(entry.getStyle(), cpp);
        else stylePanel = new StyleEditorPanel(entry.getStyle(), cpp, previewType);
        
        this.setBottomComponent(stylePanel);
        if (rulePanel != null) {
            this.setTopComponent(rulePanel);
            this.setDividerLocation(150);
        } else {
            this.setTopComponent(null);
            this.setDividerLocation(0);
        }
        stylePanel.setPreviewVisible(showPreview);        
    }
    
    public String name() { 
        return entry.getName();
    }
    
    public boolean isModified() { 
        if (rulePanel == null) return false;
        return rulePanel.isModified();
    }
    
    public void saveRuleChange() { rulePanel.okAction(); }
    
    public void cancelRuleChange() { rulePanel.cancelAction(); }
    
    public void close() {
        entry = null;
        rulePanel = null;
        stylePanel = null;
    }
    
    public static void main(String[] args) {
        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        //LookAndFeelTweaks.tweak();
        //LookAndFeelAddons addon = LookAndFeelAddons.getAddon();

        JFrame frame = new JFrame("TesT");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String name = "Historgam-1D Style";
        hep.aida.IPlotterFactory pf = hep.aida.IAnalysisFactory.create().createPlotterFactory();
        hep.aida.IPlotterStyle style = pf.createPlotterStyle();
        style.dataBoxStyle().setVisible(true);
        style.dataBoxStyle().backgroundStyle().setColor("Red");
        JELRule rule = new JELRule("OVERLAYNUMBER==2");
        StyleStoreEntry entry = new StyleStoreEntry(name, style, rule);
        StoreEntryEditorPanel panel = new StoreEntryEditorPanel(entry);
        frame.getContentPane().add(panel);
        
        //frame.setSize(500, 300);
        frame.pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation( (d.width-frame.getSize().width )/2, (d.height-frame.getSize().height )/2 );
        frame.setVisible(true);
    }
        
}
