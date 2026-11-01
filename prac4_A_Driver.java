import java.util.Scanner;
public class prac4_A_Driver {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        PasswordChecker pc = new PasswordChecker();
        System.out.print("Enter number of passwords: "); int n = sc.nextInt(); sc.nextLine();
        for (int i = 1; i <= n; i++) {
            System.out.print("Enter password: ");
            String password = sc.nextLine();
            System.out.println(pc.strength(password));
        }
        sc.close();
    }
}
