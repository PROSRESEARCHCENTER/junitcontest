// Coipyright 2002-2007, FreeHEP.
package hep.aida.ref.xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * The class reads ASCII XML documents according to specified DTD and
 * translates all related events into AidaHandler events.
 * <p>Usage sample:
 * <pre>
 *    AidaParser parser = new AidaParser(...);
 *    parser.parse(new InputSource("..."));
 * </pre>
 * <p><b>Warning:</b> the class is machine generated. DO NOT MODIFY</p>
 *
 * @author tonyj
 * @version $Id: AidaParser.java 10722 2007-05-03 18:23:39Z serbo $
 */
public class AidaParser implements ContentHandler
{
   
   private java.lang.StringBuffer buffer;
   private AidaHandler handler;
   private java.util.Stack context;
   private boolean validate;
   private EntityResolver resolver;
   
   /**
    * Creates a parser instance.
    * @param handler handler interface implementation (never <code>null</code>
    * @param resolver SAX entity resolver implementation or <code>null</code>.
    * It is recommended that it could be able to resolve at least the DTD.
    */
   public AidaParser(final AidaHandler handler, final EntityResolver resolver)
   {
      this.handler = handler;
      this.resolver = resolver;
      buffer = new StringBuffer(111);
      context = new java.util.Stack();
   }
   
   /**
    * This SAX interface method is implemented by the parser.
    *
    */
   public final void setDocumentLocator(Locator locator)
   {
   }
   
   /**
    * This SAX interface method is implemented by the parser.
    *
    */
   public final void startDocument() throws SAXException
   {
   }
   
   /**
    * This SAX interface method is implemented by the parser.
    *
    */
   public final void endDocument() throws SAXException
   {
   }
   
   /**
    * This SAX interface method is implemented by the parser.
    *
    */
   public final void startElement(java.lang.String ns, java.lang.String name, java.lang.String qname, Attributes attrs) throws SAXException
   {
      qname.intern();
      dispatch(true);
      context.push(new Object[]{qname, new org.xml.sax.helpers.AttributesImpl(attrs)});
       
      if      (qname == "item"            ) handler.handle_item(attrs);
      else if (qname == "entries1d"       ) handler.start_entries1d(attrs);
      else if (qname == "argument"        ) handler.start_argument(attrs);
      else if (qname == "data1d"          ) handler.start_data1d(attrs);
      else if (qname == "bin1d"           ) handler.handle_bin1d(attrs);
      else if (qname == "entryITuple"     ) handler.start_entryITuple(attrs);
      else if (qname == "column"          ) handler.handle_column(attrs);
      else if (qname == "tuple"           ) handler.start_tuple(attrs);
      else if (qname == "function"        ) handler.start_function(attrs);
      else if (qname == "columns"         ) handler.start_columns(attrs);
      else if (qname == "dataPointSet"    ) handler.start_dataPointSet(attrs);
      else if (qname == "dataPoint"       ) handler.start_dataPoint(attrs);
      else if (qname == "implementation"  ) handler.handle_implementation(attrs);
      else if (qname == "entry1d"         ) handler.handle_entry1d(attrs);
      else if (qname == "cloud1d"         ) handler.start_cloud1d(attrs);
      else if (qname == "row"             ) handler.start_row(attrs);
      else if (qname == "annotation"      ) handler.start_annotation(attrs);
      else if (qname == "entry3d"         ) handler.handle_entry3d(attrs);
      else if (qname == "histogram3d"     ) handler.start_histogram3d(attrs);
      else if (qname == "aida"            ) handler.start_aida(attrs);
      else if (qname == "entry"           ) handler.handle_entry(attrs);
      else if (qname == "data2d"          ) handler.start_data2d(attrs);
      else if (qname == "bin3d"           ) handler.handle_bin3d(attrs);
      else if (qname == "parameter"       ) handler.handle_parameter(attrs);
      else if (qname == "arguments"       ) handler.start_arguments(attrs);
      else if (qname == "parameters"      ) handler.start_parameters(attrs);
      else if (qname == "rows"            ) handler.start_rows(attrs);
      else if (qname == "histogram1d"     ) handler.start_histogram1d(attrs);
      else if (qname == "axis"            ) handler.start_axis(attrs);
      else if (qname == "cloud3d"         ) handler.start_cloud3d(attrs);
      else if (qname == "binBorder"       ) handler.handle_binBorder(attrs);
      else if (qname == "range"           ) handler.handle_range(attrs);
      else if (qname == "measurement"     ) handler.handle_measurement(attrs);
      else if (qname == "data3d"          ) handler.start_data3d(attrs);
      else if (qname == "entry2d"         ) handler.handle_entry2d(attrs);
      else if (qname == "profile1d"       ) handler.start_profile1d(attrs);
      else if (qname == "entries2d"       ) handler.start_entries2d(attrs);
      else if (qname == "entries3d"       ) handler.start_entries3d(attrs);
      else if (qname == "bin2d"           ) handler.handle_bin2d(attrs);
      else if (qname == "statistics"      ) handler.start_statistics(attrs);
      else if (qname == "profile2d"       ) handler.start_profile2d(attrs);
      else if (qname == "cloud2d"         ) handler.start_cloud2d(attrs);
      else if (qname == "statistic"       ) handler.handle_statistic(attrs);
      else if (qname == "histogram2d"     ) handler.start_histogram2d(attrs);
      else if (qname == "fitResult"       ) handler.start_fitResult(attrs);
      else if (qname == "fittedParameter" ) handler.handle_fittedParameter(attrs);
      else if (qname == "covarianceMatrix") handler.start_covarianceMatrix(attrs);
      else if (qname == "matrixElement"   ) handler.handle_matrixElement(attrs);
      else if (qname == "fitConstraint"   ) handler.handle_fitConstraint(attrs);
   }
   
   /**
    * This SAX interface method is implemented by the parser.
    *
    */
   public final void endElement(java.lang.String ns, java.lang.String name, java.lang.String qname) throws SAXException
   {
      qname.intern();
      dispatch(false);
      context.pop();
      
      if      (qname == "entries1d"       ) handler.end_entries1d();
      else if (qname == "argument"        ) handler.end_argument();
      else if (qname == "data1d"          ) handler.end_data1d();
      else if (qname == "entryITuple"     ) handler.end_entryITuple();
      else if (qname == "tuple"           ) handler.end_tuple();
      else if (qname == "function"        ) handler.end_function();
      else if (qname == "columns"         ) handler.end_columns();
      else if (qname == "dataPointSet"    ) handler.end_dataPointSet();
      else if (qname == "dataPoint"       ) handler.end_dataPoint();
      else if (qname == "cloud1d"         ) handler.end_cloud1d();
      else if (qname == "row"             ) handler.end_row();
      else if (qname == "annotation"      ) handler.end_annotation();
      else if (qname == "histogram3d"     ) handler.end_histogram3d();
      else if (qname == "aida"            ) handler.end_aida();
      else if (qname == "data2d"          ) handler.end_data2d();
      else if (qname == "arguments"       ) handler.end_arguments();
      else if (qname == "parameters"      ) handler.end_parameters();
      else if (qname == "rows"            ) handler.end_rows();
      else if (qname == "histogram1d"     ) handler.end_histogram1d();
      else if (qname == "axis"            ) handler.end_axis();
      else if (qname == "cloud3d"         ) handler.end_cloud3d();
      else if (qname == "data3d"          ) handler.end_data3d();
      else if (qname == "profile1d"       ) handler.end_profile1d();
      else if (qname == "entries2d"       ) handler.end_entries2d();
      else if (qname == "entries3d"       ) handler.end_entries3d();
      else if (qname == "statistics"      ) handler.end_statistics();
      else if (qname == "profile2d"       ) handler.end_profile2d();
      else if (qname == "cloud2d"         ) handler.end_cloud2d();
      else if (qname == "histogram2d"     ) handler.end_histogram2d();
      else if (qname == "fitResult"       ) handler.end_fitResult();
      else if (qname == "covarianceMatrix") handler.end_covarianceMatrix();
   }
   
   /**
    * This SAX interface method is implemented by the parser.
    *
    */
   public final void characters(char[] chars, int start, int len) throws SAXException
   {
      buffer.append(chars, start, len);
   }
   
   /**
    * This SAX interface method is implemented by the parser.
    *
    */
   public final void ignorableWhitespace(char[] chars, int start, int len) throws SAXException
   {
   }
   
   /**
    * This SAX interface method is implemented by the parser.
    *
    */
   public final void processingInstruction(java.lang.String target, java.lang.String data) throws SAXException
   {
   }
   
   /**
    * This SAX interface method is implemented by the parser.
    *
    */
   public final void startPrefixMapping(final java.lang.String prefix, final java.lang.String uri) throws SAXException
   {
   }
   
   /**
    * This SAX interface method is implemented by the parser.
    *
    */
   public final void endPrefixMapping(final java.lang.String prefix) throws SAXException
   {
   }
   
   /**
    * This SAX interface method is implemented by the parser.
    *
    */
   public final void skippedEntity(java.lang.String name) throws SAXException
   {
   }
   
   private void dispatch(final boolean fireOnlyIfMixed) throws SAXException
   {
      if (fireOnlyIfMixed && buffer.length() == 0) return; //skip it
      
      Object[] ctx = (Object[]) context.peek();
      String here = (String) ctx[0];
      Attributes attrs = (Attributes) ctx[1];
      if ("codelet" == here)
      {
         if (fireOnlyIfMixed) throw new IllegalStateException("Unexpected characters() event! (Missing DTD?)");
         handler.handle_codelet(buffer.length() == 0 ? null : buffer.toString(), attrs);
      } else
      {
         //do not care
      }
      buffer.delete(0, buffer.length());
   }
   
   /**
    * The recognizer entry method taking an InputSource.
    * @param input InputSource to be parsed.
    * @throws java.io.IOException on I/O error.
    * @throws SAXException propagated exception thrown by a DocumentHandler.
    * @throws javax.xml.parsers.ParserConfigurationException a parser satisfining requested configuration can not be created.
    * @throws javax.xml.parsers.FactoryConfigurationRrror if the implementation can not be instantiated.
    *
    */
   public void parse(final InputSource input) throws SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException
   {
      parse(input, this);
   }
   
   /**
    * The recognizer entry method taking a URL.
    * @param url URL source to be parsed.
    * @throws java.io.IOException on I/O error.
    * @throws SAXException propagated exception thrown by a DocumentHandler.
    * @throws javax.xml.parsers.ParserConfigurationException a parser satisfining requested configuration can not be created.
    * @throws javax.xml.parsers.FactoryConfigurationRrror if the implementation can not be instantiated.
    *
    */
   public void parse(final java.net.URL url) throws SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException
   {
      parse(new InputSource(url.toExternalForm()), this);
   }
   
   /**
    * The recognizer entry method taking an Inputsource.
    * @param input InputSource to be parsed.
    * @throws java.io.IOException on I/O error.
    * @throws SAXException propagated exception thrown by a DocumentHandler.
    * @throws javax.xml.parsers.ParserConfigurationException a parser satisfining requested configuration can not be created.
    * @throws javax.xml.parsers.FactoryConfigurationRrror if the implementation can not be instantiated.
    *
    */
   public static void parse(final InputSource input, final AidaHandler handler) throws SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException
   {
      parse(input, new AidaParser(handler, null));
   }
   
   /**
    * The recognizer entry method taking a URL.
    * @param url URL source to be parsed.
    * @throws java.io.IOException on I/O error.
    * @throws SAXException propagated exception thrown by a DocumentHandler.
    * @throws javax.xml.parsers.ParserConfigurationException a parser satisfining requested configuration can not be created.
    * @throws javax.xml.parsers.FactoryConfigurationRrror if the implementation can not be instantiated.
    *
    */
   public static void parse(final java.net.URL url, final AidaHandler handler) throws SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException
   {
      parse(new InputSource(url.toExternalForm()), handler);
   }
   
   public void setValidate(boolean validate)
   {
      this.validate = validate;
   }
   private static void parse(final InputSource input, final AidaParser recognizer) throws SAXException, javax.xml.parsers.ParserConfigurationException, java.io.IOException
   {
      javax.xml.parsers.SAXParserFactory factory = javax.xml.parsers.SAXParserFactory.newInstance();
      factory.setValidating(recognizer.validate);
      factory.setNamespaceAware(false);  //the code was generated according DTD
      XMLReader parser = factory.newSAXParser().getXMLReader();
      parser.setContentHandler(recognizer);
      parser.setErrorHandler(recognizer.getDefaultErrorHandler());
      if (recognizer.resolver != null) parser.setEntityResolver(recognizer.resolver);
      parser.parse(input);
   }
   
   /**
    * Creates default error handler used by this parser.
    * @return org.xml.sax.ErrorHandler implementation
    *
    */
   protected ErrorHandler getDefaultErrorHandler()
   {
      return new ErrorHandler()
      {
         public void error(SAXParseException ex) throws SAXException
         {
            if (context.isEmpty()) System.err.println("Missing DOCTYPE.");
            throw ex;
         }
         
         public void fatalError(SAXParseException ex) throws SAXException
         {
            throw ex;
         }
         
         public void warning(SAXParseException ex) throws SAXException
         {
            // ignore
         }
      };  
   }
}

