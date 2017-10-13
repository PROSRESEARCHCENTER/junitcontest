package org.freehep.j3d.plot;
import javax.media.j3d.*;
import javax.vecmath.*;
/**
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: ZAxisBuilder.java 8584 2006-08-10 23:06:37Z duns $
 */
public class ZAxisBuilder extends AxisBuilder
{
	public ZAxisBuilder()
	{
	}
	public ZAxisBuilder(String label, String[] tickLabels, double[] tickLocations)
	{
		setLabel(label);
		setTickLabels(tickLabels);
		setTickLocations(tickLocations);
	}
	public Node getNode()
	{
		Transform3D t3d = new Transform3D();
		t3d.set(1/scale,new Vector3f(-0.5f,-0.5f,0));
		Transform3D rot = new Transform3D();
		rot.rotY(-Math.PI/2);
		t3d.mul(rot);
		TransformGroup tg = new TransformGroup(t3d);
		tg.addChild(super.getNode());
		return tg;
	}

	/**
	 * createLabelsNTicks method is overridden here to support z axis
     * log scaling.
     *
     * @todo: z axis log scaling needs to be implemented in Axis labels and ticks
     *        this implementation is a minimal hack.
	 */
        public void createLabelsNTicks(double min, double max, boolean logZscaling)
	{
		super.createLabelsNTicks(min, max);
		if (logZscaling) {
                    String[] tickLabels = getTickLabels();
                    double[] tickLocations = getTickLocations();
                    int numLabels = tickLabels.length;
                    for (int i = 1; i < numLabels-1; ++i) {
                        tickLabels[i] = " ";
		        tickLocations[i] = tickLocations[numLabels-1];
                    }
                }
		// System.out.println("in z-axis createLabelsNTicks: min = " + min + ", max = " + max);
		// axisCalc.printLabels();
	}
}
