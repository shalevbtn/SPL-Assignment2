package spl.lae;

import memory.*;
import scheduling.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LAEHardTest {

    private LinearAlgebraEngine engine;
    private final int THREAD_COUNT = 4;

    @BeforeEach
    void setUp() {
        engine = new LinearAlgebraEngine(THREAD_COUNT);
    }

    /**
     * EDGE CASE: Matrix Multiplication with non-square dimensions.
     * Tests: loadColumnMajor, vecMatMul, and result reading.
     * (2x3) * (3x2) = (2x2)
     */
    @Test
    @DisplayName("Test Matrix Multiplication Correctness & Orientation")
    void testMatrixMultiplication() {
        double[][] a = {
            {1, 2, 3},
            {4, 5, 6}
        };
        double[][] b = {
            {7, 8},
            {9, 10},
            {11, 12}
        };
        // Expected: 
        // [1*7 + 2*9 + 3*11, 1*8 + 2*10 + 3*12] = [58, 64]
        // [4*7 + 5*9 + 6*11, 4*8 + 5*10 + 6*12] = [139, 154]
        
        SharedMatrix m1 = new SharedMatrix(a);
        SharedMatrix m2 = new SharedMatrix();
        m2.loadColumnMajor(b); // Critical: Engine loads right matrix as Column Major

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < m1.length(); i++) {
            SharedVector v = m1.get(i);
            tasks.add(() -> v.vecMatMul(m2));
        }

        // We use a temporary executor to test the logic manually
        TiredExecutor executor = new TiredExecutor(THREAD_COUNT);
        executor.submitAll(tasks);

        double[][] result = m1.readRowMajor();
        assertEquals(58, result[0][0], 0.001);
        assertEquals(154, result[1][1], 0.001);
    }

    /**
     * HARD CASE: Fatigue Scheduling Fairness
     * Strategy: Submit many tasks. Each worker has a different Fatigue Factor (randomized).
     * Verify that all workers are utilized and the "least fatigued" logic doesn't starve anyone.
     */
    @Test
    @DisplayName("Verify Fatigue Scheduling Fairness")
    void testFatigueScheduling() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(THREAD_COUNT);
        AtomicInteger taskCount = new AtomicInteger(0);
        
        // Submit 100 small tasks
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            tasks.add(() -> {
                try {
                    Thread.sleep(10); // Simulate work
                    taskCount.incrementAndGet();
                } catch (InterruptedException e) {}
            });
        }
        
        executor.submitAll(tasks);
        
        String report = executor.getWorkerReport();
        System.out.println(report);

        // Verify all worker IDs (0 to THREAD_COUNT-1) appear in the report
        for (int i = 0; i < THREAD_COUNT; i++) {
            assertTrue(report.contains("Worker " + i), "Worker " + i + " was never used!");
        }
        assertEquals(100, taskCount.get());
    }

    /**
     * EDGE CASE: Dimension Mismatch ArithmeticException
     * Tests: Section 2.3.1 - "If an operation is illegal, throw ArithmeticException"
     */
    @Test
    @DisplayName("Test Dimension Mismatch Exceptions")
    void testDimensionMismatch() {
        double[][] a = {{1, 2}, {3, 4}};
        double[][] b = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}; // 3x3

        SharedVector v1 = new SharedVector(a[0], VectorOrientation.ROW_MAJOR);
        SharedVector v2 = new SharedVector(b[0], VectorOrientation.ROW_MAJOR);

        // Adding vectors of different lengths should throw ArithmeticException
        assertThrows(ArithmeticException.class, () -> v1.add(v2));

        // Dot product of same orientation should throw ArithmeticException (must be Row * Col)
        assertThrows(ArithmeticException.class, () -> v1.dot(v2));
    }

    /**
     * EDGE CASE: Concurrency and Locking
     * Multiple tasks reading the same Right Matrix while it is potentially being accessed.
     */
    @Test
    @DisplayName("Stress Test Read/Write Locks")
    void testLockingConcurrency() {
        double[][] bigData = new double[50][50];
        for(int i=0; i<50; i++) for(int j=0; j<50; j++) bigData[i][j] = 1.0;

        SharedMatrix m1 = new SharedMatrix(bigData);
        SharedMatrix m2 = new SharedMatrix();
        m2.loadColumnMajor(bigData);

        // Submit many multiplications targeting the same SharedMatrix
        // This tests if SharedVector.vecMatMul correctly acquires read locks on m2 vectors
        List<Runnable> tasks = new ArrayList<>();
        for(int i=0; i<50; i++) {
            SharedVector v = m1.get(i);
            tasks.add(() -> v.vecMatMul(m2));
        }

        assertDoesNotThrow(() -> {
            TiredExecutor exec = new TiredExecutor(10);
            exec.submitAll(tasks);
            exec.shutdown();
        });
    }

    /**
     * EDGE CASE: Transpose and Negate logic
     */
    @Test
    @DisplayName("Test Transpose and Negate")
    void testUnaryOperations() {
        double[] data = {1.0, -2.0, 3.0};
        SharedVector v = new SharedVector(data, VectorOrientation.ROW_MAJOR);
        
        v.negate();
        assertEquals(-1.0, v.get(0));
        assertEquals(2.0, v.get(1));
        
        v.transpose();
        assertEquals(VectorOrientation.COLUMN_MAJOR, v.getOrientation());
    }
}