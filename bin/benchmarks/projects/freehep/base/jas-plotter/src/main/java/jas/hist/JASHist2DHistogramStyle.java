package jas.hist;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;

public class JASHist2DHistogramStyle extends JASHistStyle implements Serializable
{

	final public static int STYLE_BOX			  = 0;
	final public static int STYLE_ELLIPSE		  = 1;
	final public static int STYLE_COLORMAP		  = 2;
	final public static int STYLE_3DLEGOPLOT	  = 3;
	final public static int STYLE_3DSURFACEPLOT	  = 4;

 	final public static int COLORMAP_WARM		  = 0;
	final public static int COLORMAP_COOL		  = 1;
	final public static int COLORMAP_THERMAL	  = 2;
	final public static int COLORMAP_RAINBOW	  = 3;
	final public static int COLORMAP_GRAYSCALE	  = 4;
	final public static int COLORMAP_USERDEFINED  = 5;

	static final Color[] lineColors = 
	{ 
		Color.blue, Color.red, Color.darkGray, new Color(0,145,0)
	};
	static final Color[] overflowColors = new Color[lineColors.length];
	static
	{
		for (int i=0; i<lineColors.length; i++)
		{
			overflowColors[i] = lineColors[i].darker();
		}
	}
	static int n = 0;

	static final int[] histStyles =
	{
		STYLE_BOX, STYLE_ELLIPSE, STYLE_COLORMAP
	};

	static final int[] colorMapSchemes =
	{
		COLORMAP_WARM, COLORMAP_COOL, COLORMAP_THERMAL, COLORMAP_RAINBOW,
		COLORMAP_GRAYSCALE, COLORMAP_USERDEFINED
	};
	static final int getHistStyle(String s) {
		if (s.equals("STYLE_BOX")) return STYLE_BOX;
		if (s.equals("STYLE_ELLIPSE")) return STYLE_ELLIPSE;
		if (s.equals("STYLE_COLORMAP")) return STYLE_COLORMAP;
		System.out.println("Unrecognized style " + s + ". Using STYLE_BOX instead.");
		return STYLE_BOX;
	}
	static final int getColorMapScheme(String s) {
		if (s.equals("COLORMAP_WARM")) return COLORMAP_WARM;
		if (s.equals("COLORMAP_COOL")) return COLORMAP_COOL;
		if (s.equals("COLORMAP_THERMAL")) return COLORMAP_THERMAL;
		if (s.equals("COLORMAP_RAINBOW")) return COLORMAP_RAINBOW;
		if (s.equals("COLORMAP_GRAYSCALE")) return COLORMAP_GRAYSCALE;
		if (s.equals("COLORMAP_USERDEFINED")) return COLORMAP_USERDEFINED;
		System.out.println("Unrecognized color map " + s + ". Using COLORMAP_WARM instead.");
		return COLORMAP_WARM;
	}
	static final String getHistStyleName(int num) {
		//WARNING: The way this method is implemented ONLY works since the named constants
		//represent the ints that are the positions of these names in this array.  If this
		//changes, change this array or replace it with a Hashtable or something else.
		String[] histStyleNames =
		{
			"STYLE_BOX", "STYLE_ELLIPSE", "STYLE_COLORMAP"
		};
		return histStyleNames[num];
	}
	static final String getColorMapSchemeName(int num) {
		//WARNING: The way this method is implemented ONLY works since the named constants
		//represent the ints that are the positions of these names in this array.  If this
		//changes, change this array or replace it with a Hashtable or something else.
		String[] colorMapSchemeNames =
		{
			"COLORMAP_WARM", "COLORMAP_COOL", "COLORMAP_THERMAL", "COLORMAP_RAINBOW",
			"COLORMAP_GRAYSCALE", "COLORMAP_USERDEFINED"
		};
		return colorMapSchemeNames[num];
	}

//	static final long serialVersionUID = 7779996364086801435L;

	public JASHist2DHistogramStyle()
	{
		initTransientData();

	//	n++;
	//	if (n == lineColors.length) n = 0;
	
		m2D_dataOverFlow = false;
		m2D_dataHistStyles = histStyles[0];
		m2D_dataColorMapScheme = colorMapSchemes[0];

		m2D_shapeColor = lineColors[n];
		m2D_overFlowBinColor = overflowColors[n++];
		if (n == lineColors.length) n = 0;

		m2D_startDataColor = new Color(255,255,255);
		m2D_endDataColor = new Color(0,0,0);
		m2D_dataShowPlot = true;

	//	m2D_dataOverFlow;
	}

	//////Determine Histogram Style//////
	public int getHistStyle() 
	{
		return m2D_dataHistStyles;
	}

	public void setHistStyle(int nNewValue) 
	{	
		m2D_dataHistStyles = nNewValue;
		changeNotify();
	}

	///////Determine ColorMap Style//////
	public int getColorMapScheme() 
	{
		return m2D_dataColorMapScheme;
	}

	public void setColorMapScheme(int nNewValue) 
	{	
		m2D_dataColorMapScheme = nNewValue;
		changeNotify();
	}

	////////Determine Box/Ellipse Color////////
	public Color getShapeColor() 
	{
		return m2D_shapeColor;
	}

	public void setShapeColor(Color nNewValue) 
	{
		m2D_shapeColor = nNewValue;
		changeNotify();
	}

	////////Determine OverFlowBin Color////////
	public Color getOverflowBinColor() 
	{
		return m2D_overFlowBinColor;
	}

	public void setOverflowBinColor(Color nNewValue) 
	{
		m2D_overFlowBinColor = nNewValue;
		changeNotify();
	}

	////////Determine Start DataColor////////
	public Color getStartDataColor() 
	{
		return m2D_startDataColor;
	}

	public void setStartDataColor(Color nNewValue) 
	{
		m2D_startDataColor = nNewValue;
		changeNotify();
	}

	////////Determine End DataColor////////
	public Color getEndDataColor() 
	{
		return m2D_endDataColor;
	}

	public void setEndDataColor(Color nNewValue) 
	{
		m2D_endDataColor = nNewValue;
		changeNotify();
	}

	///////Enable/Disable OverFlow Bins///////
	public boolean getShowOverflow() 
	{
		return m2D_dataOverFlow;
	}

	public void setShowOverflow(boolean bNewValue) 
	{
		m2D_dataOverFlow = bNewValue;
		changeNotify();
	}
	public boolean getLogZ()
   {
      return m2d_logZ;
   }
   public void setLogZ(boolean log)
   {
      m2d_logZ = log;
      changeNotify();
   }
	
	///////Hide/Show Histogram////////
	public boolean getShowPlot() 
	{
		return m2D_dataShowPlot;
	}

	public void setShowPlot(boolean bNewValue) 
	{
		m2D_dataShowPlot = bNewValue;
		changeNotify();
	}

        //////// Show or not bins with Height == 0
        public boolean getShowZeroHeightBins() { 
            return showZeroHeightBins; 
        }
    
        public void setShowZeroHeightBins(boolean show) { 
            showZeroHeightBins = show; 
        }
    
        
	protected void changeNotify()
	{
		super.changeNotify();
	}
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		initTransientData();
	}
	private void initTransientData()
	{
	}

        private boolean showZeroHeightBins = true;
	private int m2D_dataHistStyles;
	private int m2D_dataColorMapScheme;

	private Color m2D_shapeColor;
	private Color m2D_overFlowBinColor;

	private Color m2D_startDataColor;
	private Color m2D_endDataColor;

	private boolean m2D_dataOverFlow;
	private boolean m2D_dataShowPlot;
	private boolean m2D_invertColorRange;
   private boolean m2d_logZ;
   
   static final long serialVersionUID=5704864807779848936L;
}
