package memory;

import static org.junit.jupiter.api.Assertions.*; 
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class SharedMatrixTest {

    private SharedMatrix matrix;

    @BeforeEach
    void setUp() {
        // Initialize your object before every test
        matrix = new SharedMatrix();
    }
}
