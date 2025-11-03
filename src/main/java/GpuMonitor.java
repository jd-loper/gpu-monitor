import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

public class GpuMonitor extends JFrame {
    private final JTextArea textArea;
    private final Timer updateTimer;
    private static final int UPDATE_INTERVAL = 1000;
    private JButton pauseButton;

    public GpuMonitor() {
        // Swing GUI setup
        setTitle("GPU Monitor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setBackground(new Color(40, 42, 54));
        textArea.setForeground(new Color(80, 250, 123));
        textArea.setCaretColor(new Color(40, 42, 54));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        JLabel label = new JLabel("NVIDIA GPU Monitor", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(label, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton copyButton = new JButton("Copy to Clipboard");
        buttonPanel.add(copyButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // ActionListener for GPU stats copy button
        copyButton.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection gpuStats = new StringSelection(textArea.getText());
            clipboard.setContents(gpuStats, null);
        });

        // Timer to fetch GPU stats every 1000 ms in background
        updateTimer = new Timer(UPDATE_INTERVAL, e -> new GpuWorker().execute());

        JButton pauseButton = new JButton("Pause");
        buttonPanel.add(pauseButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pauseButton.addActionListener(e -> {
            if (updateTimer.isRunning()) {
                updateTimer.stop();
                pauseButton.setText("Resume");
            } else {
                updateTimer.start();
                pauseButton.setText("Pause");
            }
        });
    }

    public void start() {
        setVisible(true);
        new GpuWorker().execute();
        updateTimer.start();
    }

    private class GpuWorker extends SwingWorker<String, Void> {
        protected String doInBackground() throws IOException {
            // Run command for GPU stats
            String[] command = {
                    "nvidia-smi",
                    "--query-gpu=index,name,utilization.gpu,memory.used,memory.total,memory.free,temperature.gpu,fan.speed",
                    "--format=csv,noheader"
            };

            // A ProcessBuilder to run OS commands
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process;

            try {
                process = processBuilder.start();
            } catch (IOException e) {
                return "Failed to run command: nvidia-smi. Please check to see if nvidia-smi is installed and in your PATH.";
            }

            StringBuilder output = new StringBuilder();

            // Split and format CSV output to readable string
            try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    String[] stats = line.split(", ");

                    if (stats.length >= 8) {
                        String index = stats[0];
                        String name = stats[1];
                        String utilization = stats[2];
                        String memoryUsed = stats[3];
                        String memoryTotal = stats[4];
                        String memoryFree = stats[5];
                        String temperature = stats[6];
                        String fanSpeed = stats[7];

                        output.append("GPU ").append(index).append("\n");
                        output.append(name).append("\n");
                        output.append("Utilization: ").append(utilization).append("\n");
                        output.append("Memory Used/Total: ").append(memoryUsed).append(" / ").append(memoryTotal).append("\n");
                        output.append("Memory Free: ").append(memoryFree).append("\n");
                        output.append("Temperature: ").append(temperature).append("C").append("\n");
                        output.append("Fan Speed: ").append(fanSpeed).append("\n");
                        output.append("----------------------------------------\n");
                    }
                }

                return output.toString();
            }
        }

        protected void done() {
            String stats;
            try {
                stats = get();
                textArea.setText(stats);
            } catch (InterruptedException | ExecutionException e) {
                textArea.setText("Error: " + e.getCause().getMessage());
            }
            textArea.setCaretPosition(0);
        }
    }

    public static void main(String[] args) {
        // This method updates the GUI asynchronously
        SwingUtilities.invokeLater(() -> new GpuMonitor().start());
    }
}
