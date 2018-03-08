package org.freehep.util.template.test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.freehep.util.template.Template;
import org.freehep.util.template.TemplateEngine;
/**
 *
 * @author tonyj
 */
public class Test
{
   public static void main(String[] args) throws IOException
   {
      Template store = new Template();
      
      store.set("title", "Simple Book Template Example");
      store.set("name", "JavaBY");
      
      Template book1 = new Template();
      book1.set("author", "Alexey Popov");
      book1.set("title", "Patterns of using JavaBY Template Engine.");
      book1.set("year", "2002");
      store.append("book", book1);
      
      Template book2 = new Template();
      book2.set("author", "Garmash Viacheslav");
      book2.set("title", "Creating web shop using JavaBY Template Engine.");
      book2.set("year", "2002");
      store.append("book", book2);
      
      TemplateEngine engine = new TemplateEngine();
      engine.addValueProvider(store);
      Reader in = new InputStreamReader(Test.class.getResourceAsStream("test.html"));
      Reader out = engine.filter(in);
      for (;;)
      {
         int c = out.read();
         if (c < 0) break;
         System.out.print((char) c);
      }
   }
   
}
