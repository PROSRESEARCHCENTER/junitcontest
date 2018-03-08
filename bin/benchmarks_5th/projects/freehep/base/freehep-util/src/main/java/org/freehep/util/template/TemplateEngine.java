package org.freehep.util.template;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/** A very simple template engine. The template engine takes input and transforms
 * it by looking for tags in the document, and replacing them with values provided
 * by a set of ValueProviders.
 * <p>
 * Example of HTML template:<p>
 * <code> &lt;html&gt;<br>
 * &lt;head&gt;<br>
 * &lt;title&gt;<b>{v:title}</b>&lt;/title&gt; <br>
 * &lt;/head&gt; <br>
 * &lt;body&gt;<br>
 * Welcome <b>{v:name}</b> to Simple Template Example<br>
 * &lt;table&gt;<br>
 * &lt;tr bgcolor=&quot;d0d0d0&quot;&gt;&lt;th&gt;Author&lt;/th&gt;&lt;th&gt;Title&lt;/th&gt;&lt;th&gt;Year&lt;/th&gt;&lt;/tr&gt;<br>
 * <b>&lt;t:book&gt;</b><br>
 * &lt;tr&gt;<br>
 * &lt;td&gt;&lt;a href=&quot;#&quot;&gt;<b>{v:author}</b>&lt;/a&gt;&lt;/td&gt;<br>
 * &lt;td&gt;&lt;a href=&quot;#&quot;&gt;<b>{v:title}</b>&lt;/a&gt;&lt;/td&gt;<br>
 * &lt;td&gt;&lt;a href=&quot;#&quot;&gt;<b>{v:year}</b>&lt;/a&gt;&lt;/td&gt;<br>
 * &lt;/tr&gt;<br>
 * <b>&lt;/t:book&gt;</b><br>
 * &lt;/table&gt;<br>
 * &lt;/body&gt; <br>
 * &lt;/html&gt; </code>
 * <p>
 * The design (although not the code) was inspired by the JByte template engine
 * @link {http://javaby.sourceforge.net/}.
 * @author tonyj
 * @version $Id: TemplateEngine.java 8584 2006-08-10 23:06:37Z duns $
 */
public class TemplateEngine
{
   /** Apply the template to a document.
    * @param in The Reader from which the document will be read.
    * @return A Reader from which the filtered document can be read.
    */
   public Reader filter(Reader in)
   {
      return new TemplateReader(in,providers);
   }

   /** Add a value provider to the set of value providers
    * @param p The ValueProvider to add
    */
   public void addValueProvider(ValueProvider p)
   {
      providers.add(p);
   }
   /** Remove a value provider from the list of value providers.
    * @param p The value provider to remove.
    */
   public void removeValueProvider(ValueProvider p)
   {
      providers.remove(p);
   }
   private List providers = new ArrayList();
   private static class TemplateReader extends FilterReader
   {
      TemplateReader(Reader in, List providers)
      {
         super(in);
         this.providers = providers;
      }

      public int read() throws IOException
      {
         outer: for (;;)
         {
            if (subReader != null)
            {
               int c = subReader.read();
               if (c >= 0) return c;
               subReader = null;
            }

            if (templateIterator != null)
            {
               if (templateIterator.hasNext())
               {
                  ValueProvider vp = (ValueProvider) templateIterator.next();
                  subReader = new TemplateReader(new StringReader(buf.toString()),Collections.singletonList(vp));
                  continue;
               }
               else
               {
                  templateIterator = null;
                  buf.delete(0, buf.length());
               }
            }

            if (state < 0)
            {
               int rc = buf.charAt(buf.length()+state++);
               if (state == 0) buf.delete(0,buf.length());
               return rc;
            }


            int c = in.read();
            if (c<0)
            {
               state = -buf.length();
               if (state == 0) return -1;
               continue;
            }

            if (templateMarker > 0)
            {
               buf.append((char) c);
               if      (state == 0 && c == '<') state++;
               else if (state == 1 && c == '/') state++;
               else if (state > 1 && buf.charAt(state-1) == c) state++;
               else state = 0;
               if (state-1 == templateMarker) // Found end of template
               {
                  String symb = buf.substring(3,templateMarker-1);
                  for (int i=0; i<providers.size(); i++)
                  {
                     ValueProvider vp = (ValueProvider) providers.get(i);
                     List list = vp.getValues(symb);
                     if (list == null) continue;

                     buf.delete(buf.length()-state,buf.length());
                     buf.delete(0, templateMarker);
                     templateIterator = list.iterator();
                     templateMarker = 0;
                     state = 0;
                     continue outer;
                  }
                  buf.delete(0, buf.length());
                  state = 0;
                  templateMarker = 0;
                  continue;
               }
            }
            else if (state == 0 && (c!='{' && c!='<')) return c;
            else if (state == 0)
            {
               buf.append((char) c);
               template = c == '<';
               state++;
            }
            else if (state == 1 && (template ? c=='t' : c=='v'))
            {
               buf.append((char) c);
               state++;
            }
            else if (state == 2 && c==':')
            {
               buf.append((char) c);
               state++;
            }
            else if (state > 2 && (template ? c!='>' : c!='}'))
            {
               state++;
               buf.append((char) c);
            }
            else if (state > 2)
            {
               if (template)
               {
                  // Everything must go into the string buffer until we
                  // find end of template
                  buf.append((char) c);
                  state++;
                  templateMarker = state;
                  state = 0;
               }
               else
               {
                  String symb = buf.substring(3);
                  for (int i=0; i<providers.size(); i++)
                  {
                     ValueProvider vp = (ValueProvider) providers.get(i);
                     String result = vp.getValue(symb);
                     if (result == null) continue;
                     buf.replace(0, buf.length(),result);
                     state = -buf.length();
                     continue outer;
                  }
                  state = 0;
                  buf.delete(0, buf.length());
                  continue;
               }
            }
            else
            {
               buf.append((char)c);
               state = - buf.length();
               continue;
            }
         }
      }

      public int read(char[] buf, int off, int len) throws IOException
      {
         for (int i=0; i<len; i++)
         {
            int b = read();
            if (b < 0) return i == 0 ? -1 : i;
            buf[i+off] = (char) b;
         }
         return len;
      }

      private int state = 0;
      private boolean template;
      private int templateMarker = 0;
      private StringBuffer buf = new StringBuffer();
      private Iterator templateIterator = null;
      private Reader subReader = null;
      private List providers;
   }
}