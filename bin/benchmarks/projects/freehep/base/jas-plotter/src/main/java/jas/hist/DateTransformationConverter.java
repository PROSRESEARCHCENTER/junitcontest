package jas.hist;
import jas.plot.DateCoordinateTransformation;
import jas.plot.DoubleCoordinateTransformation;

class DateTransformationConverter implements DoubleCoordinateTransformation
{
	private DateCoordinateTransformation source;
	
	DateTransformationConverter(DateCoordinateTransformation source)
	{
		this.source = source;
	}	
	public double convert(double d)
	{
		return source.convert((long) (d*1000));
	}
	public double unConvert(double i)
	{
		return source.map(i)/1000.;
	}
	public double getPlotMin()
	{
		return source.getAxisMin()/1000.;
	}
	public double getPlotMax()
	{
		return source.getAxisMax()/1000.;
	}
}
