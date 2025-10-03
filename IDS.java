package cloudsim;

import org.apache.commons.math3.linear.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;  // Added this import

public class IDS {
    private PCA pca;
    private AutoEncoder autoEncoder;
    private FuzzyCMeans fcm;
    private RealMatrix trainingData;
    private double intrusionThreshold = 1.5;
    private JTextArea outputArea;

    public IDS(RealMatrix trainingData, JTextArea outputArea) {
        if (trainingData == null || trainingData.getRowDimension() == 0 || trainingData.getColumnDimension() == 0) {
            throw new IllegalArgumentException("[ERROR] Training data matrix is empty or null.");
        }
        this.trainingData = normalizeData(trainingData);
        this.pca = new PCA();
        this.fcm = new FuzzyCMeans(4, 7);
        this.autoEncoder = new AutoEncoder(7, 7);
        this.outputArea = outputArea;
    }

    public void trainModel() {
        appendToOutput("[INFO] Training IDS Model...\n");
        pca.trainPCA(trainingData, 7);
        RealMatrix reducedData = pca.reduceDimensions(trainingData);
        fcm.train(reducedData, 10);
        appendToOutput("[INFO] Fuzzy C-Means clustering completed.\n");
        autoEncoder.train(reducedData, 10);
        appendToOutput("[INFO] IDS Model Training Completed!\n");
    }

    public String detectIntrusion(RealMatrix trafficData) {
        if (trafficData == null || trafficData.getRowDimension() == 0) {
            return "[ERROR] No traffic data received.";
        }

        RealMatrix normalizedTrafficData = normalizeData(trafficData);
        RealMatrix reducedData = pca.reduceDimensions(normalizedTrafficData);
        RealMatrix encoded = autoEncoder.encode(reducedData);
        RealMatrix reconstructed = autoEncoder.decode(encoded);
        RealMatrix error = reducedData.subtract(reconstructed);
        double reconstructionError = error.getNorm();
        appendToOutput("[DEBUG] Reconstruction Error: " + reconstructionError + "\n");

        if (reconstructionError <= intrusionThreshold) {
            return "[INFO] Normal Traffic";
        }

        String attackType = classifyAttackType(trafficData);
        if (!sendEmailAlert(attackType)) {
            appendToOutput("[WARNING] Email alert failed - attack still detected: " + attackType + "\n");
        }
        return attackType;
    }

    private String classifyAttackType(RealMatrix trafficData) {
        double[] input = trafficData.getRow(0);
        if (input.length >= 7) {
            if (input[0] >= 1000 && input[1] >= 100 && input[2] >= 0 && input[3] >= 0 && input[4] >= 0 && input[5] >= 0 && input[6] >= 0) {
                return "[ALERT] DDoS Attack Detected!";
            }
            if (input[0] >= 500 && input[1] >= 50 && input[2] >= 0 && input[3] >= 0 && input[4] >= 0 && input[5] >= 0 && input[6] >= 0) {
                return "[ALERT] DoS Attack Detected!";
            }
            if (input[0] >= 0 && input[1] >= 0 && input[2] >= 100 && input[3] >= 0 && input[4] >= 0 && input[5] >= 0 && input[6] >= 0) {
                return "[ALERT] SQL Injection Attack Detected!";
            }
            if (input[0] >= 0 && input[1] >= 0 && input[2] >= 0 && input[3] >= 50 && input[4] >= 100 && input[5] >= 0 && input[6] >= 0) {
                return "[ALERT] Brute Force Attack Detected!";
            }
        }
        return "[ALERT] Possible Intrusion Detected!";
    }

    private RealMatrix normalizeData(RealMatrix data) {
        RealMatrix normalizedData = data.copy();
        for (int i = 0; i < data.getColumnDimension(); i++) {
            double max = new ArrayRealVector(data.getColumn(i)).getMaxValue();
            double min = new ArrayRealVector(data.getColumn(i)).getMinValue();
            if (max != min) {
                for (int j = 0; j < data.getRowDimension(); j++) {
                    normalizedData.setEntry(j, i, (data.getEntry(j, i) - min) / (max - min));
                }
            }
        }
        return normalizedData;
    }

    private boolean sendEmailAlert(String attackType) {
        final String username = "punithm11122001@gmail.com";
        final String password = "orjw zbya elgk tksm";
        final String recipient = "punithmusic11@gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        try {
            Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("SECURITY ALERT: " + attackType);
            message.setText("Attack detected at " + new java.util.Date() + "\n\n" +
                          "Type: " + attackType + "\n" +
                          "Action Required: Immediate investigation needed");

            Transport.send(message);
            appendToOutput("[EMAIL] Alert sent successfully\n");
            return true;
        } catch (MessagingException e) {
            appendToOutput("[EMAIL ERROR] " + e.getMessage() + "\n");
            return false;
        }
    }

    private void appendToOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text);
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }
}