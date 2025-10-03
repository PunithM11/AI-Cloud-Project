package cloudsim;

import org.apache.commons.math3.linear.*;
import java.util.Random;

public class AttackSimulator {
    private Random random = new Random();

    public RealMatrix generateAttack(int numSamples, int numFeatures, String attackType) {
        double[][] attackData = new double[numSamples][numFeatures];

        for (int i = 0; i < numSamples; i++) {
            for (int j = 0; j < numFeatures; j++) {
                switch (attackType) {
                    case "DDoS":
                        attackData[i][j] = 0.9 + (0.1 * random.nextDouble()); // Higher values for DDoS
                        break;
                    case "Brute Force":
                        attackData[i][j] = 0.2 + (0.1 * random.nextDouble()); // Lower values for Brute Force
                        break;
                    case "SQL Injection":
                        attackData[i][j] = 0.5 + (0.2 * random.nextDouble()); // Moderate values
                        break;
                    default:
                        attackData[i][j] = 0.7 + (0.3 * random.nextDouble()); // Normal traffic
                }
            }
        }

        return new Array2DRowRealMatrix(attackData);
    }
}
