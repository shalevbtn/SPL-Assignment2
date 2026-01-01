package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        vectors = new SharedVector[0];
    }

    public SharedMatrix(double[][] matrix) {
       if (matrix == null || matrix.length == 0) {
            vectors = new SharedVector[0];
        } else {
            int rowsNum = matrix.length;
            vectors = new SharedVector[rowsNum];
            for (int i = 0; i < rowsNum; i++) {
                vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
            }
        }
    }

    public void loadRowMajor(double[][] matrix) {
        SharedVector[] oldVectors = this.vectors;
        acquireAllVectorWriteLocks(oldVectors);

        try {
            if (matrix == null || matrix.length == 0) {
                this.vectors = new SharedVector[0];
            } else {
                SharedVector[] newVectors = new SharedVector[matrix.length];
                for (int i = 0; i < matrix.length; i++) {
                    newVectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
                }
                this.vectors = newVectors;
            }
        } finally {
            releaseAllVectorWriteLocks(oldVectors);
        }
    }

    public void loadColumnMajor(double[][] matrix) {
        SharedVector[] oldVectors = this.vectors;
        acquireAllVectorWriteLocks(oldVectors);
        
        try {
            if (matrix == null || matrix.length == 0) {
                this.vectors = new SharedVector[0];
                return;
            }

            int rows = matrix.length;
            int columns = matrix[0].length;
            SharedVector[] arr = new SharedVector[columns];

            for (int i = 0; i < columns; i++) {
                double[] newVector = new double[rows];
                for (int j = 0; j < rows; j++) {
                    newVector[j] = matrix[j][i];
                }
                arr[i] = new SharedVector(newVector, VectorOrientation.COLUMN_MAJOR);
            }
            this.vectors = arr;
        } finally {
            releaseAllVectorWriteLocks(oldVectors);
        }
    }

    public double[][] readRowMajor() {
        if (length() == 0) {
            return new double[0][0];
        }

        SharedVector[] currentVectors = this.vectors;
        acquireAllVectorReadLocks(currentVectors);
        
        try {
            int rowNum;
            int columnNum;
            double[][] retMatrix;

            if (currentVectors[0].getOrientation() == VectorOrientation.COLUMN_MAJOR) {
                rowNum = currentVectors[0].length();
                columnNum = currentVectors.length;
                retMatrix = new double[rowNum][columnNum];

                for (int i = 0; i < columnNum; i++) {
                    SharedVector v = currentVectors[i];
                    for (int j = 0; j < rowNum; j++) {
                        retMatrix[j][i] = v.get(j);
                    }
                }
            } else {
                rowNum = currentVectors.length;
                columnNum = currentVectors[0].length();
                retMatrix = new double[rowNum][columnNum];

                for (int i = 0; i < rowNum; i++) {
                    SharedVector v = currentVectors[i];
                    for (int j = 0; j < columnNum; j++) {
                        retMatrix[i][j] = v.get(j);
                    }
                }
            }
            return retMatrix;
        } finally {
            releaseAllVectorReadLocks(currentVectors);
        }
    }

    public SharedVector get(int index) {
        return vectors[index];
    }

    public int length() {
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        if (vectors == null || vectors.length == 0) {
            return VectorOrientation.ROW_MAJOR; //Default
        }

        vectors[0].readLock();
        try {
            return vectors[0].getOrientation();
        } finally {
            vectors[0].readUnlock();
        }
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