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
        rightMatrix.loadRowMajor(node.getChildren().get(1).getMatrix());

        switch (node.getNodeType()) {
            case ADD:
        // handle addition
        break;

    case MULTIPLY:
        // handle multiplication
        break;

    case NEGATE:
        // handle negation
        break;

    case TRANSPOSE:
        // handle transpose
        break;
    default:
        throw new IllegalStateException("Unexpected type: " + type);
        }

    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
        return null;
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

        for(int i = 0 ; i < rightMatrix.length(); i++) {
            SharedVector v = rightMatrix.get(i);
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
