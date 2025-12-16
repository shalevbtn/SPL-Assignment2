package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        this.vector = vector;
        this.orientation = orientation;
    }

    /**
     * @param index position in the vector
     * @return element at index
     */
    public double get(int index) {
        lock.writeLock().lock();
        try {
            return vector[index];
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @return length of the vector
     */
    public int length() {
        return vector.length;
    }


    public VectorOrientation getOrientation() {
        return orientation;
    }

    public void writeLock() {
        // TODO: acquire write lock
    }

    public void writeUnlock() {
        // TODO: release write lock
    }

    public void readLock() {
        // TODO: acquire read lock
    }

    public void readUnlock() {
        // TODO: release read lock
    }

    public void transpose() {
        if(this.orientation.equals(VectorOrientation.ROW_MAJOR)) {
            this.orientation = VectorOrientation.COLUMN_MAJOR;
        }
        else {
            this.orientation = VectorOrientation.ROW_MAJOR;
        }
    }

    public void add(SharedVector other) {
                if (!this.orientation.equals(other.orientation)) { throw new IllegalArgumentException("Illegal operation: dimensions mismatch"); } 


        for (int i = 0; i < vector.length; i++) {
            vector[i] += other.vector[i];
        }
    }

    public void negate() {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = -vector[i];
        }
    }

    public double dot(SharedVector other) {
        if (this.orientation.equals(other.orientation)) { throw new IllegalArgumentException("Illegal operation: dimensions mismatch"); } 
            

        double sum = 0;

        for (int i = 0; i < vector.length; i++) {
            sum += vector[i] * other.vector[i];
        }
        
        return sum;
    }

    public void vecMatMul(SharedMatrix matrix) {

        for(int i = 0; i < matrix.length(); i++) {
            SharedVector currRow = matrix.get(i);
            currRow.dot(this);
        }
    }
}
