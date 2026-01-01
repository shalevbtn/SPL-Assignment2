package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.LinkedList;
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        try {
            ComputationNode nodeToSolve = computationRoot;
            while(computationRoot.getNodeType() != ComputationNodeType.MATRIX) {
                nodeToSolve = computationRoot.findResolvable();
                nodeToSolve.associativeNesting();
                nodeToSolve = nodeToSolve.findResolvable();
                loadAndCompute(nodeToSolve);
            }

        } finally {
            try {
                executor.shutdown();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        return computationRoot;
    }

    public void loadAndCompute(ComputationNode node) {
        if (node.getChildren().size() == 0 || node.getChildren().size() > 2) {
            throw new IllegalArgumentException();
        }
        List<Runnable> tasks;
        leftMatrix.loadRowMajor(node.getChildren().get(0).getMatrix());
        validateMatrix(leftMatrix);

        ComputationNodeType type = node.getNodeType();
        switch (type) {
            case ADD:
                if (node.getChildren().size() != 2) {
                    throw new IllegalArgumentException();
                }
                rightMatrix.loadRowMajor(node.getChildren().get(1).getMatrix());
                validateMatrix(rightMatrix);
                validateAdd();

                tasks = createAddTasks();

                break;

            case MULTIPLY:
                if (node.getChildren().size() != 2) {
                    throw new IllegalArgumentException();
                }
                rightMatrix.loadColumnMajor(node.getChildren().get(1).getMatrix());
                validateMatrix(rightMatrix);
                validateDimensions();
                
                tasks = createMultiplyTasks();
                break;

            case NEGATE:
                if (node.getChildren().size() != 1) {
                    throw new IllegalArgumentException();
                }
                tasks = createNegateTasks();
                break;

            case TRANSPOSE:
                if (node.getChildren().size() != 1) {
                    throw new IllegalArgumentException();
                }
                tasks = createTransposeTasks();
                break;
            default:
                throw new IllegalStateException("Unexpected type: " + type);
        }
        
        executor.submitAll(tasks);
        node.resolve(leftMatrix.readRowMajor());
    }

    public List<Runnable> createAddTasks() {
        List<Runnable> tasks = new LinkedList<>();
        for (int i = 0; i < leftMatrix.length(); i++){
            SharedVector lv = leftMatrix.get(i);
            SharedVector rv = rightMatrix.get(i);
            tasks.add(()->lv.add(rv));
        }
        return tasks;
    }

    public List<Runnable> createMultiplyTasks() {
        List<Runnable> ret = new LinkedList<>();
        
        for(int i = 0 ; i < leftMatrix.length(); i++) {
            SharedVector v = leftMatrix.get(i);
            ret.add(() -> v.vecMatMul(rightMatrix));
        }

        return ret;
    }

    public List<Runnable> createNegateTasks() {
        List<Runnable> ret = new LinkedList<>();

        for(int i = 0 ; i < leftMatrix.length(); i++) {
            SharedVector v = leftMatrix.get(i);
            ret.add(() -> v.negate());
        }

        return ret;
    }

    public List<Runnable> createTransposeTasks() {
        List<Runnable> ret = new LinkedList<>();
        // TO CHECK
        for(int i = 0 ; i < leftMatrix.length(); i++) {
            SharedVector v = leftMatrix.get(i);
            ret.add(() -> v.transpose());
        }

        return ret;
    }

    public String getWorkerReport() {
        return executor.getWorkerReport();
    }

    private void validateMatrix(SharedMatrix m) {
        if(m.length() <= 0 || m.get(0).length() <= 0) {
            throw new ArithmeticException("Illegal operation: dimensions mismatch");
        }
    }

    private void validateDimensions() {
        if (leftMatrix.get(0).length() != rightMatrix.get(0).length()) 
            throw new ArithmeticException("Illegal operation: dimensions mismatch");
    }

    private void validateAdd() {
        validateDimensions();
        if(leftMatrix.length() != rightMatrix.length()) 
            throw new ArithmeticException("Illegal operation: dimensions mismatch");
    }
}
