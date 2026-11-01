import java.util.Scanner;
abstract class PayrollEmployee {
    String name; int id;
    PayrollEmployee(String name, int id) { this.name = name; this.id = id; }
    abstract double monthlySalary();
}
class FullTime extends PayrollEmployee {
    double fixedSalary;
    FullTime(String n, int id, double salary) { super(n, id); fixedSalary = salary; }
    double monthlySalary() { return fixedSalary; }
}
class PartTime extends PayrollEmployee {
    double hours, rate;
    PartTime(String n, int id, double hours, double rate) { super(n, id); this.hours = hours; this.rate = rate; }
    double monthlySalary() { return hours * rate; }
}
class Intern extends PayrollEmployee {
    double stipend;
    Intern(String n, int id, double stipend) { super(n, id); this.stipend = stipend; }
    double monthlySalary() { return stipend; }
}
public class prac5_B_Payroll {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of employees: "); int n = sc.nextInt(); sc.nextLine();
        PayrollEmployee[] employees = new PayrollEmployee[n];
        for (int i = 0; i < n; i++) {
            System.out.println("\nPayrollEmployee " + (i + 1) + "\n1. FullTime\n2. PartTime\n3. Intern");
            System.out.print("Enter choice: "); int choice = sc.nextInt(); sc.nextLine();
            System.out.print("Enter name: "); String name = sc.nextLine();
            System.out.print("Enter ID: "); int id = sc.nextInt();
            switch (choice) {
                case 1 -> { System.out.print("Enter fixed monthly salary: "); employees[i] = new FullTime(name, id, sc.nextDouble()); }
                case 2 -> { System.out.print("Enter working hours: "); double h = sc.nextDouble(); System.out.print("Enter hourly rate: "); employees[i] = new PartTime(name, id, h, sc.nextDouble()); }
                case 3 -> { System.out.print("Enter stipend: "); employees[i] = new Intern(name, id, sc.nextDouble()); }
                default -> { System.out.println("Invalid choice!"); i--; }
            }
        }
        double total = 0;
        System.out.println("\n----- PAYROLL -----");
        for (PayrollEmployee e : employees) {
            double salary = e.monthlySalary();
            System.out.printf("Name: %s | ID: %d | Salary: %.2f", e.name, e.id, salary);
            if (e instanceof Intern) System.out.print(" | Note: Intern");
            System.out.println(); total += salary;
        }
        System.out.printf("Total Payroll: %.2f%n", total); sc.close();
    }
}
