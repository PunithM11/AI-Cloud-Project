package cloudsim;

import org.apache.commons.math3.linear.*;
import java.util.Random;

public class FuzzyCMeans {
    private int numClusters;
    private RealMatrix centroids;
    private RealMatrix membershipMatrix;

    // Predefined centroids for each attack type
    private static final RealMatrix PREDEFINED_CENTROIDS = MatrixUtils.createRealMatrix(new double[][] {
        {1000, 100, 0, 0, 0, 0, 0}, // DDoS
        {500, 50, 0, 0, 0, 0, 0},   // DoS
        {0, 0, 100, 0, 0, 0, 0},    // SQL Injection
        {0, 0, 0, 50, 100, 0, 0}    // BruteForce
    });

    public FuzzyCMeans(int numClusters, int featureSize) {
        if (numClusters <= 0 || featureSize <= 0) {
            throw new IllegalArgumentException("[ERROR] Number of clusters and feature size must be positive.");
        }

        this.numClusters = numClusters;
        this.centroids = PREDEFINED_CENTROIDS; // Initialize with predefined centroids
        this.membershipMatrix = null; // Will be initialized in the train method
    }

    // Method to get centroids
    public RealMatrix getCentroids() {
        return centroids;
    }

    private void initializeMembershipMatrix(int numDataPoints) {
        Random rand = new Random();
        this.membershipMatrix = MatrixUtils.createRealMatrix(numDataPoints, numClusters);
        for (int i = 0; i < numDataPoints; i++) {
            double sum = 0;
            for (int j = 0; j < numClusters; j++) {
                double value = rand.nextDouble();
                membershipMatrix.setEntry(i, j, value);
                sum += value;
            }
            for (int j = 0; j < numClusters; j++) {
                membershipMatrix.setEntry(i, j, membershipMatrix.getEntry(i, j) / sum);
            }
        }
    }

    public void train(RealMatrix data, int maxIterations) {
        if (data == null || data.getRowDimension() == 0 || data.getColumnDimension() == 0) {
            throw new IllegalArgumentException("[ERROR] Data matrix is empty or null.");
        }

        if (maxIterations <= 0) {
            throw new IllegalArgumentException("[ERROR] Maximum iterations must be positive.");
        }

        // Initialize membership matrix with the correct dimensions
        initializeMembershipMatrix(data.getRowDimension());

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            System.out.println("[DEBUG] Fuzzy C-Means Iteration: " + iteration);
            updateCentroids(data);
        }
        System.out.println("[INFO] Fuzzy C-Means clustering completed.");
    }

    private void updateCentroids(RealMatrix data) {
        for (int j = 0; j < numClusters; j++) {
            RealMatrix numerator = MatrixUtils.createRealMatrix(1, data.getColumnDimension());
            double denominator = 0;
            for (int i = 0; i < data.getRowDimension(); i++) {
                double membershipPower = Math.pow(membershipMatrix.getEntry(i, j), 2);
                numerator = numerator.add(data.getRowMatrix(i).scalarMultiply(membershipPower));
                denominator += membershipPower;
            }
            if (denominator > 0) {
                centroids.setRowMatrix(j, numerator.scalarMultiply(1 / denominator));
            }
        }
    }
}