import javax.swing.*;
import java.awt.*;
import javax.swing.Timer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

public class GpuMonitor extends JFrame {
    private final JTextArea textArea;
    private final Timer updateTimer;
    private static final int UPDATE_INTERVAL = 1000;

    public GpuMonitor() {
        // Swing GUI setup
        setTitle("GPU Monitor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // Timer to fetch GPU stats every 1000 ms in background
        updateTimer = new Timer(UPDATE_INTERVAL, e -> new GpuWorker().execute());
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
                    "--query-gpu=index,name,utilization.gpu,memory.used,memory.total,memory.free,temperature.gpu",
                    "--format=csv,noheader"
            };

            // A ProcessBuilder to run OS commands
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process;

            try {
                process = processBuilder.start();
            } catch (IOException e) {
                return "Failed to run command: nvidia-smi";
            }

            StringBuilder output = new StringBuilder();

            try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    String[] stats = line.split(", ");

                    if (stats.length == 7) {
                        String index = stats[0];
                        String name = stats[1];
                        String utilization = stats[2];
                        String memoryUsed = stats[3];
                        String memoryTotal = stats[4];
                        String memoryFree = stats[5];
                        String temperature = stats[6];

                        output.append("GPU ").append(index).append("\n");
                        output.append(name).append("\n");
                        output.append("Utilization: ").append(utilization).append("\n");
                        output.append("Memory Used/Total: ").append(memoryUsed).append(" / ").append(memoryTotal).append("\n");
                        output.append("Memory Free: ").append(memoryFree).append("\n");
                        output.append("Temperature: ").append(temperature).append("C").append("\n\n");
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
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GpuMonitor().start());
    }
}
