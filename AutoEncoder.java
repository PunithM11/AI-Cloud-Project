package cloudsim;

import org.apache.commons.math3.linear.*;
import java.util.Random;

public class AutoEncoder {
    private RealMatrix weights;
    private double learningRate = 0.001; // Reduced learning rate to prevent instability

    public AutoEncoder(int inputSize, int hiddenSize) {
        if (inputSize <= 0 || hiddenSize <= 0) {
            throw new IllegalArgumentException("[ERROR] Input size and hidden size must be positive.");
        }
        // Initialize weights with small random values to avoid instability
        this.weights = MatrixUtils.createRealMatrix(inputSize, hiddenSize);
        Random rand = new Random();
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                weights.setEntry(i, j, rand.nextDouble() * 0.01); // Small random values
            }
        }
    }

    public void train(RealMatrix data, int epochs) {
        if (data == null || data.getRowDimension() == 0 || data.getColumnDimension() == 0) {
            throw new IllegalArgumentException("[ERROR] Data matrix is empty or null.");
        }

        if (epochs <= 0) {
            throw new IllegalArgumentException("[ERROR] Number of epochs must be positive.");
        }

        for (int epoch = 0; epoch < epochs; epoch++) {
            // Encode and decode the data
            RealMatrix encoded = encode(data);
            RealMatrix reconstructed = decode(encoded);

            // Calculate the reconstruction error
            RealMatrix error = data.subtract(reconstructed);

            // Update weights using gradient descent
            RealMatrix gradient = data.transpose().multiply(error).scalarMultiply(-learningRate);
            weights = weights.add(gradient);

            // Clip gradients to prevent explosion
            clipGradients(weights, 1.0);

            System.out.println("[DEBUG] AutoEncoder Training Epoch " + epoch + " - Reconstruction Error: " + error.getNorm());
        }
    }

    private void clipGradients(RealMatrix matrix, double maxNorm) {
        double norm = matrix.getNorm();
        if (norm > maxNorm) {
            matrix = matrix.scalarMultiply(maxNorm / norm);
        }
    }

    public RealMatrix encode(RealMatrix data) {
        if (data == null || data.getRowDimension() == 0 || data.getColumnDimension() == 0) {
            throw new IllegalArgumentException("[ERROR] Data matrix is empty or null.");
        }

        if (data.getColumnDimension() != weights.getRowDimension()) {
            throw new IllegalArgumentException("[ERROR] Data matrix dimensions do not match weights matrix.");
        }

        return data.multiply(weights);
    }

    public RealMatrix decode(RealMatrix encodedData) {
        if (encodedData == null || encodedData.getRowDimension() == 0 || encodedData.getColumnDimension() == 0) {
            throw new IllegalArgumentException("[ERROR] Encoded data matrix is empty or null.");
        }

        if (encodedData.getColumnDimension() != weights.getColumnDimension()) {
            throw new IllegalArgumentException("[ERROR] Encoded data matrix dimensions do not match weights matrix.");
        }

        return encodedData.multiply(weights.transpose());
    }
}