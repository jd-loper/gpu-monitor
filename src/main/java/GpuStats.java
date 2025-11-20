public class GpuStats {
    private final int index;
    private final String name;
    private final int utilization;
    private final int memoryUsed;
    private final int memoryTotal;
    private final int memoryFree;
    private final int temperature;
    private final int fanSpeed;

    public GpuStats(int index, String name, int utilization, int memoryUsed, int memoryTotal, int memoryFree, int temperature, int fanSpeed) {
        this.index = index;
        this.name = name;
        this.utilization = utilization;
        this.memoryUsed = memoryUsed;
        this.memoryTotal = memoryTotal;
        this.memoryFree = memoryFree;
        this.temperature = temperature;
        this.fanSpeed = fanSpeed;
    }

    public int getIndex() {
        return index;
    }
    public String getName() {
        return name;
    }
    public int getUtilization() {
        return utilization;
    }
    public int getMemoryUsed() {
        return memoryUsed;
    }
    public int getMemoryTotal() {
        return memoryTotal;
    }
    public int getMemoryFree() {
        return memoryFree;
    }
    public int getTemperature() {
        return temperature;
    }
    public int getFanSpeed() {
        return fanSpeed;
    }
}
