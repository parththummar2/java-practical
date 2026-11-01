public class prac2_A_Thermostat {
    private String location;
    private int temperature;
    private static final int MIN = 16, MAX = 30;
    private static int activeCount = 0;

    prac2_A_Thermostat(String location, int startTemp) {
        this.location = location;
        temperature = (startTemp >= MIN && startTemp <= MAX) ? startTemp : 22;
        activeCount++;
    }
    prac2_A_Thermostat(String location) { this(location, 22); }
    void raise() {
        if (temperature < MAX) temperature++;
        else System.out.println("Already at maximum(30)");
    }
    void lower() {
        if (temperature > MIN) temperature--;
        else System.out.println("Already at minimum(16)");
    }
    int getTemperature() { return temperature; }
    static int getActiveCount() { return activeCount; }

    public static void main(String[] args) {
        prac2_A_Thermostat t1 = new prac2_A_Thermostat("Room", 20);
        prac2_A_Thermostat t2 = new prac2_A_Thermostat("Bedroom");
        for (int i = 0; i < 10; i++) {
            t1.raise();
            System.out.println("Temperature: " + t1.getTemperature());
        }
        for (int i = 0; i < 20; i++) {
            t2.lower();
            System.out.println("Temperature: " + t2.getTemperature());
        }
        System.out.println("Active thermostat: " + getActiveCount());
    }
}
