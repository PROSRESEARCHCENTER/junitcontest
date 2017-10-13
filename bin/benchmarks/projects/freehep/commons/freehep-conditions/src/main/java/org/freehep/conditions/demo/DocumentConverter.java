package org.freehep.conditions.demo;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.freehep.conditions.ConditionsConverter;
import org.freehep.conditions.ConditionsManager;
import org.freehep.conditions.base.ConditionsReader;
import org.freehep.conditions.base.DefaultConditionsManager;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author onoprien
 */
public class DocumentConverter  implements ConditionsConverter {
  
  private final DocumentBuilder docBuilder;

  public DocumentConverter() {
    try {
      docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException x) {
      throw new RuntimeException(x);
    }
  }

  @Override
  public Class getType() {
    return Document.class;
  }

  @Override
  public Object getData(ConditionsManager manager, String name) {
    ConditionsReader conReader = ((DefaultConditionsManager)manager).getConditionsReader(name);
    try (InputStream is = conReader.open(name)) {
      return docBuilder.parse(is);
    } catch (SAXException|IOException x) {
      return null;
    }
  }
  
  
}
