package org.freehep.application;

import java.awt.Color;
import java.awt.Rectangle;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

/**
 * A set of static methods for operating on a Properties set.
 *
 * @see java.util.Properties
 * @author tonyj
 * @version $Id: PropertyUtilities.java 16186 2014-10-18 21:10:03Z onoprien $
 */
public abstract class PropertyUtilities {

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param def a default in case the property cannot be retrieved
     */
    public static Rectangle getRectangle(Properties prop, final String key, final Rectangle def) {
        try {
            final Rectangle result = new Rectangle();
            result.x = getInteger(prop, key.concat("-x"));
            result.y = getInteger(prop, key.concat("-y"));
            result.width = getInteger(prop, key.concat("-w"));
            result.height = getInteger(prop, key.concat("-h"));
            return result;
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns <tt>Rectangle</tt> stored in the specified properties set with
     * the given key. If the <tt>Rectangle</tt> cannot be retrieved, stores the
     * specified default value into the properties set, and returns it to the
     * caller.
     */
    public static Rectangle touchRectangle(Properties prop, final String key, final Rectangle defaultValue) {
        try {
            final Rectangle result = new Rectangle();
            result.x = getInteger(prop, key.concat("-x"));
            result.y = getInteger(prop, key.concat("-y"));
            result.width = getInteger(prop, key.concat("-w"));
            result.height = getInteger(prop, key.concat("-h"));
            return result;
        } catch (Exception e) {
            setRectangle(prop, key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param rect the value to store
     */
    public static void setRectangle(Properties prop, final String key, final Rectangle rect) {
        if (rect == null) {
            prop.remove(key.concat("-x"));
            prop.remove(key.concat("-y"));
            prop.remove(key.concat("-w"));
            prop.remove(key.concat("-h"));
        } else {
            prop.put(key.concat("-x"), String.valueOf(rect.x));
            prop.put(key.concat("-y"), String.valueOf(rect.y));
            prop.put(key.concat("-w"), String.valueOf(rect.width));
            prop.put(key.concat("-h"), String.valueOf(rect.height));
        }
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param def a default in case the property cannot be retrieved
     */
    public static Color getColor(Properties prop, final String key, final java.awt.Color def) {
        try {
            return new Color(getInteger(prop, key.concat("-r")), getInteger(prop, key.concat("-g")),
                    getInteger(prop, key.concat("-b")));
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns <tt>Color</tt> stored in the specified properties set with the
     * given key. If the <tt>Color</tt> cannot be retrieved, stores the
     * specified default value into the properties set, and returns it to the
     * caller.
     */
    public static Color touchColor(Properties prop, final String key, final java.awt.Color defaultValue) {
        try {
            return new Color(getInteger(prop, key.concat("-r")), getInteger(prop, key.concat("-g")),
                    getInteger(prop, key.concat("-b")));
        } catch (Exception e) {
            setColor(prop, key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param c the value to store
     */
    public static void setColor(Properties prop, final String key, final Color c) {
        if (c == null) {
            prop.remove(key.concat("-r"));
            prop.remove(key.concat("-g"));
            prop.remove(key.concat("-b"));
        } else {
            prop.put(key.concat("-r"), String.valueOf(c.getRed()));
            prop.put(key.concat("-g"), String.valueOf(c.getGreen()));
            prop.put(key.concat("-b"), String.valueOf(c.getBlue()));
        }
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param def a default in case the property cannot be retrieved
     */
    public static Collection getStringCollection(Properties prop, final String key, final Collection def) {
        try {
            // NOTE Compatible with StringArrays
            final int length = getInteger(prop, key + "-length");
            final Collection result = new ArrayList(length);
            for (int i = 0; i < length; i++) {
                String value = prop.getProperty(key + "-" + i);
                if (value != null) {
                    result.add(value);
                }
            }
            return result;
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns String collection stored in the specified properties set with the
     * given key. If the collection cannot be retrieved, stores the specified
     * default value into the properties set, and returns it to the caller.
     */
    public static Collection touchStringCollection(Properties prop, final String key, final Collection defaultValue) {
        try {
            final int length = getInteger(prop, key + "-length");
            final Collection result = new ArrayList(length);
            for (int i = 0; i < length; i++) {
                String value = prop.getProperty(key + "-" + i);
                if (value != null) {
                    result.add(value);
                }
            }
            return result;
        } catch (Exception e) {
            setStringCollection(prop, key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param sa the value to store
     */
    public static void setStringCollection(Properties prop, final String key, Collection sa) {
        // remove previous setting
        try {
            // NOTE Compatible with StringArrays
            final int length = getInteger(prop, key + "-length");
            for (int i = 0; i < length; i++) {
                prop.remove(key + "-" + i);
            }
            prop.remove(key + "-length");
        } catch (Exception e) {
        }

        if (sa != null) {
            prop.put(key + "-length", String.valueOf(sa.size()));
            int k = 0;
            for (Iterator i = sa.iterator(); i.hasNext();) {
                prop.put(key + "-" + k, (String) i.next());
                k++;
            }
        }
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param def a default in case the property cannot be retrieved
     */
    public static String[] getStringArray(Properties prop, final String key, final String[] def) {
        try {
            final String[] result = new String[getInteger(prop, key + "-length")];
            for (int i = 0; i < result.length; i++) {
                result[i] = prop.getProperty(key + "-" + i);
            }
            return result;
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns <tt>String</tt> array stored in the specified properties set with
     * the given key. If the array cannot be retrieved for any reason, stores
     * the specified default value into the properties set, and returns it to
     * the caller.
     */
    public static String[] touchStringArray(Properties prop, final String key, final String[] defaultValue) {
        try {
            final String[] result = new String[getInteger(prop, key + "-length")];
            for (int i = 0; i < result.length; i++) {
                result[i] = prop.getProperty(key + "-" + i);
            }
            return result;
        } catch (Exception e) {
            setStringArray(prop, key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param sa the value to store
     */
    public static void setStringArray(Properties prop, final String key, String[] sa) {
        setStringCollection(prop, key, sa == null ? null : Arrays.asList(sa));
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param defaultValue a default in case the property cannot be retrieved
     */
    public static String getString(Properties prop, final String key, final String def) {
        try {
            final String s = prop.getProperty(key);
            return s == null ? def : s;
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns <tt>String</tt> stored in the specified properties set with the
     * given key. If the string cannot be retrieved for any reason, stores the
     * specified default value into the properties set, and returns it to the
     * caller.
     */
    public static String touchString(Properties prop, final String key, final String defaultValue) {
        final String s = prop.getProperty(key);
        if (s == null) {
            setString(prop, key, defaultValue);
            return defaultValue;
        } else {
            return s;
        }
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param s the value to store
     */
    public static void setString(Properties prop, final String key, String s) {
        if (s == null) {
            prop.remove(key);
        } else {
            prop.put(key, s);
        }
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param def a default in case the property cannot be retrieved
     */
    public static boolean getBoolean(Properties prop, final String key, final boolean def) {
        final String value = prop.getProperty(key);
        return value == null ? def : value.equalsIgnoreCase("true");
    }

    /**
     * Returns boolean value stored in the specified properties set with the
     * given key. If the value cannot be retrieved, stores the specified default
     * value into the properties set, and returns it to the caller.
     */
    public static boolean touchBoolean(Properties prop, final String key, final boolean defaultValue) {
        final String value = prop.getProperty(key);
        if (value == null) {
            setBoolean(prop, key, defaultValue);
            return defaultValue;
        } else {
            return value.equalsIgnoreCase("true");
        }
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param value the value to store
     */
    public static void setBoolean(Properties prop, final String key, final Boolean value) {
        if (value == null) {
            prop.remove(key);
        } else {
           prop.put(key, value.toString());
        }
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param value the value to store
     */
    public static void setBoolean(Properties prop, final String key, final boolean value) {
        prop.put(key, String.valueOf(value));
    }

    /**
     * @exception NumberFormatException if the property retrieved cannot be
     * converted to <code>int</code>
     * @param prop The Properties set
     * @param key the key used to store this property
     */
    public static int getInteger(Properties prop, final String key) throws NumberFormatException {
        return Integer.parseInt(prop.getProperty(key));
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param def a default in case the property cannot be retrieved
     */
    public static int getInteger(Properties prop, final String key, final int def) {
        try {
            final String s = prop.getProperty(key);
            return s == null ? def : Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns <tt>int</tt> value stored in the specified properties set with
     * the given key. If the value cannot be retrieved for any reason, stores
     * the specified default value into the properties set, and returns it to
     * the caller.
     */
    public static int touchInteger(Properties prop, final String key, final int defaultValue) {
        try {
            final String s = prop.getProperty(key);
            return Integer.parseInt(s);
        } catch (Exception e) {
            setInteger(prop, key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Stores integer value.
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param i the value to store
     */
    public static void setInteger(Properties prop, final String key, final int i) {
        prop.put(key, String.valueOf(i));
    }

    /**
     * Stores integer value.
     * @param prop The Properties set
     * @param key The key used to store this property
     * @param i The value to store (if <tt>null</tt>, the property with the specified key is removed).
     */
    public static void setInteger(Properties prop, final String key, final Integer value) {
        if (value == null) {
            prop.remove(key);
        } else {
           prop.put(key, value.toString());
        }
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param def a default in case the property cannot be retrieved
     */
    public static long getLong(Properties prop, final String key, final long def) {
        try {
            final String s = prop.getProperty(key);
            return s == null ? def : Long.parseLong(s);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns <tt>long</tt> value stored in the specified properties set with
     * the given key. If the value cannot be retrieved for any reason, stores
     * the specified default value into the properties set, and returns it to
     * the caller.
     */
    public static long touchLong(Properties prop, final String key, final long defaultValue) {
        try {
            final String s = prop.getProperty(key);
            return Long.parseLong(s);
        } catch (Exception e) {
            setLong(prop, key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Stores long value.
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param i the value to store
     */
    public static void setLong(Properties prop, final String key, final long i) {
        prop.put(key, String.valueOf(i));
    }

    /**
     * Stores long value.
     * @param prop The Properties set
     * @param key The key used to store this property
     * @param i The value to store (if <tt>null</tt>, the property with the specified key is removed).
     */
    public static void setLong(Properties prop, final String key, final Long value) {
        if (value == null) {
            prop.remove(key);
        } else {
           prop.put(key, value.toString());
        }
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param def a default in case the property cannot be retrieved
     */
    public static float getFloat(Properties prop, final String key, final float def) {
        try {
            final String s = prop.getProperty(key);
            return s == null ? def : Float.parseFloat(s);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns <tt>float</tt> value stored in the specified properties set with
     * the given key. If the value cannot be retrieved for any reason, stores
     * the specified default value into the properties set, and returns it to
     * the caller.
     */
    public static float touchFloat(Properties prop, final String key, final float defaultValue) {
        try {
            final String s = prop.getProperty(key);
            return Float.parseFloat(s);
        } catch (Exception e) {
            setFloat(prop, key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Stores float value.
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param f the value to store
     */
    public static void setFloat(Properties prop, final String key, final float f) {
        prop.put(key, String.valueOf(f));
    }

    /**
     * Stores float value.
     * @param prop The Properties set
     * @param key The key used to store this property
     * @param i The value to store (if <tt>null</tt>, the property with the specified key is removed).
     */
    public static void setFloat(Properties prop, final String key, final Float value) {
        if (value == null) {
            prop.remove(key);
        } else {
           prop.put(key, value.toString());
        }
    }

    /**
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param def a default in case the property cannot be retrieved
     */
    public static double getDouble(Properties prop, final String key, final double def) {
        try {
            final String s = prop.getProperty(key);
            return s == null ? def : Double.parseDouble(s);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Returns <tt>double</tt> value stored in the specified properties set with
     * the given key. If the value cannot be retrieved for any reason, stores
     * the specified default value into the properties set, and returns it to
     * the caller.
     */
    public static double touchDouble(Properties prop, final String key, final double defaultValue) {
        try {
            final String s = prop.getProperty(key);
            return Double.parseDouble(s);
        } catch (Exception e) {
            setDouble(prop, key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Stores double value.
     * @param prop The Properties set
     * @param key the key used to store this property
     * @param f the value to store
     */
    public static void setDouble(Properties prop, final String key, final double f) {
        prop.put(key, String.valueOf(f));
    }

    /**
     * Stores double value.
     * @param prop The Properties set
     * @param key The key used to store this property
     * @param i The value to store (if <tt>null</tt>, the property with the specified key is removed).
     */
    public static void setDouble(Properties prop, final String key, final Double value) {
        if (value == null) {
            prop.remove(key);
        } else {
           prop.put(key, value.toString());
        }
    }

    /**
     * Load a URL from a properties file. If the URL begins with / it is taken
     * to be a system resource (i.e. on the classpath).
     */
    public static URL getURL(Properties prop, final String key, final URL def) {
        String p = prop.getProperty(key);
        if (p == null) {
            return def;
        }
        try {
            return new URL(p);
        } catch (java.net.MalformedURLException x) {
            URL url = Application.getApplication().getClass().getResource(p);
            return url == null ? def : url;
        }
    }

    /**
     * Returns <tt>URL</tt> value stored in the specified properties set with
     * the given key. If the value cannot be retrieved for any reason, stores
     * the specified default value into the properties set, and returns it to
     * the caller.
     */
    public static URL touchURL(Properties prop, final String key, final URL defaultValue) {
        try {
            String p = prop.getProperty(key);
            return new URL(p);
        } catch (Exception x) {
            setURL(prop, key, defaultValue);
            return defaultValue;
        }
    }

    public static void setURL(Properties prop, final String key, final URL url) {
        if (url == null) {
            prop.remove(key);
        } else {
            prop.put(key, url.toString());
        }
    }

    /**
     * Translates a string by substituting tokens of the form {name} to the
     * value of property name in the properties set.
     *
     * @param prop The properties set
     * @param in The string to be translated
     * @return The resulting string.
     */
    public static String translate(Properties prop, String in) {
        if (in == null) {
            return null;
        }
        StringBuffer out = null; // avoid creating if unnecessary
        int l = in.length();
        int pos = 0;
        while (pos < l) {
            int start = in.indexOf('{', pos);
            if (start < 0) {
                break;
            }
            int end = in.indexOf('}', start);
            if (end < 0 || end - start < 2) {
                break;
            }

            if (out == null) {
                out = new StringBuffer(in.substring(0, start));
            } else {
                out.append(in.substring(pos, start));
            }

            String value = prop.getProperty(in.substring(start + 1, end));
            if (value != null) {
                out.append(value);
            }
            pos = end + 1;
        }
        if (out == null) {
            return in;
        }
        if (pos < l) {
            out.append(in.substring(pos));
        }
        return out.toString();
    }

    /**
     * Creates a TableModel from a property set
     */
    public static class PropertyTable extends AbstractTableModel {

        private ArrayList<String> list;
        private Properties properties;

        PropertyTable(Properties prop) {
            Set<Object> keys = prop.keySet();
            list = new ArrayList(keys.size());
            for (Object o : keys) {
                list.add(o.toString());
            }
            Collections.sort(list);
            properties = prop;
            
//            list = new ArrayList(prop.entrySet());
//            Collections.sort(list, new Comparator() {
//                @Override
//                public int compare(Object o1, Object o2) {
//                    Map.Entry entry1 = (Map.Entry) o1;
//                    Map.Entry entry2 = (Map.Entry) o2;
//                    String name1 = entry1.getKey().toString();
//                    String name2 = entry2.getKey().toString();
//                    return name1.compareTo(name2);
//                }
//            });
        }

        PropertyTable(Properties prop, Properties exclude) {
            Set<String> names = prop.stringPropertyNames();
            if (exclude != null) {
                names.removeAll(exclude.stringPropertyNames());
            }
            list = new ArrayList(names);
            Collections.sort(list);
            properties = prop;
        }

        @Override
        public int getRowCount() {
            return list.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int row, int column) {
            if (column == 0) {
                return list.get(row);
            } else {
                return properties.getProperty(list.get(row), "");
            }
//            Map.Entry entry = (Map.Entry) list.get(row);
//            return column == 0 ? entry.getKey() : entry.getValue();
        }

        @Override
        public String getColumnName(int col) {
            return col == 0 ? "Property" : "Value";
        }
    }

    /**
     * A Properties object whose values and defaults are automatically
     * translated if they contain {prop} tokens.
     *
     * @see #translate(Properties,String)
     */
    // TODO: Protect against recursive translation?
    public static class TranslatedProperties extends Properties {

        public TranslatedProperties() {
            super();
        }

        public TranslatedProperties(Properties def) {
            super(def);
        }

        @Override
        public String getProperty(String key) {
            return PropertyUtilities.translate(this, super.getProperty(key));
        }

        @Override
        public String getProperty(String key, String def) {
            return PropertyUtilities.translate(this, super.getProperty(key, def));
        }
    }
}