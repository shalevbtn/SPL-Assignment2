package spl.lae;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.ComputationNode;

public class LinearAlgebraEngineTest {

    private LinearAlgebraEngine lae = new LinearAlgebraEngine(10);
    private ComputationNode add;
    private ComputationNode subtract;
    private ComputationNode multiply;
    private ComputationNode divide;
    private ComputationNode transpose;
    private ComputationNode matrix1 = new ComputationNode(new {{1,2,3},{4,5,6}});
    private int[] ints = new {};
    @BeforeEach
    void setUp() {
        add = new ComputationNode("+", );
        subtract = new ComputationNode("-");

    }
    @Test
    void testNegate() {}
}
