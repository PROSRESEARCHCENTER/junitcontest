package hep.physics.matrix;

import hep.physics.vec.BasicHep3Vector;
import hep.physics.vec.Hep3Vector;
import java.util.Formatter;

/**
 * Simple operations on matrices
 * @author tonyj
 */
public class MatrixOp {

    private MatrixOp() {
    }

    /**
     * Invert matrix mIn and write it to matrix mOut.
     * This method allows both arguments to be the same, e.g. <code>inverse(this,this);</code>
     * This method currently only supports square matrices.
     */
    public static void inverse(Matrix mIn, MutableMatrix mOut) throws InvalidMatrixException {
        int order = mIn.getNRows();
        if (order != mIn.getNColumns()) {
            throw new InvalidMatrixException("Matrix.inverse only supports square matrices");
        }
        if (order != mOut.getNColumns() && order != mOut.getNRows()) {
            throw new InvalidMatrixException("mOut must be same size as mIn");
        }

        int[] ik = new int[order];
        int[] jk = new int[order];
        double[][] array = new double[order][order];
        for (int i = 0; i < order; i++) {
            for (int j = 0; j < order; j++) {
                array[i][j] = mIn.e(i, j);
            }
        }

        for (int k = 0; k < order; k++) {
            // Find largest element in rest of matrix
            double amax = 0;
            for (int i = k; i < order; i++) {
                for (int j = k; j < order; j++) {
                    if (Math.abs(array[i][j]) > Math.abs(amax)) {
                        amax = array[i][j];
                        ik[k] = i;
                        jk[k] = j;
                    }
                }
            }

            // Interchange rows and columns to put max in array[k][k]

            if (amax == 0) {
                throw new IndeterminateMatrixException();
            }

            {
                int i = ik[k];
                assert (k <= i);
                if (i > k) {
                    for (int j = 0; j < order; j++) {
                        double save = array[k][j];
                        array[k][j] = array[i][j];
                        array[i][j] = -save;
                    }
                }
            }
            {
                int j = jk[k];
                assert (k <= j);
                if (j > k) {
                    for (int i = 0; i < order; i++) {
                        double save = array[i][k];
                        array[i][k] = array[i][j];
                        array[i][j] = -save;
                    }
                }
            }

            // Accumulate elements of inverse matrix

            for (int i = 0; i < order; i++) {
                if (i == k) {
                    continue;
                }
                array[i][k] = -array[i][k] / amax;
            }
            for (int i = 0; i < order; i++) {
                if (i == k) {
                    continue;
                }
                for (int j = 0; j < order; j++) {
                    if (j == k) {
                        continue;
                    }
                    array[i][j] += array[i][k] * array[k][j];
                }
            }
            for (int j = 0; j < order; j++) {
                if (j == k) {
                    continue;
                }
                array[k][j] = array[k][j] / amax;
            }
            array[k][k] = 1 / amax;
        }

        // restore ordering of matrix

        for (int l = 0; l < order; l++) {
            int k = order - l - 1;
            {
                int j = ik[k];
                if (j > k) {
                    for (int i = 0; i < order; i++) {
                        double save = array[i][k];
                        array[i][k] = -array[i][j];
                        array[i][j] = save;
                    }
                }
            }
            {
                int i = jk[k];
                if (i > k) {
                    for (int j = 0; j < order; j++) {
                        double save = array[k][j];
                        array[k][j] = -array[i][j];
                        array[i][j] = save;
                    }
                }
            }
        }
        for (int i = 0; i < order; i++) {
            for (int j = 0; j < order; j++) {
                mOut.setElement(i, j, array[i][j]);
            }
        }
    }

    /**
     * Convenience method to invert a matrix in one step.
     *
     * @param m matrix to be inverted. This remains unchanged by this operation
     * @return inverted matrix
     */
    public static Matrix inverse(Matrix m) {

        MutableMatrix minv = new BasicMatrix(m.getNRows(), m.getNColumns());
        MatrixOp.inverse(m, minv);
        return minv;
    }

    public static String toString(Matrix m) {
        Formatter formatter = new Formatter();
        formatter.format("[");
        for (int i = 0;;) {
            formatter.format("[");
            for (int j = 0;;) {
                formatter.format("%12.5g", m.e(i, j));
                if (++j >= m.getNColumns()) {
                    break;
                }
                formatter.format(",");
            }
            if (++i >= m.getNRows()) {
                break;
            }
            formatter.format("]\n ");
        }
        formatter.format("]");
        return formatter.out().toString();
    }
    // ToDo: Clean up the code here cut and pasted from inverse().

    public static double det(Matrix mIn) {
        int order = mIn.getNRows();
        if (order != mIn.getNColumns()) {
            throw new InvalidMatrixException("Matrix.det only supports square matrices");
        }

        int[] ik = new int[order];
        int[] jk = new int[order];
        double[][] array = new double[order][order];
        for (int i = 0; i < order; i++) {
            for (int j = 0; j < order; j++) {
                array[i][j] = mIn.e(i, j);
            }
        }

        double det = 1;

        for (int k = 0; k < order; k++) {
            // Find largest element array[i][k] in rest of matrix
            double amax = 0;
            for (int i = k; i < order; i++) {
                for (int j = k; j < order; j++) {
                    if (Math.abs(array[i][j]) > Math.abs(amax)) {
                        amax = array[i][j];
                        ik[k] = i;
                        jk[k] = j;
                    }
                }
            }

            // Interchange rows and columns to put max in array[k][k]

            if (amax == 0) {
                return 0;
            }

            {
                int i = ik[k];
                assert (k <= i);
                if (i > k) {
                    for (int j = 0; j < order; j++) {
                        double save = array[k][j];
                        array[k][j] = array[i][j];
                        array[i][j] = -save;
                    }
                }
            }
            {
                int j = jk[k];
                assert (k <= j);
                if (j > k) {
                    for (int i = 0; i < order; i++) {
                        double save = array[i][k];
                        array[i][k] = array[i][j];
                        array[i][j] = -save;
                    }
                }
            }

            // Accumulate elements of inverse matrix

            for (int i = 0; i < order; i++) {
                if (i == k) {
                    continue;
                }
                array[i][k] = -array[i][k] / amax;
            }
            for (int i = 0; i < order; i++) {
                if (i == k) {
                    continue;
                }
                for (int j = 0; j < order; j++) {
                    if (j == k) {
                        continue;
                    }
                    array[i][j] += array[i][k] * array[k][j];
                }
            }
            for (int j = 0; j < order; j++) {
                if (j == k) {
                    continue;
                }
                array[k][j] = array[k][j] / amax;
            }
            array[k][k] = 1 / amax;
            det *= amax;
        }
        return det;
    }

    /**
     * Traspose matrix mIn and write it to matrix mOut.
     * For a square matrix this method allows both arguments to be the same, e.g. <code>transposed(this,this);</code>
     */
    public static void transposed(Matrix mIn, MutableMatrix mOut) {
        if (mIn.getNRows() != mOut.getNColumns() || mIn.getNColumns() != mOut.getNRows()) {
            throw new InvalidMatrixException("Incompatible matrixes for transposed");
        }

        if (mOut == mIn) { // special handling for square matrix
            int order = mIn.getNRows();
            for (int i = 0; i < order; i++) {
                for (int j = 0; j < i; j++) {
                    double t1 = mIn.e(i, j); // In case mIn == mOut
                    mOut.setElement(i, j, mIn.e(j, i));
                    mOut.setElement(j, i, t1);
                }
                mOut.setElement(i, i, mIn.e(i, i));
            }
        } else {
            for (int i = 0; i < mIn.getNRows(); i++) {
                for (int j = 0; j < mIn.getNColumns(); j++) {
                    mOut.setElement(j, i, mIn.e(i, j));
                }
            }
        }
    }

    /**
     * Returns the transpose of the matrix.
     *
     * @param m matrix to be transposed
     * @return transposed matrix
     */
    public static Matrix transposed(Matrix m) {

        MutableMatrix mt = new BasicMatrix(m.getNColumns(), m.getNRows());
        for (int i = 0; i < m.getNRows(); i++) {
            for (int j = 0; j < m.getNColumns(); j++) {
                mt.setElement(j, i, m.e(i, j));
            }
        }
        return mt;
    }

    public static Matrix mult(Matrix m1, Matrix m2) {
        int nAdd = m1.getNColumns();
        if (nAdd != m2.getNRows()) {
            throw new InvalidMatrixException("Incompatible matrices for multiplication");
        }
        int nRows = m1.getNRows();
        int nCols = m2.getNColumns();
        BasicMatrix result = new BasicMatrix(nRows, nCols);
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                double sum = 0;
                for (int k = 0; k < nAdd; k++) {
                    sum += m1.e(i, k) * m2.e(k, j);
                }
                result.setElement(i, j, sum);
            }
        }
        return result;
    }

    /**
     * Add two matrices together.  Matrices must have the same dimensions.
     *
     * @param a first matrix
     * @param b second matrix
     * @return sum of the two matrices
     */
    public static Matrix add(Matrix a, Matrix b) {

        if (a.getNRows() != b.getNRows()
                || a.getNColumns() != b.getNColumns()) {
            throw new InvalidMatrixException("Trying to add two matrices with different dimensions");
        }
        MutableMatrix c = new BasicMatrix(a.getNRows(), a.getNColumns());
        for (int i = 0; i < a.getNRows(); i++) {
            for (int j = 0; j < a.getNColumns(); j++) {
                c.setElement(i, j, a.e(i, j) + b.e(i, j));
            }
        }
        return c;
    }

    /**
     * Subtract matrix b from matrix a.  Matrices must have the same dimensions.
     *
     * @param a starting matrix
     * @param b matrix to be subtracted
     * @return difference (a - b)
     */
    public static Matrix sub(Matrix a, Matrix b) {

        if (a.getNRows() != b.getNRows()
                || a.getNColumns() != b.getNColumns()) {
            throw new InvalidMatrixException("Trying to add two matrices with different dimensions");
        }
        MutableMatrix c = new BasicMatrix(a.getNRows(), a.getNColumns());
        for (int i = 0; i < a.getNRows(); i++) {
            for (int j = 0; j < a.getNColumns(); j++) {
                c.setElement(i, j, a.e(i, j) - b.e(i, j));
            }
        }
        return c;
    }

    /**
     * Multiply a matrix by a scaler constant.
     *
     * @param c constant that will mutliply the matrix
     * @param m matrix to be scaled
     * @return scaled matrix (c * m)
     */
    public static Matrix mult(double c, Matrix m) {

        MutableMatrix b = new BasicMatrix(m.getNRows(), m.getNColumns());
        for (int i = 0; i < m.getNRows(); i++) {
            for (int j = 0; j < m.getNColumns(); j++) {
                b.setElement(i, j, c * m.e(i, j));
            }
        }
        return b;
    }

    /**
     * Fill in part of a matrix with the contents of another matrix.
     *
     * @param mat matrix to be modified
     * @param sub submatrix to be inserted
     * @param row row where insertion is to take place
     * @param col column where insertion is to take place
     */
    public static void setSubMatrix(MutableMatrix mat, Matrix sub, int row, int col) {

        //  First check that the submatrix fits in the matrix
        if (row < 0
                || col < 0
                || sub.getNRows() + row > mat.getNRows()
                || sub.getNColumns() + col > mat.getNColumns()) {
            throw new InvalidMatrixException("Invalid attempt to insert a submatrix into a matrix");
        }

        //  Loop over rows & columns to insert matrix
        for (int i = 0; i < sub.getNRows(); i++) {
            for (int j = 0; j < sub.getNColumns(); j++) {
                mat.setElement(i + row, j + col, sub.e(i, j));
            }
        }
    }

    /**
     * Extract part of a matrix.
     *
     * @param m matrix containing the desired submatrix
     * @param row row where the submatrix is located
     * @param col column where the submatrix is located
     * @param nrow number of rows in the submatrix
     * @param ncol number of columns in the submatrix
     * @return submatrix
     */
    public static Matrix getSubMatrix(Matrix m, int row, int col, int nrow, int ncol) {

        //  First check that the submatrix is a valid size
        if (row < 0
                || row + nrow > m.getNRows()
                || col < 0
                || col + ncol > m.getNColumns()) {
            throw new InvalidMatrixException("Invalid attempt to get a submatrix from a matrix");
        }

        //  Loop over rows & columns and get submatrix
        MutableMatrix sm = new BasicMatrix(nrow, ncol);
        for (int i = 0; i < nrow; i++) {
            for (int j = 0; j < ncol; j++) {
                sm.setElement(i, j, m.e(i + row, j + col));
            }
        }
        return sm;
    }

    /**
     * Convenience method to turn a 3 row column matrix into a vector.
     * @param m column matrix
     * @return vector containing contents of column matrix
     */
    public static Hep3Vector as3Vector(Matrix m) {

        //  First check that we have a 3x1 matrix
        if (m.getNRows() != 3 || m.getNColumns() != 1) {
            throw new InvalidMatrixException("Invalid attempt to form a vector from a matrix");
        }
        return new BasicHep3Vector(m.e(0, 0), m.e(1, 0), m.e(2, 0));
    }

    public static class IndeterminateMatrixException extends InvalidMatrixException {

        public IndeterminateMatrixException() {
            super("Matrix is indeterminate");
        }
    };

    public static class InvalidMatrixException extends RuntimeException {

        public InvalidMatrixException(String message) {
            super(message);
        }
    }
}
