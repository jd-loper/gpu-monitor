import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MonitorModel {
    public String fetchStats() throws IOException {
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
            return "Failed to run command: nvidia-smi. Please check to see if nvidia-smi is installed and in your PATH.";
        }

        StringBuilder output = new StringBuilder();

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
}
