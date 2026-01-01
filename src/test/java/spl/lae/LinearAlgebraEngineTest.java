package spl.lae;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.ComputationNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinearAlgebraEngineTest {

    private LinearAlgebraEngine lae;
    private ComputationNode add;
    private ComputationNode add2;
    private ComputationNode multiply;
    private ComputationNode negate;
    private ComputationNode transpose;
    private ComputationNode matrix1 = new ComputationNode(new double[][] {{1,2,3},{4,5,6}});
    private ComputationNode matrix2 = new ComputationNode(new double[][] {{1,1,1},{2,2,2}});
    private ComputationNode matrix3 = new ComputationNode(new double[][] {{1,2},{1,2},{2,3}});
    private ComputationNode matrix4 = new ComputationNode(new double[][] {{1,1},{2,2}});
    private ComputationNode matrix5 = new ComputationNode(new double[][] {{5,5,5},{8,8,8}});
    private ComputationNode matrix6 = new ComputationNode(new double[][] {{0,1,9},{1,2,3}});

    @BeforeEach
    void setUp() {
        lae = new LinearAlgebraEngine(3);
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
    void testAdd() throws InterruptedException {
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

    @Test
    void complexTest(){
        List<ComputationNode> addChildren = new ArrayList<>();
        addChildren.add(matrix1);
        addChildren.add(matrix2);
        addChildren.add(matrix6);
        List<ComputationNode> multiplyChildren = new ArrayList<>();
        multiplyChildren.add(matrix4);
        multiplyChildren.add(matrix5);
        add = new ComputationNode("+", addChildren);
        multiply = new ComputationNode("*", multiplyChildren);
        List<ComputationNode> add2Children = new ArrayList<>();
        add2Children.add(add);
        add2Children.add(multiply);
        add2 = new ComputationNode("+", add2Children);
        List<ComputationNode> negateChildren = new ArrayList<>();
        negateChildren.add(add2);
        negate = new ComputationNode("-", negateChildren);
        List<ComputationNode> transposeChildren = new ArrayList<>();
        transposeChildren.add(negate);
        transpose = new ComputationNode("T", transposeChildren);
        double[][] solve = new double[][] {{-15,-32},{-16,-33},{-17,-34}};
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

    @Test
    void testAddThirtyChildren() {
        int numChildren = 30;
        int rows = 2;
        int cols = 2;
        List<ComputationNode> children = new ArrayList<>();

        // יצירת 30 בנים - כל אחד הוא מטריצה של 1 על 1 (או גודל אחר לבחירתך) מלאה ב-1
        for (int i = 0; i < numChildren; i++) {
            double[][] data = new double[rows][cols];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    data[r][c] = 1.0;
                }
            }
            children.add(new ComputationNode(data));
        }

        // יצירת ה-Node המרכזי מסוג חיבור
        ComputationNode root = new ComputationNode("+", children);

        // הרצה
        lae.run(root);

        // בדיקת התוצאה: כל תא אמור להיות שווה ל-30.0
        double[][] result = root.getMatrix();
        assertEquals(rows, result.length, "Number of rows should match");
        assertEquals(cols, result[0].length, "Number of columns should match");

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                assertEquals(30.0, result[i][j], "Cell [" + i + "][" + j + "] should be 30.0");
            }
        }
    }

    @Test
    void testAddThreeHundredChildren() {
        int numChildren = 300;
        int rows = 3;
        int cols = 3;
        double valuePerCell = 0.5;
        double expectedValue = numChildren * valuePerCell; // 150.0

        List<ComputationNode> children = new ArrayList<>();

        // יצירת 300 בנים - כל אחד מכיל מטריצה 3x3 עם הערך 0.5
        for (int i = 0; i < numChildren; i++) {
            double[][] data = new double[rows][cols];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    data[r][c] = valuePerCell;
                }
            }
            children.add(new ComputationNode(data));
        }

        // יצירת ה-Node המרכזי לחיבור 300 המטריצות
        ComputationNode root = new ComputationNode("+", children);

        // הרצת המנוע
        lae.run(root);

        // בדיקת התוצאות
        double[][] result = root.getMatrix();
        assertEquals(rows, result.length, "Rows mismatch");
        assertEquals(cols, result[0].length, "Cols mismatch");

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                assertEquals(expectedValue, result[i][j], 0.0001,
                        "Cell [" + i + "][" + j + "] should be " + expectedValue);
            }
        }
    }
}
