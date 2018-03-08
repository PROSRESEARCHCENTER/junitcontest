package jas.hist.test;
import jas.hist.Matrix;

public class MatrixTest
{
	public static void main(String[] argv)
	{
		double[][] in = Matrix.create(3);

		for (int i=0; i<3; i++)
		{
			for (int j=0; j<3; j++)
			{
				in[i][j] = Math.random();
			}
		}
		Matrix.print(in);
		double[][] out = Matrix.clone(in);

		try
		{
			double det = Matrix.invert(out);
			System.out.println("det="+det);
			Matrix.print(out);
			out = Matrix.multiply(out,in);
			Matrix.print(out);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
