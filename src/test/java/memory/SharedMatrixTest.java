package memory;

import static org.junit.jupiter.api.Assertions.*; 
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class SharedMatrixTest {

    private double[][] arg = {{1.0,2.0},{5.0,6.0},{8.0,9.0}};
    private SharedMatrix matrix;

    @BeforeEach
    void setUp() {
        matrix = new SharedMatrix();
    }

    @Test
    void loadRowMajorTest(){
        matrix.loadRowMajor(arg);

        assertEquals(1.0, matrix.get(0).get(0));
        assertEquals(2.0, matrix.get(0).get(1));
        assertEquals(5.0, matrix.get(1).get(0));
        assertEquals(6.0, matrix.get(1).get(1));
        assertEquals(8.0, matrix.get(2).get(0));
        assertEquals(9.0, matrix.get(2).get(1));
    }

    @Test
    void loadColMajorTest(){
        matrix.loadColumnMajor(arg);

        assertEquals(1.0, matrix.get(0).get(0));
        assertEquals(5.0, matrix.get(0).get(1));
        assertEquals(8.0, matrix.get(0).get(2));
        assertEquals(2.0, matrix.get(1).get(0));
        assertEquals(6.0, matrix.get(1).get(1));
        assertEquals(9.0, matrix.get(1).get(2));
    }

    @Test
    void readRowMajorTest(){
        matrix.loadRowMajor(arg);

        for (int i=0; i<arg.length; i++){
            for(int j=0; j<arg[i].length; j++){
                assertEquals(arg[i][j],matrix.readRowMajor()[i][j]);
            }
        }

    }

    @Test
    void testDotProduct() {
        SharedVector v1 = new SharedVector(new double[]{1, 2}, VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(new double[]{3, 4}, VectorOrientation.COLUMN_MAJOR);
        assertEquals(11.0, v1.dot(v2)); // (1*3) + (2*4) = 11
    }

    @Test
    void testNegate() {
        SharedVector v = new SharedVector(new double[]{5, -2}, VectorOrientation.ROW_MAJOR);
        v.negate();
        assertEquals(-5.0, v.get(0));
        assertEquals(2.0, v.get(1));
    }

    @Test
    void testEmptyMatrix() {
        SharedMatrix empty = new SharedMatrix();
        assertEquals(0, empty.length());
    }

    @Test
    void testSingleElement() {
        double[][] data = {{5.0}};
        matrix.loadRowMajor(data);
        assertEquals(5.0, matrix.get(0).get(0));
    }
}
