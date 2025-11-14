import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MonitorModel {
    private boolean isPaused = false;
    private List<GpuStats> currentStats = new ArrayList<>();

    public List<GpuStats> fetchStats() throws IOException {
        String[] command = {
                "nvidia-smi",
                "--query-gpu=index,name,utilization.gpu,memory.used,memory.total,memory.free,temperature.gpu,fan.speed",
                "--format=csv,noheader"
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process;

        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new IOException("Failed to run command: nvidia-smi. Please check to see if nvidia-smi is installed and in your PATH.");
        }

        // A list to store stats for each GPU
        List<GpuStats> statsList = new ArrayList<>();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = input.readLine()) != null) {
                String[] stats = line.split(", ");

                if (stats.length >= 8) {
                    int index = parseStats(stats[0]);
                    String name = stats[1];
                    int utilization = parseStats(stats[2]);
                    int memoryUsed = parseStats(stats[3]);
                    int memoryTotal = parseStats(stats[4]);
                    int memoryFree = parseStats(stats[5]);
                    int temperature = parseStats(stats[6]);
                    int fanSpeed = parseStats(stats[7]);

                    statsList.add(new GpuStats(index, name, utilization, memoryUsed, memoryTotal, memoryFree, temperature, fanSpeed));
                }
            }
        }
        return statsList;
    }

    // Parses a string to an integer, returning 0 if the string is not a number
    private int parseStats(String value) {
        try {
            String[] parts = value.split("\\D", 2);
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public List<GpuStats> getGpuStats() {
        return currentStats;
    }

    public void setGpuStats(List<GpuStats> stats) {
        this.currentStats = stats;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void togglePauseState() {
        this.isPaused = !this.isPaused();
    }
}
