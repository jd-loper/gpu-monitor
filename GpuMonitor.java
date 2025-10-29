import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GpuMonitor extends JFrame {
    private JTextArea textArea;
    private Timer updateTimer;
    private static final int UPDATE_INTERVAL = 1000;

    public GpuMonitor() {
        // Swing GUI setup
        setTitle("GPU Monitor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        displayArea = new JTextArea();
        displayArea = setEditable(false);
        displayArea = setFont(new Font("Monospaced", Font.PLAIN, 13));
        displayArea = setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);

        // Timer to fetch GPU stats every 1000ms in background
        updateTimer = new Timer(UPDATE_INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new GpuWorker().execute();
            }
        })
    }

    public void start() {
        setVisible(true);
        new GpuWorker().execute();
        updateTimer.start();
    }

    private class GpuWorker extends SwingWorker<String, Void> {
        protected String doInBackground() {
            // Run command for GPU stats
            String[] command = {
                    "nvidia-smi",
                    "--query-gpu=index,name,utilization.gpu,memory.used,memory.total,memory.free,temperature.gpu",
                    "--format=csv,noheader"
            }

            // A ProcessBuilder to run OS commands
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = null;
        }
    }
}