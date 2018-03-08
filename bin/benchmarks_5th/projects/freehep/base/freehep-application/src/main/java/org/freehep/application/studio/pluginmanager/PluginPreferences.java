package org.freehep.application.studio.pluginmanager;

import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import org.freehep.application.PropertyUtilities;
import org.freehep.application.studio.PluginMap.Orphan;

/**
 * Plugin manager preferences panel.
 *
 * @author tonyj
 * @version $Id: PluginPreferences.java 14095 2013-01-29 20:57:42Z tonyj $
 */
public class PluginPreferences {

// -- Private parts : ----------------------------------------------------------

    protected PluginManager manager;

    protected String keyPrefix = "PluginManager.";
  
    protected String urlKey = "URL";
    protected String snapshotsKey = "snapshots";
    protected String checkAtStartKey = "checkForPluginsAtStart";
    protected String orphanActionKey = "orphanAction";
    protected String orphanPromptKey = "orphanPrompt";
    protected String notifyPluginUpdatesKey = "notifyPluginUpdates";
    protected String downloadMissingKey = "downloadMissing";
  
    protected URL urlDefault;
    protected boolean snapshotsDefault = false;
    protected boolean checkAtStartDefault = true;
    protected Orphan orphanActionDefault = Orphan.Ignore;
    protected boolean orphanPromptDefault = true;
    protected boolean notifyPluginUpdatesDefault = true;
    protected boolean downloadMissingDefault = true;
  
    /** property - URL from which the list of available plugins should be downloaded */
    protected URL url;
    /** property - should we include SNAPSHOT versions in the list of available plugins */
    protected boolean snapshots;
    /** property - should we check for updates when the application starts */
    protected boolean checkAtStart;
    /** property - what to do with unclaimed libraries */
    protected Orphan orphanAction;
    /** property - should we warn the user when unclaimed libraries are found */
    protected boolean orphanPrompt;
    /** property - should we prompt the user to download plugin updates if available */
    protected boolean notifyPluginUpdates;
    /** property - should we try to download missing libraries and plugin dependencies */
    protected boolean downloadMissing;
    
    protected final int HSPACE = 10;
    protected final int VSPACE = 5;
    

// -- Construction, reading and saving preferences : ---------------------------

    /** Constructs PluginPreferences object and optionally initializes property values from saved user preferences. */
    public PluginPreferences(PluginManager manager, boolean restore) {
        this.manager = manager;
        if (restore) restore();
    }

    /** Initializes current settings from saved user preferences. */
    protected final void restore() {
        Properties prop = manager.getApplication().getUserProperties();
        url = PropertyUtilities.getURL(prop, keyPrefix + urlKey, urlDefault);
        snapshots = PropertyUtilities.getBoolean(prop, keyPrefix + snapshotsKey, snapshotsDefault);
        checkAtStart = PropertyUtilities.getBoolean(prop, keyPrefix + checkAtStartKey, checkAtStartDefault);
        try {
            orphanAction = Orphan.valueOf(PropertyUtilities.getString(prop, keyPrefix + orphanActionKey, orphanActionDefault.name()));
        } catch (IllegalArgumentException x) {
            orphanAction = orphanActionDefault;
        }
        orphanPrompt = PropertyUtilities.getBoolean(prop, keyPrefix + orphanPromptKey, orphanPromptDefault);
        notifyPluginUpdates = PropertyUtilities.getBoolean(prop, keyPrefix + notifyPluginUpdatesKey, notifyPluginUpdatesDefault);
        downloadMissing = PropertyUtilities.getBoolean(prop, keyPrefix + downloadMissingKey, downloadMissingDefault);
    }
    
    /** Saves user preferences. */
    protected void save() {
        Properties prop = manager.getApplication().getUserProperties();
        PropertyUtilities.setURL(prop, keyPrefix + urlKey, url);
        PropertyUtilities.setBoolean(prop, keyPrefix + snapshotsKey, snapshots == snapshotsDefault ? null : snapshots);
        PropertyUtilities.setBoolean(prop, keyPrefix + checkAtStartKey, checkAtStart == checkAtStartDefault ? null : checkAtStart);
        PropertyUtilities.setString(prop, keyPrefix + orphanActionKey, orphanAction == orphanActionDefault ? null : orphanAction.name());
        PropertyUtilities.setBoolean(prop, keyPrefix + orphanPromptKey, orphanPrompt == orphanPromptDefault ? null : orphanPrompt);
        PropertyUtilities.setBoolean(prop, keyPrefix + notifyPluginUpdatesKey, notifyPluginUpdates == notifyPluginUpdatesDefault ? null : notifyPluginUpdates);
        PropertyUtilities.setBoolean(prop, keyPrefix + downloadMissingKey, downloadMissing == downloadMissingDefault ? null : downloadMissing);
    }

// -- Getters : ----------------------------------------------------------------

    /**
     * Getter for property <tt>url</tt>.
     * URL from which the list of available and updatable plugins should be downloaded.
     */
    public URL getUrl() {
        if (snapshots && urlDefault.equals(url)) {
            try {
                return new URL(urlDefault.toString() +"?snapshots=true");
            } catch (MalformedURLException x) {
                return url;
            }
        } else {
            return url;
        }
    }

    /**
     * Getter for property <tt>snapshots</tt>.
     * If <tt>false</tt>, the plugin manager will ignore snapshot versions of plugins found in the downloaded list.
     * If <tt>true</tt> and the plugin list download URL is set to default, "?snapshots=true" will be appended to the URL.
     */
    public boolean isSnapshots() {
        return snapshots;
    }
    
    /**
     * Getter for property <tt>checkAtStart</tt>.
     * If <tt>true</tt>, the plugin manager will start downloading a list of available
     * and updatable plugins when the application is started.
     */
    public boolean isCheckAtStart() {
        return checkAtStart;
    }

    /**
     * Getter for property <tt>orphanAction</tt>.
     * Options for dealing with unclaimed libraries.
     */
    public Orphan getOrphanAction() {
        return orphanAction;
    }

    /**
     * Getter for property <tt>orphanPrompt</tt>.
     * If <tt>true</tt>, the user will be prompted on what to do with unclaimed libraries
     * every time such libraries are discovered.
     */
    public boolean isOrphanPrompt() {
        return orphanPrompt;
    }

    /**
     * Getter for property <tt>notifyPluginUpdates</tt>.
     * If <tt>true</tt>, the user should be prompted to download updates to installed plugins if updates are available.
     */
    public boolean isNotifyPluginUpdates() {
//        return notifyPluginUpdates;
        return isCheckAtStart(); // shortcircuit for now - do we need these separate ?
    }

    /**
     * Getter for property <tt>downloadMissingLibs</tt>.
     * If <tt>true</tt>, the application will try to download missing libraries required by active plugins.
     */
    public boolean isDownloadMissing() {
        return downloadMissing;
    }

// -- Setters : ----------------------------------------------------------------

    public void setUrl(URL url, boolean save) {
        this.url = url;
        if (save) {
            PropertyUtilities.setURL(manager.getApplication().getUserProperties(), keyPrefix + urlKey, url);
        }
    }

    public void setSnapshots(boolean snapshots, boolean save) {
        this.snapshots = snapshots;
        if (save) {
            PropertyUtilities.setBoolean(manager.getApplication().getUserProperties(), keyPrefix + snapshotsKey, snapshots == snapshotsDefault ? null : snapshots);
        }
    }
    
    public void setCheckAtStart(boolean checkAtStart, boolean save) {
        this.checkAtStart = checkAtStart;
        if (save) {
            PropertyUtilities.setBoolean(manager.getApplication().getUserProperties(), keyPrefix + checkAtStartKey, checkAtStart == checkAtStartDefault ? null : checkAtStart);
        }
    }

    public void setOrphanAction(Orphan orphanAction, boolean save) {
        this.orphanAction = orphanAction;
        if (save) {
            PropertyUtilities.setString(manager.getApplication().getUserProperties(), keyPrefix + orphanActionKey, orphanAction == orphanActionDefault ? null : orphanAction.name());
        }
    }

    public void setOrphanPrompt(boolean orphanPrompt, boolean save) {
        this.orphanPrompt = orphanPrompt;
        if (save) {
            PropertyUtilities.setBoolean(manager.getApplication().getUserProperties(), keyPrefix + orphanPromptKey, orphanPrompt == orphanPromptDefault ? null : orphanPrompt);
        }
    }

    public void setNotifyPluginUpdates(boolean notifyPluginUpdates, boolean save) {
        this.notifyPluginUpdates = notifyPluginUpdates;
        if (save) {
            PropertyUtilities.setBoolean(manager.getApplication().getUserProperties(), keyPrefix + notifyPluginUpdatesKey, notifyPluginUpdates == notifyPluginUpdatesDefault ? null : notifyPluginUpdates);
        }
    }

    public void setDownloadMissing(boolean downloadMissing, boolean save) {
        this.downloadMissing = downloadMissing;
        if (save) {
            PropertyUtilities.setBoolean(manager.getApplication().getUserProperties(), keyPrefix + downloadMissingKey, downloadMissing == downloadMissingDefault ? null : downloadMissing);
        }
    }
    

// -- Handling preferences dialog : --------------------------------------------
    
    protected String getDescription(Orphan action) {
        if (action == null) return "<html>Prompt me every time unclaimed libraries are detected.</html>";
        switch (action) {
            case Remove:
                return "<html>Recommended. Unclaimed library files will be deleted.</html>";
            case Ignore:
                return "<html>Unclaimed library files will not be deleted but classes <br>they contain will remain inaccessible to the application. <br>It will be possible to load or delete these libraries later through <br>the plugin manager.</html>";
            case Load:
                return "<html>Libraries found in the extensions directories will be added to <br>the classpath whether or not they are claimed by any installed plugins. <br>This can be convenient when developing new plugins, <br>but it may affect application performance and result in unpredictable behavior, <br>depending on the content of the loaded libraries.</html>";
            default:
                return "<html>Prompt me every time unclaimed libraries are detected.</html>";
        }
    }
    
    /** Returns GUI panel to be used for setting preferences. */
    public JComponent getPreferencesPanel() {
        return new GUI();
    }

    /**
     * Reads GUI and updates current settings, calls PluginManager setters.
     * The JComponent instance passed to this method must be created by calling {@link #getPreferencesPanel}.
     * @return <tt>true</tt> if valid values were read from GUI.
     */
    public boolean apply(JComponent gui) {
        try {
            URL oldURL = url;
            boolean oldSnap = snapshots;
            boolean out = ((GUI)gui).get();
            if ( out && url != null && (!url.equals(oldURL) || (oldSnap != snapshots)) ) {
                manager.startPluginListDownload();
            }
            save();
            return out;
        } catch (ClassCastException x) {
            return false;
        }
    }
    
    protected class GUI extends JPanel {

        protected JCheckBox _snapshotsBox;
        protected JCheckBox _checkAtStartBox;
        protected JCheckBox _downloadMissingBox;
        protected JTextField _urlField;
        protected JButton _urlButton;
        protected ButtonGroup _orphanGroup;
        protected EnumMap<Orphan, JRadioButton> _orphanButtons;
        protected JCheckBox _promptBox;
        protected boolean _checkAtStartBoxState; // remember whether the box was selected by user, even if it is unselected due to empty url

        GUI() {

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            // Plugin updates/downloads:

            Box panel = Box.createVerticalBox();
            panel.setBorder(BorderFactory.createTitledBorder("Plugin catalog"));
            panel.setAlignmentX(LEFT_ALIGNMENT);

            Box row = Box.createHorizontalBox();
            row.setAlignmentX(LEFT_ALIGNMENT);
            row.add(new JLabel("URL: "));
            _urlField = new JTextField(8);
            _urlField.setMaximumSize(new Dimension(Integer.MAX_VALUE, _urlField.getPreferredSize().height));
            _urlField.addCaretListener(new CaretListener() {
                public void caretUpdate(CaretEvent fe) {
                    String text = _urlField.getText().trim();
                    if (text.isEmpty()) {
                        _checkAtStartBox.setEnabled(false);
                        _checkAtStartBox.setSelected(false);
                        _snapshotsBox.setEnabled(false);
                    } else {
                        _checkAtStartBox.setEnabled(true);
                        _checkAtStartBox.setSelected(_checkAtStartBoxState);
                        _snapshotsBox.setEnabled(true);
                    }
                }
            });
            row.add(_urlField);
            row.add(Box.createRigidArea(new Dimension(HSPACE, 0)));
            _urlButton = new JButton("Default");
            _urlButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    _urlField.setText(urlDefault == null ? "" : urlDefault.toString());
                }
            });
            row.add(_urlButton);
            row.add(Box.createHorizontalGlue());
            panel.add(row);

            _snapshotsBox = new JCheckBox("Include snapshot versions of plugins");
            _snapshotsBox.setAlignmentX(LEFT_ALIGNMENT);
            _snapshotsBox.setToolTipText("Uncheck to ignore snapshots.");
            panel.add(_snapshotsBox);
            panel.add(Box.createRigidArea(new Dimension(0, VSPACE)));

            _checkAtStartBox = new JCheckBox("Check for updated plugins at startup");
            _checkAtStartBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    _checkAtStartBoxState = _checkAtStartBox.isSelected();
                }
            });
            _checkAtStartBox.setAlignmentX(LEFT_ALIGNMENT);
            _checkAtStartBox.setToolTipText("<html>Check to download the list of available plugins at startup.<br> The list will be used to offer updates and download missing plugin dependencies.</html>");
            panel.add(_checkAtStartBox);
            panel.add(Box.createRigidArea(new Dimension(0, VSPACE)));

            _downloadMissingBox = new JCheckBox("Download missing libraries");
            _downloadMissingBox.setAlignmentX(LEFT_ALIGNMENT);
            _downloadMissingBox.setToolTipText("<html>Check to attempt downloading any missing libraries referenced by installed plugins when the application starts.</html>");
            panel.add(_downloadMissingBox);
            panel.add(Box.createRigidArea(new Dimension(0, VSPACE)));

            add(panel);

            // Handling unclaimed libraries:

            panel = Box.createVerticalBox();
            panel.setBorder(BorderFactory.createTitledBorder("Handling of unclaimed libraries"));
            panel.setAlignmentX(LEFT_ALIGNMENT);
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getMaximumSize().height));
            panel.setToolTipText("<html>Choose how to handle libraries found in the application extensions<br> directories if they are not referenced by any installed plugins.</html>");

            _orphanGroup = new ButtonGroup();
            _orphanButtons = new EnumMap(Orphan.class);
            JRadioButton radio = new JRadioButton("Remove  (recommended)");
            radio.setActionCommand(Orphan.Remove.name());
            radio.setToolTipText(getDescription(Orphan.Remove));
            _orphanButtons.put(Orphan.Remove, radio);
            _orphanGroup.add(radio);
            panel.add(radio);
            radio = new JRadioButton(Orphan.Ignore.name());
            radio.setActionCommand(Orphan.Ignore.name());
            radio.setToolTipText(getDescription(Orphan.Ignore));
            _orphanButtons.put(Orphan.Ignore, radio);
            _orphanGroup.add(radio);
            panel.add(radio);
            radio = new JRadioButton("Load  (dangerous, use at your own risk) ");
            radio.setActionCommand(Orphan.Load.name());
            radio.setToolTipText(getDescription(Orphan.Load));
            _orphanButtons.put(Orphan.Load, radio);
            _orphanGroup.add(radio);
            panel.add(radio);
            _promptBox = new JCheckBox("Warn when unclaimed libraries are found");
            _promptBox.setToolTipText(getDescription(null));
            panel.add(_promptBox);
            add(panel);

            add(Box.createRigidArea(new Dimension(0, VSPACE)));
            add(Box.createVerticalGlue());

            set();
        }

        final boolean get() {
            snapshots = _snapshotsBox.isSelected();
            String urlText = _urlField.getText().trim();
            if (urlText.isEmpty()) {
                url = null;
            } else {
                try {
                    url = new URL(urlText);
                } catch (MalformedURLException x) {
                    if (url == null) {
                        _urlField.setText("");
                        _snapshotsBox.setEnabled(false);
                        _checkAtStartBox.setSelected(false);
                        _checkAtStartBox.setEnabled(false);
                    } else {
                        _urlField.setText(url.toString());
                        _snapshotsBox.setEnabled(true);
                        _checkAtStartBox.setSelected(_checkAtStartBoxState);
                        _checkAtStartBox.setEnabled(true);
                    }
                    return false;
                }
            }
            checkAtStart = _checkAtStartBoxState;
            downloadMissing = _downloadMissingBox.isSelected();
            try {
                orphanAction = Orphan.valueOf(_orphanGroup.getSelection().getActionCommand());
            } catch (NullPointerException x) {
                orphanAction = orphanActionDefault;
            }
            orphanPrompt = _promptBox.isSelected();
            return true;
        }

        final void set() {
            _snapshotsBox.setSelected(snapshots);
            _checkAtStartBoxState = checkAtStart;
            _downloadMissingBox.setSelected(downloadMissing);
            if (url == null) {
                _snapshotsBox.setEnabled(false);
                _checkAtStartBox.setSelected(false);
                _checkAtStartBox.setEnabled(false);
                _urlField.setText("");
            } else {
                _snapshotsBox.setEnabled(true);
                _checkAtStartBox.setSelected(checkAtStart);
                _checkAtStartBox.setEnabled(true);
                _urlField.setText(url.toString());
            }
            _orphanButtons.get(orphanAction).setSelected(true);
            _promptBox.setSelected(orphanPrompt);
        }
    }


// -- Warning users of unclaimed libraries : -----------------------------------
    
    public void showUnclaimedLibrariesWarning() {
        
        Box panel = Box.createVerticalBox();
        panel.setBorder(BorderFactory.createEmptyBorder(HSPACE, VSPACE, HSPACE, VSPACE));
        
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>Some of the libraries found in application extension directories are not required by any installed plugins.<p>");
        sb.append("Based on your current settings, unclaimed libraries ");
        switch (orphanAction) {
            case Remove:
                sb.append("have been deleted"); break;
            case Ignore:
                sb.append("are ignored. You can inspect, load, or delete these libraries through the Plugin Manager"); break;
            case Load:
                sb.append("have been loaded on the application classpath"); break;
        }
        sb.append(".<p>");
        sb.append("Settings that affect treatment of unclaimed libraries can be modified through the application preferences dialog.</body></html>");
        
        JEditorPane textPane = new JEditorPane();
        textPane.setOpaque(false);
        textPane.setContentType("text/html");
        textPane.setText(sb.toString());
        textPane.setPreferredSize(new Dimension(520,200));
        textPane.setEditable(false);
        textPane.setAlignmentX(LEFT_ALIGNMENT);
        
        panel.add(textPane);
        panel.add(Box.createRigidArea(new Dimension(0, 2*VSPACE)));
        final JCheckBox checkBox = new JCheckBox("Don't show this again");
        checkBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setOrphanPrompt(!checkBox.isSelected(), true);
            }
        });
        checkBox.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(checkBox);
        panel.add(Box.createVerticalGlue());
        
        JOptionPane.showMessageDialog(manager.getApplication(), new JScrollPane(panel), "Unclaimed Libraries Found", JOptionPane.PLAIN_MESSAGE);
        
    }

}
