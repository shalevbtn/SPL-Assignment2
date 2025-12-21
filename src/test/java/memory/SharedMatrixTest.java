package memory;

import static org.junit.jupiter.api.Assertions.*; 
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class SharedMatrixTest {

    private double[][] arg = {{1.0,2.0},{5.0,6.0},{8.0,9.0}};
    private SharedMatrix matrix;

    @BeforeEach
    void setUp() {
        // Initialize your object before every test
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
}
