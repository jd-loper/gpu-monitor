import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MonitorController {
    private MonitorModel model;
    private MonitorView view;
    private final Timer updateTimer;
    private static final int UPDATE_INTERVAL = 1000;

    public MonitorController(MonitorModel model, MonitorView view) {
        this.model = model;
        this.view = view;

        this.view.addCopyListener(e -> copyToClipboard());
        this.view.addPauseListener(e -> togglePause());

        // Timer to fetch GPU stats every 1000 ms in background
        this.updateTimer = new Timer(UPDATE_INTERVAL, e -> new GpuWorker().execute());
    }

    private void togglePause() {
        model.pauseState();

        if (model.isPaused()) {
            updateTimer.stop();
            view.setPauseButtonText("Resume");
        } else {
            updateTimer.start();
            view.setPauseButtonText("Pause");
        }
    }

    private void copyToClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection gpuStats = new StringSelection(view.getStatsText());
        clipboard.setContents(gpuStats, null);
    }

    private class GpuWorker extends SwingWorker<String, Void> {
        protected String doInBackground() throws IOException {
            return model.fetchStats();
        }

        protected void done() {
            try {
                String stats = get();
                model.setCurrentStats(stats);
                view.setStatsText(stats);
            } catch (InterruptedException | ExecutionException e) {
                view.setStatsText("Error: " + e.getCause().getMessage());
            }
        }
    }

    public void start() {
        view.setVisible(true);
        new GpuWorker().execute();
        updateTimer.start();
    }

    public static void main(String[] args) {
        // This method updates the GUI asynchronously
        SwingUtilities.invokeLater(() -> {
            MonitorModel model = new MonitorModel();
            MonitorView view = new MonitorView();
            MonitorController controller = new MonitorController(model, view);
            controller.start();
        });
    }
}