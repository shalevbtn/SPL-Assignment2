package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
        // TODO: construct matrix as row-major SharedVectors
        int rowsNum = matrix.length;
        vectors = new SharedVector[rowsNum];

        for(int i = 0; i < rowsNum; i++) {
            vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }
    }

    public void loadRowMajor(double[][] matrix) {
        // TODO: replace internal data with new row-major matrix
        this.vectors = new SharedMatrix(matrix).vectors;
    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
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

    public SharedVector get(int index) {
        return vectors[index];
    }

    public int length() {
        return vectors.length;
    }

   public VectorOrientation getOrientation() {
    if (vectors != null && vectors.length > 0) {
        return vectors[0].getOrientation();
    }
    return VectorOrientation.ROW_MAJOR; // Default
}

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
    }
}
