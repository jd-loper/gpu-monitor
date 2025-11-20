import com.formdev.flatlaf.FlatDarculaLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.List;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

public class MonitorController {
    private final MonitorModel model;
    private final MonitorView view;
    private XYChart tempChart;
    private final List<Double> timeData = new ArrayList<>();
    private final List<Double> tempData = new ArrayList<>();
    private double chartCounter = 0;
    private final Timer updateTimer;
    private static final int UPDATE_INTERVAL = 1000;

    public MonitorController(MonitorModel model, MonitorView view) {
        this.model = model;
        this.view = view;

        this.view.addCopyListener(e -> copyToClipboard());
        this.view.addPauseListener(e -> togglePause());

        // Timer to fetch GPU stats every 1000 ms in background
        this.updateTimer = new Timer(UPDATE_INTERVAL, e -> new GpuWorker().execute());

        // Initialize and pass chart to view
        initTempChart();
        XChartPanel<XYChart> tempChartPanel = new XChartPanel<>(tempChart);
        view.setChartPanel(tempChartPanel);
    }

    private void initTempChart() {
        tempChart = new XYChartBuilder()
                .width(450)
                .height(400)
                .title("GPU Temperature")
                .xAxisTitle("Time (s)")
                .yAxisTitle("Temperature (°C)")
                .build();

        tempChart.getStyler().setLegendVisible(false);
        tempChart.getStyler().setYAxisMin(30.0);
        tempChart.getStyler().setYAxisMax(100.0);
        tempChart.getStyler().setPlotGridLinesVisible(true);
        tempChart.getStyler().setChartBackgroundColor(new Color(40, 42, 54));
        tempChart.getStyler().setPlotBackgroundColor(new Color(40, 42, 54));
        tempChart.getStyler().setChartFontColor(new Color(248, 248, 242));
        tempChart.getStyler().setAxisTickLabelsColor(new Color(248, 248, 242));
        tempChart.getStyler().setSeriesColors(new Color[] {new Color(80, 250, 123)});
        tempChart.getStyler().setPlotGridLinesColor(new Color(99, 99, 99));

        timeData.add(0.0);
        tempData.add(40.0);
        tempChart.addSeries("Temperature", timeData, tempData);
    }

    private void togglePause() {
        model.togglePauseState();

        if (model.isPaused()) {
            updateTimer.stop();
            view.setPauseButtonText("Resume");
        } else {
            updateTimer.start();
            view.setPauseButtonText("Pause");
        }
    }

    private void copyToClipboard() {
        List<GpuStats> statsList = model.getGpuStats();

        String clipboardText = StatsFormatter.format(statsList);

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection gpuStats = new StringSelection(clipboardText);
        clipboard.setContents(gpuStats, null);
    }

    private class GpuWorker extends SwingWorker<List<GpuStats>, Void> {
        protected List<GpuStats> doInBackground() throws IOException {
            return model.fetchStats();
        }

        protected void done() {
            try {
                List<GpuStats> statsList = get();
                model.setGpuStats(statsList);

                String formattedStats = StatsFormatter.format(statsList);
                view.setStatsText(formattedStats);

                // Repaints temperature chart with real-time data
                if (!statsList.isEmpty() && !model.isPaused()) {
                    int currentTemp = statsList.get(0).getTemperature();

                    chartCounter += UPDATE_INTERVAL / 1000.0;
                    timeData.add(chartCounter);
                    tempData.add((double) currentTemp);

                    tempChart.setTitle(statsList.get(0).getName());
                    tempChart.updateXYSeries("Temperature", timeData, tempData, null);
                    view.repaintChart();
                }
            } catch (InterruptedException | ExecutionException e) {
                String error = "Failed to fetch GPU stats: " + e.getCause().getMessage();
                view.setStatsText(error);
            }
        }
    }

    public void start() {
        view.setVisible(true);
        new GpuWorker().execute();
        updateTimer.start();
    }

    public static void main(String[] args) {
        FlatDarculaLaf.setup();

        // This method updates the GUI asynchronously
        SwingUtilities.invokeLater(() -> {
            MonitorModel model = new MonitorModel();
            MonitorView view = new MonitorView();
            MonitorController controller = new MonitorController(model, view);
            controller.start();
        });
    }
}