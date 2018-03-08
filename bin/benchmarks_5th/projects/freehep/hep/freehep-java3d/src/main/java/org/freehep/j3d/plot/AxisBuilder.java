package org.freehep.j3d.plot;
import java.awt.Font;
import javax.media.j3d.*;
import javax.vecmath.*;

public class AxisBuilder
{
   /**
    * Constructs an axis
    * @author Joy Kyriakopulos (joyk@fnal.gov)
    * @version $Id: AxisBuilder.java 8584 2006-08-10 23:06:37Z duns $
    */
	AxisBuilder()
	{
		// build all the major components of the Axis, keeping the important
		// parts in member variables so we can modify them later.

		label = new Text3D(); // The Axis label
		label.setAlignment(label.ALIGN_CENTER);
		Point3f pos = new Point3f(scale/2,-labelOffSet,0);
		label.setPosition(pos);
		label.setCapability(label.ALLOW_FONT3D_WRITE);
		label.setCapability(label.ALLOW_STRING_WRITE);

		axis = new Shape3D(); // The axis and tick marks.
		axis.setCapability(axis.ALLOW_GEOMETRY_WRITE);

		// We can create the tick labels yet, since we don't know how many there
		// will be, but we can make a group to hold them.

		ticks = new BranchGroup();
		ticks.setCapability(ticks.ALLOW_CHILDREN_READ);
		ticks.setCapability(ticks.ALLOW_CHILDREN_WRITE);
		ticks.setCapability(ticks.ALLOW_DETACH);

		// Group the components together

		mainGroup = new TransformGroup();
		mainGroup.setCapability(ticks.ALLOW_CHILDREN_WRITE);
		mainGroup.setCapability(ticks.ALLOW_CHILDREN_EXTEND);
		mainGroup.addChild(new Shape3D(label));
		mainGroup.addChild(axis);
		mainGroup.addChild(ticks);

		// Set up a BulletinBoard behaviour to keep the axis oriented
		// towards the user.

        mainGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        mainGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        Billboard bboard = new Billboard( mainGroup );
        mainGroup.addChild( bboard );
        bboard.setSchedulingBounds( new BoundingSphere(new Point3d(0,0,0),10));
		bboard.setAlignmentAxis( 1.0f, 0.0f, 0.0f);
	}
	public String getLabel()
	{
		return labelText;
	}
	public void setLabel(String label)
	{
		labelText = label;
	}
	public Font3D getLabelFont()
	{
		return labelFont;
	}
	public void setLabelFont(Font3D font)
	{
		labelFont = font;
	}
	public Font3D getTickFont()
	{
		return tickFont;
	}
	public void setTickFont(Font3D font)
	{
		tickFont = font;
	}

	/**
	 * Tick labels and locations (positions) can be set
	 * by the caller or calculated and set by the
	 * createLabelsNTicks method as a convenience.
	 */
	public double[] getTickLocations()
	{
		return tickLocations;
	}
	public void setTickLocations(double[] ticks)
	{
		tickLocations = ticks;
	}
	public String[] getTickLabels()
	{
		return tickLabels;
	}
	public void setTickLabels(String[] labels)
	{
		tickLabels = labels;
	}

	/**
	 * Call the createLabelsNTicks method if you would like the
	 * axisbuilder to create axis labels and tick positions for you.
	 */
	public void createLabelsNTicks(double min, double max)
	{
		AxisLabelCalculator axisCalc = new AxisLabelCalculator();
		axisCalc.createNewLabels(min, max);
		// System.out.println("in createLabelsNTicks: min = " + min + ", max = " + max);
		// axisCalc.printLabels();
		tickLabels = axisCalc.getLabels();
		tickLocations = axisCalc.getPositions();
	}

	/**
	 * Call this method after setting the required axis properties, to actually
	 * setup/modify the axis appearance.
	 */
	public void apply()
	{
		/*
		 * Build an axis on the X axis from 0 to 256. Note the reason for the
		 * 256 is to compensate for the giant size of the Text3D characters.
		 * The axis is built in the XY plane.
		 * We will use suitable translation and rotation to position it later.
		 *
		 * Note we currently use Text3D to draw the labels. This is because Text3D
		 * seems somewhat easier to use. A better solution would
		 * probably involve using Text2D, but the com.sun.j3d.utils.geometry.Text2D
		 * class seems pretty brain dead. A better solution may be to get the source
		 * for the Text2D class and rewrite it for our own purposes.
		 */

		int tMajor = (tickLocations.length-1)/(tickLabels.length-1);

		LineArray lines = new LineArray(2*tickLocations.length+2, LineArray.COORDINATES);
		int coordIdx = 0;			// coordinate index into Axis linearray

		// Actual axis
 		lines.setCoordinate(coordIdx++, new Point3d(0, 0, 0));
		lines.setCoordinate(coordIdx++, new Point3d(scale, 0, 0));

		// Remove the old tick labels. Note a more efficient implementation
		// we would keep as many as of the old labels as possible, adding/removing
		// new tick marks only as the number of tick marks change, and changing the
		// text only as necessary. For now we just do the easiest thing.
		// TODO: More efficient implementation.

		// while (ticks.numChildren() > 0)
		//	    ticks.removeChild(0);

		ticks.detach();
		ticks = new BranchGroup();
		ticks.setCapability(ticks.ALLOW_CHILDREN_READ);
		ticks.setCapability(ticks.ALLOW_CHILDREN_WRITE);
		ticks.setCapability(ticks.ALLOW_DETACH);

		// Rendering Ticks on Axis

		for(int i = 0; i < tickLocations.length; i++)
		{
			float x = (float) tickLocations[i]*scale;
			if (i % tMajor == 0) // Major tick mark?
			{
				lines.setCoordinate(coordIdx++, new Point3d(x, 0, 0));
				lines.setCoordinate(coordIdx++, new Point3d(x, -major, 0));

				// Add the tick label

				int nt = i/tMajor;
				Point3f pos = new Point3f(x,-tickOffSet,0);
				Text3D tickLabel = new Text3D(tickFont,tickLabels[nt],pos);
				tickLabel.setAlignment(tickLabel.ALIGN_CENTER);
				ticks.addChild(new Shape3D(tickLabel));
			}
			else // Minor tick mark
			{
				lines.setCoordinate(coordIdx++, new Point3d(x, 0, 0));
				lines.setCoordinate(coordIdx++, new Point3d(x, -minor, 0));
			}
		}
		// TODO: It would be more efficient to only update the label if something has changed.
		mainGroup.addChild(ticks);
		label.setFont3D(labelFont);
		label.setString(labelText);
		axis.setGeometry(lines);
	}
	/**
	 * Returns the node representing this Axis
	 * Subclasses can override this method to transform this axis
	 * to make it into an X,Y,Z axis.
	 */
	public Node getNode()
	{
		return mainGroup;
	}
	private String labelText;
	private Font3D labelFont = defaultLabelFont;
	private double[] tickLocations;
	private String[] tickLabels;
	private Font3D tickFont = defaultTickFont;

	private TransformGroup mainGroup;
	private Text3D label;
	private Shape3D axis;
	private BranchGroup ticks;

	protected static float scale = 256f; // See comment on apply
	protected static float major = 0.02f*scale;
	protected static float minor = 0.01f*scale;
	protected static float tickOffSet = 0.06f*scale;
	protected static float labelOffSet = 0.12f*scale;

	private static final Font3D defaultLabelFont = new Font3D(new Font("DIALOG",Font.BOLD,16),null);
	private static final Font3D defaultTickFont = new Font3D(new Font("DIALOG",Font.PLAIN,12),null);

	private static Color3f white = new Color3f(1,1,1);
}
