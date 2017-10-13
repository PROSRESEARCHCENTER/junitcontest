package org.freehep.j3d.plot;

import javax.media.j3d.*;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.swing.*;
import javax.vecmath.*;



/**
 * A simple convenience class that end users can pop into their GUI to produce a
 * lego plot.
 *
 * Warning: LegoPlot extends Canvas3D and thus is a heavyweight object.
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: LegoPlot.java 8584 2006-08-10 23:06:37Z duns $
 */

public class LegoPlot extends Plot3D
{
	private Binned2DData data;
	private LegoBuilder builder;
	private Node plot;
        private boolean logZscaling = false;
        private boolean drawBlocks = true;
        private boolean linesWhileAnim = true;
        private int sparsifyThreshold = 600;
	private AxisBuilder xAxis;
	private AxisBuilder yAxis;
	private ZAxisBuilder zAxis;
        private String xAxisLabel = "X Axis";
        private String yAxisLabel = "Y Axis";
        private String zAxisLabel = "Z Axis";
	private double xmin;
	private double xmax;
	private double ymin;
	private double ymax;
	private double zmin;
	private double zmax;

	public LegoPlot()
	{
		super();
	}
	public void setData(Binned2DData data)
	{
		this.data = data;
		if (init) {
			if (data.xMin() != xmin || data.xMax() != xmax) {
				xmin = data.xMin();
				xmax = data.xMax();
				xAxis.createLabelsNTicks(xmin, xmax);
				xAxis.apply();
			}
			if (data.yMin() != ymin || data.yMax() != ymax) {
				ymin = data.yMin();
				ymax = data.yMax();
				yAxis.createLabelsNTicks(ymin, ymax);
				yAxis.apply();
			}
			if (data.zMin() != zmin || data.zMax() != zmax) {
				zmin = data.zMin();
				zmax = data.zMax();
				zAxis.createLabelsNTicks(zmin, zmax, logZscaling);
				zAxis.apply();
			}
			if (logZscaling)
                            builder.updatePlot(new NormalizedBinned2DLogData(data));
                        else
                            builder.updatePlot(new NormalizedBinned2DData(data));
		}
	}

	public boolean getLogZscaling()
	{
	    return logZscaling;
	}

	public void setLogZscaling(boolean b)
	{
	    // System.out.println("setting Log Scaling to: " + b + " from: " + logZscaling);
            if (logZscaling != b) {
                logZscaling = b;
                if (data != null) {
                    zmin = data.zMin();
                    zmax = data.zMax();
                    zAxis.createLabelsNTicks(zmin, zmax, logZscaling);
                    zAxis.apply();
                    if (logZscaling)
                        builder.updatePlot(new NormalizedBinned2DLogData(data));
                    else
                        builder.updatePlot(new NormalizedBinned2DData(data));
                }
	    }
	}

        public boolean getDrawBlocks()
	{
	    return drawBlocks;
	}

	public void setDrawBlocks(boolean b)
	{
            if (drawBlocks != b) {
                drawBlocks = b;
                if (builder != null) {
                    builder.setDrawBlocks(b);
                    if (data != null) {
                        if (logZscaling)
                            builder.updatePlot(new NormalizedBinned2DLogData(data));
                        else
                            builder.updatePlot(new NormalizedBinned2DData(data));
                    }
                }
	    }
	}
        
        public boolean getLinesWhileAnim()
	{
	    return linesWhileAnim;
	}

	public void setLinesWhileAnim(boolean b)
	{
            if (linesWhileAnim != b) {
                linesWhileAnim = b;
                if (builder != null) {
                    builder.setLinesWhileAnim(b);
                    if (data != null) {
                        if (logZscaling)
                            builder.updatePlot(new NormalizedBinned2DLogData(data));
                        else
                            builder.updatePlot(new NormalizedBinned2DData(data));
                    }
                }
	    }
	}

        public int getSparsifyThreshold()
	{
	    return sparsifyThreshold;
	}

	public void setSparsifyThreshold(int s)
	{
            // The sparsify threshold is a limit on the number of bins that should be drawn when animating plot
            // The lego builder's numWiredLines is the target number of bins that it will draw during animations
            if (sparsifyThreshold != s) {
                if (s > 1) {
                    sparsifyThreshold = s;
                    if (builder != null) {
                        builder.setNumWireLines(s);   // Builder will sparsify when numWiredLines > # populated Bins
                        if (data != null) {
                        if (logZscaling)
                            builder.updatePlot(new NormalizedBinned2DLogData(data));
                        else
                            builder.updatePlot(new NormalizedBinned2DData(data));
                    }

                    }
                }
                else
                    System.out.println("LegoPlot: Invalid SparsifyThreshold: " + s);
            }
	}

        public String getXAxisLabel()
	{
	    return xAxisLabel;
	}

	public void setXAxisLabel(String s)
	{
            xAxisLabel = s;
            xAxis.setLabel(s);
            xAxis.apply();
	}

        public String getYAxisLabel()
	{
	    return yAxisLabel;
	}

	public void setYAxisLabel(String s)
	{
            yAxisLabel = s;
            yAxis.setLabel(s);
            yAxis.apply();
	}

        public String getZAxisLabel()
	{
	    return zAxisLabel;
	}

	public void setZAxisLabel(String s)
	{
            zAxisLabel = s;
            zAxis.setLabel(s);
            zAxis.apply();
	}

	protected Node createPlot()
	{
		builder = new LegoBuilder();
                builder.setDrawBlocks(drawBlocks);
                builder.setNumWireLines(sparsifyThreshold);  // Builder will sparsify when numWiredLines/2 > # populated Bins
                builder.setLinesWhileAnim(linesWhileAnim);
		Node box = builder.buildOutsideBox();
		if (logZscaling)
                    plot = builder.buildContent(new NormalizedBinned2DLogData(data));
		else
		    plot = builder.buildContent(new NormalizedBinned2DData(data));
		double[] tick = {0,.1,.2,.3,.4,.5,.6,.7,.8,.9,1.0};
		String[] labels = {"0.0","0.2","0.4","0.6","0.8","1.0" };
		xAxis = new XAxisBuilder(xAxisLabel,labels,tick);
		yAxis = new YAxisBuilder(yAxisLabel,labels,tick);
		zAxis = new ZAxisBuilder(zAxisLabel,labels,tick);
		xAxis.createLabelsNTicks(data.xMin(), data.xMax());
		yAxis.createLabelsNTicks(data.yMin(), data.yMax());
		zAxis.createLabelsNTicks(data.zMin(), data.zMax(), logZscaling);
		xAxis.apply();
		yAxis.apply();
		zAxis.apply();

		Group g = new Group();
		g.addChild(box);
		g.addChild(plot);
		g.addChild(xAxis.getNode());
		g.addChild(yAxis.getNode());
		g.addChild(zAxis.getNode());
		return g;
	}

	// Add behavior to switch to "wire-frame" display when mouse is pressed
	//			by overriding defineMouseBehavior
	protected BranchGroup defineMouseBehaviour(Node scene)
	{
		BranchGroup bg = super.defineMouseBehaviour(scene);
		Bounds bounds = getDefaultBounds();

		// change this to be switch node below i.e. not scene
		if (plot instanceof Switch) {
			Switch sw = (Switch)plot;
			MouseDownUpBehavior mouseDnUp = new MouseDownUpBehavior(bounds, sw);
			bg.addChild(mouseDnUp);
		}

		return bg;
	}
}

