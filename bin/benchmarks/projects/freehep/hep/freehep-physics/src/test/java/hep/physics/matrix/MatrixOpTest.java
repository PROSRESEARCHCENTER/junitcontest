package hep.physics.matrix;

import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author tonyj
 */
public class MatrixOpTest extends TestCase {

    private Random r = new Random(123456);

    public MatrixOpTest(String testName) {
        super(testName);
    }

    /**
     * Test of transposed method, of class MatrixOp.
     */
    public void testTransposed() {
        MutableMatrix min = new BasicMatrix(6, 2);
        fillRandom(min);
        MutableMatrix mout = new BasicMatrix(2, 6);
        MatrixOp.transposed(min, mout);
        testTransposed(min, mout);
    }

    public void testTransposed2() {
        MutableMatrix min = new BasicMatrix(6, 2);
        Matrix mout = MatrixOp.transposed(min);
        testTransposed(min, mout);
    }

    public void testSquareTranspose() {
        MutableMatrix min = new BasicMatrix(3, 3);
        fillRandom(min);
        MutableMatrix mout = new BasicMatrix(min);
        MatrixOp.transposed(mout, mout);
        testTransposed(min, mout);
    }

    public void testInverse() {
        MutableMatrix min = new BasicMatrix(5, 5);
        fillRandom(min);
        Matrix mout = MatrixOp.inverse(min);
        double detIn = MatrixOp.det(min);
        double detOut = MatrixOp.det(mout);
        assertEquals(1.0, detIn * detOut, 1e-8);
        Matrix mult = MatrixOp.mult(min, mout);
        double det = MatrixOp.det(mult);
        assertEquals(1.0, det, 1e-8);
    }

    public void testInverse2() {
        MutableMatrix min = new BasicMatrix(5, 5);
        fillRandom(min);
        MutableMatrix mout = new BasicMatrix(min);
        MatrixOp.inverse(mout, mout);
        double detIn = MatrixOp.det(min);
        double detOut = MatrixOp.det(mout);
        assertEquals(1.0, detIn * detOut, 1e-8);
        Matrix mult = MatrixOp.mult(min, mout);
        double det = MatrixOp.det(mult);
        assertEquals(1.0, det, 1e-8);
    }

    private void fillRandom(MutableMatrix min) {
        for (int i = 0; i < min.getNRows(); i++) {
            for (int j = 0; j < min.getNColumns(); j++) {
                min.setElement(i, j, r.nextDouble());
            }
        }
    }

    private void testTransposed(Matrix min, Matrix mout) {
        for (int i = 0; i < min.getNRows(); i++) {
            for (int j = 0; j < min.getNColumns(); j++) {
                assertEquals(min.e(i, j), mout.e(j, i));
            }
        }
    }
}
