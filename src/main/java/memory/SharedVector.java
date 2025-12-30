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
        readLock();
        try {
            return vector[index];
        }
        finally { readUnlock(); }

    }

    /**
     * @return length of the vector
     */
    public int length() {
        readLock();
        try {
            return vector.length;
        }
        finally { readUnlock(); }
    }


    public VectorOrientation getOrientation() {
        readLock();
        try {
            return orientation;
        }
        finally { readUnlock(); }
    }

    public void writeLock() {
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        lock.writeLock().unlock();
    }

    public void readLock() {
        lock.readLock().lock();
    }

    public void readUnlock() {
        lock.readLock().unlock();
    }

    public void transpose() {
        writeLock();
        try {
            if(this.orientation.equals(VectorOrientation.ROW_MAJOR)) {
                this.orientation = VectorOrientation.COLUMN_MAJOR;
            }
            else {
                this.orientation = VectorOrientation.ROW_MAJOR;
            }
        } finally { writeUnlock(); }
    }

    public void add(SharedVector other) {
        writeLock();
        other.readLock();

        if (!this.orientation.equals(other.orientation) || vector.length != other.length()) {
            writeUnlock();
            other.readUnlock();
            throw new ArithmeticException("Illegal operation: dimensions mismatch");
        }
        try
        {
            for (int i = 0; i < vector.length; i++)
                vector[i] += other.vector[i];
        }
        finally {
            writeUnlock();
            other.readUnlock();
        }
    }

    public void negate() {
        writeLock();
        try
        {
            for (int i = 0; i < vector.length; i++)
                vector[i] = -vector[i];
        }
        finally { writeUnlock(); }
    }

    public double dot(SharedVector other) {
        readLock();
        other.readLock();

        if (this.orientation.equals(other.orientation) || vector.length != other.length()) {
            throw new ArithmeticException("Illegal operation: dimensions mismatch");
        }    

        double sum = 0;

        try
        {
            for (int i = 0; i < vector.length; i++) 
                sum += vector[i] * other.vector[i];
            
        }
        finally {
            readUnlock();
            other.readUnlock();
        }

        return sum;
    }

    public void vecMatMul(SharedMatrix matrix) {
        writeLock();

        if(this.orientation == VectorOrientation.COLUMN_MAJOR) {
            writeUnlock();
            throw new ArithmeticException("Illegal operation: dimensions mismatch");
        }

        if(matrix.length() == 0) { return; }

        SharedVector fstVecotr = matrix.get(0);

        if(fstVecotr.orientation == VectorOrientation.ROW_MAJOR) {
            writeUnlock();
            throw new ArithmeticException("Illegal operation: dimensions mismatch");
        }

        if(length() != fstVecotr.length()) {
            writeUnlock();
            throw new ArithmeticException("Illegal operation: dimensions mismatch");
        }

        double[] result = new double[matrix.length()];

        /*for(int i = 0; i < matrix.length(); i++) {
            matrix.get(i).readLock();
        }*/

        for(int i = 0; i < matrix.length(); i++) {
            SharedVector currColumn = matrix.get(i);
            result[i] = this.dot(currColumn);
        }

        /*for(int i = 0; i < matrix.length(); i++) {
            matrix.get(i).readUnlock();
        }*/

        vector = result;
        writeUnlock();
    }
}
