package cloudsim;

import org.apache.commons.math3.linear.*;
import java.util.Random;

public class TrafficGenerator {
    private Random rand = new Random();

    // Generate normal traffic data
    public RealMatrix generateNormalTraffic(int numSamples, int numFeatures) {
        RealMatrix data = MatrixUtils.createRealMatrix(numSamples, numFeatures);
        for (int i = 0; i < numSamples; i++) {
            for (int j = 0; j < numFeatures; j++) {
                data.setEntry(i, j, rand.nextDouble() * 10); // Normal traffic values between 0 and 10
            }
        }
        return data;
    }

    // Generate anomalous traffic data
    public RealMatrix generateAnomalousTraffic(int numSamples, int numFeatures) {
        RealMatrix data = MatrixUtils.createRealMatrix(numSamples, numFeatures);
        for (int i = 0; i < numSamples; i++) {
            for (int j = 0; j < numFeatures; j++) {
                data.setEntry(i, j, rand.nextDouble() * 100); // Anomalous traffic values between 0 and 100
            }
        }
        return data;
    }
}