package hep.aida.ref.histogram;

import hep.aida.IAxis;
import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.ICloud3D;
import hep.aida.IDataPoint;
import hep.aida.IDataPointSet;
import hep.aida.IDataPointSetFactory;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.IHistogramFactory;
import hep.aida.IProfile1D;
import hep.aida.IProfile2D;
import hep.aida.ITree;
import hep.aida.ref.AidaUtils;
import hep.aida.ref.tree.Tree;

/**
 * Basic user-level interface for creating a factory
 * of IDataPointSet.
 *
 * @author The AIDA team @ SLAC.
 *
 */
public class DataPointSetFactory implements IDataPointSetFactory {

    private Tree tree;
    
    private String nameInPath( String path ) {
        return AidaUtils.parseName(path);
    }
    
    private String parentPath( String path ) {
        return AidaUtils.parseDirName(path);
    }

  /**
   * Create an IDataPointSetFactory.
   * @param t The ITree which created IDataPointSet will be associated to.
   * @throws     IllegalArgumentException if tree is null.
   */
    public DataPointSetFactory(ITree t) {
	this.tree = null;
	if (t instanceof hep.aida.ref.tree.Tree) this.tree = (hep.aida.ref.tree.Tree) t;
    }

    public DataPointSetFactory() { this.tree = null; }

    public IDataPointSet create(String path, String title, int dimOfPoints) {
        return create(path, title, dimOfPoints, null);
    }
    public IDataPointSet create(String path, String title, int dimOfPoints, String options) {
	DataPointSet set =  new DataPointSet(nameInPath(path), title, dimOfPoints, options);
	if (tree != null) tree.addFromFactory( parentPath(path),set);
	return set;
    }

    public IDataPointSet create(String pathAndTitle, int dimOfPoints, String options) {
	return  create(pathAndTitle, nameInPath(pathAndTitle), dimOfPoints, options);
    }

    public IDataPointSet create(String pathAndTitle, int dimOfPoints) {
	return  create(pathAndTitle, nameInPath(pathAndTitle), dimOfPoints);
    }

    public IDataPointSet createCopy(String path, IDataPointSet dataPointSet) {
	int dimension = dataPointSet.dimension();
	int size = dataPointSet.size();
	DataPointSet set = new DataPointSet(nameInPath(path), dataPointSet.title(), dimension, size);
	for (int i=0; i<size; i++) { set.setPoint(i, new DataPoint(dataPointSet.point(i))); }
	if (tree != null) tree.addFromFactory( parentPath(path),set);
        HistogramFactory.copy(set.annotation(),dataPointSet.annotation());
        return set;
    }

    public void destroy(IDataPointSet dataPointSet) throws IllegalArgumentException {
	//dataPointSet.clear();
	if (tree != null) {
	    String path = tree.findPath((DataPointSet) dataPointSet);
	    tree.rm(path);
	}
    }

    public IDataPointSet create(String path, IHistogram1D hist) throws IllegalArgumentException {
        return create( path, hist, "" );
    }

    public IDataPointSet create(String path, IHistogram1D hist, String options) throws IllegalArgumentException {
	int dim = 2;
	IAxis axis = hist.axis();
	int nBins = axis.bins();
	DataPointSet set = new DataPointSet(nameInPath(path), hist.title(), dim, nBins);
	double[] val = new double[dim];
	double[] err = new double[dim];
	for (int i=0; i<nBins; i++) {
	    if (hist instanceof hep.aida.ref.histogram.Histogram1D) {
		val[dim-2] = hist.binMean(i);
		err[dim-2] = ((hep.aida.ref.histogram.Histogram1D) hist).binRms(i);
	    } else {
		val[dim-2] = (axis.binUpperEdge(i) + axis.binLowerEdge(i))/2. ;
		err[dim-2] = 0;
	    }

	    val[dim-1] = hist.binHeight(i);
	    err[dim-1] = hist.binError(i);

	    set.setPoint(i, new DataPoint(val ,err));
	}
	if (tree != null) tree.addFromFactory( parentPath(path),set);
	return set;
    }

    public IDataPointSet create(String path, IHistogram2D hist) throws IllegalArgumentException {
        return create( path, hist, "" );
    }
    
    public IDataPointSet create(String path, IHistogram2D hist, String options) throws IllegalArgumentException {
	int dim = 3;
	IAxis axisX = hist.xAxis();
	IAxis axisY = hist.yAxis();
	int nBinsX = axisX.bins();
	int nBinsY = axisY.bins();
	int nBins = nBinsX * nBinsY;
	DataPointSet set = new DataPointSet(nameInPath(path), hist.title(), dim, nBins);
	double[] val = new double[dim];
	double[] err = new double[dim];
	int index = 0;
	for (int i=0; i<nBinsX; i++) {
	    for (int j=0; j<nBinsY; j++) {
		if (hist instanceof hep.aida.ref.histogram.Histogram2D) {
		    val[dim-3] = hist.binMeanX(i, j);
		    err[dim-3] = ((hep.aida.ref.histogram.Histogram2D) hist).binRmsX(i, j);

		    val[dim-2] = hist.binMeanY(i, j);
		    err[dim-2] = ((hep.aida.ref.histogram.Histogram2D) hist).binRmsY(i, j);
		} else {
		    val[dim-3] = (axisX.binUpperEdge(i) + axisX.binLowerEdge(i))/2. ;
		    err[dim-3] = 0;

		    val[dim-2] = (axisY.binUpperEdge(j) + axisY.binLowerEdge(j))/2. ;
		    err[dim-2] = 0;
		}
		val[dim-1] = hist.binHeight(i, j);
		err[dim-1] = hist.binError(i,j);

		set.setPoint(index, new DataPoint(val ,err));
		index++;
	    }
	}
	if (tree != null) tree.addFromFactory( parentPath(path),set);
	return set;
    }

    public IDataPointSet create(String path, IHistogram3D hist) throws IllegalArgumentException {
        return create( path, hist, "" );
    }

    public IDataPointSet create(String path, IHistogram3D hist, String options) throws IllegalArgumentException {
	int dim = 4;
	IAxis axisX = hist.xAxis();
	IAxis axisY = hist.yAxis();
	IAxis axisZ = hist.zAxis();
	int nBinsX = axisX.bins();
	int nBinsY = axisY.bins();
	int nBinsZ = axisZ.bins();
	int nBins = nBinsX * nBinsY * nBinsZ;
	DataPointSet set = new DataPointSet(nameInPath(path), hist.title(), dim, nBins);
	double[] val = new double[dim];
	double[] err = new double[dim];
	int index = 0;
	for (int i=0; i<nBinsX; i++) {
	    for (int j=0; j<nBinsY; j++) {
		for (int k=0; k<nBinsY; k++) {
		if (hist instanceof hep.aida.ref.histogram.Histogram3D) {
		    val[dim-4] = hist.binMeanX(i, j, k);
		    err[dim-4] = ((hep.aida.ref.histogram.Histogram3D) hist).binRmsX(i, j, k);

		    val[dim-3] = hist.binMeanY(i, j, k);
		    err[dim-3] = ((hep.aida.ref.histogram.Histogram3D) hist).binRmsY(i, j, k);

		    val[dim-2] = hist.binMeanZ(i, j, k);
		    err[dim-2] = ((hep.aida.ref.histogram.Histogram3D) hist).binRmsZ(i, j, k);
		} else {
		    val[dim-4] = (axisX.binUpperEdge(i) + axisX.binLowerEdge(i))/2. ;
		    err[dim-4] = 0;

		    val[dim-3] = (axisY.binUpperEdge(j) + axisY.binLowerEdge(j))/2. ;
		    err[dim-3] = 0;

		    val[dim-2] = (axisZ.binUpperEdge(k) + axisZ.binLowerEdge(k))/2. ;
		    err[dim-2] = 0;
		}
		    val[dim-1] = hist.binHeight(i, j, k);
		    err[dim-1] = hist.binError(i, j, k);

		    set.setPoint(index, new DataPoint(val ,err));
		    index++;
		}
	    }
	}
	if (tree != null) tree.addFromFactory( parentPath(path),set);
	return set;
    }

    public IDataPointSet create(String path, ICloud1D cloud) throws IllegalArgumentException {
        return create( path, cloud, "" );
    }

    public IDataPointSet create(String path, ICloud1D cloud, String options) throws IllegalArgumentException {
	if (cloud.isConverted()) return create(path, cloud.histogram());

	int dim = 2;
	int nBins = cloud.entries();
	DataPointSet set = new DataPointSet(nameInPath(path), cloud.title(), dim, nBins);
	double[] val = new double[dim];
	double[] err = new double[dim];
	for (int i=0; i<nBins; i++)
	{
	    val[dim-2] = cloud.value(i);
 	    err[dim-2] = 0;

	    val[dim-1] = cloud.weight(i);
 	    err[dim-1] = 0;

	    set.setPoint(i, new DataPoint(val ,err));
	}
	if (tree != null) tree.addFromFactory( parentPath(path),set);
	return set;
    }

    public IDataPointSet create(String path, ICloud2D cloud) throws IllegalArgumentException {
        return create( path, cloud, "" );
    }

    public IDataPointSet create(String path, ICloud2D cloud, String options) throws IllegalArgumentException {
	if (cloud.isConverted()) return create(path, cloud.histogram());
	int dim = 3;
	int nBins = cloud.entries();
	DataPointSet set = new DataPointSet(nameInPath(path), cloud.title(), dim, nBins);
	double[] val = new double[dim];
	double[] err = new double[dim];
	for (int i=0; i<nBins; i++)
	{
	    val[dim-3] = cloud.valueX(i);
 	    err[dim-3] = 0;

	    val[dim-2] = cloud.valueY(i);
 	    err[dim-2] = 0;

	    val[dim-1] = cloud.weight(i);
 	    err[dim-1] = 0;

	    set.setPoint(i, new DataPoint(val ,err));
	}
	if (tree != null) tree.addFromFactory( parentPath(path),set);
	return set;
    }

    public IDataPointSet create(String path, ICloud3D cloud) throws IllegalArgumentException {
        return create( path, cloud, "" );
    }

    public IDataPointSet create(String path, ICloud3D cloud, String options) throws IllegalArgumentException {
	if (cloud.isConverted()) return create(path, cloud.histogram());
	int dim = 4;
	int nBins = cloud.entries();
	DataPointSet set = new DataPointSet(nameInPath(path), cloud.title(), dim, nBins);
	double[] val = new double[dim];
	double[] err = new double[dim];
	for (int i=0; i<nBins; i++)
	{
	    val[dim-4] = cloud.valueX(i);
 	    err[dim-4] = 0;

	    val[dim-3] = cloud.valueY(i);
 	    err[dim-3] = 0;

	    val[dim-2] = cloud.valueZ(i);
 	    err[dim-2] = 0;

	    val[dim-1] = cloud.weight(i);
 	    err[dim-1] = 0;

	    set.setPoint(i, new DataPoint(val ,err));
	}
	if (tree != null) tree.addFromFactory( parentPath(path),set);
	return set;
    }

    public IDataPointSet create(String path, IProfile1D profile) throws IllegalArgumentException {
        return create(path, profile, "");
    }

    public IDataPointSet create(String path, IProfile1D profile, String options) throws IllegalArgumentException {
	int dim = 2;
	IAxis axis = profile.axis();
	int nBins = axis.bins();
	DataPointSet set = new DataPointSet(nameInPath(path), profile.title(), dim, nBins);
	double[] val = new double[dim];
	double[] err = new double[dim];
	for (int i=0; i<nBins; i++)
	{
	    val[dim-2] = (axis.binUpperEdge(i) + axis.binLowerEdge(i))/2. ;
	    err[dim-2] = 0;

	    val[dim-1] = profile.binHeight(i);
	    err[dim-1] = profile.binRms(i);

	    set.setPoint(i, new DataPoint(val ,err));
	}
	if (tree != null) tree.addFromFactory( parentPath(path),set);
	return set;
    }

    public IDataPointSet create(String path, IProfile2D profile) throws IllegalArgumentException {
        return create(path, profile, "");
    }

    public IDataPointSet create(String path, IProfile2D profile, String options) throws IllegalArgumentException {
	int dim = 3;
	IAxis axisX = profile.xAxis();
	IAxis axisY = profile.yAxis();
	int nBinsX = axisX.bins();
	int nBinsY = axisY.bins();
	int nBins = nBinsX * nBinsY;
	DataPointSet set = new DataPointSet(nameInPath(path), profile.title(), dim, nBins);
	double[] val = new double[dim];
	double[] err = new double[dim];
	for (int i=0; i<nBinsX; i++) {
	    for (int j=0; j<nBinsY; j++) {

		val[dim-3] = (axisX.binUpperEdge(i) + axisX.binLowerEdge(i))/2. ;
		err[dim-3] = 0;

		val[dim-2] = (axisY.binUpperEdge(j) + axisY.binLowerEdge(j))/2. ;
		err[dim-2] = 0;

		val[dim-1] = profile.binHeight(i, j);
		err[dim-1] = profile.binRms(i, j);

		set.setPoint(i, new DataPoint(val ,err));
	    }
	}
	if (tree != null) tree.addFromFactory( parentPath(path),set);
	return set;
    }

    public IDataPointSet add(String path, IDataPointSet dataPointSet1, IDataPointSet dataPointSet2) throws IllegalArgumentException {
	if (dataPointSet1.dimension() != dataPointSet2.dimension() || dataPointSet1.size() != dataPointSet2.size())
	    throw new IllegalArgumentException("DataSets have different dimension or size: dimension = "+dataPointSet1.dimension()+
					       ",  "+dataPointSet2.dimension()+";   size = "+dataPointSet1.size()+",  "+dataPointSet2.size());
	DataPointSet set = new DataPointSet(nameInPath(path), dataPointSet1.title(), dataPointSet1.dimension(), dataPointSet1.size());
	for (int i=0; i<dataPointSet1.size(); i++) {
	    IDataPoint p1 = dataPointSet1.point(i);
	    IDataPoint p2 = dataPointSet2.point(i);
	    double[] val = new double[dataPointSet1.dimension()];
	    double[] errMinus = new double[dataPointSet1.dimension()];
	    double[] errPlus = new double[dataPointSet1.dimension()];
	    for (int j=0; j<dataPointSet1.dimension(); j++) {
		val[j]      = p1.coordinate(j).value() + p2.coordinate(j).value();
		errMinus[j] = p1.coordinate(j).errorMinus() * p1.coordinate(j).errorMinus();
		errMinus[j] = errMinus[j] + p2.coordinate(j).errorMinus() * p2.coordinate(j).errorMinus();
		errMinus[j] = Math.sqrt(errMinus[j]);
		errPlus[j] = p1.coordinate(j).errorPlus() * p1.coordinate(j).errorPlus();
		errPlus[j] = errPlus[j] + p2.coordinate(j).errorPlus() * p2.coordinate(j).errorPlus();
		errPlus[j] = Math.sqrt(errPlus[j]);
	    }
	    set.setPoint(i, new DataPoint(val, errMinus, errPlus));
	}

	if (tree != null) tree.addFromFactory( parentPath(path),set);
	return set;
    }

    public IDataPointSet subtract(String path, IDataPointSet dataPointSet1, IDataPointSet dataPointSet2) throws IllegalArgumentException {
	if (dataPointSet1.dimension() != dataPointSet2.dimension() || dataPointSet1.size() != dataPointSet2.size())
	    throw new IllegalArgumentException("DataSets have different dimension or size: dimension = "+dataPointSet1.dimension()+
					       ",  "+dataPointSet2.dimension()+";   size = "+dataPointSet1.size()+",  "+dataPointSet2.size());
	DataPointSet set = new DataPointSet(nameInPath(path), dataPointSet1.title(), dataPointSet1.dimension(), dataPointSet1.size());
	for (int i=0; i<dataPointSet1.size(); i++) {
	    IDataPoint p1 = dataPointSet1.point(i);
	    IDataPoint p2 = dataPointSet2.point(i);
	    double[] val = new double[dataPointSet1.dimension()];
	    double[] errMinus = new double[dataPointSet1.dimension()];
	    double[] errPlus = new double[dataPointSet1.dimension()];
	    for (int j=0; j<dataPointSet1.dimension(); j++) {
		val[j]      = p1.coordinate(j).value() - p2.coordinate(j).value();
		errMinus[j] = p1.coordinate(j).errorMinus() * p1.coordinate(j).errorMinus();
		errMinus[j] = errMinus[j] + p2.coordinate(j).errorMinus() * p2.coordinate(j).errorMinus();
		errMinus[j] = Math.sqrt(errMinus[j]);
		errPlus[j] = p1.coordinate(j).errorPlus() * p1.coordinate(j).errorPlus();
		errPlus[j] = errPlus[j] + p2.coordinate(j).errorPlus() * p2.coordinate(j).errorPlus();
		errPlus[j] = Math.sqrt(errPlus[j]);
	    }
	    set.setPoint(i, new DataPoint(val, errMinus, errPlus));
	}

	if (tree != null) tree.addFromFactory( parentPath(path),set);
	return set;
    }

    public IDataPointSet multiply(String path, IDataPointSet dataPointSet1, IDataPointSet dataPointSet2) throws IllegalArgumentException {
	if (dataPointSet1.dimension() != dataPointSet2.dimension() || dataPointSet1.size() != dataPointSet2.size())
	    throw new IllegalArgumentException("DataSets have different dimension or size: dimension = "+dataPointSet1.dimension()+
					       ",  "+dataPointSet2.dimension()+";   size = "+dataPointSet1.size()+",  "+dataPointSet2.size());
	DataPointSet set = new DataPointSet(nameInPath(path), dataPointSet1.title(), dataPointSet1.dimension(), dataPointSet1.size());
	for (int i=0; i<dataPointSet1.size(); i++) {
	    IDataPoint p1 = dataPointSet1.point(i);
	    IDataPoint p2 = dataPointSet2.point(i);
	    double[] val = new double[dataPointSet1.dimension()];
	    double[] errMinus = new double[dataPointSet1.dimension()];
	    double[] errPlus = new double[dataPointSet1.dimension()];
	    for (int j=0; j<dataPointSet1.dimension(); j++) {
		val[j]      = p1.coordinate(j).value() * p2.coordinate(j).value();
		errMinus[j] = Math.pow(p1.coordinate(j).errorMinus() * p2.coordinate(j).value(), 2);
		errMinus[j] = errMinus[j] + Math.pow(p1.coordinate(j).value() * p2.coordinate(j).errorMinus(), 2);
		errMinus[j] = Math.sqrt(errMinus[j]);
		errPlus[j] = Math.pow(p1.coordinate(j).errorPlus() * p2.coordinate(j).value(), 2);
		errPlus[j] = errPlus[j] + Math.pow(p1.coordinate(j).value() * p2.coordinate(j).errorPlus(), 2);
		errPlus[j] = Math.sqrt(errPlus[j]);
	    }
	    set.setPoint(i, new DataPoint(val, errMinus, errPlus));
	}

	if (tree != null) tree.addFromFactory( parentPath(path),set);
	return set;
    }

    public IDataPointSet divide(String path, IDataPointSet dataPointSet1, IDataPointSet dataPointSet2) throws IllegalArgumentException {
	if (dataPointSet1.dimension() != dataPointSet2.dimension() || dataPointSet1.size() != dataPointSet2.size())
	    throw new IllegalArgumentException("DataSets have different dimension or size: dimension = "+dataPointSet1.dimension()+
					       ",  "+dataPointSet2.dimension()+";   size = "+dataPointSet1.size()+",  "+dataPointSet2.size());
	DataPointSet set = new DataPointSet(nameInPath(path), dataPointSet1.title(), dataPointSet1.dimension(), dataPointSet1.size());
	for (int i=0; i<dataPointSet1.size(); i++) {
	    IDataPoint p1 = dataPointSet1.point(i);
	    IDataPoint p2 = dataPointSet2.point(i);
	    double[] val = new double[dataPointSet1.dimension()];
	    double[] errMinus = new double[dataPointSet1.dimension()];
	    double[] errPlus = new double[dataPointSet1.dimension()];
	    for (int j=0; j<dataPointSet1.dimension(); j++) {
		val[j]      = p1.coordinate(j).value() / p2.coordinate(j).value();
		errMinus[j] = Math.pow(p1.coordinate(j).errorMinus() / p2.coordinate(j).value(), 2);
		errMinus[j] = errMinus[j] + Math.pow(p1.coordinate(j).value() * p2.coordinate(j).errorMinus()/Math.pow(p2.coordinate(j).value(),2), 2);
		errMinus[j] = Math.sqrt(errMinus[j]);
		errPlus[j] = Math.pow(p1.coordinate(j).errorPlus() / p2.coordinate(j).value(), 2);
		errPlus[j] = errPlus[j] + Math.pow(p1.coordinate(j).value() * p2.coordinate(j).errorPlus() / Math.pow(p2.coordinate(j).value(),2), 2);
		errPlus[j] = Math.sqrt(errPlus[j]);
	    }
	    set.setPoint(i, new DataPoint(val, errMinus, errPlus));
	}

	if (tree != null) tree.addFromFactory( parentPath(path),set);
	return set;
    }

    public IDataPointSet weightedMean(String path, IDataPointSet dataPointSet1, IDataPointSet dataPointSet2) throws IllegalArgumentException {
	if (dataPointSet1.dimension() != dataPointSet2.dimension() || dataPointSet1.size() != dataPointSet2.size())
	    throw new IllegalArgumentException("DataSets have different dimension or size: dimension = "+dataPointSet1.dimension()+
					       ",  "+dataPointSet2.dimension()+";   size = "+dataPointSet1.size()+",  "+dataPointSet2.size());
	DataPointSet set = new DataPointSet(nameInPath(path), dataPointSet1.title(), dataPointSet1.dimension(), dataPointSet1.size());
	for (int i=0; i<dataPointSet1.size(); i++) {
	    IDataPoint p1 = dataPointSet1.point(i);
	    IDataPoint p2 = dataPointSet2.point(i);
	    double[] val = new double[dataPointSet1.dimension()];
	    double[] err = new double[dataPointSet1.dimension()];
	    for (int j=0; j<dataPointSet1.dimension(); j++) {
		if (p1.coordinate(j).errorPlus() == p1.coordinate(j).errorMinus() &&
		    p2.coordinate(j).errorPlus() == p2.coordinate(j).errorMinus())
		{
		    err[j] = 1./(1./Math.pow(p1.coordinate(j).errorPlus(), 2) + 1./Math.pow(p2.coordinate(j).errorPlus(), 2));
		    val[j]  = p1.coordinate(j).value() / Math.pow(p1.coordinate(j).errorPlus(),2);
		    val[j] += p2.coordinate(j).value() / Math.pow(p2.coordinate(j).errorPlus(),2);
		    val[j]  = val[j]*err[j];
		    err[j]  = Math.sqrt(err[j]);
		}
		else throw new IllegalArgumentException("Can not use \"weightedMean()\" method with Asymmetric errors! Problem Point index="+j);
	    }
	    set.setPoint(i, new DataPoint(val, err));
	}

	if (tree != null) tree.addFromFactory( parentPath(path),set);
	return set;
    }    
    
    public IDataPointSet createX(String path, double[] xVal, double[] xErr) throws IllegalArgumentException {
        return createX(path, nameInPath(path), xVal, xErr, xErr);
    }
    
    public IDataPointSet createX(String path, String title, double[] xVal, double[] xErr) throws IllegalArgumentException {
        return createX(path, title, xVal, xErr, xErr);
    }
    
    public IDataPointSet createX(String path, double[] xVal, double[] xErrPlus, double[] xErrMinus) throws IllegalArgumentException {
        return createX(path, nameInPath(path), xVal, xErrPlus, xErrMinus);
    }
    
    public IDataPointSet createX(String path, String title, double[] xVal, double[] xErrPlus, double[] xErrMinus) throws IllegalArgumentException {
        int nPoints = xVal.length;
        double[] yVal = new double[nPoints];
        double[] yErr = new double[nPoints];
        for ( int i = 0; i < nPoints; i++ ) {
            yVal[i] = i;
            yErr[i] = 0;
        }
        return createXY(path,title,xVal,yVal,xErrPlus,yErr,xErrMinus,yErr);
    }
    
    public IDataPointSet createY(String path, double[] yVal, double[] yErr) throws IllegalArgumentException {
        return createY(path, nameInPath(path), yVal, yErr, yErr);
    }
    
    public IDataPointSet createY(String path, double[] yVal, double[] yErrPlus, double[] yErrMinus) throws IllegalArgumentException {
        return createY(path, nameInPath(path), yVal, yErrPlus, yErrMinus);
    }
    
    public IDataPointSet createY(String path, String title, double[] yVal, double[] yErr) throws IllegalArgumentException {
        return createY(path, title, yVal, yErr, yErr);
    }
    
    public IDataPointSet createY(String path, String title, double[] yVal, double[] yErrPlus, double[] yErrMinus) throws IllegalArgumentException {
        int nPoints = yVal.length;
        double[] xVal = new double[nPoints];
        double[] xErr = new double[nPoints];
        for ( int i = 0; i < nPoints; i++ ) {
            xVal[i] = i;
            xErr[i] = 0;
        }
        return createXY(path,title,xVal,yVal,xErr,yErrPlus,xErr,yErrMinus);
    }
    
    public IDataPointSet createXY(String path, double[] xVal, double[] yVal, double[] xErr, double[] yErr) throws IllegalArgumentException {
        return createXY(path, nameInPath(path), xVal, yVal, xErr, yErr, xErr, yErr);
    }
    
    public IDataPointSet createXY(String path, String title, double[] xVal, double[] yVal, double[] xErr, double[] yErr) throws IllegalArgumentException {
        return createXY(path, title, xVal, yVal, xErr, yErr, xErr, yErr);
    }
    
    public IDataPointSet createXY(String path, double[] xVal, double[] yVal, double[] xErrPlus, double[] yErrPlus, double[] xErrMinus, double[] yErrMinus) throws IllegalArgumentException {
        return createXY(path, nameInPath(path), xVal, yVal, xErrPlus, yErrPlus, xErrMinus, yErrMinus);
    }
    
    public IDataPointSet createXY(String path, String title, double[] xVal, double[] yVal, double[] xErrPlus, double[] yErrPlus, double[] xErrMinus, double[] yErrMinus) throws IllegalArgumentException {
        int nPoints = xVal.length;
        if ( nPoints != yVal.length ) throw new IllegalArgumentException("The x value and y value arrays have inconsistent lenghts");
        if ( nPoints != xErrPlus.length ) throw new IllegalArgumentException("The x value and plus error arrays have inconsistent lenghts");
        if ( nPoints != xErrMinus.length ) throw new IllegalArgumentException("The x value and minus error arrays have inconsistent lenghts");
        if ( nPoints != yErrPlus.length ) throw new IllegalArgumentException("The x value and y plus error arrays have inconsistent lenghts");
        if ( nPoints != yErrMinus.length ) throw new IllegalArgumentException("The x value and y minus error arrays have inconsistent lenghts");
	DataPointSet dataPointSet = new DataPointSet(nameInPath(path), title, 2, nPoints);        
        dataPointSet.setCoordinate(0,xVal,xErrPlus,xErrMinus);
        dataPointSet.setCoordinate(1,yVal,yErrPlus,yErrMinus);
	if (tree != null) tree.addFromFactory( parentPath(path),dataPointSet);
        return dataPointSet;
    }
    
    public IDataPointSet createXYZ(String path, double[] xVal, double[] yVal, double[] zVal, double[] xErr, double[] yErr, double[] zErr) throws IllegalArgumentException {
        return createXYZ(path, nameInPath(path), xVal, yVal, zVal, xErr, yErr, zErr, xErr, yErr, zErr);
    }
    
    public IDataPointSet createXYZ(String path, String title, double[] xVal, double[] yVal, double[] zVal, double[] xErr, double[] yErr, double[] zErr) throws IllegalArgumentException {
        return createXYZ(path, title, xVal, yVal, zVal, xErr, yErr, zErr, xErr, yErr, zErr);
    }
    
    public IDataPointSet createXYZ(String path, double[] xVal, double[] yVal, double[] zVal, double[] xErrPlus, double[] yErrPlus, double[] zErrPlus, double[] xErrMinus, double[] yErrMinus, double[] zErrMinus) throws IllegalArgumentException {
        return createXYZ(path, nameInPath(path), xVal, yVal, zVal, xErrPlus, yErrPlus, zErrPlus, xErrMinus, yErrMinus, zErrMinus);
    }
    
    public IDataPointSet createXYZ(String path, String title, double[] xVal, double[] yVal, double[] zVal, double[] xErrPlus, double[] yErrPlus, double[] zErrPlus, double[] xErrMinus, double[] yErrMinus, double[] zErrMinus) throws IllegalArgumentException {
        int nPoints = xVal.length;
        if ( nPoints != yVal.length ) throw new IllegalArgumentException("The x value and y value arrays have inconsistent lenghts");
        if ( nPoints != zVal.length ) throw new IllegalArgumentException("The x value and z value arrays have inconsistent lenghts");
        if ( nPoints != xErrPlus.length ) throw new IllegalArgumentException("The x value and plus error arrays have inconsistent lenghts");
        if ( nPoints != xErrMinus.length ) throw new IllegalArgumentException("The x value and minus error arrays have inconsistent lenghts");
        if ( nPoints != yErrPlus.length ) throw new IllegalArgumentException("The x value and y plus error arrays have inconsistent lenghts");
        if ( nPoints != yErrMinus.length ) throw new IllegalArgumentException("The x value and y minus error arrays have inconsistent lenghts");
        if ( nPoints != zErrPlus.length ) throw new IllegalArgumentException("The x value and z plus error arrays have inconsistent lenghts");
        if ( nPoints != zErrMinus.length ) throw new IllegalArgumentException("The x value and z minus error arrays have inconsistent lenghts");
	DataPointSet dataPointSet = new DataPointSet(nameInPath(path), title, 3, nPoints);        
        dataPointSet.setCoordinate(0,xVal,xErrPlus,xErrMinus);
        dataPointSet.setCoordinate(1,yVal,yErrPlus,yErrMinus);
        dataPointSet.setCoordinate(1,zVal,zErrPlus,zErrMinus);
	if (tree != null) tree.addFromFactory( parentPath(path),dataPointSet);
        return dataPointSet;
    }
    
    
} // class or interface
