interface Switchable {
    void on(); void off();
    default void toggle() { System.out.println("Toggling device..."); off(); on(); }
}
class Fan implements Switchable {
    public void on() { System.out.println("Fan: ON"); }
    public void off() { System.out.println("Fan: OFF"); }
    public String toString() { return "Fan"; }
}
class Light implements Switchable {
    public void on() { System.out.println("Light: ON"); }
    public void off() { System.out.println("Light: OFF"); }
    public String toString() { return "Light"; }
}
@FunctionalInterface interface SwitchPolicy { boolean maySwitchOn(Switchable device, int hour); }
public class prac6_A_RemoteControl {
    public static void main(String[] args) {
        Switchable[] devices = {new Fan(), new Light()};
        System.out.println("--- Toggling Devices ---");
        for (Switchable d : devices) { System.out.println(d); d.toggle(); }
        System.out.println("\n--- Anonymous Class Policy ---");
        SwitchPolicy anonymous = new SwitchPolicy() {
            public boolean maySwitchOn(Switchable d, int hour) { return hour >= 6 && hour <= 22; }
        };
        System.out.println("Fan at 10:00 -> " + anonymous.maySwitchOn(devices[0], 10));
        System.out.println("Light at 23:00 -> " + anonymous.maySwitchOn(devices[1], 23));
        System.out.println("\n--- Lambda Policy ---");
        SwitchPolicy lambda = (d, hour) -> hour >= 7 && hour <= 23;
        System.out.println("Fan at 8:00 -> " + lambda.maySwitchOn(devices[0], 8));
        System.out.println("Light at 2:00 -> " + lambda.maySwitchOn(devices[1], 2));
    }
}
