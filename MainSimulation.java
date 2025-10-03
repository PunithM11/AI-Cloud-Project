package cloudsim;

import org.apache.commons.math3.linear.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainSimulation {
    private static JFrame mainFrame;
    private static JTextArea outputArea;
    private static IDS ids;
    private static TrafficGenerator trafficGenerator;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createMainUI();
            
            // Initialize components
            trafficGenerator = new TrafficGenerator();
            RealMatrix trainingData = trafficGenerator.generateNormalTraffic(100, 7);
            ids = new IDS(trainingData, outputArea);
            
            // Training phase
            outputArea.append("[INFO] Training model...\n");
            
            // Simulate training process
            new Thread(() -> {
                ids.trainModel();
                SwingUtilities.invokeLater(() -> {
                    showMainMenu();
                });
            }).start();
        });
    }

    private static void createMainUI() {
        mainFrame = new JFrame("Intrusion Detection System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Intrusion Detection System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("System Output"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Initial status panel
        JPanel statusPanel = new JPanel();
        statusPanel.add(new JLabel("Initializing system..."));
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        mainFrame.setContentPane(mainPanel);
        mainFrame.setVisible(true);
    }

    private static void showMainMenu() {
        JPanel menuPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JButton randomButton = createMenuButton("Test with Random Traffic");
        JButton customButton = createMenuButton("Test with Custom Values");
        JButton exitButton = createMenuButton("Exit Simulation");

        randomButton.addActionListener(e -> testRandomTraffic());
        customButton.addActionListener(e -> testCustomValues());
        exitButton.addActionListener(e -> exitSimulation());

        menuPanel.add(randomButton);
        menuPanel.add(customButton);
        menuPanel.add(exitButton);

        // Clear and update the main panel
        JPanel mainPanel = (JPanel) mainFrame.getContentPane();
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        
        // Re-add title
        JLabel titleLabel = new JLabel("Intrusion Detection System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Add output area
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("System Output"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add menu panel
        mainPanel.add(menuPanel, BorderLayout.SOUTH);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setPreferredSize(new Dimension(300, 60));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        return button;
    }

    private static void testRandomTraffic() {
        outputArea.append("\n[ACTION] Testing with random traffic...\n");
        RealMatrix testData = trafficGenerator.generateNormalTraffic(1, 7);
        double[] values = testData.getRow(0);
        
        StringBuilder inputValues = new StringBuilder("Input Values: [");
        for (int i = 0; i < values.length; i++) {
            inputValues.append(String.format("%.2f", values[i]));
            if (i < values.length - 1) inputValues.append(", ");
        }
        inputValues.append("]\n");
        outputArea.append(inputValues.toString());

        String result = ids.detectIntrusion(testData);
        outputArea.append(result + "\n");
        
        // Auto-scroll to bottom
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private static void testCustomValues() {
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField[] inputFields = new JTextField[7];
        for (int i = 0; i < 7; i++) {
            JLabel label = new JLabel("Feature " + (i + 1) + ":");
            label.setFont(new Font("Arial", Font.PLAIN, 16));
            inputPanel.add(label);
            
            inputFields[i] = new JTextField("0");
            inputFields[i].setFont(new Font("Arial", Font.PLAIN, 16));
            inputPanel.add(inputFields[i]);
        }

        JButton testButton = new JButton("Detect Threat");
        testButton.setFont(new Font("Arial", Font.BOLD, 16));
        testButton.setBackground(new Color(34, 139, 34));
        testButton.setForeground(Color.WHITE);
        
        JButton backButton = new JButton("Back to Menu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> showMainMenu());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(testButton);
        buttonPanel.add(backButton);

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(inputPanel, BorderLayout.CENTER);
        containerPanel.add(buttonPanel, BorderLayout.SOUTH);

        testButton.addActionListener(e -> {
            try {
                double[] values = new double[7];
                for (int i = 0; i < 7; i++) {
                    values[i] = Double.parseDouble(inputFields[i].getText());
                }
                
                outputArea.append("\n[ACTION] Testing with custom values...\n");
                StringBuilder inputValues = new StringBuilder("Input Values: [");
                for (int i = 0; i < values.length; i++) {
                    inputValues.append(String.format("%.2f", values[i]));
                    if (i < values.length - 1) inputValues.append(", ");
                }
                inputValues.append("]\n");
                outputArea.append(inputValues.toString());

                RealMatrix testData = new Array2DRowRealMatrix(new double[][]{values});
                String result = ids.detectIntrusion(testData);
                outputArea.append(result + "\n");
                
                // Auto-scroll to bottom
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Please enter valid numbers in all fields", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Update the main panel
        JPanel mainPanel = (JPanel) mainFrame.getContentPane();
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        
        // Re-add title
        JLabel titleLabel = new JLabel("Intrusion Detection System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Add output area
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("System Output"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add input form
        mainPanel.add(containerPanel, BorderLayout.SOUTH);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static void exitSimulation() {
        outputArea.append("\n[INFO] Simulation ended. Closing application...\n");
        Timer timer = new Timer(1500, e -> {
            mainFrame.dispose();
            System.exit(0);
        });
        timer.setRepeats(false);
        timer.start();
    }
}