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
        int rowNum;
        int columnNum;
        double[][] retMatrix;

        acquireAllVectorReadLocks(vectors);

        if(get(0).getOrientation() == VectorOrientation.COLUMN_MAJOR) {
            rowNum = get(0).length();
            columnNum = length();
            retMatrix = new double[rowNum][columnNum];

            for(int i = 0 ; i < columnNum; i++) {
                SharedVector v = get(i);
                for(int j = 0; j < rowNum; j++) {
                    retMatrix[j][i] = v.get(j);
                }
            }
        }
        else {
            rowNum = length();
            columnNum = vectors[0].length();
            retMatrix = new double[rowNum][columnNum];

            for(int i = 0 ; i < rowNum; i++) {
                SharedVector v = get(i);
                for(int j = 0; j < columnNum; j++) {
                    retMatrix[i][j] = v.get(j);
                }
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
        if (vecs != null) {
            for (SharedVector v : vecs)
                v.readUnlock();
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        if (vecs != null) {
            for (SharedVector v : vecs)
                v.writeLock();
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        if (vecs != null) {
            for (SharedVector v : vecs)
                v.writeUnlock();
        }
    }
}