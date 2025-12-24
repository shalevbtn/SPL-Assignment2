package spl.lae;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.ComputationNode;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinearAlgebraEngineTest {

    private LinearAlgebraEngine lae = new LinearAlgebraEngine(1);
    private ComputationNode add;
    private ComputationNode multiply;
    private ComputationNode negate;
    private ComputationNode transpose;
    private ComputationNode matrix1 = new ComputationNode(new double[][] {{1,2,3},{4,5,6}});
    private ComputationNode matrix2 = new ComputationNode(new double[][] {{1,1,1},{2,2,2}});
    private ComputationNode matrix3 = new ComputationNode(new double[][] {{1,2},{1,2},{2,3}});

    @BeforeEach
    void setUp() {
        List<ComputationNode> addChildren = new ArrayList<>();
        List<ComputationNode> mulChildren = new ArrayList<>();
        List<ComputationNode> negChildren = new ArrayList<>();
        addChildren.add(matrix1);
        addChildren.add(matrix2);
        mulChildren.add(matrix1);
        mulChildren.add(matrix3);
        negChildren.add(matrix1);
        add = new ComputationNode("+", addChildren);
        multiply = new ComputationNode("*", mulChildren);
        negate = new ComputationNode("-", negChildren);
        transpose = new ComputationNode("T", negChildren);
    }
    @Test
    void testAdd() {
        double[][] solve = new double[][] {{2,3,4},{6,7,8}};
        ComputationNode root = add;
        lae.run(root);
        assertEquals(root.getMatrix().length, solve.length);
        assertEquals(root.getMatrix()[0].length, solve[0].length);
        for (int i = 0; i < root.getMatrix().length; i++) {
            for (int j = 0; j < root.getMatrix()[0].length; j++) {
                assertEquals(root.getMatrix()[i][j], solve[i][j]);
            }
        }
    }

    @Test
    void testMultiply() {
        double[][] solve = new double[][] {{9,15},{21,36}};
        ComputationNode root = multiply;
        lae.run(root);

        assertEquals(root.getMatrix().length, solve.length);
        assertEquals(root.getMatrix()[0].length, solve[0].length);
        for (int i = 0; i < root.getMatrix().length; i++) {
            for (int j = 0; j < root.getMatrix()[0].length; j++) {
                assertEquals(root.getMatrix()[i][j], solve[i][j]);
            }
        }
    }

    @Test
    void testNegate() {
        double[][] solve = new double[][] {{-1,-2,-3},{-4,-5,-6}};
        ComputationNode root = negate;
        lae.run(root);
        assertEquals(root.getMatrix().length, solve.length);
        assertEquals(root.getMatrix()[0].length, solve[0].length);
        for (int i = 0; i < root.getMatrix().length; i++) {
            for (int j = 0; j < root.getMatrix()[0].length; j++) {
                assertEquals(root.getMatrix()[i][j], solve[i][j]);
            }
        }
    }

    @Test
    void testTranspose() {
        double[][] solve = new double[][] {{1,4},{2,5},{3,6}};
        ComputationNode root = transpose;
        lae.run(root);
        assertEquals(root.getMatrix().length, solve.length);
        assertEquals(root.getMatrix()[0].length, solve[0].length);
        for (int i = 0; i < root.getMatrix().length; i++) {
            for (int j = 0; j < root.getMatrix()[0].length; j++) {
                assertEquals(root.getMatrix()[i][j], solve[i][j]);
            }
        }
    }
}
