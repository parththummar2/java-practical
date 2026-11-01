import java.io.*;
class SerializedEmployee implements Serializable {
    private static final long serialVersionUID = 1L;
    int id; String name; double salary; transient String temporaryData;
    SerializedEmployee(int id, String name, double salary, String temporaryData) {
        this.id = id; this.name = name; this.salary = salary; this.temporaryData = temporaryData;
    }
    void display() {
        System.out.println("SerializedEmployee ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Salary: " + salary);
        System.out.println("Temporary field (transient): " + temporaryData);
    }
}
public class prac11_A_SaveLoad {
    public static void main(String[] args) {
        SerializedEmployee[] employees = {
            new SerializedEmployee(101, "Rahul", 45000.0, "Session-A"),
            new SerializedEmployee(102, "Priya", 52000.0, "Session-B")
        };
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("employees.dat"))) {
            out.writeObject(employees);
            System.out.println("Objects saved to employees.dat");
        } catch (IOException e) { System.out.println("Error saving objects: " + e.getMessage()); return; }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("employees.dat"))) {
            SerializedEmployee[] loaded = (SerializedEmployee[]) in.readObject();
            System.out.println("\nReading objects from file...");
            for (SerializedEmployee employee : loaded) employee.display();
            System.out.println("\nSerialization test completed.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading objects: " + e.getMessage());
        }
    }
}
