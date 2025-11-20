import java.util.List;

public class StatsFormatter {

    public static String format(List<GpuStats> statsList) {
        StringBuilder output = new StringBuilder();

        if (statsList.isEmpty()) {
            return "No data available.";
        }

        for (GpuStats stats : statsList) {
            output.append("GPU ").append(stats.getIndex()).append("\n");
            output.append(stats.getName()).append("\n");
            output.append("Utilization: ").append(stats.getUtilization()).append("%\n");
            output.append("Memory Used: ").append(stats.getMemoryUsed()).append(" MB\n");
            output.append("Memory Total: ").append(stats.getMemoryTotal()).append(" MB\n");
            output.append("Memory Free: ").append(stats.getMemoryFree()).append(" MB\n");
            output.append("Temperature: ").append(stats.getTemperature()).append(" C\n");
            output.append("Fan Speed: ").append(stats.getFanSpeed()).append("%\n");
        }
        return output.toString();
    }
}
