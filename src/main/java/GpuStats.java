public class GpuStats {
    private int index;
    private String name;
    private int utilization;
    private int memoryUsed;
    private int memoryTotal;
    private int memoryFree;
    private int temperature;
    private int fanSpeed;

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
