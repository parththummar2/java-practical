import java.util.InputMismatchException;
import java.util.Scanner;
class DivideByZeroException extends Exception {
    DivideByZeroException(String message) { super(message); }
}
public class prac8_A_GuardedCalculator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean success = false; int attempt = 1;
        while (!success) {
            System.out.println("\n--- Calculation Attempt " + attempt + " ---");
            try {
                System.out.print("Enter first number: "); double a = sc.nextDouble();
                System.out.print("Enter operator (+, -, *, /): "); char op = sc.next().charAt(0);
                System.out.print("Enter second number: "); double b = sc.nextDouble();
                double result;
                switch (op) {
                    case '+': result = a + b; break;
                    case '-': result = a - b; break;
                    case '*': result = a * b; break;
                    case '/': if (b == 0) throw new DivideByZeroException("Cannot divide by zero."); result = a / b; break;
                    default: throw new IllegalArgumentException("Invalid operator.");
                }
                System.out.println("Result = " + result); success = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid number input. Please enter a valid number.");
                sc.nextLine();
            } catch (DivideByZeroException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                System.out.println("Attempt " + attempt + " completed.");
                attempt++;
            }
        }
        System.out.println("\nValid calculation completed successfully.");
        sc.close();
    }
}
