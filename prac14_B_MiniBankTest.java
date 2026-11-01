// JUnit 5 test source. Requires junit-jupiter-api on the project classpath.
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class prac14_B_MiniBankTest {
    @Test void addingAccountIncreasesCount() {
        BankService service = new BankService();
        assertEquals(0, service.count());
        service.addAccount(new BankAccountModel("Yug", 1000));
        assertEquals(1, service.count());
    }
    @Test void addingTwoAccountsIncreasesCountTwice() {
        BankService service = new BankService();
        service.addAccount(new BankAccountModel("Yug", 1000));
        service.addAccount(new BankAccountModel("Riya", 1500));
        assertEquals(2, service.count());
    }
    @Test void invalidActionThrows() {
        BankService service = new BankService();
        assertThrows(IllegalArgumentException.class, () -> service.performAction("DELETE"));
    }
}
