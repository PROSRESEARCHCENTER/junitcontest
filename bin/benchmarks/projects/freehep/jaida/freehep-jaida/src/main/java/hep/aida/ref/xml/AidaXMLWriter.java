// Copyright FreeHEP, 2001-2007
package hep.aida.ref.xml;

import hep.aida.IAnnotation;
import hep.aida.IAxis;
import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.ICloud3D;
import hep.aida.IConstants;
import hep.aida.IDataPoint;
import hep.aida.IDataPointSet;
import hep.aida.IFitResult;
import hep.aida.IFitParameterSettings;
import hep.aida.IFunction;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.IManagedObject;
import hep.aida.IMeasurement;
import hep.aida.IModelFunction;
import hep.aida.IProfile1D;
import hep.aida.IProfile2D;
import hep.aida.IRangeSet;
import hep.aida.ITree;
import hep.aida.ITuple;
import hep.aida.ref.AidaUtils;
import hep.aida.ref.Annotation;
import hep.aida.ref.histogram.Cloud;
import hep.aida.ref.histogram.Cloud1D;
import hep.aida.ref.histogram.Cloud2D;
import hep.aida.ref.histogram.Cloud3D;
import hep.aida.ref.histogram.Histogram1D;
import hep.aida.ref.histogram.Histogram2D;
import hep.aida.ref.histogram.Histogram3D;
import hep.aida.ref.histogram.VariableAxis;
import hep.aida.ref.tuple.AbstractTuple;
import hep.aida.ref.tuple.Tuple;
import hep.aida.ref.xml.ascii.AidaAsciiXMLWriter;
import hep.aida.ref.xml.binary.AidaWBXML;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Writer;

import org.freehep.wbxml.WBXMLTagWriter;
import org.freehep.wbxml.WBXMLWriter;

/**
 * Convert AIDA objects to XML (binary or ASCII).
 * 
 * @author tonyj
 * @author Mark Donszelmann
 * @version $Id: AidaXMLWriter.java 13402 2007-11-02 21:19:21Z serbo $
 */
public class AidaXMLWriter {
	protected String[] skip;
	protected WBXMLTagWriter xml;

	private static final String X = "x";
	private static final String Y = "y";
	private static final String Z = "z";

	public AidaXMLWriter(Writer writer) throws IOException {
		this(writer, null);
	}

	AidaXMLWriter(Writer writer, String[] skip) throws IOException {
		xml = new AidaAsciiXMLWriter(writer);
		xml.openDoc("1.0", "ISO-8859-1", false);
		init(skip);
	}

	AidaXMLWriter(DataOutputStream dos) throws IOException {
		this(dos, null);
	}

	AidaXMLWriter(DataOutputStream dos, String[] skip) throws IOException {
		xml = new WBXMLWriter(dos, AidaWBXML.attributes.length - 1);
		xml.openDoc("BinaryAIDA/1.0", "UTF-8", false);
		init(skip);
	}

	private void init(String[] skip) throws IOException {
		this.skip = skip;

		xml.referToDTD("aida", "http://aida.freehep.org/schemas/"
				+ IConstants.AIDA_VERSION + "/aida.dtd");
		xml.setAttribute(AidaWBXML.VERSION,
				IConstants.AIDA_VERSION);
		xml.openTag(AidaWBXML.AIDA);

		xml.setAttribute(AidaWBXML.PACKAGE, "FreeHEP");
		xml.setAttribute(AidaWBXML.VERSION, "1.1");
		xml.printTag(AidaWBXML.IMPLEMENTATION);
	}

	public void close() throws IOException {
		xml.closeTag();
		xml.close();
	}

	void toXML(ITree tree) {
		String[] objNames = tree.listObjectNames("/", true);
		String[] objTypes = null;
		if (skip != null && skip.length > 0)
			objTypes = tree.listObjectTypes("/", true);

		for (int i = 0; i < objNames.length; i++) {
			// Skip directories
			if (objNames[i].endsWith("/") && !objNames[i].endsWith("\\/")) {
				continue;
			}
                        String dirName = AidaUtils.parseDirName(objNames[i]);
			// Skip writing some types to file
			if (skip != null && skip.length > 0
					&& AidaUtils.findInArray(objTypes[i], skip) >= 0)
				continue;

                        IManagedObject obj = tree.find(objNames[i]);
                        if (obj instanceof AidaObjectProxy.ObjectProvider) 
                            obj = ((AidaObjectProxy.ObjectProvider) obj).getManagedObject();
			toXML(obj, dirName);
		}
	}

	public void toXML(IManagedObject o, String path) {
		try {
			// Skip writing some types to file
			if (o != null && skip != null && skip.length > 0
					&& AidaUtils.findInArray(o.type(), skip) >= 0)
				return;

                        if (o instanceof AidaObjectProxy.ObjectProvider) 
                            o = ((AidaObjectProxy.ObjectProvider) o).getManagedObject();

                        if (o instanceof IHistogram1D) {
				toXML((IHistogram1D) o, path);
			} else if (o instanceof IHistogram2D) {
				toXML((IHistogram2D) o, path);
			} else if (o instanceof IHistogram3D) {
				toXML((IHistogram3D) o, path);
			} else if (o instanceof ICloud1D) {
				toXML((ICloud1D) o, path);
			} else if (o instanceof ICloud2D) {
				toXML((ICloud2D) o, path);
			} else if (o instanceof ICloud3D) {
				toXML((ICloud3D) o, path);
			} else if (o instanceof ITuple) {
				toXML((ITuple) o, path);
			} else if (o instanceof IDataPointSet) {
				toXML((IDataPointSet) o, path);
			} else if (o instanceof IProfile1D) {
				toXML((IProfile1D) o, path);
			} else if (o instanceof IProfile2D) {
				toXML((IProfile2D) o, path);
			} else if (o instanceof IFunction) {
				toXML((IFunction) o, path);
			}

			// otherwise silently ignore
		} catch (IOException e) {
			// FIXME: ignored
		}
	}

        // Beginning of the IFitResult 
        
	public void toXML(IFitResult f, String path) throws IOException {
		if ((path != null) && !path.equals("")) {
			xml.setAttribute(AidaWBXML.PATH, path);
		}
                xml.setAttribute(AidaWBXML.FIT_DIMENSION, f.fittedParameterNames().length);
                xml.setAttribute(AidaWBXML.FIT_ENGINE_NAME, f.engineName());
                xml.setAttribute(AidaWBXML.FIT_METHOD_NAME, f.fitMethodName());
                xml.setAttribute(AidaWBXML.FIT_IS_VALID, f.isValid());
                xml.setAttribute(AidaWBXML.FIT_QUALITY, f.quality());
                xml.setAttribute(AidaWBXML.FIT_STATUS, f.fitStatus());
                xml.setAttribute(AidaWBXML.FIT_DEGREES_OF_FREEDOM, f.ndf());
                xml.setAttribute(AidaWBXML.FIT_DATA_DESCRIPTION, f.dataDescription());
                
                xml.openTag(AidaWBXML.FIT_RESULT);
                if (f.fittedFunction() != null) toXML(f.fittedFunction(), path);
                
                String[] con = f.constraints();
                if (con != null && con.length > 0) writeFitConstrains(con);
                
                String[] names = f.fittedParameterNames();
                if (names != null && names.length > 0) {
                    writeFittedParameters(f);
                    writeFitCovarianceMatrix(f);
                }
                
                xml.closeTag();
        }

        private void writeFittedParameters(IFitResult f) throws IOException {
            String[] names = f.fittedParameterNames();
            double[] vals = f.fittedParameters();
            double[] ep = f.errorsPlus();
            double[] em = f.errorsMinus();
            for (int i=0; i<names.length; i++) {
		//xml.openTag(AidaWBXML.FITTED_PARAMETER);
		xml.setAttribute(AidaWBXML.FITTED_PARAMETER_NAME, names[i]);
		xml.setAttribute(AidaWBXML.FITTED_PARAMETER_VALUE, vals[i]);
		xml.setAttribute(AidaWBXML.FITTED_PARAMETER_ERROR_PLUS, ep[i]);
		if (ep[i] != em[i]) xml.setAttribute(AidaWBXML.FITTED_PARAMETER_ERROR_MINUS, em[i]);

                IFitParameterSettings fps = f.fitParameterSettings(names[i]);
		xml.setAttribute(AidaWBXML.FITTED_PARAMETER_IS_FIXED, fps.isFixed());
                double stepSize = fps.stepSize();
                if (!Double.isNaN(stepSize))
                    xml.setAttribute(AidaWBXML.FITTED_PARAMETER_STEP_SIZE, stepSize);
                if (fps.isBound()) {
                    if (!Double.isNaN(fps.lowerBound()))
                        xml.setAttribute(AidaWBXML.FITTED_PARAMETER_LOWER_BOUND, fps.lowerBound());
                    if (!Double.isNaN(fps.upperBound()))
                        xml.setAttribute(AidaWBXML.FITTED_PARAMETER_UPPER_BOUND, fps.upperBound());
                }
                       
                xml.printTag(AidaWBXML.FITTED_PARAMETER);
		//xml.closeTag();                
            }
        }
        private void writeFitCovarianceMatrix(IFitResult f) throws IOException {
            String[] names = f.fittedParameterNames();
            xml.openTag(AidaWBXML.COVARIANCE_MATRIX);
            for (int i=0; i<names.length; i++) {
                for (int j=0; j<names.length; j++) {
                    xml.setAttribute(AidaWBXML.MATRIX_ELEMENT_ROW, i);
                    xml.setAttribute(AidaWBXML.MATRIX_ELEMENT_COLUMN, j);
                    xml.setAttribute(AidaWBXML.MATRIX_ELEMENT_VALUE, f.covMatrixElement(i, j));
                    xml.printTag(AidaWBXML.MATRIX_ELEMENT);
                }
            }
            xml.closeTag();
        }
        private void writeFitConstrains(String[] con) throws IOException {
            if (con == null) return;
            for (int i=0; i<con.length; i++) {
                if (con[i] == null || con[i].trim().equals("")) continue;
		xml.setAttribute(AidaWBXML.FIT_CONSTRAINT_VALUE, con[i]);
		xml.printTag(AidaWBXML.FIT_CONSTRAINT);
            }
        }
        // End of IFitResult 
        
        
        private void toXML(IHistogram1D h, String path) throws IOException {
		String name = ((IManagedObject) h).name();
		xml.setAttribute(AidaWBXML.NAME, name);

		String title = h.title();
		if ((title != null) && (title != null) && !title.equals("")) {
			xml.setAttribute(AidaWBXML.TITLE, title);
		}
		if ((path != null) && !path.equals("")) {
			xml.setAttribute(AidaWBXML.PATH, path);
		}

		xml.openTag(AidaWBXML.HISTOGRAM_1D);

		IAnnotation annotation = h.annotation();
		if (annotation != null) {
			toXML(annotation);
		}

		writeHistogramAxis(h.axis(), X);

		xml.setAttribute(AidaWBXML.ENTRIES, h.entries());
		xml.openTag(AidaWBXML.STATISTICS);
		xml.setAttribute(AidaWBXML.DIRECTION, X);
		xml.setAttribute(AidaWBXML.MEAN, h.mean());
		xml.setAttribute(AidaWBXML.RMS, h.rms());
		xml.printTag(AidaWBXML.STATISTIC);
		xml.closeTag();

		xml.openTag(AidaWBXML.DATA_1D);

		int bins = h.axis().bins();
		for (int i = -2; i < bins; i++) {
			double height = h.binHeight(i);
			double error = h.binError(i);
			int entries = h.binEntries(i);
			if ((height != 0) && (error != 0)) {
				xml.setAttribute(AidaWBXML.BIN_NUM, i);
				if (h instanceof Histogram1D) {
					xml.setAttribute(AidaWBXML.WEIGHTED_MEAN,
							((Histogram1D) h).binMean(i));
					xml.setAttribute(AidaWBXML.WEIGHTED_RMS,
							((Histogram1D) h).binRms(i));
				}

				if (height != entries) {
					xml.setAttribute(AidaWBXML.HEIGHT, height);
					xml.setAttribute(AidaWBXML.ERROR, error);
				}

				// setAttribute(AidaWBXML.ERROR2,"fix me");
				xml.setAttribute(AidaWBXML.ENTRIES, entries);
				xml.printTag(AidaWBXML.BIN_1D);
			}
		}
		xml.closeTag();
		xml.closeTag();
	}

	private void toXML(IHistogram2D h, String path) throws IOException {
		String name = ((IManagedObject) h).name();
		xml.setAttribute(AidaWBXML.NAME, name);

		String title = h.title();
		if ((title != null) && !title.equals("")) {
			xml.setAttribute(AidaWBXML.TITLE, title);
		}
		if ((path != null) && !path.equals("")) {
			xml.setAttribute(AidaWBXML.PATH, path);
		}
		xml.openTag(AidaWBXML.HISTOGRAM_2D);

		IAnnotation annotation = h.annotation();
		if (annotation != null) {
			toXML(annotation);
		}

		writeHistogramAxis(h.xAxis(), X);
		writeHistogramAxis(h.yAxis(), Y);

		xml.setAttribute(AidaWBXML.ENTRIES, h.entries());
		xml.openTag(AidaWBXML.STATISTICS);
		xml.setAttribute(AidaWBXML.DIRECTION, X);
		xml.setAttribute(AidaWBXML.MEAN, h.meanX());
		xml.setAttribute(AidaWBXML.RMS, h.rmsX());
		xml.printTag(AidaWBXML.STATISTIC);
		xml.setAttribute(AidaWBXML.DIRECTION, Y);
		xml.setAttribute(AidaWBXML.MEAN, h.meanY());
		xml.setAttribute(AidaWBXML.RMS, h.rmsY());
		xml.printTag(AidaWBXML.STATISTIC);
		xml.closeTag();

		xml.openTag(AidaWBXML.DATA_2D);

		int xbins = h.xAxis().bins();
		int ybins = h.yAxis().bins();
		for (int i = -2; i < xbins; i++) {
			for (int j = -2; j < ybins; j++) {
				double height = h.binHeight(i, j);
				double error = h.binError(i, j);
				int entries = h.binEntries(i, j);
				if ((height != 0) && (error != 0)) {
					xml.setAttribute(AidaWBXML.BIN_NUM_X, i);
					xml.setAttribute(AidaWBXML.BIN_NUM_Y, j);
					if (h instanceof Histogram2D) {
						xml.setAttribute(
								AidaWBXML.WEIGHTED_MEAN_X,
								((Histogram2D) h).binMeanX(i, j));
						xml.setAttribute(
								AidaWBXML.WEIGHTED_MEAN_Y,
								((Histogram2D) h).binMeanY(i, j));
						xml.setAttribute(
								AidaWBXML.WEIGHTED_RMS_X,
								((Histogram2D) h).binRmsX(i, j));
						xml.setAttribute(
								AidaWBXML.WEIGHTED_RMS_Y,
								((Histogram2D) h).binRmsY(i, j));
					}

					if (height != entries) {
						xml
								.setAttribute(AidaWBXML.HEIGHT,
										height);
						xml.setAttribute(AidaWBXML.ERROR, error);
					}

					// xml.setAttribute(AidaWBXML.ERROR2,"fix me");
					xml.setAttribute(AidaWBXML.ENTRIES, entries);
					xml.printTag(AidaWBXML.BIN_2D);
				}
			}
		}
		xml.closeTag();
		xml.closeTag();
	}

	private void toXML(IHistogram3D h, String path) throws IOException {
		String name = ((IManagedObject) h).name();
		xml.setAttribute(AidaWBXML.NAME, name);

		String title = h.title();
		if ((title != null) && !title.equals("")) {
			xml.setAttribute(AidaWBXML.TITLE, title);
		}
		if ((path != null) && !path.equals("")) {
			xml.setAttribute(AidaWBXML.PATH, path);
		}
		xml.openTag(AidaWBXML.HISTOGRAM_3D);

		IAnnotation annotation = h.annotation();
		if (annotation != null) {
			toXML(annotation);
		}

		writeHistogramAxis(h.xAxis(), X);
		writeHistogramAxis(h.yAxis(), Y);
		writeHistogramAxis(h.zAxis(), Z);

		xml.setAttribute(AidaWBXML.ENTRIES, h.entries());
		xml.openTag(AidaWBXML.STATISTICS);
		xml.setAttribute(AidaWBXML.DIRECTION, X);
		xml.setAttribute(AidaWBXML.MEAN, h.meanX());
		xml.setAttribute(AidaWBXML.RMS, h.rmsX());
		xml.printTag(AidaWBXML.STATISTIC);
		xml.setAttribute(AidaWBXML.DIRECTION, Y);
		xml.setAttribute(AidaWBXML.MEAN, h.meanY());
		xml.setAttribute(AidaWBXML.RMS, h.rmsY());
		xml.printTag(AidaWBXML.STATISTIC);
		xml.setAttribute(AidaWBXML.DIRECTION, Z);
		xml.setAttribute(AidaWBXML.MEAN, h.meanZ());
		xml.setAttribute(AidaWBXML.RMS, h.rmsZ());
		xml.printTag(AidaWBXML.STATISTIC);
		xml.closeTag();

		xml.openTag(AidaWBXML.DATA_3D);

		int xbins = h.xAxis().bins();
		int ybins = h.yAxis().bins();
		int zbins = h.zAxis().bins();
		for (int i = -2; i < xbins; i++) {
			for (int j = -2; j < ybins; j++) {
				for (int k = -2; k < zbins; k++) {
					double height = h.binHeight(i, j, k);
					double error = h.binError(i, j, k);
					int entries = h.binEntries(i, j, k);
					if ((height != 0) && (error != 0)) {
						xml.setAttribute(AidaWBXML.BIN_NUM_X, i);
						xml.setAttribute(AidaWBXML.BIN_NUM_Y, j);
						xml.setAttribute(AidaWBXML.BIN_NUM_Z, k);
						if (h instanceof Histogram3D) {
							xml.setAttribute(
									AidaWBXML.WEIGHTED_MEAN_X,
									((Histogram3D) h).binMeanX(i, j, k));
							xml.setAttribute(
									AidaWBXML.WEIGHTED_MEAN_Y,
									((Histogram3D) h).binMeanY(i, j, k));
							xml.setAttribute(
									AidaWBXML.WEIGHTED_MEAN_Z,
									((Histogram3D) h).binMeanZ(i, j, k));
							xml.setAttribute(
									AidaWBXML.WEIGHTED_RMS_X,
									((Histogram3D) h).binRmsX(i, j, k));
							xml.setAttribute(
									AidaWBXML.WEIGHTED_RMS_Y,
									((Histogram3D) h).binRmsY(i, j, k));
							xml.setAttribute(
									AidaWBXML.WEIGHTED_RMS_Z,
									((Histogram3D) h).binRmsZ(i, j, k));
						}

						if (height != entries) {
							xml.setAttribute(AidaWBXML.HEIGHT,
									height);
							xml.setAttribute(AidaWBXML.ERROR,
									error);
						}

						// xml.setAttribute(AidaWBXML.ERROR2,"fix me");
						xml.setAttribute(AidaWBXML.ENTRIES,
								entries);
						xml.printTag(AidaWBXML.BIN_3D);
					}
				}
			}
		}
		xml.closeTag();
		xml.closeTag();
	}

	private void toXML(ICloud1D c, String path) throws IOException {
		xml.setAttribute(AidaWBXML.NAME, ((IManagedObject) c)
				.name());
		xml.setAttribute(AidaWBXML.MAX_ENTRIES, ((Cloud) c)
				.maxEntries());

		String title = c.title();
		if ((title != null) && !title.equals("")) {
			xml.setAttribute(AidaWBXML.TITLE, title);
		}
		if ((path != null) && !path.equals("")) {
			xml.setAttribute(AidaWBXML.PATH, path);
		}

		String options = ((Cloud) c).getOptions();
		if ((options != null) && !options.equals("")) {
			xml.setAttribute(AidaWBXML.OPTIONS, options);
		}

		if (c instanceof Cloud1D) {
			Cloud1D cl = (Cloud1D) c;
			xml.setAttribute(AidaWBXML.CONVERSION_BINS, cl
					.conversionBins());
			double le = cl.conversionLowerEdge();
			if (!Double.isNaN(le))
				xml.setAttribute(AidaWBXML.CONVERSION_LOWER_EDGE,
						le);
			double ue = cl.conversionUpperEdge();
			if (!Double.isNaN(ue))
				xml.setAttribute(AidaWBXML.CONVERSION_UPPER_EDGE,
						ue);
		}

		IHistogram1D hist = null;
		try {
			hist = c.histogram();
			xml.setAttribute(AidaWBXML.LOWER_EDGE_X, c
					.lowerEdge());
			xml.setAttribute(AidaWBXML.UPPER_EDGE_X, c
					.upperEdge());
		} catch (RuntimeException re) {
		}
		xml.openTag(AidaWBXML.CLOUD_1D);

		IAnnotation annotation = c.annotation();
		if (annotation != null) {
			toXML(annotation);
		}

		if (hist != null) {
			toXML(hist, path);
		} else {
			xml.openTag(AidaWBXML.ENTRIES_1D);
			for (int i = 0; i < c.entries(); i++) {
				xml.setAttribute(AidaWBXML.VALUE_X, c.value(i));

				double w = c.weight(i);
				if (w != 1.) {
					xml.setAttribute(AidaWBXML.WEIGHT, w);
				}
				xml.printTag(AidaWBXML.ENTRY_1D);
			}
			xml.closeTag();
		}
		xml.closeTag();
	}

	private void toXML(ICloud2D c, String path) throws IOException {
		xml.setAttribute(AidaWBXML.NAME, ((IManagedObject) c)
				.name());
		xml.setAttribute(AidaWBXML.MAX_ENTRIES, ((Cloud) c)
				.maxEntries());

		String title = c.title();
		if ((title != null) && !title.equals("")) {
			xml.setAttribute(AidaWBXML.TITLE, title);
		}
		if ((path != null) && !path.equals("")) {
			xml.setAttribute(AidaWBXML.PATH, path);
		}

		String options = ((Cloud) c).getOptions();
		if ((options != null) && !options.equals("")) {
			xml.setAttribute(AidaWBXML.OPTIONS, options);
		}

		if (c instanceof Cloud2D) {
			Cloud2D cl = (Cloud2D) c;
			xml.setAttribute(AidaWBXML.CONVERSION_BINS_X, cl
					.conversionBinsX());
			double lex = cl.conversionLowerEdgeX();
			if (!Double.isNaN(lex))
				xml.setAttribute(
						AidaWBXML.CONVERSION_LOWER_EDGE_X, lex);
			double uex = cl.conversionUpperEdgeX();
			if (!Double.isNaN(uex))
				xml.setAttribute(
						AidaWBXML.CONVERSION_UPPER_EDGE_X, uex);
			xml.setAttribute(AidaWBXML.CONVERSION_BINS_Y, cl
					.conversionBinsY());
			double ley = cl.conversionLowerEdgeY();
			if (!Double.isNaN(ley))
				xml.setAttribute(
						AidaWBXML.CONVERSION_LOWER_EDGE_Y, ley);
			double uey = cl.conversionUpperEdgeY();
			if (!Double.isNaN(uey))
				xml.setAttribute(
						AidaWBXML.CONVERSION_UPPER_EDGE_Y, uey);
		}

		IHistogram2D hist = null;
		try {
			hist = c.histogram();
			xml.setAttribute(AidaWBXML.LOWER_EDGE_X, c
					.lowerEdgeX());
			xml.setAttribute(AidaWBXML.UPPER_EDGE_X, c
					.upperEdgeX());
			xml.setAttribute(AidaWBXML.LOWER_EDGE_Y, c
					.lowerEdgeY());
			xml.setAttribute(AidaWBXML.UPPER_EDGE_Y, c
					.upperEdgeY());
		} catch (RuntimeException re) {
		}
		xml.openTag(AidaWBXML.CLOUD_2D);

		IAnnotation annotation = c.annotation();
		if (annotation != null) {
			toXML(annotation);
		}

		if (hist != null) {
			toXML(hist, path);
		} else {
			xml.openTag(AidaWBXML.ENTRIES_2D);
			for (int i = 0; i < c.entries(); i++) {
				xml.setAttribute(AidaWBXML.VALUE_X, c.valueX(i));
				xml.setAttribute(AidaWBXML.VALUE_Y, c.valueY(i));

				double w = c.weight(i);
				if (w != 1.) {
					xml.setAttribute(AidaWBXML.WEIGHT, w);
				}
				xml.printTag(AidaWBXML.ENTRY_2D);
			}
			xml.closeTag();
		}
		xml.closeTag();
	}

	private void toXML(ICloud3D c, String path) throws IOException {
		xml.setAttribute(AidaWBXML.NAME, ((IManagedObject) c)
				.name());
		xml.setAttribute(AidaWBXML.MAX_ENTRIES, ((Cloud) c)
				.maxEntries());

		String title = c.title();
		if ((title != null) && !title.equals("")) {
			xml.setAttribute(AidaWBXML.TITLE, title);
		}
		if ((path != null) && !path.equals("")) {
			xml.setAttribute(AidaWBXML.PATH, path);
		}

		String options = ((Cloud) c).getOptions();
		if ((options != null) && !options.equals("")) {
			xml.setAttribute(AidaWBXML.OPTIONS, options);
		}

		if (c instanceof Cloud3D) {
			Cloud3D cl = (Cloud3D) c;
			xml.setAttribute(AidaWBXML.CONVERSION_BINS_X, cl
					.conversionBinsX());
			double lex = cl.conversionLowerEdgeX();
			if (!Double.isNaN(lex))
				xml.setAttribute(
						AidaWBXML.CONVERSION_LOWER_EDGE_X, lex);
			double uex = cl.conversionUpperEdgeX();
			if (!Double.isNaN(uex))
				xml.setAttribute(
						AidaWBXML.CONVERSION_UPPER_EDGE_X, uex);
			xml.setAttribute(AidaWBXML.CONVERSION_BINS_Y, cl
					.conversionBinsY());
			double ley = cl.conversionLowerEdgeY();
			if (!Double.isNaN(ley))
				xml.setAttribute(
						AidaWBXML.CONVERSION_LOWER_EDGE_Y, ley);
			double uey = cl.conversionUpperEdgeY();
			if (!Double.isNaN(uey))
				xml.setAttribute(
						AidaWBXML.CONVERSION_UPPER_EDGE_Y, uey);
			xml.setAttribute(AidaWBXML.CONVERSION_BINS_Z, cl
					.conversionBinsZ());
			double lez = cl.conversionLowerEdgeZ();
			if (!Double.isNaN(lez))
				xml.setAttribute(
						AidaWBXML.CONVERSION_LOWER_EDGE_Z, lez);
			double uez = cl.conversionUpperEdgeZ();
			if (!Double.isNaN(uez))
				xml.setAttribute(
						AidaWBXML.CONVERSION_UPPER_EDGE_Z, uez);
		}

		IHistogram3D hist = null;
		try {
			hist = c.histogram();
			xml.setAttribute(AidaWBXML.LOWER_EDGE_X, c
					.lowerEdgeX());
			xml.setAttribute(AidaWBXML.UPPER_EDGE_X, c
					.upperEdgeX());
			xml.setAttribute(AidaWBXML.LOWER_EDGE_Y, c
					.lowerEdgeY());
			xml.setAttribute(AidaWBXML.UPPER_EDGE_Y, c
					.upperEdgeY());
			xml.setAttribute(AidaWBXML.LOWER_EDGE_Z, c
					.lowerEdgeZ());
			xml.setAttribute(AidaWBXML.UPPER_EDGE_Z, c
					.upperEdgeZ());
		} catch (RuntimeException re) {
		}
		xml.openTag(AidaWBXML.CLOUD_3D);

		IAnnotation annotation = c.annotation();
		if (annotation != null) {
			toXML(annotation);
		}

		if (hist != null) {
			toXML(hist, path);
		} else {
			xml.openTag(AidaWBXML.ENTRIES_3D);
			for (int i = 0; i < c.entries(); i++) {
				xml.setAttribute(AidaWBXML.VALUE_X, c.valueX(i));
				xml.setAttribute(AidaWBXML.VALUE_Y, c.valueY(i));
				xml.setAttribute(AidaWBXML.VALUE_Z, c.valueZ(i));

				double w = c.weight(i);
				if (w != 1.) {
					xml.setAttribute(AidaWBXML.WEIGHT, w);
				}
				xml.printTag(AidaWBXML.ENTRY_3D);
			}
			xml.closeTag();
		}
		xml.closeTag();
	}

	private void toXML(ITuple tuple, String path) throws IOException {
		xml.setAttribute(AidaWBXML.NAME, ((IManagedObject) tuple)
				.name());
		xml.setAttribute(AidaWBXML.TITLE, tuple.title());
		if ((path != null) && !path.equals("")) {
			xml.setAttribute(AidaWBXML.PATH, path);
		}

		String options = null;
		if (tuple instanceof Tuple)
			options = ((Tuple) tuple).getOptions();
		if ((options != null) && !options.equals("")) {
			xml.setAttribute(AidaWBXML.OPTIONS, options);
		}

		xml.openTag(AidaWBXML.TUPLE);

		IAnnotation annotation = tuple.annotation();
		if (annotation != null) {
			toXML(annotation);
		}

		writeTupleColumns(tuple);

		xml.openTag(AidaWBXML.ROWS);

		writeTupleEntries(tuple);
		xml.closeTag();
		xml.closeTag();
	}

	private void toXML(IDataPointSet dps, String path) throws IOException {
		xml.setAttribute(AidaWBXML.NAME, ((IManagedObject) dps)
				.name());
		xml.setAttribute(AidaWBXML.TITLE, dps.title());
		if ((path != null) && !path.equals("")) {
			xml.setAttribute(AidaWBXML.PATH, path);
		}
		xml.setAttribute(AidaWBXML.DIMENSION, dps.dimension());
		xml.openTag(AidaWBXML.DATA_POINT_SET);

		IAnnotation annotation = dps.annotation();
		if (annotation != null) {
			toXML(annotation);
		}
		for (int i = 0; i < dps.size(); i++) {
			IDataPoint point = dps.point(i);
			xml.openTag(AidaWBXML.DATA_POINT);
			for (int j = 0; j < point.dimension(); j++) {
				IMeasurement m = point.coordinate(j);
				xml.setAttribute(AidaWBXML.VALUE_DOUBLE, m
						.value());

				double ep = m.errorPlus();
				if (!Double.isNaN(ep)) {
					xml.setAttribute(AidaWBXML.ERROR_PLUS, ep);
				}

				double em = m.errorMinus();
				if (!Double.isNaN(em)) {
					xml.setAttribute(AidaWBXML.ERROR_MINUS, em);
				}
				xml.printTag(AidaWBXML.MEASUREMENT);
			}
			xml.closeTag();
		}
		xml.closeTag();
	}

	private void toXML(IProfile1D h, String path) throws IOException {
		String name = ((IManagedObject) h).name();
		xml.setAttribute(AidaWBXML.NAME, name);

		String title = h.title();
		if ((title != null) && !title.equals("")) {
			xml.setAttribute(AidaWBXML.TITLE, title);
		}
		if ((path != null) && !path.equals("")) {
			xml.setAttribute(AidaWBXML.PATH, path);
		}
		xml.openTag(AidaWBXML.PROFILE_1D);

		IAnnotation annotation = h.annotation();
		if (annotation != null) {
			toXML(annotation);
		}

		writeHistogramAxis(h.axis(), X);

		xml.setAttribute(AidaWBXML.ENTRIES, h.entries());
		xml.openTag(AidaWBXML.STATISTICS);
		xml.setAttribute(AidaWBXML.DIRECTION, X);
		xml.setAttribute(AidaWBXML.MEAN, h.mean());
		xml.setAttribute(AidaWBXML.RMS, h.rms());
		xml.printTag(AidaWBXML.STATISTIC);
		xml.closeTag();

		xml.openTag(AidaWBXML.DATA_1D);

		int bins = h.axis().bins();
		for (int i = -2; i < bins; i++) {
			double height = h.binHeight(i);
			double error = h.binError(i);
			if ((height != 0) && (error != 0)) {
				xml.setAttribute(AidaWBXML.BIN_NUM, i);
				xml.setAttribute(AidaWBXML.WEIGHTED_MEAN, h
						.binMean(i));
				xml.setAttribute(AidaWBXML.RMS, h.binRms(i));
				xml.setAttribute(AidaWBXML.HEIGHT, height);
				xml.setAttribute(AidaWBXML.ERROR, error);

				// xml.setAttribute(AidaWBXML.ERROR2,"fix me");
				xml.setAttribute(AidaWBXML.ENTRIES, h
						.binEntries(i));
				xml.printTag(AidaWBXML.BIN_1D);
			}
		}
		xml.closeTag();
		xml.closeTag();
	}

	private void toXML(IProfile2D h, String path) throws IOException {
		String name = ((IManagedObject) h).name();
		xml.setAttribute(AidaWBXML.NAME, name);

		String title = h.title();
		if ((title != null) && !title.equals("")) {
			xml.setAttribute(AidaWBXML.TITLE, title);
		}
		if ((path != null) && !path.equals("")) {
			xml.setAttribute(AidaWBXML.PATH, path);
		}
		xml.openTag(AidaWBXML.PROFILE_2D);

		IAnnotation annotation = h.annotation();
		if (annotation != null) {
			toXML(annotation);
		}

		writeHistogramAxis(h.xAxis(), X);
		writeHistogramAxis(h.yAxis(), Y);

		xml.setAttribute(AidaWBXML.ENTRIES, h.entries());
		xml.openTag(AidaWBXML.STATISTICS);
		xml.setAttribute(AidaWBXML.DIRECTION, X);
		xml.setAttribute(AidaWBXML.MEAN, h.meanX());
		xml.setAttribute(AidaWBXML.RMS, h.rmsX());
		xml.printTag(AidaWBXML.STATISTIC);
		xml.setAttribute(AidaWBXML.DIRECTION, Y);
		xml.setAttribute(AidaWBXML.MEAN, h.meanY());
		xml.setAttribute(AidaWBXML.RMS, h.rmsY());
		xml.printTag(AidaWBXML.STATISTIC);
		xml.closeTag();

		xml.openTag(AidaWBXML.DATA_2D);

		int xbins = h.xAxis().bins();
		int ybins = h.yAxis().bins();
		for (int i = -2; i < xbins; i++) {
			for (int j = -2; j < ybins; j++) {
				double height = h.binHeight(i, j);
				double error = h.binError(i, j);
				if ((height != 0) && (error != 0)) {
					xml.setAttribute(AidaWBXML.BIN_NUM_X, i);
					xml.setAttribute(AidaWBXML.BIN_NUM_Y, j);
					xml.setAttribute(AidaWBXML.WEIGHTED_MEAN_X, h
							.binMeanX(i, j));
					xml.setAttribute(AidaWBXML.WEIGHTED_MEAN_Y, h
							.binMeanY(i, j));
					xml.setAttribute(AidaWBXML.RMS, h
							.binRms(i, j));
					xml.setAttribute(AidaWBXML.HEIGHT, height);
					xml.setAttribute(AidaWBXML.ERROR, error);

					// xml.setAttribute(AidaWBXML.ERROR2,"fix me");
					xml.setAttribute(AidaWBXML.ENTRIES, h
							.binEntries(i, j));
					xml.printTag(AidaWBXML.BIN_2D);
				}
			}
		}
		xml.closeTag();
		xml.closeTag();
	}

	private void toXML(IAnnotation annotation) throws IOException {
		xml.openTag(AidaWBXML.ANNOTATION);
		for (int i = 0; i < annotation.size(); i++) {
			String key = annotation.key(i);
			xml.setAttribute(AidaWBXML.KEY, key);
			xml.setAttribute(AidaWBXML.VALUE_STRING, annotation
					.value(key));
			if (annotation instanceof Annotation) {
				xml.setAttribute(AidaWBXML.STICKY,
						((Annotation) annotation).isSticky(key));
			}
			xml.printTag(AidaWBXML.ITEM);
		}
		xml.closeTag();
	}

	private void writeHistogramAxis(IAxis axis, String direction)
			throws IOException {
		xml.setAttribute(AidaWBXML.DIRECTION, direction);
		xml.setAttribute(AidaWBXML.MIN, axis.lowerEdge());
		xml.setAttribute(AidaWBXML.MAX, axis.upperEdge());
		xml.setAttribute(AidaWBXML.NUMBER_OF_BINS, axis.bins());
		if (axis instanceof VariableAxis) {
			xml.openTag(AidaWBXML.AXIS);
			for (int i = 0; i < (axis.bins() - 1); i++) {
				xml.setAttribute(AidaWBXML.VALUE_DOUBLE, axis
						.binUpperEdge(i));
				xml.printTag(AidaWBXML.BIN_BORDER);
			}
			xml.closeTag();
		} else {
			xml.printTag(AidaWBXML.AXIS);
		}
	}

	private void writeTupleEntries(ITuple tup) throws IOException {
		tup.start();
		for (int j = 0; j < tup.rows(); j++) {
			tup.next();
			xml.openTag(AidaWBXML.ROW);
			for (int i = 0; i < tup.columns(); i++) {
				Class colType = tup.columnType(i);
				if (colType == ITuple.class) {
					ITuple tupp = (ITuple) tup.getObject(i);
					if (tupp == null) {
						xml.printTag(AidaWBXML.ENTRY_ITUPLE);
					} else {
						xml.openTag(AidaWBXML.ENTRY_ITUPLE);
						writeTupleEntries(tupp);
						xml.closeTag();
					}
				} else {
					if (colType == Integer.TYPE) {
						xml.setAttribute(AidaWBXML.VALUE_INT, tup
								.getInt(i));
					} else if (colType == Short.TYPE) {
						xml.setAttribute(AidaWBXML.VALUE_SHORT,
								tup.getShort(i));
					} else if (colType == Long.TYPE) {
						xml.setAttribute(AidaWBXML.VALUE_LONG,
								tup.getLong(i));
					} else if (colType == Float.TYPE) {
						xml.setAttribute(AidaWBXML.VALUE_FLOAT,
								tup.getFloat(i));
					} else if (colType == Double.TYPE) {
						xml.setAttribute(AidaWBXML.VALUE_DOUBLE,
								tup.getDouble(i));
					} else if (colType == Boolean.TYPE) {
						xml.setAttribute(AidaWBXML.VALUE_BOOLEAN,
								tup.getBoolean(i));
					} else if (colType == Byte.TYPE) {
						xml.setAttribute(AidaWBXML.VALUE_BYTE,
								tup.getByte(i));
					} else if (colType == Character.TYPE) {
						xml.setAttribute(AidaWBXML.VALUE_CHAR,
								tup.getChar(i));
					} else if (colType == String.class) {
						xml.setAttribute(AidaWBXML.VALUE_STRING,
								tup.getString(i));
					} else if (colType == Object.class) {
						xml.setAttribute(AidaWBXML.VALUE_STRING,
								tup.getObject(i).toString());
					}
					xml.printTag(AidaWBXML.ENTRY);
				}
			}
			xml.closeTag();
		}
	}

	private void writeTupleColumns(ITuple tuple) throws IOException {

		xml.openTag(AidaWBXML.COLUMNS);

		int columns = tuple.columns();
		for (int i = 0; i < columns; i++) {
			Class colType = tuple.columnType(i);
			String def = null;
			if (tuple instanceof AbstractTuple) {
				try {
					def = ((AbstractTuple) tuple).columnDefaultString(i);
				} catch (Exception e) {
					if (colType == Boolean.TYPE)
						def = "false";
					else if (colType != ITuple.class)
						def = "0";
				}
			}
			String tupName = tuple.columnName(i);
			if (def != null && !def.equals("")) {
				tupName += ("=" + def);
			}
			if (colType == ITuple.class) {
				tupName = def;
			}
			if (colType == ITuple.class) {
				xml.setAttribute(AidaWBXML.TYPE, "ITuple");
				tupName = tupName.substring(0, tupName.indexOf("="));
				xml.setAttribute(AidaWBXML.NAME, tupName);
				xml.openTag(AidaWBXML.COLUMN);
				writeTupleColumns(tuple.findTuple(i));
				xml.closeTag();
			} else {
				xml.setAttribute(AidaWBXML.TYPE, String.class
						.isAssignableFrom(colType) ? "string" : colType
						.getName());
				xml.setAttribute(AidaWBXML.NAME, tupName);
				xml.printTag(AidaWBXML.COLUMN);
			}
		}
		xml.closeTag();
	}

	private void toXML(IFunction function, String path) throws IOException {
		IModelFunction model = null;
		if (function instanceof IModelFunction)
			model = (IModelFunction) function;

		xml.setAttribute(AidaWBXML.NAME,
				((IManagedObject) function).name());
		xml.setAttribute(AidaWBXML.TITLE, function.title());
		if ((path != null) && !path.equals(""))
			xml.setAttribute(AidaWBXML.PATH, path);
		if (model != null)
			xml.setAttribute(AidaWBXML.IS_NORMALIZED, model
					.isNormalized());

		xml.openTag(AidaWBXML.FUNCTION);

		// Function's elements annotation, codelet, arguments, parameters
		xml.openTag(AidaWBXML.CODELET);
		xml.print(function.codeletString());
		xml.closeTag();

		if (model != null) {
			xml.openTag(AidaWBXML.ARGUMENTS);
			for (int i = 0; i < function.dimension(); i++) {
				xml.setAttribute(AidaWBXML.NAME, model
						.variableName(i));
				xml.openTag(AidaWBXML.ARGUMENT);
				IRangeSet rangeSet = model.normalizationRange(i);
				double[] lb = rangeSet.lowerBounds();
				double[] ub = rangeSet.upperBounds();
				for (int j = 0; j < lb.length; j++) {
					xml.setAttribute(AidaWBXML.MIN, lb[j]);
					xml.setAttribute(AidaWBXML.MAX, ub[j]);
					xml.printTag(AidaWBXML.RANGE);
				}
				xml.closeTag();
			}
			xml.closeTag();
		}

		xml.openTag(AidaWBXML.PARAMETERS);
		String[] parNames = function.parameterNames();
		double[] parValues = function.parameters();

		for (int i = 0; i < function.numberOfParameters(); i++) {
			xml.setAttribute(AidaWBXML.NAME, parNames[i]);
			xml
					.setAttribute(AidaWBXML.VALUE_DOUBLE,
							parValues[i]);
			xml.printTag(AidaWBXML.PARAMETER);
		}
		xml.closeTag();

		xml.closeTag();
	}
}
