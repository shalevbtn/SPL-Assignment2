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
        // TODO: resolve computation tree step by step until final matrix is produced
        computationRoot.associativeNesting();
        while(computationRoot.getNodeType() != ComputationNodeType.MATRIX) {
            ComputationNode nodeToSolve = computationRoot.findResolvable();
            loadAndCompute(nodeToSolve);
            
        }
        loadAndCompute(computationRoot);
        return null;
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        leftMatrix.loadRowMajor(node.getChildren().get(0).getMatrix());

        ComputationNodeType type = node.getNodeType();
        switch (type) {
            case ADD:
                rightMatrix.loadRowMajor(node.getChildren().get(1).getMatrix());
                if (leftMatrix.length() != rightMatrix.length()) {
                    throw new ArithmeticException();
                }
                executor.submitAll(createAddTasks());
                break;

            case MULTIPLY:
                rightMatrix.loadColumnMajor(node.getChildren().get(1).getMatrix());
                executor.submitAll(createMultiplyTasks());
                break;

            case NEGATE:
                executor.submitAll(createNegateTasks());
                break;

            case TRANSPOSE:
                executor.submitAll(createTransposeTasks());
                break;
            default:
                throw new IllegalStateException("Unexpected type: " + type);
        }
        node.resolve(leftMatrix.readRowMajor());
    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        List<Runnable> tasks = new LinkedList<>();
        for (int i = 0; i < leftMatrix.length(); i++){
            SharedVector lv = leftMatrix.get(i);
            SharedVector rv = rightMatrix.get(i);
            tasks.add(()->lv.add(rv));
        }
        return tasks;
    }

    public List<Runnable> createMultiplyTasks() {
        // TODO: return tasks that perform row × matrix multiplication
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
        for(int i = 0 ; i < rightMatrix.length(); i++) {
            SharedVector v = rightMatrix.get(i);
            ret.add(() -> v.transpose());
        }

        return ret;
    }

    public String getWorkerReport() {
        return executor.getWorkerReport();
    }
}
