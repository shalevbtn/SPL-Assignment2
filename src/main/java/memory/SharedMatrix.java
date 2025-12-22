package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        int rowsNum = matrix.length;
        vectors = new SharedVector[rowsNum];

        for(int i = 0; i < rowsNum; i++) {
            vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }
    }

    public void loadRowMajor(double[][] matrix) {
        SharedMatrix holder;
        acquireAllVectorWriteLocks(vectors);
        if (matrix == null || matrix.length == 0) {
            holder = new SharedMatrix();
        }
        else {
            holder = new SharedMatrix(matrix);
        }
        releaseAllVectorWriteLocks(vectors);
        this.vectors = holder.vectors;
    }

    public void loadColumnMajor(double[][] matrix) {
        int rows = matrix.length;
        int columns = matrix[0].length;
        acquireAllVectorWriteLocks(vectors);
        SharedVector[] arr = new SharedVector[columns];

        for(int i = 0; i < columns; i++) {
            double[] newVector = new double[rows];

            for(int j = 0; j < rows; j++) {
                newVector[j] = matrix[j][i];
            }

            arr[i] = new SharedVector(newVector, VectorOrientation.COLUMN_MAJOR);
        }
        releaseAllVectorWriteLocks(vectors);
        vectors = arr;

    }

    public double[][] readRowMajor() {
        acquireAllVectorReadLocks(vectors);
        int rowNum = length();
        int columnNum = vectors[0].length();

        double[][] retMatrix = new double[rowNum][columnNum];

        for(int i = 0 ; i < rowNum; i++) {
            SharedVector currRow = vectors[i];
            for(int j = 0; j < columnNum; j++) {
                retMatrix[i][j] = currRow.get(j);
            }
        }

        releaseAllVectorReadLocks(vectors);
        return retMatrix;
    }

    public SharedVector get(int index) {
        vectors[index].readLock();
        try {
            return vectors[index];
        }
        finally { vectors[index].readUnlock(); }
    }

    public int length() {
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        vectors[0].readLock();
        try {
            if (vectors != null && vectors.length > 0) {
                return vectors[0].getOrientation();
            }
            return VectorOrientation.ROW_MAJOR; // Default
        } finally { vectors[0].readUnlock(); }
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        if (vecs != null) {
            for (SharedVector v : vecs)
                v.readLock();
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        if (vecs != null) {
            for (SharedVector v : vecs)
                v.readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        if (vecs != null) {
            for (SharedVector v : vecs)
                v.writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
        if (vecs != null) {
            for (SharedVector v : vecs)
                v.writeUnlock();
        }
    }
}