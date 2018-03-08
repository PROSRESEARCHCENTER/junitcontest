package jas.plot;
import java.text.NumberFormat;
// Used only by the DoubleAxis type to format number.
// Not meant to be public.
final class DoubleNumberFormatter
{
	DoubleNumberFormatter(int power)
	{
		if (formatter == null)
			formatter = NumberFormat.getInstance();
		this.power = power;
	}
	void setFractionDigits(int fractDigits)
	{
		formatter.setMinimumFractionDigits(fractDigits);
		formatter.setMaximumFractionDigits(fractDigits);
	}
	String format(final double d)
	{
		return formatter.format(power != 0 ? d / Math.pow(10.0, power) : d);
	}
	private static NumberFormat formatter = null;
	private int power;
}
