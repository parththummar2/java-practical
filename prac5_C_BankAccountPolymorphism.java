import java.util.Scanner;
abstract class PolyAccount {
    String name; long accountNumber, balance;
    PolyAccount(String name, long accountNumber, long balance) { this.name = name; this.accountNumber = accountNumber; this.balance = balance; }
    abstract double interestRate();
    abstract boolean canWithdraw(long amount);
}
class SavingsPolyAccount extends PolyAccount {
    long minBalance;
    SavingsPolyAccount(String n, long no, long b, long min) { super(n, no, b); minBalance = min; }
    double interestRate() { return 4.0; }
    boolean canWithdraw(long amount) { return balance - amount >= minBalance; }
}
class CurrentPolyAccount extends PolyAccount {
    long overdraftLimit;
    CurrentPolyAccount(String n, long no, long b, long limit) { super(n, no, b); overdraftLimit = limit; }
    double interestRate() { return 0.0; }
    boolean canWithdraw(long amount) { return balance - amount >= -overdraftLimit; }
}
class FixedDepositPolyAccount extends PolyAccount {
    FixedDepositPolyAccount(String n, long no, long b) { super(n, no, b); }
    double interestRate() { return 7.0; }
    boolean canWithdraw(long amount) { return false; }
}
public class prac5_C_BankAccountPolymorphism {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of accounts: "); int n = sc.nextInt(); sc.nextLine();
        PolyAccount[] accounts = new PolyAccount[n];
        for (int i = 0; i < n; i++) {
            System.out.println("\nPolyAccount " + (i + 1) + "\n1. Savings PolyAccount\n2. Current PolyAccount\n3. Fixed Deposit PolyAccount");
            System.out.print("Enter account type: "); int choice = sc.nextInt(); sc.nextLine();
            System.out.print("Enter name: "); String name = sc.nextLine();
            System.out.print("Enter account number: "); long number = sc.nextLong();
            System.out.print("Enter balance: "); long balance = sc.nextLong();
            switch (choice) {
                case 1 -> { System.out.print("Enter minimum balance: "); accounts[i] = new SavingsPolyAccount(name, number, balance, sc.nextLong()); }
                case 2 -> { System.out.print("Enter overdraft limit: "); accounts[i] = new CurrentPolyAccount(name, number, balance, sc.nextLong()); }
                case 3 -> accounts[i] = new FixedDepositPolyAccount(name, number, balance);
                default -> { System.out.println("Invalid account type!"); i--; }
            }
        }
        System.out.println("\n----- ACCOUNT DETAILS -----");
        for (PolyAccount a : accounts) {
            System.out.println("Name: " + a.name + " | PolyAccount No: " + a.accountNumber + " | Balance: " + a.balance);
            System.out.println("Interest Rate: " + a.interestRate() + "%");
            if (a instanceof FixedDepositPolyAccount) System.out.println("Note: Fixed Deposit is locked.");
        }
        System.out.print("\nEnter amount to withdraw: "); long amount = sc.nextLong();
        System.out.println("\n----- WITHDRAWAL CHECK -----");
        for (PolyAccount a : accounts) System.out.println(a.name + " -> Can withdraw " + amount + "? " + a.canWithdraw(amount));
        sc.close();
    }
}
