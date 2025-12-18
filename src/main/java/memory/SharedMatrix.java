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
        
        if (matrix == null || matrix.length == 0) {
            holder = new SharedMatrix();
        }
        else {
            holder = new SharedMatrix(matrix);
        }
        this.vectors = holder.vectors;
    }

    public void loadColumnMajor(double[][] matrix) {
        int rows = matrix.length;
        int columns = matrix[0].length;
        vectors = new SharedVector[columns];

        for(int i = 0; i < columns; i++) {
            double[] newVector = new double[rows];

            for(int j = 0; j < rows; j++) {
                newVector[j] = matrix[j][i];
            }

            vectors[i] = new SharedVector(newVector, VectorOrientation.COLUMN_MAJOR);
        }

    }

    public double[][] readRowMajor() {
        // TODO: return matrix contents as a row-major double[][]
        int rowNum = length();
        int columnNum = vectors[0].length();

        double[][] retMatrix = new double[rowNum][columnNum];
        
        for(int i = 0 ; i < rowNum; i++) {
            SharedVector currRow = vectors[i];
            for(int j = 0; j < columnNum; j++) {
                retMatrix[i][j] = currRow.get(j);
            }
        }
        return retMatrix;
    }

    public synchronized SharedVector get(int index) {
        return vectors[index];
    }

    public synchronized int length() {
        return vectors.length;
    }

   public synchronized VectorOrientation getOrientation() {
    if (vectors != null && vectors.length > 0) {
        return vectors[0].getOrientation();
    }
    return VectorOrientation.ROW_MAJOR; // Default
}

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        for(SharedVector v : vecs)
            v.readLock();
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        for(SharedVector v : vecs)
            v.readUnlock();
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        for(SharedVector v : vecs)
            v.writeLock();
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
        for(SharedVector v : vecs)
            v.writeUnlock();
    }
}
