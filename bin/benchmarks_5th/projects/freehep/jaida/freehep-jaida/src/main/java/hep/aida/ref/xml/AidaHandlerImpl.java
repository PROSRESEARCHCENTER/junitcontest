// Copyright FreeHEP, 2002-2007
package hep.aida.ref.xml;

import hep.aida.IAnnotation;
import hep.aida.IAxis;
import hep.aida.IDataPoint;
import hep.aida.IFunction;
import hep.aida.IManagedObject;
import hep.aida.IMeasurement;
import hep.aida.IModelFunction;
import hep.aida.dev.IDevManagedObject;
import hep.aida.ref.Annotation;
import hep.aida.ref.ContainerManagedObject;
import hep.aida.ref.fitter.FitParameterSettings;
import hep.aida.ref.fitter.FitResult;
import hep.aida.ref.function.FunctionCatalog;
import hep.aida.ref.histogram.Cloud1D;
import hep.aida.ref.histogram.Cloud2D;
import hep.aida.ref.histogram.Cloud3D;
import hep.aida.ref.histogram.DataPointSet;
import hep.aida.ref.histogram.FixedAxis;
import hep.aida.ref.histogram.Histogram1D;
import hep.aida.ref.histogram.Histogram2D;
import hep.aida.ref.histogram.Histogram3D;
import hep.aida.ref.histogram.Profile1D;
import hep.aida.ref.histogram.Profile2D;
import hep.aida.ref.histogram.VariableAxis;
import hep.aida.ref.tree.Folder;
import hep.aida.ref.tuple.Tuple;
import hep.aida.ref.xml.binary.AidaWBXML;
import hep.aida.ref.xml.binary.AidaWBXMLConverter;
import hep.aida.ref.xml.binary.AidaWBXMLLookup;
import java.util.ArrayList;

import java.util.Stack;
import java.util.StringTokenizer;

import org.freehep.wbxml.Attributes;
import org.xml.sax.SAXException;

/**
 * Implementation of the AIDA handlers for ASCII and Binary XML
 * 
 * @author Tony Johnson
 * @author Mark Donszelmann
 * @version $Id: AidaHandlerImpl.java 10736 2007-05-16 19:32:45Z serbo $
 */
public class AidaHandlerImpl extends AidaWBXMLLookup implements AidaHandler,
		AidaBinaryHandler {
	public static final boolean DEBUG = false;
	private Cloud1D cloud1d;
	private Cloud2D cloud2d;
	private Cloud3D cloud3d;
	private DataPointSet dataPointSet;
	private IAnnotation annotation;
	private IAnnotation saveAnnotation;
	private IDataPoint dataPoint;
	private Stack stack = new Stack();
	private String aidaVersion;
	private String name;
	private String options;
	private String path;
	private String saveName;
	private String saveTitle;
	private String title;
	//private hep.aida.ref.tree.Tree tree;
        private hep.aida.dev.IAddable tree;
	private Tuple tuple;
	private IAxis[] axes = new IAxis[3];
	private double[] binBorders;
	private int[] entries1d;
	private int[][] entries2d;
	private int[][][] entries3d;
	private double[] errors1d;
	private double[][] errors2d;
	private double[][][] errors3d;
	private double[] heights1d;
	private double[][] heights2d;
	private double[][][] heights3d;
	private double[] mean = new double[3];
	private double[] means1d;
	private double[][] meansx2d;
	private double[][][] meansx3d;
	private double[][] meansy2d;
	private double[][][] meansy3d;
	private double[][][] meansz3d;
	private double[] rms = new double[3];
	private double[] rmss1d;
	private double[][] rmssx2d;
	private double[][][] rmssx3d;
	private double[][] rmssy2d;
	private double[][][] rmssy3d;
	private double[][][] rmssz3d;
	private boolean aida22;
	private boolean beforeAida33;
	private boolean hasBinMeanAndRms;
	private double binMax;
	private double binMin;
	private int column;
	private int nAxis;
	private int nBin;
	private int nBins;
	private int nextCoord;
	private IFunction function;
	private String argName;
	private boolean isNormalized;
	private String tupleString = "";
	private int innerFolders = 0;

        // IFitResult objects
        private FitResult fitResult;
        private ArrayList constrains;
        private String fitName;
        private String fitTitle;
        private String fitPath;
        private String fitOptions;        
        
	private boolean markAsFilled = true;

	public AidaHandlerImpl(hep.aida.dev.IAddable tree) {
		this(tree, true);
	}

	public AidaHandlerImpl(hep.aida.dev.IAddable tree, boolean markAsFilled) {
		//this.tree = (hep.aida.ref.tree.Tree) tree;
		this.tree = tree;
		this.markAsFilled = markAsFilled;
	}

        // Begin of the IFitResult handling
	public void start_fitResult(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_fitResult(new AttributesAdapter(atts));
	}
	public void start_fitResult(final Attributes meta) throws SAXException {
                int dim = meta.getIntValue(this.FIT_DIMENSION);
		fitResult = new FitResult(dim);
                
		fitName = meta.getStringValue(NAME, "");
		fitTitle = meta.getStringValue(aida22 ? LABEL : TITLE, "");
		fitPath = meta.getStringValue(PATH, "");
		fitOptions = meta.getStringValue(OPTIONS, "");
                
                fitResult.setNdf(meta.getIntValue(this.FIT_DEGREES_OF_FREEDOM));
                fitResult.setEngineName(meta.getStringValue(this.FIT_ENGINE_NAME));
                fitResult.setFitMethodName(meta.getStringValue(this.FIT_METHOD_NAME));
                fitResult.setIsValid(meta.getBooleanValue(this.FIT_IS_VALID));
                fitResult.setQuality(meta.getDoubleValue(this.FIT_QUALITY));
                fitResult.setFitStatus(meta.getIntValue(this.FIT_STATUS));
                fitResult.setDataDescription(meta.getStringValue(this.FIT_DATA_DESCRIPTION));
	}

	public void end_fitResult() throws SAXException {
		if (function != null) {
			fitResult.setFittedFunction(function);
			function = null;
		}
                if (constrains != null && constrains.size() > 0) {
                    String[] constr = new String[constrains.size()];
                    constr = (String[]) constrains.toArray(constr);
                    constrains.clear();
                    fitResult.setConstraints(constr);                    
                }
                constrains = null;
                
                ContainerManagedObject cmo = new ContainerManagedObject(name);
                cmo.setObject(fitResult);
		add(path, cmo);
		fitResult = null;
		fitName = null;
		fitTitle = null;
		fitPath = null;
		fitOptions = null;
	}
        
	public void start_covarianceMatrix(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_covarianceMatrix(new AttributesAdapter(atts));
	}
        public void start_covarianceMatrix (final Attributes meta) throws SAXException {
        }
    
        public void end_covarianceMatrix () throws SAXException {
        }
    
	public void handle_fittedParameter(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_fittedParameter(new AttributesAdapter(atts));
	}
	public void handle_fittedParameter(final Attributes meta) throws SAXException {
            String fpName = meta.getStringValue(this.FITTED_PARAMETER_NAME);
            FitParameterSettings fps = new FitParameterSettings(fpName);
            fps.setFixed(meta.getBooleanValue(this.FITTED_PARAMETER_IS_FIXED, false));
            
            double stepSize = meta.getDoubleValue(this.FITTED_PARAMETER_STEP_SIZE, Double.NaN);
            if (!Double.isNaN(stepSize)) fps.setStepSize(stepSize);

            double bound = meta.getDoubleValue(this.FITTED_PARAMETER_LOWER_BOUND, Double.NEGATIVE_INFINITY);
            fps.setLowerBound(bound);
            bound = meta.getDoubleValue(this.FITTED_PARAMETER_UPPER_BOUND, Double.POSITIVE_INFINITY);
            fps.setUpperBound(bound);
            fitResult.setFitParameterSettings(fpName, fps);
	}

	public void handle_fitConstraint(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_fitConstraint(new AttributesAdapter(atts));
	}

        public void handle_fitConstraint(final Attributes meta) throws SAXException {
                if (constrains == null) constrains = new ArrayList();
                String fcValue = meta.getStringValue(this.FIT_CONSTRAINT_VALUE);
                if (fcValue != null && !fcValue.trim().equals(""))
                    constrains.add(fcValue);
        }

	public void handle_matrixElement(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_matrixElement(new AttributesAdapter(atts));
	}

        public void handle_matrixElement(final Attributes meta) throws SAXException {
                int i = meta.getIntValue(this.MATRIX_ELEMENT_ROW);
                int j = meta.getIntValue(this.MATRIX_ELEMENT_COLUMN);
                fitResult.setCovMatrixElement(i, j, meta.getDoubleValue(this.MATRIX_ELEMENT_VALUE));
        }
        // End of the IFitResult handling
        
	public void end_aida() throws SAXException {
	}

	public void end_annotation() throws SAXException {
	}

	public void end_argument() throws SAXException {
	}

	public void end_arguments() throws SAXException {
		if (function instanceof IModelFunction) {
			IModelFunction model = (IModelFunction) function;
			model.normalize(isNormalized);
		}
	}

	public void end_axis() throws SAXException {
		if (binBorders != null) {
			axes[nAxis] = new VariableAxis(binBorders);
		} else {
			axes[nAxis] = new FixedAxis(nBins, binMin, binMax);
		}
	}

	public void end_cloud1d() throws SAXException {
		if (annotation != null) {
			cloud1d.setAnnotation(annotation);
			annotation = null;
		}
		cloud1d.setTitle((title == null) ? name : title);
		add(path, cloud1d);
		cloud1d = null;
	}

	public void end_cloud2d() throws SAXException {
		if (annotation != null) {
			cloud2d.setAnnotation(annotation);
			annotation = null;
		}
		cloud2d.setTitle((title == null) ? name : title);
		add(path, cloud2d);
		cloud2d = null;
	}

	public void end_cloud3d() throws SAXException {
		if (annotation != null) {
			cloud3d.setAnnotation(annotation);
			annotation = null;
		}
		cloud3d.setTitle((title == null) ? name : title);
		add(path, cloud3d);
		cloud3d = null;
	}

	public void end_columns() throws SAXException {
		if (innerFolders != 0) {
			tupleString += "}";
			innerFolders--;
		}
	}

	public void end_data1d() throws SAXException {
	}

	public void end_data2d() throws SAXException {
	}

	public void end_data3d() throws SAXException {
	}

	public void end_dataPoint() throws SAXException {
	}

	public void end_dataPointSet() throws SAXException {
		if (annotation != null) {
			dataPointSet.setAnnotation(annotation);
			annotation = null;
		}
		dataPointSet.setTitle((title == null) ? name : title);
		add(path, dataPointSet);
		dataPointSet = null;
	}

	public void end_entries1d() throws SAXException {
	}

	public void end_entries2d() throws SAXException {
		if (DEBUG) {
			System.err.println("end_entries2d()");
		}
	}

	public void end_entries3d() throws SAXException {
		if (DEBUG) {
			System.err.println("end_entries3d()");
		}
	}

	public void end_entryITuple() throws SAXException {
		Object[] elements = (Object[]) stack.pop();
		tuple = (Tuple) elements[0];
		column = ((Integer) elements[1]).intValue();
		column++;
	}

	public void end_function() throws SAXException {
		((IDevManagedObject) function).setName(name);
		if (fitResult == null) add(path, (IManagedObject) function);
	}

	public void end_histogram1d() throws SAXException {
		Histogram1D hist = new Histogram1D();
		hist.setName(name);
		hist.initHistogram1D(axes[0], options);

		hist.setContents(heights1d, errors1d, entries1d, means1d, rmss1d);
		//if (!hasBinMeanAndRms) {
			hist.setMeanAndRms(mean[0], rms[0]);
		//}
		if (annotation != null) {
			hist.setAnnotation(annotation);
			annotation = null;
		}
		hist.setTitle((title == null) ? name : title);
		if (cloud1d != null) {
			cloud1d.setHistogram(hist);
			annotation = saveAnnotation;
			title = saveTitle;
			name = saveName;
		} else {
			add(path, hist);
		}
	}

	public void end_histogram2d() throws SAXException {
		Histogram2D hist = new Histogram2D();
		hist.setName(name);
		hist.initHistogram2D(axes[0], axes[1], options);

		hist.setContents(heights2d, errors2d, entries2d, meansx2d, rmssx2d,
				meansy2d, rmssy2d);
		if (!hasBinMeanAndRms) {
			hist.setMeanX(mean[0]);
			hist.setRmsX(rms[0]);
			hist.setMeanY(mean[1]);
			hist.setRmsY(rms[1]);
		}
		if (annotation != null) {
			hist.setAnnotation(annotation);
			annotation = null;
		}
		hist.setTitle((title == null) ? name : title);
		if (cloud2d != null) {
			cloud2d.setHistogram(hist);
			annotation = saveAnnotation;
			title = saveTitle;
			name = saveName;
		} else {
			add(path, hist);
		}
	}

	public void end_histogram3d() throws SAXException {
		Histogram3D hist = new Histogram3D();
		hist.setName(name);
		hist.initHistogram3D(axes[0], axes[1], axes[2], options);

		hist.setContents(heights3d, errors3d, entries3d, meansx3d, rmssx3d,
				meansy3d, rmssy3d, meansz3d, rmssz3d);
		if (!hasBinMeanAndRms) {
			hist.setMeanX(mean[0]);
			hist.setRmsX(rms[0]);
			hist.setMeanY(mean[1]);
			hist.setRmsY(rms[1]);
			hist.setMeanZ(mean[2]);
			hist.setRmsZ(rms[2]);
		}
		if (annotation != null) {
			hist.setAnnotation(annotation);
			annotation = null;
		}
		hist.setTitle((title == null) ? name : title);
		if (cloud3d != null) {
			cloud3d.setHistogram(hist);
			annotation = saveAnnotation;
			title = saveTitle;
			name = saveName;
		} else {
			add(path, hist);
		}
	}

	public void end_parameters() throws SAXException {
	}

	public void end_profile1d() throws SAXException {
		Profile1D hist = new Profile1D();
		hist.setName(name);
		hist.initProfile1D(axes[0]);
		hist.setContents(heights1d, errors1d, entries1d, rmss1d, means1d);
		hist.setMean(mean[0]);
		hist.setRms(rms[0]);
		if (annotation != null) {
			hist.setAnnotation(annotation);
			annotation = null;
		}
		hist.setTitle((title == null) ? name : title);
		add(path, hist);
	}

	public void end_profile2d() throws SAXException {
		Profile2D hist = new Profile2D();
		hist.setName(name);
		hist.initProfile2D(axes[0], axes[1]);
		hist.setContents(heights2d, errors2d, entries2d, rmssx2d, meansx2d,
				meansy2d);
		hist.setMeanX(mean[0]);
		hist.setRmsX(rms[0]);
		hist.setMeanY(mean[1]);
		hist.setRmsY(rms[1]);
		if (annotation != null) {
			hist.setAnnotation(annotation);
			annotation = null;
		}
		hist.setTitle((title == null) ? name : title);
		add(path, hist);
	}

	public void end_row() throws SAXException {
		tuple.addRow();
	}

	public void end_rows() throws SAXException {
	}

	public void end_statistics() throws SAXException {
		if (DEBUG) {
			System.err.println("end_statistics()");
		}
	}

	public void end_tuple() throws SAXException {
		if (annotation != null) {
			tuple.setAnnotation(annotation);
			annotation = null;
		}
		tuple.setTitle((title == null) ? name : title);
		add(path, tuple);
	}

	public void handle_bin1d(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_bin1d(new AttributesAdapter(atts));
	}

	public void handle_bin1d(final Attributes meta) throws SAXException {
		int bin = getBinNum(meta.getIntValue(BIN_NUM), axes[0].bins());
		errors1d[bin] = meta.getDoubleValue(ERROR, Double.NaN);
		entries1d[bin] = meta.getIntValue(ENTRIES);
		heights1d[bin] = meta.getDoubleValue(HEIGHT, (double) entries1d[bin]);
		if (hasBinMeanAndRms) {
			boolean hasWeightedMean = meta.getType(WEIGHTED_MEAN) >= 0;
			boolean hasWeightedRms = meta.getType(WEIGHTED_RMS) >= 0;
			boolean hasRms = meta.getType(RMS) >= 0;
			hasBinMeanAndRms = hasWeightedMean && (hasWeightedRms || hasRms);
			if (hasBinMeanAndRms) {
				means1d[bin] = meta.getDoubleValue(WEIGHTED_MEAN);
				rmss1d[bin] = meta.getDoubleValue(hasWeightedRms ? WEIGHTED_RMS
						: RMS);
			}
		}
	}

	public void handle_bin2d(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_bin2d(new AttributesAdapter(atts));
	}

	public void handle_bin2d(final Attributes meta) throws SAXException {
		int xBin = getBinNum(meta.getIntValue(BIN_NUM_X), axes[0].bins());
		int yBin = getBinNum(meta.getIntValue(BIN_NUM_Y), axes[1].bins());
		errors2d[xBin][yBin] = meta.getDoubleValue(ERROR, Double.NaN);
		entries2d[xBin][yBin] = meta.getIntValue(ENTRIES);
		heights2d[xBin][yBin] = meta.getDoubleValue(HEIGHT,
				(double) entries2d[xBin][yBin]);
		if (hasBinMeanAndRms) {
			boolean hasWeightedMeanX = meta.getType(WEIGHTED_MEAN_X) >= 0;
			boolean hasWeightedRmsX = meta.getType(WEIGHTED_RMS_X) >= 0;
			boolean hasRms = meta.getType(RMS) >= 0;
			boolean hasWeightedMeanY = meta.getType(WEIGHTED_MEAN_Y) >= 0;

			hasBinMeanAndRms = hasWeightedMeanX && hasWeightedMeanY
					&& (hasWeightedRmsX || hasRms);
			if (hasBinMeanAndRms) {
				meansx2d[xBin][yBin] = meta.getDoubleValue(WEIGHTED_MEAN_X);
				rmssx2d[xBin][yBin] = meta
						.getDoubleValue(hasWeightedRmsX ? WEIGHTED_RMS_X : RMS);
				meansy2d[xBin][yBin] = meta.getDoubleValue(WEIGHTED_MEAN_Y);
				if (meta.getType(WEIGHTED_RMS_Y) >= 0) {
					rmssy2d[xBin][yBin] = meta.getDoubleValue(WEIGHTED_RMS_Y);
				}
			}
		}
	}

	public void handle_bin3d(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_bin3d(new AttributesAdapter(atts));
	}

	public void handle_bin3d(final Attributes meta) throws SAXException {
		int xBin = getBinNum(meta.getIntValue(BIN_NUM_X), axes[0].bins());
		int yBin = getBinNum(meta.getIntValue(BIN_NUM_Y), axes[1].bins());
		int zBin = getBinNum(meta.getIntValue(BIN_NUM_Z), axes[2].bins());
		errors3d[xBin][yBin][zBin] = meta.getDoubleValue(ERROR, Double.NaN);
		entries3d[xBin][yBin][zBin] = meta.getIntValue(ENTRIES);
		heights3d[xBin][yBin][zBin] = meta.getDoubleValue(HEIGHT,
				(double) entries3d[xBin][yBin][zBin]);
		if (hasBinMeanAndRms) {
			boolean hasWeightedMeanX = meta.getType(WEIGHTED_MEAN_X) >= 0;
			boolean hasWeightedRmsX = meta.getType(WEIGHTED_RMS_X) >= 0;
			boolean hasWeightedMeanY = meta.getType(WEIGHTED_MEAN_Y) >= 0;
			boolean hasWeightedRmsY = meta.getType(WEIGHTED_RMS_Y) >= 0;
			boolean hasWeightedMeanZ = meta.getType(WEIGHTED_MEAN_Z) >= 0;
			boolean hasWeightedRmsZ = meta.getType(WEIGHTED_RMS_Z) >= 0;
			hasBinMeanAndRms = hasWeightedMeanX && hasWeightedMeanY
					&& hasWeightedMeanZ && hasWeightedRmsX && hasWeightedRmsY
					&& hasWeightedRmsZ;
			if (hasBinMeanAndRms) {
				meansx3d[xBin][yBin][zBin] = meta
						.getDoubleValue(WEIGHTED_MEAN_X);
				rmssx3d[xBin][yBin][zBin] = meta.getDoubleValue(WEIGHTED_RMS_X);
				meansy3d[xBin][yBin][zBin] = meta
						.getDoubleValue(WEIGHTED_MEAN_Y);
				rmssy3d[xBin][yBin][zBin] = meta.getDoubleValue(WEIGHTED_RMS_Y);
				meansz3d[xBin][yBin][zBin] = meta
						.getDoubleValue(WEIGHTED_MEAN_Z);
				rmssz3d[xBin][yBin][zBin] = meta.getDoubleValue(WEIGHTED_RMS_Z);
			}
		}
	}

	public void handle_binBorder(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_binBorder(new AttributesAdapter(atts));
	}

	public void handle_binBorder(final Attributes meta) throws SAXException {
		if (binBorders == null) {
			binBorders = new double[nBins + 1];
			binBorders[0] = binMin;
			binBorders[nBins] = binMax;
			nBin = 1;
		}
		binBorders[nBin++] = meta.getDoubleValue(VALUE_DOUBLE);
	}

	public void handle_codelet(final java.lang.String data,
			final org.xml.sax.Attributes atts) throws SAXException {
		handle_codelet(data, new AttributesAdapter(atts));
	}

	public void handle_codelet(final java.lang.String data,
			final Attributes meta) throws SAXException {
		String codelet = data;
		if (codelet.startsWith("\n"))
			codelet = codelet.substring(1);
		if (codelet.endsWith("\n"))
			codelet = codelet.substring(0, codelet.length() - 1);
		codelet = codelet.trim();
		function = FunctionCatalog.getFunctionCatalog().getFunctionCreator()
				.createFromCodelet(codelet);
		if (title != null)
			function.setTitle(title);
	}

	public void handle_column(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_column(new AttributesAdapter(atts));
	}

	public void handle_column(final Attributes meta) throws SAXException {
		String type = meta.getStringValue(TYPE);

		if (!tupleString.equals("") && !tupleString.endsWith(",")
				&& !tupleString.endsWith("{"))
			tupleString += ",";

		tupleString += type + " " + meta.getStringValue(NAME);

		if (!beforeAida33 && type.equalsIgnoreCase("ITuple")) {
			tupleString += "={";
			innerFolders++;
		}
	}

	public void handle_entry(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_entry(new AttributesAdapter(atts));
	}

	public void handle_entry(final Attributes meta) throws SAXException {
		Class colType = tuple.columnType(column);
		if (colType == Integer.TYPE) {
			tuple.fill(column, meta.getIntValue(VALUE_INT));
		} else if (colType == Short.TYPE) {
			tuple.fill(column, meta.getShortValue(VALUE_SHORT));
		} else if (colType == Long.TYPE) {
			tuple.fill(column, meta.getLongValue(VALUE_LONG));
		} else if (colType == Float.TYPE) {
			tuple.fill(column, meta.getFloatValue(VALUE_FLOAT));
		} else if (colType == Double.TYPE) {
			tuple.fill(column, meta.getDoubleValue(VALUE_DOUBLE));
		} else if (colType == Boolean.TYPE) {
			tuple.fill(column, meta.getBooleanValue(VALUE_BOOLEAN));
		} else if (colType == Byte.TYPE) {
			tuple.fill(column, meta.getByteValue(VALUE_BYTE));
		} else if (colType == Character.TYPE) {
			tuple.fill(column, meta.getCharValue(VALUE_CHAR));
		} else if (colType == String.class) {
			tuple.fill(column, meta.getStringValue(VALUE_STRING));
		} else {
			tuple.fill(column, meta.getStringValue(VALUE_STRING)); // fix me!
		}
		column++;
	}

	public void handle_entry1d(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_entry1d(new AttributesAdapter(atts));
	}

	public void handle_entry1d(final Attributes meta) throws SAXException {
		double value = meta.getDoubleValue(VALUE_X);
		double weight = meta.getDoubleValue(WEIGHT, 1);
		cloud1d.fill(value, weight);
	}

	public void handle_entry2d(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_entry2d(new AttributesAdapter(atts));
	}

	public void handle_entry2d(final Attributes meta) throws SAXException {
		double x = meta.getDoubleValue(VALUE_X);
		double y = meta.getDoubleValue(VALUE_Y);
		double weight = meta.getDoubleValue(WEIGHT, 1);
		cloud2d.fill(x, y, weight);
	}

	public void handle_entry3d(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_entry3d(new AttributesAdapter(atts));
	}

	public void handle_entry3d(final Attributes meta) throws SAXException {
		double x = meta.getDoubleValue(VALUE_X);
		double y = meta.getDoubleValue(VALUE_Y);
		double z = meta.getDoubleValue(VALUE_Z);
		double weight = meta.getDoubleValue(WEIGHT, 1);
		cloud3d.fill(x, y, z, weight);
	}

	public void handle_implementation(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_implementation(new AttributesAdapter(atts));
	}

	public void handle_implementation(final Attributes meta)
			throws SAXException {
		if (DEBUG) {
			System.err.println("handle_implementation: " + meta);
		}
	}

	public void handle_item(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_item(new AttributesAdapter(atts));
	}

	public void handle_item(final Attributes meta) throws SAXException {
		String key = meta.getStringValue(KEY);
		String val = meta.getStringValue(VALUE_STRING);
		boolean sticky = meta.getBooleanValue(STICKY, false);
		annotation.addItem(key, val, sticky);
	}

	public void handle_measurement(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_measurement(new AttributesAdapter(atts));
	}

	public void handle_measurement(final Attributes meta) throws SAXException {
		IMeasurement m = dataPoint.coordinate(nextCoord++);
		m.setValue(meta.getDoubleValue(VALUE_DOUBLE));

		if (meta.getType(ERROR_PLUS) >= 0) {
			m.setErrorPlus(meta.getDoubleValue(ERROR_PLUS));
		}
		if (meta.getType(ERROR_MINUS) >= 0) {
			m.setErrorMinus(meta.getDoubleValue(ERROR_MINUS));
		}
	}

	public void handle_parameter(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_parameter(new AttributesAdapter(atts));
	}

	public void handle_parameter(final Attributes meta) throws SAXException {
		String parName = meta.getStringValue(NAME);
		function.setParameter(parName, meta.getDoubleValue(VALUE_DOUBLE));
	}

	public void handle_range(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_range(new AttributesAdapter(atts));
	}

	public void handle_range(final Attributes meta) throws SAXException {
		if (function instanceof IModelFunction) {
			IModelFunction model = (IModelFunction) function;
			int argIndex = -1;
			for (int i = 0; i < model.dimension(); i++) {
				if (model.variableName(i).equals(argName)) {
					argIndex = i;
					break;
				}
			}
			double min = meta.getDoubleValue(MIN);
			double max = meta.getDoubleValue(MAX);
			model.normalizationRange(argIndex).include(min, max);
		}
	}

	public void handle_statistic(final org.xml.sax.Attributes atts)
			throws SAXException {
		handle_statistic(new AttributesAdapter(atts));
	}

	public void handle_statistic(final Attributes meta) throws SAXException {
		int dir = directionToInt(meta.getStringValue(DIRECTION, "x"));
		mean[dir] = meta.getDoubleValue(MEAN);
		rms[dir] = meta.getDoubleValue(RMS);
	}

	public void start_aida(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_aida(new AttributesAdapter(atts));
	}

	public void start_aida(final Attributes meta) throws SAXException {
		aidaVersion = meta.getStringValue(VERSION);
		aida22 = aidaVersion.startsWith("2.2");

		StringTokenizer st = new StringTokenizer(aidaVersion, ".");
		int nTokens = st.countTokens();
		if (nTokens > 3)
			throw new RuntimeException("Illegal version for parsing.");
		int majorVersion = Integer.parseInt(st.nextToken());
		int revisionVersion = Integer.parseInt(st.nextToken());
		int patchVersion = 0;
		if (nTokens == 3)
			Integer.parseInt(st.nextToken());

		beforeAida33 = (majorVersion < 3)
				|| (majorVersion == 3 && revisionVersion <= 2);

	}

	public void start_annotation(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_annotation(new AttributesAdapter(atts));
	}

	public void start_annotation(final Attributes meta) throws SAXException {
		annotation = new Annotation();
	}

	public void start_argument(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_argument(new AttributesAdapter(atts));
	}

	public void start_argument(final Attributes meta) throws SAXException {
		argName = meta.getStringValue(NAME);
	}

	public void start_arguments(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_arguments(new AttributesAdapter(atts));
	}

	public void start_arguments(final Attributes meta) throws SAXException {
	}

	public void start_axis(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_axis(new AttributesAdapter(atts));
	}

	public void start_axis(final Attributes meta) throws SAXException {
		binMin = meta.getDoubleValue(MIN);
		binMax = meta.getDoubleValue(MAX);
		nBins = meta.getIntValue(NUMBER_OF_BINS);
		nAxis = directionToInt(meta.getStringValue(DIRECTION, "x"));
		binBorders = null;
	}

	public void start_cloud1d(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_cloud1d(new AttributesAdapter(atts));
	}

	public void start_cloud1d(final Attributes meta) throws SAXException {
		start_managedObject(meta);
		cloud1d = new Cloud1D();
		cloud1d.initCloud(meta.getIntValue(MAX_ENTRIES), options);
		cloud1d.setName(name);

		double uE = meta.getDoubleValue(UPPER_EDGE_X, Double.NaN);
		double lE = meta.getDoubleValue(LOWER_EDGE_X, Double.NaN);
		if (!Double.isNaN(uE)) {
			cloud1d.setUpperEdge(uE);
		}
		if (!Double.isNaN(lE)) {
			cloud1d.setLowerEdge(lE);
		}

		int conversionBins = meta.getIntValue(CONVERSION_BINS, -1);
		if (conversionBins >= 0) {
			double le = meta.getDoubleValue(CONVERSION_LOWER_EDGE, Double.NaN);
			double ue = meta.getDoubleValue(CONVERSION_UPPER_EDGE, Double.NaN);
			cloud1d.setConversionParameters(conversionBins, le, ue);
		}

	}

	public void start_cloud2d(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_cloud2d(new AttributesAdapter(atts));
	}

	public void start_cloud2d(final Attributes meta) throws SAXException {
		start_managedObject(meta);
		cloud2d = new Cloud2D();
		cloud2d.initCloud(meta.getIntValue(MAX_ENTRIES), options);
		cloud2d.setName(name);

		double uE = meta.getDoubleValue(UPPER_EDGE_X, Double.NaN);
		double lE = meta.getDoubleValue(LOWER_EDGE_X, Double.NaN);
		if (!Double.isNaN(uE)) {
			cloud2d.setUpperEdgeX(uE);
		}
		if (!Double.isNaN(lE)) {
			cloud2d.setLowerEdgeX(lE);
		}
		uE = meta.getDoubleValue(UPPER_EDGE_Y, Double.NaN);
		lE = meta.getDoubleValue(LOWER_EDGE_Y, Double.NaN);
		if (!Double.isNaN(uE)) {
			cloud2d.setUpperEdgeY(uE);
		}
		if (!Double.isNaN(lE)) {
			cloud2d.setLowerEdgeY(lE);
		}

		int conversionBinsX = meta.getIntValue(CONVERSION_BINS_X, -1);
		int conversionBinsY = meta.getIntValue(CONVERSION_BINS_Y, -1);
		if (conversionBinsX >= 0 && conversionBinsY >= 0) {
			double lex = meta.getDoubleValue(CONVERSION_LOWER_EDGE_X,
					Double.NaN);
			double uex = meta.getDoubleValue(CONVERSION_UPPER_EDGE_X,
					Double.NaN);
			double ley = meta.getDoubleValue(CONVERSION_LOWER_EDGE_Y,
					Double.NaN);
			double uey = meta.getDoubleValue(CONVERSION_UPPER_EDGE_Y,
					Double.NaN);
			cloud2d.setConversionParameters(conversionBinsX, lex, uex,
					conversionBinsY, ley, uey);
		}

	}

	public void start_cloud3d(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_cloud3d(new AttributesAdapter(atts));
	}

	public void start_cloud3d(final Attributes meta) throws SAXException {
		start_managedObject(meta);
		cloud3d = new Cloud3D();
		cloud3d.initCloud(meta.getIntValue(MAX_ENTRIES), options);
		cloud3d.setName(name);

		double uE = meta.getDoubleValue(UPPER_EDGE_X, Double.NaN);
		double lE = meta.getDoubleValue(LOWER_EDGE_X, Double.NaN);
		if (!Double.isNaN(uE)) {
			cloud3d.setUpperEdgeX(uE);
		}
		if (!Double.isNaN(lE)) {
			cloud3d.setLowerEdgeX(lE);
		}
		uE = meta.getDoubleValue(UPPER_EDGE_Y, Double.NaN);
		lE = meta.getDoubleValue(LOWER_EDGE_Y, Double.NaN);
		if (!Double.isNaN(uE)) {
			cloud3d.setUpperEdgeY(uE);
		}
		if (!Double.isNaN(lE)) {
			cloud3d.setLowerEdgeY(lE);
		}
		uE = meta.getDoubleValue(UPPER_EDGE_Z, Double.NaN);
		lE = meta.getDoubleValue(LOWER_EDGE_Z, Double.NaN);
		if (!Double.isNaN(uE)) {
			cloud3d.setUpperEdgeZ(uE);
		}
		if (!Double.isNaN(lE)) {
			cloud3d.setLowerEdgeZ(lE);
		}

		int conversionBinsX = meta.getIntValue(CONVERSION_BINS_X, -1);
		int conversionBinsY = meta.getIntValue(CONVERSION_BINS_Y, -1);
		int conversionBinsZ = meta.getIntValue(CONVERSION_BINS_Z, -1);
		if (conversionBinsX >= 0 && conversionBinsY >= 0
				&& conversionBinsZ >= 0) {
			double lex = meta.getDoubleValue(CONVERSION_LOWER_EDGE_X,
					Double.NaN);
			double uex = meta.getDoubleValue(CONVERSION_UPPER_EDGE_X,
					Double.NaN);
			double ley = meta.getDoubleValue(CONVERSION_LOWER_EDGE_Y,
					Double.NaN);
			double uey = meta.getDoubleValue(CONVERSION_UPPER_EDGE_Y,
					Double.NaN);
			double lez = meta.getDoubleValue(CONVERSION_LOWER_EDGE_Z,
					Double.NaN);
			double uez = meta.getDoubleValue(CONVERSION_UPPER_EDGE_Z,
					Double.NaN);
			cloud3d.setConversionParameters(conversionBinsX, lex, uex,
					conversionBinsY, ley, uey, conversionBinsZ, lez, uez);
		}
	}

	public void start_columns(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_columns(new AttributesAdapter(atts));
	}

	public void start_columns(final Attributes meta) throws SAXException {
	}

	public void start_data1d(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_data1d(new AttributesAdapter(atts));
	}

	public void start_data1d(final Attributes meta) throws SAXException {
		int xBins = axes[0].bins() + 2;
		heights1d = new double[xBins];
		errors1d = new double[xBins];
		entries1d = new int[xBins];
		means1d = new double[xBins];
		rmss1d = new double[xBins];
		hasBinMeanAndRms = true;
	}

	public void start_data2d(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_data2d(new AttributesAdapter(atts));
	}

	public void start_data2d(final Attributes meta) throws SAXException {
		int xBins = axes[0].bins() + 2;
		int yBins = axes[1].bins() + 2;
		heights2d = new double[xBins][yBins];
		errors2d = new double[xBins][yBins];
		entries2d = new int[xBins][yBins];
		meansx2d = new double[xBins][yBins];
		rmssx2d = new double[xBins][yBins];
		meansy2d = new double[xBins][yBins];
		rmssy2d = new double[xBins][yBins];
		hasBinMeanAndRms = true;
	}

	public void start_data3d(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_data3d(new AttributesAdapter(atts));
	}

	public void start_data3d(final Attributes meta) throws SAXException {
		int xBins = axes[0].bins() + 2;
		int yBins = axes[1].bins() + 2;
		int zBins = axes[2].bins() + 2;
		heights3d = new double[xBins][yBins][zBins];
		errors3d = new double[xBins][yBins][zBins];
		entries3d = new int[xBins][yBins][zBins];
		meansx3d = new double[xBins][yBins][zBins];
		rmssx3d = new double[xBins][yBins][zBins];
		meansy3d = new double[xBins][yBins][zBins];
		rmssy3d = new double[xBins][yBins][zBins];
		meansz3d = new double[xBins][yBins][zBins];
		rmssz3d = new double[xBins][yBins][zBins];
		hasBinMeanAndRms = true;
	}

	public void start_dataPoint(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_dataPoint(new AttributesAdapter(atts));
	}

	public void start_dataPoint(final Attributes meta) throws SAXException {
		dataPointSet.addPoint();
		dataPoint = dataPointSet.point(dataPointSet.size() - 1);
		nextCoord = 0;
	}

	public void start_dataPointSet(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_dataPointSet(new AttributesAdapter(atts));
	}

	public void start_dataPointSet(final Attributes meta) throws SAXException {
		start_managedObject(meta);

		int dim = meta.getIntValue(DIMENSION);
		dataPointSet = new DataPointSet(name, title, dim);
	}

	public void start_entries1d(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_entries1d(new AttributesAdapter(atts));
	}

	public void start_entries1d(final Attributes meta) throws SAXException {
	}

	public void start_entries2d(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_entries2d(new AttributesAdapter(atts));
	}

	public void start_entries2d(final Attributes meta) throws SAXException {
		if (DEBUG) {
			System.err.println("start_entries2d: " + meta);
		}
	}

	public void start_entries3d(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_entries3d(new AttributesAdapter(atts));
	}

	public void start_entries3d(final Attributes meta) throws SAXException {
		if (DEBUG) {
			System.err.println("start_entries3d: " + meta);
		}
	}

	public void start_entryITuple(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_entryITuple(new AttributesAdapter(atts));
	}

	public void start_entryITuple(final Attributes meta) throws SAXException {
		stack.push(new Object[] { tuple, new Integer(column) });
		tuple = (Tuple) tuple.getObject(column);
		column = 0;
	}

	public void start_function(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_function(new AttributesAdapter(atts));
	}

	public void start_function(final Attributes meta) throws SAXException {
		start_managedObject(meta);
		isNormalized = meta.getBooleanValue(IS_NORMALIZED);
	}

	public void start_histogram1d(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_histogram1d(new AttributesAdapter(atts));
	}

	public void start_histogram1d(final Attributes meta) throws SAXException {
		if (cloud1d != null) {
			saveAnnotation = annotation;
			saveTitle = title;
			saveName = name;
		}
		start_managedObject(meta);
	}

	public void start_histogram2d(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_histogram2d(new AttributesAdapter(atts));
	}

	public void start_histogram2d(final Attributes meta) throws SAXException {
		if (cloud2d != null) {
			saveAnnotation = annotation;
			saveTitle = title;
			saveName = name;
		}
		start_managedObject(meta);
	}

	public void start_histogram3d(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_histogram3d(new AttributesAdapter(atts));
	}

	public void start_histogram3d(final Attributes meta) throws SAXException {
		if (cloud3d != null) {
			saveAnnotation = annotation;
			saveTitle = title;
			saveName = name;
		}
		start_managedObject(meta);
	}

	public void start_parameters(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_parameters(new AttributesAdapter(atts));
	}

	public void start_parameters(final Attributes meta) throws SAXException {
	}

	public void start_profile1d(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_profile1d(new AttributesAdapter(atts));
	}

	public void start_profile1d(final Attributes meta) throws SAXException {
		start_managedObject(meta);
	}

	public void start_profile2d(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_profile2d(new AttributesAdapter(atts));
	}

	public void start_profile2d(final Attributes meta) throws SAXException {
		start_managedObject(meta);
	}

	public void start_row(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_row(new AttributesAdapter(atts));
	}

	public void start_row(final Attributes meta) throws SAXException {
		column = 0;
	}

	public void start_rows(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_rows(new AttributesAdapter(atts));
	}

	public void start_rows(final Attributes meta) throws SAXException {
		if (tupleString.endsWith(","))
			tupleString = tupleString.substring(0, tupleString.length() - 1);
		tuple = new Tuple(name, title, tupleString, options);
	}

	public void start_statistics(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_statistics(new AttributesAdapter(atts));
	}

	public void start_statistics(final Attributes meta) throws SAXException {
		if (DEBUG) {
			System.err.println("start_statistics: " + meta);
		}
	}

	public void start_tuple(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_tuple(new AttributesAdapter(atts));
	}

	public void start_tuple(final Attributes meta) throws SAXException {
		start_managedObject(meta);
		tupleString = "";
	}

	private void add(String path, IManagedObject obj) {
		if (path == null) {
			path = "/";
			if (markAsFilled) tree.hasBeenFilled("/");
		} else {
			tree.mkdirs(path);

			StringTokenizer st = new StringTokenizer(path, "/");
			String currentpath = "/";
			if (st.countTokens() > 0) {
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					currentpath += token + "/";
					if (markAsFilled) {
						tree.hasBeenFilled(currentpath);
						if (obj instanceof Folder)
							tree.hasBeenFilled(currentpath + obj.name());
					}
				}
			}
		}
		tree.add(path, obj);
	}

	private int getBinNum(int bin, int nBins) {
		switch (bin) {
		case -2:
			return 0;
		case -1:
			return nBins + 1;
		default:
			return bin + 1;
		}
	}

	public void start_managedObject(final org.xml.sax.Attributes atts)
			throws SAXException {
		start_managedObject(new AttributesAdapter(atts));
	}

	private void start_managedObject(final Attributes meta) throws SAXException {
		name = meta.getStringValue(NAME);
		title = meta.getStringValue(aida22 ? LABEL : TITLE);
		path = meta.getStringValue(PATH);
		options = meta.getStringValue(OPTIONS);
	}

	private int directionToInt(String direction) {
		if (direction.equals("x")) return 0;
		if (direction.equals("y")) return 1;
		if (direction.equals("z")) return 2;
		return 0;
	}
	
	private class AttributesAdapter implements Attributes {
		private org.xml.sax.Attributes atts;

		public AttributesAdapter(org.xml.sax.Attributes atts) {
			this.atts = atts;
		}

		public int[] getTags() {
			int[] tags = new int[atts.getLength()];
			for (int i=0; i<tags.length; i++) {
				tags[i] = AidaWBXMLLookup.getAttribute(atts.getQName(i));
			}
			return tags;
		}
		
		public int getType(int tag) {
			return atts.getValue(getAttributeName(tag)) == null ? -1 : AidaWBXMLLookup.getAttributeType(tag);
		}

		private String getAttributeName(int tag) {
			switch (tag) {
			case AidaWBXML.VALUE_BOOLEAN:
			case AidaWBXML.VALUE_BYTE:
			case AidaWBXML.VALUE_CHAR:
			case AidaWBXML.VALUE_DOUBLE:
			case AidaWBXML.VALUE_FLOAT:
			case AidaWBXML.VALUE_INT:
			case AidaWBXML.VALUE_LONG:
			case AidaWBXML.VALUE_SHORT:
			case AidaWBXML.VALUE_STRING:
				return "value";
			default:
				return AidaWBXMLLookup.getAttributeName(tag);
			}
		}

		public String getStringValue(int tag, String def) {
			String val = atts.getValue(getAttributeName(tag));
			return val != null ? val : def;
		}

		public double getDoubleValue(int tag, double def) {
			String val = atts.getValue(getAttributeName(tag));
			return val != null ? AidaWBXMLConverter.toDouble(-1, tag, val) : def;
		}

		public float getFloatValue(int tag, float def) {
			return (float) getDoubleValue(tag, def);
		}

		public long getLongValue(int tag, long def) {
			String val = atts.getValue(getAttributeName(tag));
			return val != null ? Long.parseLong(val) : def;
		}

		public int getIntValue(int tag, int def) {
			String val = atts.getValue(getAttributeName(tag));
			return val != null ? AidaWBXMLConverter.toInt(-1, tag, val) : def;
		}

		public short getShortValue(int tag, short def) {
			String val = atts.getValue(getAttributeName(tag));
			return val != null ? Short.parseShort(val) : def;
		}

		public char getCharValue(int tag, char def) {
			String val = atts.getValue(getAttributeName(tag));
			return val != null ? val.charAt(0) : def;
		}

		public byte getByteValue(int tag, byte def) {
			String val = atts.getValue(getAttributeName(tag));
			return val != null ? Byte.parseByte(val) : def;
		}

		public boolean getBooleanValue(int tag, boolean def) {
			String val = atts.getValue(getAttributeName(tag));
			return val != null ? AidaWBXMLConverter.toBoolean(-1, tag, val) : def;
		}

		public boolean getBooleanValue(int tag) {
			return AidaWBXMLConverter.toBoolean(-1, tag, atts
					.getValue(getAttributeName(tag)));
		}

		public boolean[] getBooleanArray(int tag) {
			// FIXME
			throw new NumberFormatException();
		}
		
		public byte getByteValue(int tag) {
			return Byte.parseByte(atts.getValue(getAttributeName(tag)));
		}

		public byte[] getByteArray(int tag) {
			// FIXME
			throw new NumberFormatException();
		}
		
		public char getCharValue(int tag) {
			return atts.getValue(getAttributeName(tag)).charAt(0);
		}

		public char[] getCharArray(int tag) {
			// FIXME
			throw new NumberFormatException();
		}
		
		public double getDoubleValue(int tag) {
			return AidaWBXMLConverter.toDouble(-1, tag, atts
					.getValue(getAttributeName(tag)));
		}

		public double[] getDoubleArray(int tag) {
			// FIXME
			throw new NumberFormatException();
		}
		
		public float getFloatValue(int tag) {
			return (float) getDoubleValue(tag);
		}

		public float[] getFloatArray(int tag) {
			// FIXME
			throw new NumberFormatException();
		}
		
		public int getIntValue(int tag) {
			return AidaWBXMLConverter.toInt(-1, tag, atts
					.getValue(getAttributeName(tag)));
		}

		public int[] getIntArray(int tag) {
			// FIXME
			throw new NumberFormatException();
		}
		
		public long getLongValue(int tag) {
			return Long.parseLong(atts.getValue(getAttributeName(tag)));
		}

		public long[] getLongArray(int tag) {
			// FIXME
			throw new NumberFormatException();
		}
		
		public short getShortValue(int tag) {
			return Short.parseShort(atts.getValue(getAttributeName(tag)));
		}

		public short[] getShortArray(int tag) {
			// FIXME
			throw new NumberFormatException();
		}
		
		public String getStringValue(int tag) {
			return atts.getValue(getAttributeName(tag));
		}
		
		public String[] getStringArray(int tag) {
			// FIXME
			throw new NumberFormatException();
		}		
	};
}
