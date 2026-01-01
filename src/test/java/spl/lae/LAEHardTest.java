package spl.lae;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import parser.ComputationNode;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LAEHardTest {

    private LinearAlgebraEngine lae;

    @BeforeEach
    void setUp() {
        // We use a high number of threads to force maximum contention 
        // and exercise the fatigue-based priority queue.
        lae = new LinearAlgebraEngine(16); 
    }

    /**
     * THE HELL TEST: "The Grand Gauntlet"
     * Logic: -((((A * B) + C)^T * D) + E)
     * This test covers:
     * 1. Multiplication of non-square matrices (2x3 * 3x2)
     * 2. Addition of result to square matrix
     * 3. Transposition of the result
     * 4. Multiplication of transposed (2x2) with a skinny matrix (2x5)
     * 5. Final negation
     * 6. Deep nesting (Tree depth 5+)
     */
    @Test
    @DisplayName("The Grand Gauntlet - Deeply Nested Complex Operations")
    void grandGauntletTest() {
        // A: 2x3
        double[][] m1 = {{1, 2, 3}, {4, 5, 6}};
        // B: 3x2
        double[][] m2 = {{7, 8}, {9, 10}, {11, 12}};
        // Result A*B: [[58, 64], [139, 154]] (2x2)

        // C: 2x2
        double[][] m3 = {{1, 1}, {1, 1}};
        // Result (A*B)+C: [[59, 65], [140, 155]]

        // D: 2x5 (Skinny)
        double[][] m4 = {{1, 0, 1, 0, 1}, {0, 1, 0, 1, 0}};
        
        // Build the Tree: -(( ( (m1 * m2) + m3 )^T * m4 ))
        ComputationNode node1 = new ComputationNode(m1);
        ComputationNode node2 = new ComputationNode(m2);
        ComputationNode node3 = new ComputationNode(m3);
        ComputationNode node4 = new ComputationNode(m4);

        // A * B
        ComputationNode mul1 = new ComputationNode("*", List.of(node1, node2));
        // (A * B) + C
        ComputationNode add1 = new ComputationNode("+", List.of(mul1, node3));
        // ((A * B) + C)^T  -> Results in a 2x2: [[59, 140], [65, 155]]
        ComputationNode trans1 = new ComputationNode("T", List.of(add1));
        // (...)^T * D -> (2x2 * 2x5) = 2x5
        ComputationNode mul2 = new ComputationNode("*", List.of(trans1, node4));
        // Final Negation
        ComputationNode root = new ComputationNode("-", List.of(mul2));

        lae.run(root);

        double[][] result = root.getMatrix();

        // EXPECTED CALCULATION:
        // Transposed matrix was [[59, 140], [65, 155]]
        // Multiply by [[1,0,1,0,1], [0,1,0,1,0]]
        // Row 1: [59*1 + 140*0, 59*0 + 140*1, ...] -> [59, 140, 59, 140, 59]
        // Row 2: [65*1 + 155*0, 65*0 + 155*1, ...] -> [65, 155, 65, 155, 65]
        // Then Negated:
        double[][] expected = {
            {-59, -140, -59, -140, -59},
            {-65, -155, -65, -155, -65}
        };

        assertEquals(2, result.length);
        assertEquals(5, result[0].length);
        for (int i = 0; i < 2; i++) {
            assertArrayEquals(expected[i], result[i], 0.0001);
        }
    }

    /**
     * EDGE CASE: Associative Nesting Check
     * Requirement 2.1: Binary operators with >2 operands must be left-associative.
     * Test: +(A, B, C, D) -> (((A+B)+C)+D)
     */
    @Test
    @DisplayName("Associative Nesting - Multi-operand Addition")
    void testAssociativeNesting() {
        double[][] m;
        List<ComputationNode> children = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            m = new double[][] {{1}};
            children.add(new ComputationNode(m));
        } 
        // This will trigger computationNode.associativeNesting()
        ComputationNode root = new ComputationNode("+", children);
        
        lae.run(root);
        
        // 1 added to itself 10 times = 10
        assertEquals(10.0, root.getMatrix()[0][0]);
    }

    /**
     * EDGE CASE: Large Matrix to Force Thread Starvation
     * This creates a 50x50 matrix. If your tasks are one-per-row, this generates 50 tasks.
     * This ensures the PriorityBlockingQueue in TiredExecutor is working and threads
     * are being cycled correctly.
     */
    @Test
    @DisplayName("Stress Test - Large Matrix Contention")
    void testLargeMatrixContention() {
        int size = 100;
        double[][] bigData = new double[size][size];
        for(int i=0; i<size; i++) bigData[i][i] = 1.0; // Identity Matrix

        ComputationNode n1 = new ComputationNode(bigData);
        ComputationNode n2 = new ComputationNode(bigData);
        // I * I = I (but performed with 100 rows, 10,000 dot products)
        ComputationNode root = new ComputationNode("*", List.of(n1, n2));

        lae.run(root);

        for(int i=0; i<size; i++) {
            assertEquals(1.0, root.getMatrix()[i][i], 0.0001);
        }
    }

    /**
     * ERROR HANDLING: Illegal Dimensions Halt
     * Checks if the engine properly throws when matrix sizes don't match.
     */
    @Test
    @DisplayName("Error Handling - Dimension Mismatch M1_cols != M2_rows")
    void testIllegalDimensions() {
        double[][] m1 = {{1, 2, 3}}; // 1x3
        double[][] m2 = {{1, 2}, {3, 4}}; // 2x2
        
        ComputationNode root = new ComputationNode("*", List.of(
            new ComputationNode(m1), 
            new ComputationNode(m2)
        ));

        // Should throw ArithmeticException based on your validateDimensions()
        assertThrows(ArithmeticException.class, () -> lae.loadAndCompute(root));
    }
    
    /**
     * EDGE CASE: The "Zero Vector" and "Single Element"
     */
    @Test
    @DisplayName("Edge Case - Single Element Matrices")
    void testSingleElement() {
        double[][] m1 = {{2}};
        double[][] m2 = {{3}};
        ComputationNode root = new ComputationNode("*", List.of(
            new ComputationNode(m1), new ComputationNode(m2)
        ));
        lae.run(root);
        assertEquals(6.0, root.getMatrix()[0][0]);
    }
}