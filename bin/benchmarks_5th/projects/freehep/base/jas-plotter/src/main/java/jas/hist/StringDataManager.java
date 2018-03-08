package jas.hist;

import jas.plot.DataArea;
import jas.plot.DoubleAxis;
import jas.plot.Legend;
import jas.plot.StringAxis;

import java.util.Enumeration;
import java.util.Hashtable;

final class StringDataManager
	extends OneDDataManager
{
	StringDataManager(JASHist plot, DataArea da, Legend l, StatisticsBlock stats)
	{
		super(plot, da, l, stats);
		
		xAxis = new StringAxis();
		yAxis = new DoubleAxis();
		yAxis.setUseSuggestedRange(true);
		
		// Configure the Axes

		xm.setDataManager(this,true , xAxis);
		xm.setFixed(true);
		ym[0].setDataManager(this,false, yAxis);
	}
	JASHistData add(DataSource ds)
	{
		Rebinnable1DHistogramData d = (Rebinnable1DHistogramData) ds;
		if (d.getAxisType() != d.STRING) throw new DataManagerException("Data incompatible with String axis");
		JASHist1DHistogramData dw = new JASHist1DHistogramData(this,d);
		data.addElement(dw);
		return dw;
	}
	void destroy() // detaches data, but doesn't set up the plot for further use
	{
		Enumeration e;
		for (e = data.elements(); e.hasMoreElements();)
		{
			JASHistData d = (JASHistData) e.nextElement();
			d.deleteNormalizationObserver();
			d.show(false);
		}
		data.removeAllElements();
	}
	void XAxisUpdated()
	{
	}
	void computeXAxisRange()
	{
		if (data.isEmpty()) return;

		Hashtable labels = new Hashtable();
      String[] original = null;
      int n = 0;
      
		for (Enumeration e = data.elements(); e.hasMoreElements();)
		{
			JASHist1DHistogramData dw = (JASHist1DHistogramData) e.nextElement();
			if (!dw.isShowing()) continue;

			String[] label = dw.getAxisLabels();
         if (n++ == 0) original = label;

			for (int i=0; i<label.length; i++)
			{
				labels.put(label[i],label[i]);
			}
		}
		if (n == 0) return;
      else if (n > 1)
      {
         String[] result = new String[labels.size()];
         Enumeration e = labels.keys();
         for (int i=0; e.hasMoreElements(); i++)
         {
            result[i] = (String) e.nextElement();
         }
         xAxis.setLabels(result);
      }
      else
      {
         xAxis.setLabels(original);
      }
		xAxis.getAxis().invalidate();
	}
	void computeYAxisRange()
	{
		if (data.isEmpty()) return;
		if (!ym[0].getRangeAutomatic()) return;

		double ymin = 0;
		double ymax = 0;
		boolean first = true;

		for (Enumeration e = data.elements(); e.hasMoreElements();)
		{
			JASHist1DHistogramData dw = (JASHist1DHistogramData) e.nextElement();
			if (!dw.isShowing()) continue;

			if (first)
			{
				ymin = dw.getYMin();
				ymax = dw.getYMax();
				first = false;
			}
			else
			{
				ymin = Math.min(ymin,dw.getYMin());
				ymax = Math.max(ymax,dw.getYMax());
			}
		}
		if  (ymax <= ymin) ymax = ymin + 1;
		if (yAxis.isLogarithmic()) ymin = Math.max(ymin,1); // TODO: Correct calculation

		// Only update the axis if the new range is outside of the old range, or occupies less 
		// than 75% of the old range

		double oldYMin = yAxis.getPlotMin();
		double oldYMax = yAxis.getPlotMax();
		if (ymin < oldYMin || ymax > oldYMax || (ymax - ymin) / (oldYMax - oldYMin) < .75)
		{
 			yAxis.setMin(ymin);
			yAxis.setMax(ymax);
			yAxis.getAxis().revalidate();
		}
	}
	private StringAxis xAxis;
	private DoubleAxis yAxis;
}
