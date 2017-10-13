// Copyright 2000, SLAC, Stanford, California, U.S.A.
package org.freehep.xml.menus;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JToolBar.Separator;
import javax.xml.parsers.SAXParserFactory;
import org.freehep.util.images.ImageHandler;
import org.freehep.xml.util.BadXMLException;
import org.freehep.xml.util.ClassPathEntityResolver;
import org.freehep.xml.util.SAXErrorHandler;
import org.freehep.xml.util.SAXTraverser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.awt.Event;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Create a set of menus by reading an XML file.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @author Peter Armstrong
 * @version $Id: XMLMenuBuilder.java 8584 2006-08-10 23:06:37Z duns $
 */
public class XMLMenuBuilder
{
    public static final String LOCATION_PROPERTY = "Location";
    private static ClassLoader defaultLoader;
    /**
     * Create an (empty) XMLMenuBuilder
     */
    public XMLMenuBuilder()
    {
        // Make sure to change Look and feel of unparented menus
        UIManager.addPropertyChangeListener(new UIListener(this));
    }
    /**
     * Build an XMLMenuSystem using the default SAX parser.
     * @param xml The URL from which to read the XML
     */
    public void build(URL xml) throws SAXException, IOException
    {
        XMLReader reader;
        try
        {
            reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        }
        catch (SecurityException x) // JAXP doesn't work if downloaded into webstart
        {
            try
            {
                String parser = System.getProperty("org.xml.sax.driver");
                if (parser == null) System.getProperties().setProperty("org.xml.sax.driver","org.apache.xerces.parsers.SAXParser");
            }
            catch (SecurityException xx)
            {}
            reader = XMLReaderFactory.createXMLReader();
        }
        catch (Exception x)
        {
            throw new RuntimeException("Error creating XML Parser",x);
        }
        build(xml,reader);
    }
    /**
     * Build an XMLMenuSystem
     * @param xml The URL from which to read the XML
     * @param reader The XMLReader to use to parse the XML
     */
    public void build(URL xml, XMLReader reader) throws SAXException, IOException
    {
        reader.setFeature("http://xml.org/sax/features/validation", true);
        reader.setEntityResolver(new ClassPathEntityResolver(XMLMenuBuilder.class,"http://java.freehep.org/schemas/menus/"));
        MenuSystemTraverser t = new MenuSystemTraverser();
        t.setReader(reader);
        reader.setErrorHandler(new SAXErrorHandler());
        baseURL = xml;
        InputStream in = new BufferedInputStream(xml.openStream());
        try
        {
            InputSource input = new InputSource(in);
            input.setSystemId(xml.toString());
            input.setPublicId(xml.getFile());
            reader.parse(input);
        }
        finally
        {
            in.close();
        }
    }
   /*
    * sets the default class for menubars
    * @param klass the new default class, must be a subclass of JMenuBar
    */
    public void setDefaultMenuBarClass(Class klass) throws IllegalDefaultClassException
    {
        defaultMenuBarClass = setDefaultClass(JMenuBar.class, klass);
    }
    /**
     * sets the default class of menus to be klass
     * @param klass the new default class, must be a subclass of JMenu
     */
    public void setDefaultMenuClass(Class klass) throws IllegalDefaultClassException
    {
        defaultMenuClass = setDefaultClass(JMenu.class, klass);
    }
    
    /**
     * sets the default class of menu items to be klass
     * @param klass the new default class, must be a subclass of JMenuItem
     */
    public void setDefaultMenuItemClass(Class klass) throws IllegalDefaultClassException
    {
        defaultMenuItemClass = setDefaultClass(JMenuItem.class, klass);
    }
    
    /**
     * sets the default class of radio menu items to be klass
     * @param klass the new default class, must be a subclass of JMenuItem
     */
    public void setDefaultRadioMenuItemClass(Class klass) throws IllegalDefaultClassException
    {
        defaultRadioButtonMenuItemClass = setDefaultClass(JMenuItem.class, klass);
    }
    
    /**
     * sets the default class of checkbox menu items to be klass
     * @param klass the new default class, must be a subclass of JMenuItem
     */
    public void setDefaultCheckBoxMenuItemClass(Class klass) throws IllegalDefaultClassException
    {
        defaultCheckBoxMenuItemClass = setDefaultClass(JMenuItem.class, klass);
    }
    
    /**
     * sets the default class of popup menus to be klass
     * @param klass the new default class, must be a subclass of JPopupMenu
     */
    public void setDefaultPopupMenuClass(Class klass) throws IllegalDefaultClassException
    {
        defaultPopupMenuClass = setDefaultClass(JPopupMenu.class, klass);
    }
    
    /**
     * sets the default class of toolbars to be klass
     * @param klass the new default class, must be a subclass of JToolBar
     */
    public void setDefaultToolBarClass(Class klass) throws IllegalDefaultClassException
    {
        defaultToolBarClass = setDefaultClass(JToolBar.class, klass);
    }
    
    /**
     * sets the default class of menu items to be klass
     * @param klass the new default class, must be a subclass of JButton
     */
    public void setDefaultToolBarItemClass(Class klass) throws IllegalDefaultClassException
    {
        defaultToolBarButtonClass = setDefaultClass(JButton.class, klass);
    }
    
    /**
     * Override this method to create your own subclass of JMenuBar
     */
    protected JMenuBar createMenuBar(String className) throws SAXException
    {
        Class klass;
        if (className == null) klass = defaultMenuBarClass;
        else klass = findClass(className);
        
        return (JMenuBar) createObject(klass, JMenuBar.class);
    }
    /**
     * Override this method to create your own subclass of JMenu
     */
    protected JMenu createMenu(String className) throws SAXException
    {
        Class klass;
        if (className == null) klass = defaultMenuClass;
        else klass = findClass(className);
        
        return (JMenu) createObject(klass, JMenu.class);
    }
    /**
     * Override this method to create your own subclass of JMenuItem
     */
    protected JMenuItem createMenuItem(String className, String name, String type, String command) throws SAXException
    {
        Class klass;
        if (className == null)
        {
            if      (type.equals("checkbox")) klass = defaultCheckBoxMenuItemClass;
            else if (type.equals("radio"))    klass = defaultRadioButtonMenuItemClass;
            else klass = defaultMenuItemClass;
        }
        else  klass = findClass(className);
        
        JMenuItem result = (JMenuItem) createObject(klass, JMenuItem.class);
        
        result.setText(name);
        if (command != null) result.setActionCommand(command);
        return result;
    }
    
    /**
     * Override this method to create your own subclass of JPopupMenu
     */
    protected JPopupMenu createPopupMenu(String className) throws SAXException
    {
        Class klass;
        if (className == null) klass = defaultPopupMenuClass;
        else klass = findClass(className);
        return (JPopupMenu) createObject(klass, JPopupMenu.class);
    }
    /**
     * Override this method to create your own subclass of JToolBar
     */
    protected JToolBar createToolBar(String className) throws SAXException
    {
        Class klass;
        if (className == null) klass = defaultToolBarClass;
        else klass = findClass(className);
        return (JToolBar) createObject(klass, JToolBar.class);
    }
    /**
     * Override this method to create your own subclass of JButton
     */
    protected AbstractButton createToolBarItem(String className, String name, String type, String command) throws SAXException
    {
        Class klass;
        if (className == null) klass = defaultToolBarButtonClass;
        else klass = findClass(className);
        if (type.equals("checkbox")) klass = JToggleButton.class;
        else klass = JButton.class;
        AbstractButton result = (AbstractButton) createObject(klass, AbstractButton.class);
        result.setMargin(new Insets(0,0,0,0));
        if (command != null) result.setActionCommand(command);
        return result;
    }
    
    /**
     * Override this method for customized icon creation.
     * The default implementation treats the argument as a relative or absolute URL.
     * If relative it is interpreted relative to the URL of the menu file being read.
     * If the icon cannot be found a "broken" icon is substituted.
     */
    protected Icon createIcon(String name) throws SAXException
    {
        try
        {
            URL url = null;
            if (name.startsWith("/")) url = getClass().getResource(name);
            if (url == null) url = new URL(baseURL,name);
            return ImageHandler.getIcon(url);
        }
        catch (MalformedURLException x)
        {
            throw new BadXMLException("Could not load image: "+name,x);
        }
    }
    private void merge(JComponent oldMenu, JComponent newMenu)
    {
        // Loop over each element of the new Menu,
        // and if it is not already in the old menu insert it as appropriate
        
        if (newMenu instanceof JMenu) newMenu = ((JMenu) newMenu).getPopupMenu();
        if (oldMenu instanceof JMenu) oldMenu = ((JMenu) oldMenu).getPopupMenu();
        
        outer: for (int i=0; i<newMenu.getComponentCount(); i++)
        {
            Component c = newMenu.getComponent(i);
            if (c instanceof JComponent)
            {
                JComponent comp = (JComponent) c;
                Integer location = (Integer) comp.getClientProperty(LOCATION_PROPERTY);
                if (location != null)
                {
                    int oldCount = oldMenu.getComponentCount();
                    for (int j=0; j<oldCount; j++)
                    {
                        Component old = oldMenu.getComponent(j);
                        if (old instanceof JComponent)
                        {
                            JComponent oldComp = (JComponent) old;
                            Integer oldLocation = (Integer) oldComp.getClientProperty(LOCATION_PROPERTY);
                            if (oldLocation != null)
                            {
                                if (oldLocation.equals(location))
                                {
                                    merge(oldComp,comp);
                                    continue outer;
                                }
                                else if (oldLocation.compareTo(location) > 0)
                                {
                                    oldMenu.add(comp,j);
                                    i--;
                                    continue outer;
                                }
                            }
                        }
                    }
                }
            }
            oldMenu.add(c);
            i--;
        }
    }
    
    /**
     * Get the menu bar with ID id
     */
    public JMenuBar getMenuBar(String id)
    {
        return (JMenuBar) menuBarHash.get(id);
    }
    
    /**
     * Get the tool bar with ID id
     */
    public JToolBar getToolBar(String id)
    {
        return (JToolBar) toolBarHash.get(id);
    }
    
    /**
     * Get the popup menu with ID id
     */
    public JPopupMenu getPopupMenu(String id)
    {
        return (JPopupMenu) popupMenuHash.get(id);
    }
    /**
     * Merge the popup menu with ID id onto the end of an existing menu
     * @param id The id of the popup menu
     * @param menu The menu to be merged
     */
    public JPopupMenu mergePopupMenu(String id, JPopupMenu menu)
    {
        try
        {
            JPopupMenu merge = getPopupMenu(id);
            if (menu == null) return merge;
            Component[] c = merge.getComponents();
            for (int i=0; i<c.length; i++)
            {
                if (c[i] instanceof JSeparator) menu.addSeparator();
                else menu.add(deepCopy(c[i]));
            }
            return menu;
        }
        catch (SAXException x)
        {
            throw new RuntimeException("Error parsing XML",x);
        }
    }
    private Component deepCopy(Component c) throws SAXException
    {
        if (c instanceof JMenu)
        {
            JMenu old = (JMenu) c;
            JMenu menu = createMenu(old.getClass().getName());
            Component[] cc = old.getPopupMenu().getComponents();
            for (int i=0; i<cc.length; i++)
            {
                if (cc[i] instanceof JSeparator) menu.addSeparator();
                else menu.add(deepCopy(cc[i]));
            }
            menu.setText(old.getText());
            menu.setMnemonic(old.getMnemonic());
            return menu;
        }
        else if (c instanceof JMenuItem)
        {
            JMenuItem old = (JMenuItem) c;
            JMenuItem item = createMenuItem(old.getClass().getName(),old.getText(),null,old.getActionCommand());
            item.setToolTipText(old.getToolTipText());
            item.setIcon(old.getIcon());
            item.setMnemonic(old.getMnemonic());
            item.setAccelerator(old.getAccelerator());
            return item;
        }
        else return c;
    }
    
    //
    // private methods, variables and classes
    //
    private Object createObject(Class klass, Class superKlass) throws SAXException
    {
        try
        {
            if (!superKlass.isAssignableFrom(klass)) throw new SAXException("Illegal class "+klass);
            return klass.newInstance();
        }
        catch (SAXException x)
        {
            throw x;
        }
        catch (Exception x)
        {
            throw new BadXMLException("Could not create "+klass,x);
        }
    }
    /**
     * Set the classloader that will be used to load classes named in the XML
     */
    public static void setClassLoader(ClassLoader loader)
    {
        defaultLoader = loader;
    }
    private Class findClass(String className) throws SAXException
    {
        try
        {
            ClassLoader loader = defaultLoader;
            if (loader == null) loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) loader = getClass().getClassLoader();
            return Class.forName(className,true,loader);
        }
        catch (ClassNotFoundException x)
        {
            throw new BadXMLException("Can not find class "+className,x);
        }
    }
    private Class setDefaultClass(Class baseClass, Class klass) throws IllegalDefaultClassException
    {
        if (baseClass.isAssignableFrom(klass)) return klass;
        throw new IllegalDefaultClassException("Cannot assign into " + baseClass + " from " + klass);
    }
    private Class defaultMenuBarClass = JMenuBar.class;
    private Class defaultToolBarClass = JToolBar.class;
    private Class defaultMenuItemClass = JMenuItem.class;
    private Class defaultRadioButtonMenuItemClass = JRadioButtonMenuItem.class;
    private Class defaultCheckBoxMenuItemClass = JCheckBoxMenuItem.class;
    private Class defaultPopupMenuClass = JPopupMenu.class;
    private Class defaultToolBarButtonClass = JButton.class;
    private Class defaultMenuClass = JMenu.class;
    private URL baseURL;
    
    private Hashtable menuBarHash = new Hashtable();
    private Hashtable toolBarHash = new Hashtable();
    private Hashtable popupMenuHash = new Hashtable();
    
    
    private class MenuSystemTraverser extends SAXTraverser
    {
        protected SAXTraverser handleElement(String name, Attributes attrs) throws SAXException
        {
            if      (name == "MenuSystem")  return this;
            else if (name == "MenuBar")     return new MenuBarTraverser();
            else if (name == "ToolBar")     return new ToolBarTraverser();
            else if (name == "PopupMenu")   return new PopupMenuTraverser();
            else if (name == "Menu")        return new MenuTraverser();
            else if (name == "Component")   return new MenuItemTraverser();
            else if (name == "Accelerator") return new AcceleratorTraverser();
            else if (name == "Separator")   return new SeparatorTraverser();
            else return super.handleElement(name,attrs);
        }
    }
    private class MenuBarTraverser extends MenuSystemTraverser
    {
        protected void handleSubElement(SAXTraverser sub) throws SAXException
        {
            if (sub instanceof MenuTraverser)
            {
                menuBar.add(((MenuTraverser) sub).getMenu());
            }
        }
        protected void handleElementAttributes(Attributes attrs) throws SAXException
        {
            String klass = attrs.getValue("class");
            menuBar = createMenuBar(klass);
            id = attrs.getValue("id");
        }
        protected void handleEndElement(String name)
        {
            JMenuBar oldMenu = (JMenuBar) menuBarHash.get(id);
            if (oldMenu == null) menuBarHash.put(id,menuBar);
            else merge(oldMenu,menuBar);
        }
        private JMenuBar menuBar;
        private String id;
    }
    private class PopupMenuTraverser extends MenuSystemTraverser
    {
        protected void handleSubElement(SAXTraverser sub) throws SAXException
        {
            if  (sub instanceof MenuItemTraverser)
            {
                menu.add(((MenuItemTraverser) sub).getMenuItem());
            }
            else if (sub instanceof MenuTraverser)
            {
                menu.add(((MenuTraverser) sub).getMenu());
            }
            else if (sub instanceof SeparatorTraverser)
            {
                JSeparator sep = new javax.swing.JPopupMenu.Separator();
                String location = ((SeparatorTraverser) sub).getLocation();
                if (location != null) sep.putClientProperty(LOCATION_PROPERTY,Integer.valueOf(location));
                menu.add(sep);
            }
        }
        protected void handleElementAttributes(Attributes attrs) throws SAXException
        {
            String klass = attrs.getValue("class");
            menu = createPopupMenu(klass);
            menu.setName(attrs.getValue("name"));
            id = attrs.getValue("id");
        }
        protected void handleEndElement(String name)
        {
            popupMenuHash.put(id,menu);
        }
        private JPopupMenu menu;
        private String id;
    }
    private class MenuTraverser extends MenuSystemTraverser
    {
        protected void handleSubElement(SAXTraverser sub)
        {
            if (sub instanceof MenuItemTraverser)
            {
                menu.add(((MenuItemTraverser) sub).getMenuItem());
            }
            else if (sub instanceof MenuTraverser)
            {
                menu.add(((MenuTraverser) sub).getMenu());
            }
            else if (sub instanceof SeparatorTraverser)
            {
                JSeparator sep = new javax.swing.JPopupMenu.Separator();
                String location = ((SeparatorTraverser) sub).getLocation();
                if (location != null) sep.putClientProperty(LOCATION_PROPERTY,Integer.valueOf(location));
                menu.add(sep);
            }
        }
        protected void handleElementAttributes(Attributes attrs) throws SAXException
        {
            String klass = attrs.getValue("class");
            menu = createMenu(klass);
            menu.setText(attrs.getValue("name"));
            String mnemonic = attrs.getValue("mnemonic");
            if (mnemonic != null) menu.setMnemonic(mnemonic.charAt(0));
            String location = attrs.getValue("location");
            if (location != null) menu.putClientProperty(LOCATION_PROPERTY,Integer.valueOf(location));
        }
        JMenu getMenu()
        {
            return menu;
        }
        private JMenu menu;
    }
    private class ToolBarTraverser extends MenuSystemTraverser
    {
        protected SAXTraverser handleElement(String name, Attributes attrs) throws SAXException
        {
            if (name == "Component")
            {
                return new ToolBarItemTraverser();
            }
            else return super.handleElement(name,attrs);
        }
        protected void handleSubElement(SAXTraverser sub)
        {
            if (sub instanceof ToolBarItemTraverser)
            {
                toolBar.add(((ToolBarItemTraverser) sub).getToolBarItem());
            }
            else if (sub instanceof SeparatorTraverser)
            {
                JSeparator sep = new Separator();
                sep.setOrientation(JSeparator.VERTICAL); // FREEHEP-382
                String location = ((SeparatorTraverser) sub).getLocation();
                if (location != null) sep.putClientProperty(LOCATION_PROPERTY,Integer.valueOf(location));
                toolBar.add(sep);
            }
        }
        protected void handleElementAttributes(Attributes attrs) throws SAXException
        {
            id = attrs.getValue("id");
            String klass = attrs.getValue("class");
            toolBar = createToolBar(klass);
            toolBar.setName(attrs.getValue("name"));
            toolBar.setRollover(toBoolean(attrs.getValue("rollover")));
        }
        protected void handleEndElement(String name)
        {
            toolBarHash.put(id,toolBar);
        }
        private String id;
        private JToolBar toolBar;
    }
    private abstract class ComponentTraverser extends MenuSystemTraverser
    {
        protected void handleSubElement(SAXTraverser sub)
        {
            if (sub instanceof AcceleratorTraverser)
                setAccelerator(((AcceleratorTraverser) sub).getAccelerator());
        }
        abstract void setAccelerator(KeyStroke key);
    }
    private class AcceleratorTraverser extends SAXTraverser
    {
        protected void handleElementAttributes(Attributes attrs) throws SAXException
        {
            String theKey		= attrs.getValue("key");
            // command is equivalent to meta on Mac but Ctrl everywhere else
            String lcOSName = System.getProperty("os.name").toLowerCase();
            boolean isMac = lcOSName.startsWith("mac os x");
            String command = attrs.getValue("command"); // Not present in version 1.0 of DTD
            boolean is10 = command == null;
            boolean theCmdMod   = !is10 && toBoolean(command);
            boolean theCtrlMod  = (!isMac && theCmdMod) || toBoolean(attrs.getValue("ctrl"));
            boolean theAltMod   = toBoolean(attrs.getValue("alt"));
            boolean theShiftMod = toBoolean(attrs.getValue("shift"));
            boolean theMetaMod  = (isMac && theCmdMod) || toBoolean(attrs.getValue("meta"));
            if (is10 && isMac)
            {
                boolean temp = theCtrlMod;
                theCtrlMod = theMetaMod;
                theMetaMod = temp;
            }
            
            try
            {
                key = KeyStroke.getKeyStroke(
                        KeyEvent.class.getField(theKey).getInt(null),
                        ( (theCtrlMod ? Event.CTRL_MASK : 0)
                        + (theAltMod ? Event.ALT_MASK : 0)
                        + (theShiftMod ? Event.SHIFT_MASK : 0)
                        + (theMetaMod ? Event.META_MASK : 0)));
            }
            catch (Exception eh)
            {
                throw new BadXMLException("Illegal accelerator key: "+theKey);
            }
        }
        KeyStroke getAccelerator()
        {
            return key;
        }
        protected KeyStroke key;
    }
    private class MenuItemTraverser extends ComponentTraverser
    {
        protected void handleElementAttributes(Attributes attrs) throws SAXException
        {
            String klass = attrs.getValue("class");
            String name = attrs.getValue("name");
            String type = attrs.getValue("type");
            String command = attrs.getValue("command");
            item = createMenuItem(klass,name,type,command);
            
            String mnemonic = attrs.getValue("mnemonic");
            if (mnemonic != null) item.setMnemonic(mnemonic.charAt(0));
            String location = attrs.getValue("location");
            if (location != null) item.putClientProperty(LOCATION_PROPERTY,Integer.valueOf(location));
            String desc = attrs.getValue("description");
            if (desc == null) desc = name;
            item.setToolTipText(desc);
            String icon = attrs.getValue("icon");
            if (icon != null) item.setIcon(createIcon(icon));
        }
        protected void setAccelerator(KeyStroke key)
        {
            item.setAccelerator(key);
        }
        JMenuItem getMenuItem()
        {
            return item;
        }
        private JMenuItem item;
    }
    private class SeparatorTraverser extends MenuSystemTraverser
    {
        protected void handleElementAttributes(Attributes attrs) throws SAXException
        {
            location = attrs.getValue("location");;
        }
        String getLocation()
        {
            return location;
        }
        private String location;
    }
    private static class UIListener implements PropertyChangeListener
    {
        WeakReference link;
        UIListener(XMLMenuBuilder builder)
        {
            // make sure we dont prevent the XMLMenuBuilder from being GCd
            // by creating a ref from the static UTManager
            link = new WeakReference(builder);
        }
        public void propertyChange(PropertyChangeEvent evt)
        {
            XMLMenuBuilder builder = (XMLMenuBuilder) link.get();
            if (builder == null) UIManager.removePropertyChangeListener(this);
            else if (evt.getPropertyName().equals("lookAndFeel"))
            {
                // change the UI of any cached menus
                Enumeration e = builder.popupMenuHash.elements();
                while (e.hasMoreElements())
                {
                    JComponent c = (JComponent) e.nextElement();
                    SwingUtilities.updateComponentTreeUI(c);
                }
            }
        }
    }
    private class ToolBarItemTraverser extends ComponentTraverser
    {
        protected  void handleElementAttributes(Attributes attrs) throws SAXException
        {
            String klass = attrs.getValue("class");
            String name = attrs.getValue("name");
            String type = attrs.getValue("type");
            String command = attrs.getValue("command");
            item = createToolBarItem(klass,name,type,command);
            
            String mnemonic = attrs.getValue("mnemonic");
            if (mnemonic != null) item.setMnemonic(mnemonic.charAt(0));
            String location = attrs.getValue("location");
            if (location != null) item.putClientProperty(LOCATION_PROPERTY,location);
            String desc = attrs.getValue("description");
            if (desc == null) desc = name;
            item.setToolTipText(desc);
            String icon = attrs.getValue("icon");
            if (icon != null) item.setIcon(createIcon(icon));
        }
        protected void setAccelerator(KeyStroke key)
        {
            //item.setAccelerator(key);
        }
        AbstractButton getToolBarItem()
        {
            return item;
        }
        private AbstractButton item;
    }
    public static class IllegalDefaultClassException extends RuntimeException
    {
        IllegalDefaultClassException(String s)
        {
            super(s);
        }
    }
}
