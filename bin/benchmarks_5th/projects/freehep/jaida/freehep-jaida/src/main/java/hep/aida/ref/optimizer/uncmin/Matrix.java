package hep.aida.ref.optimizer.uncmin;
import java.text.NumberFormat;

class Matrix
{
	public static double[][] create(int size)
	{
		double[][] result = new double[size][];
		for (int i=0; i<size; i++) result[i] = new double[size];
		return result;
	}
	public static double[][] clone(double[][] in)
	{
		double[][] result = (double[][]) in.clone();
		for (int i=0; i<result.length; i++)
			result[i] = (double[]) result[i].clone();
		return result;
	}
	public static void print(double[][] in)
	{
		NumberFormat nf = NumberFormat.getInstance();

		for (int i=0; i<in.length; i++)
		{
			for (int j=0; j<in[i].length; j++)
			{
				System.out.print(nf.format(in[i][j])+" ");
			}
			System.out.println();
		}
	}
	public static double[][] multiply(double[][] in1, double[][] in2) throws IncompatibleMatrices
	{
		double[][] out = new double[in1.length][];
		for (int i=0; i<in1.length; i++)
		{
			if (in1[i].length != in2.length) throw new IncompatibleMatrices();
			out[i] = new double[in2[i].length];
			for (int j=0; j<in2[i].length; j++)
			{
				out[i][j] = 0;
				for (int k=0; k<in1[i].length; k++)
				{
					out[i][j] += in1[i][k]*in2[k][j];
				}
			}
		}
		return out;
	}
	public static double invert(double[][] array) throws IndeterminateMatrix
	{
		double det = 1;
		int order = array.length;
		int[] ik = new int[order];
		int[] jk = new int[order];

		for (int k=0; k<order; k++)
		{
			// Find largest element array[i][k] in rest of matrix
			double amax = 0;
			for (int i=k; i<order; i++)
			{
				for (int j=k; j<order; j++)
				{
					if (Math.abs(array[i][j]) > Math.abs(amax))
					{
						amax = array[i][j];
						ik[k] = i;
						jk[k] = j;
					}
				}
			}
		
		// Interchange rows and columns to put max in array[k][k]

			if (amax == 0) throw new IndeterminateMatrix();
		
			{
				int i = ik[k];
				if (k > i) throw new MatrixBug();
				if (i > k)
				{
					for (int j=0; j<order; j++)
					{
						double save = array[k][j];
						array[k][j] = array[i][j];
						array[i][j] = -save;
					}
				}
			}
			{
				int j = jk[k];
				if (k > j) throw new MatrixBug();
				if (j > k)
				{
					for (int i=0; i<order; i++)
					{
						double save = array[i][k];
						array[i][k] = array[i][j];
						array[i][j] = -save;
					}
				}
			}
		
			// Accumulate elements of inverse matrix
				
			for (int i=0; i<order; i++)
			{
				if (i == k) continue; 
				array[i][k] = -array[i][k]/amax;
			}
			for (int i=0; i<order; i++)
			{
				if (i == k) continue;
				for (int j=0; j<order; j++)
				{
					if (j == k) continue;
					array[i][j] += array[i][k]*array[k][j];
				}
			}
			for (int j=0; j<order; j++)
			{
				if (j == k) continue; 
				array[k][j] = array[k][j]/amax;
			}
			array[k][k] = 1/amax;
			det *= amax;
		}

		// restore ordering of matrix

		for (int l=0; l<order; l++)
		{
			int k = order - l - 1;
			{
				int j = ik[k];
				if (j>k)
				{
					for (int i=0; i<order; i++)
					{
						double save = array[i][k];
						array[i][k] = -array[i][j];
						array[i][j] = save;
					}
				}
			}
			{
				int i = jk[k];
				if (i>k)
				{
					for (int j=0; j<order; j++)
					{
						double save = array[k][j];
						array[k][j] = -array[i][j];
						array[i][j] = save;
					}
				}
			}
		}	

		return det;
	}
}
