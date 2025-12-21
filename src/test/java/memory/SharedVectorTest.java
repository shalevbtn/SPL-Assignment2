package memory;

import static org.junit.jupiter.api.Assertions.*; 
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class SharedVectorTest {
    private double[] data = {1.0, 2.0, 3.0};
    private SharedVector v;

    @BeforeEach
    void setUp() {
        v = new SharedVector(data, VectorOrientation.ROW_MAJOR);
    }

    @Test
    void testGetSuccess() {
        assertEquals(1.0, v.get(0));
        assertEquals(2.0, v.get(1));
        assertEquals(3.0, v.get(2));

        v.add(v);

        assertEquals(2.0, v.get(0));
        assertEquals(4.0, v.get(1));
        assertEquals(6.0, v.get(2));
    }

    @Test
    void testLengthSuccess() {
        assertEquals(3, v.length());
    }

    @Test
    void testTransposeSuccess() {
        v.transpose();
        assertEquals(VectorOrientation.COLUMN_MAJOR, v.getOrientation());
    }

}