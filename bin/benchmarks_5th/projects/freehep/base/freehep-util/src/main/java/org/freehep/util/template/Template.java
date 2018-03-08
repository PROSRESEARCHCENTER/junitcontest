package org.freehep.util.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A simple implementation of ValueProvider.
 * <pre>      Template store = new Template();
 *
 *      store.set("title", "Simple Book Template Example");
 *      store.set("name", "JavaBY");
 *
 *      Template book1 = new Template();
 *      book1.set("author", "Alexey Popov");
 *      book1.set("title", "Patterns of using JavaBY Template Engine.");
 *      book1.set("year", "2002");
 *      store.append("book", book1);</PRE>
 *
 *      Template book2 = new Template();
 *      book2.set("author", "Garmash Viacheslav");
 *      book2.set("title", "Creating web shop using JavaBY Template Engine.");
 *      book2.set("year", "2002");
 *      store.append("book", book2);
 *
 *      TemplateEngine engine = new TemplateEngine();
 *      engine.addValueProvider(store);
 *      Reader in = new InputStreamReader(Test.class.getResourceAsStream("test.html"));
 *      Reader out = engine.filter(in);</pre>
 * @author tonyj
 * @version $Id: Template.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Template implements ValueProvider
{
   private Map values = new HashMap();
   private Map templates = new HashMap();
   /** Create an empty template. */   
   public Template()
   {
      
   }
   public String getValue(String name)
   {
      return (String) values.get(name);
   }
   public List getValues(String name)
   {
      return (List) templates.get(name);
   }
   /** Set a value.
    * @param name The name of the value
    * @param value The value
    */   
   public void set(String name, String value)
   {
      values.put(name,value);
   }
   /** Append a template to this template
    * @param name The name of the template to add.
    * @param template The template.
    */   
   public void append(String name, Template template)
   {
      List l = (List) templates.get(name);
      if (l == null)
      {
         l = new ArrayList();
         templates.put(name,l);
      }
      l.add(template);
   }
}
