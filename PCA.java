package cloudsim;

import org.apache.commons.math3.linear.*;

public class PCA {
    private RealMatrix transformationMatrix;

    public void trainPCA(RealMatrix data, int numComponents) {
        if (data == null || data.getRowDimension() == 0 || data.getColumnDimension() == 0) {
            throw new IllegalArgumentException("[ERROR] Data matrix is empty or null.");
        }

        if (numComponents <= 0 || numComponents > data.getColumnDimension()) {
            throw new IllegalArgumentException("[ERROR] Invalid number of components.");
        }

        // Center the data (subtract the mean of each feature)
        RealMatrix centeredData = centerData(data);

        // Perform Singular Value Decomposition (SVD)
        SingularValueDecomposition svd = new SingularValueDecomposition(centeredData);
        this.transformationMatrix = svd.getV().getSubMatrix(0, data.getColumnDimension() - 1, 0, numComponents - 1);
        System.out.println("[INFO] PCA training completed.");
    }

    private RealMatrix centerData(RealMatrix data) {
        RealMatrix centeredData = data.copy();
        for (int i = 0; i < data.getColumnDimension(); i++) {
            double mean = new ArrayRealVector(data.getColumn(i)).getL1Norm() / data.getRowDimension();
            for (int j = 0; j < data.getRowDimension(); j++) {
                centeredData.setEntry(j, i, data.getEntry(j, i) - mean);
            }
        }
        return centeredData;
    }

    public RealMatrix reduceDimensions(RealMatrix data) {
        if (transformationMatrix == null) {
            throw new IllegalStateException("[ERROR] PCA transformation matrix is not initialized. Call trainPCA() first.");
        }

        if (data == null || data.getRowDimension() == 0 || data.getColumnDimension() == 0) {
            throw new IllegalArgumentException("[ERROR] Data matrix is empty or null.");
        }

        if (data.getColumnDimension() != transformationMatrix.getRowDimension()) {
            throw new IllegalArgumentException("[ERROR] Data matrix dimensions do not match transformation matrix.");
        }

        return data.multiply(transformationMatrix);
    }
}