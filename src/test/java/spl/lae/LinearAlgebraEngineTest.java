package spl.lae;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.ComputationNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinearAlgebraEngineTest {

    private LinearAlgebraEngine lae = new LinearAlgebraEngine(10);
    private ComputationNode add;
    private ComputationNode multiply;
    private ComputationNode negate;
    private ComputationNode transpose;
    private ComputationNode matrix1 = new ComputationNode(new double[][] {{1,2,3},{4,5,6}});
    private ComputationNode matrix2 = new ComputationNode(new double[][] {{1,1,1},{2,2,2}});

    @BeforeEach
    void setUp() {
        List<ComputationNode> children1 = new ArrayList<>();
        List<ComputationNode> children2 = new ArrayList<>();
        children1.add(matrix1);
        children1.add(matrix2);
        children2.add(matrix1);
        add = new ComputationNode("+", children1);
        multiply = new ComputationNode("*", children1);
        negate = new ComputationNode("-", children2);
        transpose = new ComputationNode("T", children2);
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
        double[][] solve = new double[][] {{6,12},{15,30}};
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
        double[][] solve = new double[][] {{1},{2},{3}};
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
