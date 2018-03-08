package jas.plot;

public interface StringCoordinateTransformation extends CoordinateTransformation
{
	double convert(String d);
	double binWidth();
}
