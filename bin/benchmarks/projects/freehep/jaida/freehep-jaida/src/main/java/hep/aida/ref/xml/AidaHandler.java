// Copyright 2002-2007, FreeHEP.
package hep.aida.ref.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Aida handler for ASCII XML
 * 
 * @author tonyj
 * @version $Id: AidaHandler.java 10717 2007-05-03 00:19:12Z serbo $
 */
public interface AidaHandler
{

    // Methods to handls IFitResult
    
    public void start_fitResult(final Attributes meta) throws SAXException;
    
    public void end_fitResult() throws SAXException;
    
    public void start_covarianceMatrix (final Attributes meta) throws SAXException;
    
    public void end_covarianceMatrix () throws SAXException;
    
    public void handle_fittedParameter(final Attributes meta) throws SAXException;

    public void handle_fitConstraint(final Attributes meta) throws SAXException;

    public void handle_matrixElement(final Attributes meta) throws SAXException;
    
   /**
    * An empty element event handling method.
    * @param meta data value or null
    *
    */
   public void handle_item(final Attributes meta) throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_entries1d(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_entries1d() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_argument(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_argument() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_data1d(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_data1d() throws SAXException;
   
   /**
    * An empty element event handling method.
    * @param meta data value or null
    *
    */
   public void handle_bin1d(final Attributes meta) throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_entryITuple(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_entryITuple() throws SAXException;
   
   /**
    * A data element event handling method.
    * @param data value or null
    * @param meta attributes
    *
    */
   public void handle_codelet(final java.lang.String data, final Attributes meta) throws SAXException;
   
   /**
    * An empty element event handling method.
    * @param meta data value or null
    *
    */
   public void handle_column(final Attributes meta) throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_tuple(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_tuple() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_function(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_function() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_columns(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_columns() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_dataPointSet(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_dataPointSet() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_dataPoint(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_dataPoint() throws SAXException;
   
   /**
    * An empty element event handling method.
    * @param meta data value or null
    *
    */
   public void handle_implementation(final Attributes meta) throws SAXException;
   
   /**
    * An empty element event handling method.
    * @param meta data value or null
    *
    */
   public void handle_entry1d(final Attributes meta) throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_cloud1d(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_cloud1d() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_row(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_row() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_annotation(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_annotation() throws SAXException;
   
   /**
    * An empty element event handling method.
    * @param meta data value or null
    *
    */
   public void handle_entry3d(final Attributes meta) throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_histogram3d(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_histogram3d() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_aida(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_aida() throws SAXException;
   
   /**
    * An empty element event handling method.
    * @param meta data value or null
    *
    */
   public void handle_entry(final Attributes meta) throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_data2d(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_data2d() throws SAXException;
   
   /**
    * An empty element event handling method.
    * @param meta data value or null
    *
    */
   public void handle_bin3d(final Attributes meta) throws SAXException;
   
   /**
    * An empty element event handling method.
    * @param meta data value or null
    *
    */
   public void handle_parameter(final Attributes meta) throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_arguments(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_arguments() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_parameters(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_parameters() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_rows(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_rows() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_histogram1d(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_histogram1d() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_axis(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_axis() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_cloud3d(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_cloud3d() throws SAXException;
   
   /**
    * An empty element event handling method.
    * @param meta data value or null
    *
    */
   public void handle_binBorder(final Attributes meta) throws SAXException;
   
   /**
    * An empty element event handling method.
    * @param meta data value or null
    *
    */
   public void handle_range(final Attributes meta) throws SAXException;
   
   /**
    * An empty element event handling method.
    * @param meta data value or null
    *
    */
   public void handle_measurement(final Attributes meta) throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_data3d(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_data3d() throws SAXException;
   
   /**
    * An empty element event handling method.
    * @param meta data value or null
    *
    */
   public void handle_entry2d(final Attributes meta) throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_profile1d(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_profile1d() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_entries2d(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_entries2d() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_entries3d(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_entries3d() throws SAXException;
   
   /**
    * An empty element event handling method.
    * @param meta data value or null
    *
    */
   public void handle_bin2d(final Attributes meta) throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_statistics(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_statistics() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_profile2d(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_profile2d() throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_cloud2d(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_cloud2d() throws SAXException;
   
   /**
    * An empty element event handling method.
    * @param meta data value or null
    *
    */
   public void handle_statistic(final Attributes meta) throws SAXException;
   
   /**
    * A container element start event handling method.
    * @param meta attributes
    *
    */
   public void start_histogram2d(final Attributes meta) throws SAXException;
   
   /**
    * A container element end event handling method.
    *
    */
   public void end_histogram2d() throws SAXException;
   
}

