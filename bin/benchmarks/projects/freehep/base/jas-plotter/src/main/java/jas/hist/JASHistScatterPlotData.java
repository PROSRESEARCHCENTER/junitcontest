package jas.hist;

class JASHistScatterPlotData extends JASHist2DScatterData
{
	JASHistScatterPlotData(final DataManager dm, final ScatterPlotSource ds)
	{
		super(dm, (HasScatterPlotData) new jas.hist.util.ScatterTwoDAdapter(ds));
	}
	private JASHistScatterPlotStyle style;
	JASHistStyle createStyle()
	{
		style = new JASHistScatterPlotStyle();
		style.setDisplayAsScatterPlot(true);
		return style;
	}
	//wrong!!!doesnt meeet dtd rules
	/*public void writeAsXML(XMLPrintWriter pw) 
	{
		pw.print("<data2d type=\"scatter2d\" title=\"" + getTitle() + "\" ");
		pw.indent();
		String histStyleName = JASHist2DHistogramStyle.getHistStyleName(style.getHistStyle());
		pw.print("dataHistStyles=\"" + histStyleName + "\" ");
		if (histStyleName.equals("STYLE_COLORMAP")) {
			pw.print("dataColorMapScheme=\"" +
				JASHist2DHistogramStyle.getColorMapSchemeName(style.getColorMapScheme()) + "\" ");
		}
		pw.print("shapeColor=\"" +
			jas.util.ColorConverter.colorToString(style.getShapeColor()) + "\" ");
		pw.print("overFlowBinColor=\"" +
			jas.util.ColorConverter.colorToString(style.getOverflowBinColor()) + "\" ");
		pw.print("startDataColor=\"" +
			jas.util.ColorConverter.colorToString(style.getStartDataColor()) + "\" ");
		pw.print("endDataColor=\"" +
			jas.util.ColorConverter.colorToString(style.getEndDataColor()) + "\" ");
		pw.print("dataOverFlow=\"" + style.getShowOverflow() + "\" ");
		pw.print("dataShowPlot=\"" + style.getShowPlot() + "\" ");
		pw.print("displayAsScatterPlot=\"" + style.getDisplayAsScatterPlot() + "\" ");
		pw.print("dataPointSize=\"" + style.getDataPointSize() + "\" ");
		pw.print("dataPointStyle=\"" + style.getDataPointStyle() + "\" ");
		pw.println("dataPointColor=\"" +
			jas.util.ColorConverter.colorToString(style.getDataPointColor()) + "\">");
		
		pw.println("<points dimensions=\"2\">");

		final double[] d = new double[2];

		if (dataSource.hasScatterPlotData()) {
			ScatterEnumeration se = dataSource.startEnumeration();
			while (se.getNextPoint(d)) {
				pw.println(d[0] + "," + d[1]);
			}
		}
		pw.outdent();	
		pw.println("</points>");
		
		pw.println("<pointDataAxisAttributes axis=\"x\" type=\"double\"/>");
		pw.println("<pointDataAxisAttributes axis=\"y\" type=\"double\"/>");
		pw.outdent();
		pw.println("</data2d>");
	}*/
}

	
