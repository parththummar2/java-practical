import java.util.ArrayList;
import java.util.List;
class BankAccountModel {
    private final String owner;
    private final int balance;
    BankAccountModel(String owner, int balance) {
        if (owner == null || owner.isBlank()) throw new IllegalArgumentException("Owner required");
        if (balance < 0) throw new IllegalArgumentException("Balance cannot be negative");
        this.owner = owner; this.balance = balance;
    }
    String getOwner() { return owner; }
    int getBalance() { return balance; }
}
class BankService {
    private final List<BankAccountModel> accounts = new ArrayList<>();
    void addAccount(BankAccountModel a) {
        if (a == null) throw new IllegalArgumentException("Account required");
        accounts.add(a);
    }
    int count() { return accounts.size(); }
    void performAction(String action) {
        if (!"LIST".equalsIgnoreCase(action) && !"COUNT".equalsIgnoreCase(action))
            throw new IllegalArgumentException("Invalid action: " + action);
        if (action.equalsIgnoreCase("COUNT")) System.out.println("Accounts: " + count());
        else for (BankAccountModel a : accounts) System.out.println(a.getOwner() + " | balance=" + a.getBalance());
    }
}
public class prac14_A_MiniBankApp {
    public static void main(String[] args) {
        BankService service = new BankService();
        service.addAccount(new BankAccountModel("Yug", 1000));
        service.addAccount(new BankAccountModel("Riya", 1500));
        service.performAction("LIST");
        service.performAction("COUNT");
    }
}
