import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

public class GpuMonitor extends JFrame {
    private JTextArea textArea;
    private final Timer updateTimer;
    private static final int UPDATE_INTERVAL = 1000;

    public GpuMonitor() {
        // Swing GUI setup
        setTitle("GPU Monitor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea = setEditable(false);
        textArea = setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea = setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // Timer to fetch GPU stats every 1000 ms in background
        updateTimer = new Timer(UPDATE_INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new GpuWorker().execute();
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
                    String[] stats = line.split(",");

                    if (stats.length >= 5) {
                        String name = stats[0];
                        String utilization = stats[1];
                        String memoryUsed = stats[2];
                        String memoryTotal = stats[3];
                        String memoryFree = stats[4];
                        String temperature = stats[5];

                        output.append(name).append("\n");
                        output.append(memoryUsed).append(" / ").append(memoryTotal).append("\n");
                        output.append(utilization).append("\n");
                        output.append(temperature).append("\n\n");
                        output.append(memoryFree).append("\n");
                    }
                }

                return output.toString();
            }
        }

        protected void done() {
            String stats = null;
            try {
                stats = get();
                textArea.setText(stats);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GpuMonitor().start();
            }
        });
    }
}
