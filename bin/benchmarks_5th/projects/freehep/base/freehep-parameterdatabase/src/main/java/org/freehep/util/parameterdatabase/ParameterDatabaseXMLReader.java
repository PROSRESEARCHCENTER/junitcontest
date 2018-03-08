package org.freehep.util.parameterdatabase;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class ParameterDatabaseXMLReader extends DefaultHandler {

    private ParameterDatabase database;

    private Hashtable instanceMap;

    private XMLReader xml;

    public ParameterDatabaseXMLReader() throws SAXException,
            ParserConfigurationException {

        xml = SAXParserFactory.newInstance().newSAXParser().getXMLReader();

        xml.setContentHandler(this);
        xml.setDTDHandler(this);
        xml.setErrorHandler(this);
        xml.setEntityResolver(this);
    }

    public void readParameters(ParameterDatabase database, Reader reader,
            Hashtable instanceMap) throws SAXException, IOException {

        if (database == null)
            throw new IllegalArgumentException();
        this.database = database;
        this.instanceMap = instanceMap;

        InputSource is = new InputSource(reader);
        xml.parse(is);
    }

    public void startElement(String namespace, String tag, String qName,
            Attributes atts) throws SAXException {

        if (tag.equals("ClassParameter")) {

            String className = atts.getValue("class");
            String parameterName = atts.getValue("name");
            String valueAsString = atts.getValue("value");
            String valueTypeAsString = atts.getValue("type");

            if (className != null && parameterName != null
                    && valueAsString != null && valueTypeAsString != null) {

                // Setup the call parameters for a constructor which takes
                // a String as the only argument.
                Object[] parameters = new Object[1];
                parameters[0] = valueAsString;
                Class[] parameterTypes = new Class[1];
                parameterTypes[0] = String.class;

                try {

                    // Construct the new value Object based on the given
                    // String value.
                    Class valueClass = Class.forName(valueTypeAsString);
                    Constructor constructor = valueClass
                            .getConstructor(parameterTypes);
                    Object newValue = constructor.newInstance(parameters);

                    // Get the class this parameter belongs to.
                    Class baseClass = Class.forName(className);
                    ClassIterator cIterator = new ClassIterator(baseClass);

                    // We set the parameter listener to null here. It is
                    // important that the individual classes still
                    // register their parameters so that they can become
                    // true listeners of these parameters.
                    //
                    // NOTE: this will overwrite the current values in the
                    // database. This is done because the views must be
                    // restored first (along with their default values). Here
                    // we overwrite those default values with the saved ones.
                    database.addParameter(parameterName, newValue, cIterator,
                            null, true);

                } catch (NoSuchMethodException nsme) {
                    nsme.printStackTrace();
                } catch (InstantiationException ie) {
                    ie.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (tag.equals("InstanceParameter")) {

            Integer id = new Integer(atts.getValue("id"));
            String parameterName = atts.getValue("name");
            String valueAsString = atts.getValue("value");
            String valueTypeAsString = atts.getValue("type");

            Object instance = instanceMap.get(id);

            if (id != null && parameterName != null && valueAsString != null
                    && valueTypeAsString != null) {

                // Setup the call parameters for a constructor which takes
                // a String as the only argument.
                Object[] parameters = new Object[1];
                parameters[0] = valueAsString;
                Class[] parameterTypes = new Class[1];
                parameterTypes[0] = String.class;

                try {

                    // Construct the new value Object based on the given
                    // String value.
                    Class valueClass = Class.forName(valueTypeAsString);
                    Constructor constructor = valueClass
                            .getConstructor(parameterTypes);
                    Object newValue = constructor.newInstance(parameters);

                    // Get the class this parameter belongs to.
                    ClassIterator cIterator = new ClassIterator(instance);

                    if (instance instanceof PropertyChangeListener) {
                        PropertyChangeListener pcl = (PropertyChangeListener) instance;
                        database.addParameter(parameterName, newValue,
                                cIterator, pcl, true);
                    } else {
                        database.addParameter(parameterName, newValue,
                                cIterator, null, true);
                    }

                } catch (NoSuchMethodException nsme) {
                    nsme.printStackTrace();
                } catch (InstantiationException ie) {
                    ie.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
