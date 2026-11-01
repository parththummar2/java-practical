import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import static java.lang.Math.max;

@Target (ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface Id {}

@Target (ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface Positive {
    String message() default "balance must be greater than 0";
}

@Target (ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface MaxLength {
    int value();
}

class BankException extends Exception {
    public BankException(String msg) {
        super(msg);
    }
}

class InsufficientFundsException extends BankException {
    private final long deficit;

    public InsufficientFundsException(String msg, long deficit) {
        super(msg);
        this.deficit = deficit;
    }

    public long getDeficit() {
        return deficit;
    }
}

class AccountNotFoundException extends BankException {
    public AccountNotFoundException(String msg) {
        super(msg);
    }
}

class InvalidAmountException extends BankException {
    public InvalidAmountException(String msg) {
        super(msg);
    }
}

class BankAuditSession implements AutoCloseable {
    private final String sessId;

    public BankAuditSession(String sessId) {
        this.sessId = sessId;
        System.out.println("[AUDIT] Session started: " + sessId);
    }

    @Override
    public void close() {
        System.out.println("[AUDIT] Session ended " + sessId);
    }
}

class AccountWorker implements Runnable {
    private final Account tgt;
    private final int iters;
    private final long val;

    public AccountWorker(Account tgt, int iters, long val) {
        this.tgt = tgt;
        this.iters = iters;
        this.val = val;
    }

    @Override
    public void run() {
        try {
            for (int j = 0; j < iters; j++) {
                tgt.deposit(val);
            }
        } catch (InvalidAmountException ignored) {
        }
    }
}

class TransactionProcessor {
    private final ExecutorService executor;
    private final ConcurrentHashMap<String, Account> accMap = new ConcurrentHashMap<>();

    public TransactionProcessor(int size) {
        this.executor = Executors.newFixedThreadPool(size);
    }

    public void registerAccount(Account acc) {
        accMap.put(acc.getAccNum(), acc);
    }

    public Account getAccount(String accNo) {
        return accMap.get(accNo);
    }

    public void submit(Runnable task) {
        executor.execute(task);
    }

    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

class TransactionBuffer {
    private final Queue<String> txQueue = new LinkedList<>();
    private final int maxCap;

    public TransactionBuffer(int maxCap) {
        this.maxCap = maxCap;
    }

    public synchronized void produce(String transaction) throws InterruptedException {
        while (txQueue.size() >= maxCap) {
            wait();
        }
        txQueue.add(transaction);
        System.out.println("[queue] generated -> " + transaction);
        notifyAll();
    }

    public synchronized String consume() throws InterruptedException {
        while (txQueue.isEmpty()) {
            wait();
        }
        String transaction = txQueue.poll();
        System.out.println("[queue] processed <- " + transaction);
        notifyAll();
        return transaction;
    }
}

class StatePersister {
    public static void save(Account[] accs, Path filePath) {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            try (ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
                outStream.writeObject(accs);
            }
        } catch (Exception ex) {
            System.out.println("Save state error: " + ex.getMessage());
        }
    }

    public static Account[] load(Path filePath) {
        if (!Files.exists(filePath)) {
            return new Account[0];
        }
        try (ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            return (Account[]) inStream.readObject();
        } catch (Exception ex) {
            System.out.println("Load state error: " + ex.getMessage());
            return new Account[0];
        }
    }
}

class TransactionLog {
    public static void append(Path filePath, String record) {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            byte[] bytes = (record + System.lineSeparator()).getBytes(StandardCharsets.UTF_8);
            Files.write(filePath, bytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ex) {
            System.out.println("Log append error: " + ex.getMessage());
        }
    }
}

class ReportGenerator {
    public static void generateReport(Path dir, Path outPath) {
        long sumDeposits = 0;
        long sumWithdrawals = 0;
        int countRead = 0;

        try {
            if (outPath.getParent() != null) {
                Files.createDirectories(outPath.getParent());
            }

            if (Files.exists(dir)) {
                try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
                    for (Path p : ds) {
                        if (Files.isDirectory(p)) {
                            continue;
                        }
                        try (BufferedReader br = Files.newBufferedReader(p)) {
                            String txt;
                            while ((txt = br.readLine()) != null) {
                                String[] chunks = txt.trim().split("\\s+");
                                if (chunks.length < 3) {
                                    continue;
                                }
                                try {
                                    long val = Long.parseLong(chunks[2]);
                                    if (chunks[0].equalsIgnoreCase("DEPOSIT")) {
                                        sumDeposits += val;
                                        countRead++;
                                    } else if (chunks[0].equalsIgnoreCase("WITHDRAW")) {
                                        sumWithdrawals += val;
                                        countRead++;
                                    }
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                    }
                }
            }

            StringBuilder bldr = new StringBuilder();
            bldr.append("========================\n");
            bldr.append("    DAILY SUMMARY       \n");
            bldr.append("========================\n");
            bldr.append("Processed Records : ").append(countRead).append("\n");
            bldr.append("Total Inwards     : Rs. ").append(sumDeposits).append("\n");
            bldr.append("Total Outwards    : Rs. ").append(sumWithdrawals).append("\n");
            bldr.append("Net Cashflow      : Rs. ").append(sumDeposits - sumWithdrawals).append("\n");
            bldr.append("========================\n");

            Files.write(outPath, bldr.toString().getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.print(bldr);

        } catch (Exception ex) {
            System.out.println("Report gen error: " + ex.getMessage());
        }
    }
}

class AccountStore {
    private final Map<String, Account> accDict = new HashMap<>();
    private final List<String> txHistory = new ArrayList<>();

    public void add(Account acc) {
        if (acc != null) {
            accDict.put(acc.getAccNum(), acc);
            txHistory.add("Account added: " + acc.getAccNum());
        }
    }

    public Account get(String accNum) {
        return accDict.get(accNum);
    }

    public List<Account> getAll() {
        return new ArrayList<>(accDict.values());
    }

    public void logTransaction(String record) {
        txHistory.add(record);
    }

    public List<String> getHistory() {
        return new ArrayList<>(txHistory);
    }

    public List<Account> sortedByBalance() {
        List<Account> res = new ArrayList<>(accDict.values());
        res.sort((x, y) -> Long.compare(y.getCurrBalance(), x.getCurrBalance()));
        return res;
    }
}

class Repository<T> {
    private final HashMap<String, T> store = new HashMap<>();

    private String extractId(T obj) {
        Class<?> cls = obj.getClass();
        while (cls != null && cls != Object.class) {
            for (Field f : cls.getDeclaredFields()) {
                if (f.isAnnotationPresent(Id.class)) {
                    f.setAccessible(true);
                    try {
                        Object v = f.get(obj);
                        return v != null ? v.toString() : null;
                    } catch (IllegalAccessException ex) {
                        return null;
                    }
                }
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

    public T save(T obj) {
        if (obj == null) return null;
        String idStr = extractId(obj);
        if (idStr == null) return null;
        store.put(idStr, obj);
        return obj;
    }

    @SafeVarargs
    public final void saveAll(T... elements) {
        for (T e : elements) {
            save(e);
        }
    }

    public T findById(String idStr) {
        return store.get(idStr);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(store.values());
    }

    public boolean delete(String idStr) {
        return store.remove(idStr) != null;
    }

    public int count() {
        return store.size();
    }

    public static <N extends Number> double average(N[] arr) {
        if (arr == null || arr.length == 0) return 0.0;
        double total = 0.0;
        for (N n : arr) {
            total += n.doubleValue();
        }
        return total / arr.length;
    }
}

record BankInfo(String name, String branch) {}

enum MenuOption {
    OPEN_ACCOUNT,
    DEPOSIT,
    WITHDRAW,
    TRANSFER,
    STATEMENT,
    LIST_ACCOUNTS,
    BATCH_PROCESS,
    RUN_REPORT,
    EXIT
}

enum TransactionType {
    DEPOSIT,
    WITHDRAW,
    TRANSFER
}

record Command(TransactionType type, String accNum, long val) {}

interface Transactable {
    void deposit(long val) throws InvalidAmountException;
    boolean withdraw(long val) throws InsufficientFundsException, InvalidAmountException;
}

interface InterestBearing {
    double interestRate();

    default double yearlyInterest(long bal) {
        return (bal * interestRate()) / 100.0;
    }
}

interface Premium {}

@FunctionalInterface
interface WithdrawRule {
    boolean allow(Account acc, long val);
}

class AnnotationValidator {
    public static String[] validate(Object obj) {
        if (obj == null) return new String[0];
        List<String> errs = new ArrayList<>();
        Class<?> cls = obj.getClass();

        while (cls != null && cls != Object.class) {
            for (Field f : cls.getDeclaredFields()) {
                f.setAccessible(true);
                try {
                    Object v = f.get(obj);

                    if (f.isAnnotationPresent(Positive.class)
                            && v instanceof Number n
                            && n.longValue() <= 0) {
                        errs.add(f.getAnnotation(Positive.class).message());
                    }

                    if (f.isAnnotationPresent(MaxLength.class)
                            && v instanceof String s) {
                        int maxLen = f.getAnnotation(MaxLength.class).value();
                        if (s.length() > maxLen) {
                            errs.add(f.getName() + " is over " + maxLen + " chars");
                        }
                    }
                } catch (IllegalAccessException ignored) {
                }
            }
            cls = cls.getSuperclass();
        }
        return errs.toArray(new String[0]);
    }
}

class Validator {
    private static final Pattern MOB_PATTERN = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern MAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PAN_PAT = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");
    private static final Pattern IFSC_PAT = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");

    public static boolean isValidMobile(String mob) {
        return mob != null && MOB_PATTERN.matcher(mob).matches();
    }

    public static boolean isValidEmail(String mail) {
        return mail != null && MAIL_PATTERN.matcher(mail).matches();
    }

    public static boolean isValidPan(String panNum) {
        return panNum != null && PAN_PAT.matcher(panNum).matches();
    }

    public static boolean isValidIfsc(String ifscCode) {
        return ifscCode != null && IFSC_PAT.matcher(ifscCode).matches();
    }
}

class CommandParser {
    public static Command parse(String txt) {
        if (txt == null) return null;
        String[] segments = txt.trim().split("\\s+");
        if (segments.length != 3) {
            return null;
        }
        try {
            TransactionType tType = TransactionType.valueOf(segments[0].toUpperCase());
            long val = Long.parseLong(segments[2]);
            return new Command(tType, segments[1], val);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}

class StatementFormatter {
    public static String buildStatement(Account acc) {
        StringBuilder bldr = new StringBuilder();
        bldr.append("========================\n");
        bldr.append("   ACCOUNT STATEMENT    \n");
        bldr.append("========================\n");
        bldr.append("Account No       : ").append(acc.getAccNum()).append("\n");
        bldr.append("Account Holder   : ").append(acc.getAccHolder()).append("\n");
        bldr.append("Account Type     : ").append(acc.getClass().getSimpleName()).append("\n");
        bldr.append("Balance          : Rs. ").append(acc.getCurrBalance()).append("\n");
        bldr.append("Available Funds  : Rs. ").append(max(0, acc.availableFunds())).append("\n");
        bldr.append("Interest Rate    : ").append(acc.interestRate()).append("%\n");
        bldr.append("Yearly Interest  : Rs. ").append(String.format("%.2f", acc.yearlyInterest(acc.getCurrBalance()))).append("\n");
        bldr.append("Status           : ").append(acc.isActive() ? "Active" : "Inactive").append("\n");
        bldr.append("========================\n");
        return bldr.toString();
    }
}

class Customer implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;
    private static long custCounter = 100;

    @Id
    private final String custId;

    @MaxLength(50)
    private String fullName;
    private String mail;
    private String phoneNum;
    private Address addr;

    public static class Address implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String street;
        private final String town;
        private final String pin;

        public Address(String street, String town, String pin) {
            this.street = street;
            this.town = town;
            this.pin = pin;
        }

        public String getStreet() {
            return street;
        }

        public String getTown() {
            return town;
        }

        public String getPin() {
            return pin;
        }

        @Override
        public String toString() {
            return street + ", " + town + " - " + pin;
        }
    }

    private static synchronized String getNextCustId() {
        custCounter++;
        return "CUST" + custCounter;
    }

    public Customer(String fullName, String mail, String phoneNum, Address addr) {
        this.custId = getNextCustId();
        this.fullName = fullName;
        this.mail = mail;
        this.phoneNum = phoneNum;
        this.addr = addr;
    }

    public Customer(String fullName, String mail, String phoneNum) {
        this(fullName, mail, phoneNum, null);
    }

    public String getCustId() {
        return custId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getMail() {
        return mail;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public Address getAddr() {
        return addr;
    }

    public void setAddr(Address addr) {
        this.addr = addr;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

abstract class Account implements Transactable, InterestBearing, Serializable, Comparable<Account> {
    private static final long serialVersionUID = 1L;
    private static final String ACC_PREFIX = "MB";
    private static long accCounter = 0;

    @Id
    private final String accNum;

    @MaxLength(50)
    private String accHolder;

    @Positive
    protected long currBalance;

    private boolean activeStatus;

    public static void setCounter(long val) {
        accCounter = val;
    }

    public static void restoreCounter(Account[] existingArr) {
        long maxVal = 0;
        for (Account a : existingArr) {
            try {
                maxVal = max(maxVal, Long.parseLong(a.accNum.substring(ACC_PREFIX.length())));
            } catch (NumberFormatException ignored) {
            }
        }
        accCounter = maxVal;
    }

    private static synchronized String genAccNum() {
        accCounter++;
        return String.format("%s%05d", ACC_PREFIX, accCounter);
    }

    public Account(String accHolder, long currBalance) {
        this.accNum = genAccNum();
        this.accHolder = accHolder;
        this.currBalance = currBalance;
        this.activeStatus = true;
    }

    public Account(String accNum, String accHolder, long currBalance) {
        this.accNum = accNum;
        this.accHolder = accHolder;
        this.currBalance = currBalance;
        this.activeStatus = true;
    }

    public Account(String accHolder) {
        this(accHolder, 0);
    }

    @Override
    public int compareTo(Account o) {
        return this.accNum.compareTo(o.accNum);
    }

    @Override
    public abstract double interestRate();

    public abstract long availableFunds();

    public boolean canWithdraw(long val) {
        return val <= availableFunds();
    }

    @Override
    public synchronized void deposit(long val) throws InvalidAmountException {
        if (val <= 0) {
            throw new InvalidAmountException("Deposit value must be > 0.");
        }
        currBalance += val;
    }

    @Override
    public synchronized boolean withdraw(long val)
            throws InsufficientFundsException, InvalidAmountException {
        if (val <= 0) {
            throw new InvalidAmountException("Withdrawal value must be > 0.");
        }
        if (!canWithdraw(val)) {
            long deficit = val - availableFunds();
            throw new InsufficientFundsException(
                    "Withdrawal denied: not enough funds.", max(0, deficit));
        }
        currBalance -= val;
        return true;
    }

    public void transfer(Account targetAcc, long val) throws BankException {
        if (targetAcc == null) {
            throw new AccountNotFoundException("Target account missing.");
        }
        if (targetAcc == this) {
            throw new BankException("Accounts must be different.");
        }

        boolean thisFirstLock = this.accNum.compareTo(targetAcc.accNum) < 0;
        Account lock1 = thisFirstLock ? this : targetAcc;
        Account lock2 = thisFirstLock ? targetAcc : this;

        synchronized (lock1) {
            synchronized (lock2) {
                this.withdraw(val);
                targetAcc.deposit(val);
            }
        }
    }

    public String getAccNum() {
        return accNum;
    }

    public String getAccHolder() {
        return accHolder;
    }

    public synchronized long getCurrBalance() {
        return currBalance;
    }

    public boolean isActive() {
        return activeStatus;
    }

    @Override
    public String toString() {
        return accNum + " - " + accHolder + " [" + getClass().getSimpleName()
                + "] Rs. " + currBalance;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account acc = (Account) obj;
        return Objects.equals(accNum, acc.accNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accNum);
    }
}

class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;
    public static final long MIN_BAL_DEFAULT = 1500;
    private final long minBal;

    public SavingsAccount(String accHolder, long currBalance, long minBal) {
        super(accHolder, currBalance);
        this.minBal = minBal;
    }

    public SavingsAccount(String accHolder, long currBalance) {
        this(accHolder, currBalance, MIN_BAL_DEFAULT);
    }

    @Override
    public double interestRate() {
        return 4.5;
    }

    @Override
    public synchronized long availableFunds() {
        return currBalance - minBal;
    }

    public long getMinBal() {
        return minBal;
    }
}

class CurrentAccount extends Account implements Premium {
    private static final long serialVersionUID = 1L;
    public static final long OVERDRAFT_DEF = 10000;
    private final long odLimit;

    public CurrentAccount(String accHolder, long currBalance, long odLimit) {
        super(accHolder, currBalance);
        this.odLimit = odLimit;
    }

    public CurrentAccount(String accHolder, long currBalance) {
        this(accHolder, currBalance, OVERDRAFT_DEF);
    }

    @Override
    public double interestRate() {
        return 0.0;
    }

    @Override
    public synchronized long availableFunds() {
        return currBalance + odLimit;
    }

    public long getOdLimit() {
        return odLimit;
    }
}

class FixedDepositAccount extends Account {
    private static final long serialVersionUID = 1L;

    public FixedDepositAccount(String accHolder, long currBalance) {
        super(accHolder, currBalance);
    }

    @Override
    public double interestRate() {
        return 7.5;
    }

    @Override
    public long availableFunds() {
        return 0;
    }
}

class BankService {
    private static final Path PATH_LOG = Paths.get("logs", "transactions.log");
    private final Repository<Account> repo;
    private boolean isLoggingActive = true;

    public BankService(Repository<Account> repo) {
        this.repo = repo;
    }

    public void setLoggingActive(boolean status) {
        this.isLoggingActive = status;
    }

    private void saveLog(String logEntry) {
        if (isLoggingActive) {
            TransactionLog.append(PATH_LOG, logEntry);
        }
    }

    private Account fetchAcc(String accId, String prefix) throws AccountNotFoundException {
        Account a = repo.findById(accId);
        if (a == null) {
            throw new AccountNotFoundException(prefix + " " + accId + " not found.");
        }
        return a;
    }

    public Account openNewAccount(int accType, String custName, long startBal) throws BankException {
        if (custName == null || custName.isBlank()) {
            throw new BankException("Name is required.");
        }
        if (startBal <= 0) {
            throw new InvalidAmountException("Start balance must be > 0.");
        }

        Account a = switch (accType) {
            case 1 -> new SavingsAccount(custName, startBal);
            case 2 -> new CurrentAccount(custName, startBal);
            case 3 -> new FixedDepositAccount(custName, startBal);
            default -> throw new BankException("Invalid type.");
        };

        String[] validationErrs = AnnotationValidator.validate(a);
        if (validationErrs.length > 0) {
            throw new BankException("Validation error: " + String.join(", ", validationErrs));
        }

        repo.save(a);
        saveLog("OPEN " + a.getAccNum() + " " + startBal);
        return a;
    }

    public void doDeposit(String accId, long val) throws BankException {
        fetchAcc(accId, "Account").deposit(val);
        saveLog("DEPOSIT " + accId + " " + val);
    }

    public void doWithdraw(String accId, long val) throws BankException {
        fetchAcc(accId, "Account").withdraw(val);
        saveLog("WITHDRAW " + accId + " " + val);
    }

    public void doTransfer(String srcAcc, String destAcc, long val) throws BankException {
        Account source = fetchAcc(srcAcc, "Source");
        Account destination = fetchAcc(destAcc, "Destination");
        source.transfer(destination, val);
        saveLog("WITHDRAW " + srcAcc + " " + val);
        saveLog("DEPOSIT " + destAcc + " " + val);
    }

    public String generateStatement(String accId) throws BankException {
        return StatementFormatter.buildStatement(fetchAcc(accId, "Account"));
    }

    public List<Account> getAllAccounts() {
        List<Account> res = repo.findAll();
        Collections.sort(res);
        return res;
    }

    public long calcTotalHoldings() {
        long sum = 0;
        for (Account a : repo.findAll()) {
            sum += a.getCurrBalance();
        }
        return sum;
    }

    public void executeBatch(TransactionProcessor proc, List<Command> cmds) {
        for (Account a : repo.findAll()) {
            proc.registerAccount(a);
        }

        for (Command c : cmds) {
            proc.submit(() -> {
                try {
                    switch (c.type()) {
                        case DEPOSIT -> doDeposit(c.accNum(), c.val());
                        case WITHDRAW -> doWithdraw(c.accNum(), c.val());
                        default -> System.out.println("Unsupported batch cmd: " + c);
                    }
                } catch (BankException ex) {
                    System.out.println("Batch rejection: " + ex.getMessage());
                }
            });
        }
    }
}

public class MiniBank {
    private static final Path PATH_DATA = Paths.get("data", "accounts.dat");
    private static final Path DIR_LOGS = Paths.get("logs");
    private static final Path PATH_REPORT = Paths.get("reports", "daily_summary.txt");

    public static void displayMenu() {
        System.out.println("\n*** MINI BANK APP ***");
        System.out.println("1. Create Account");
        System.out.println("2. Make Deposit");
        System.out.println("3. Make Withdrawal");
        System.out.println("4. Fund Transfer");
        System.out.println("5. Print Statement");
        System.out.println("6. View All Accounts");
        System.out.println("7. Start Batch Process");
        System.out.println("8. End-of-Day Report");
        System.out.println("9. Exit");
        System.out.print("Select an option (1-9): ");
    }

    private static String getStrInput(Scanner in, String msg) {
        System.out.print(msg);
        return in.nextLine().trim();
    }

    private static long getNumInput(Scanner in, String msg) {
        System.out.print(msg);
        long val = in.nextLong();
        in.nextLine();
        return val;
    }

    private static void verifyTest(boolean status, String label) {
        System.out.println((status ? "[OK] " : "[FAIL] ") + label);
    }

    public static void runDiagnostics(BankService svc, Repository<Account> repository) {
        System.out.println("\n--- Initiating Diagnostics ---");
        svc.setLoggingActive(false);
        try {
            Account sav = svc.openNewAccount(1, "TestClient", 2000);
            svc.doDeposit(sav.getAccNum(), 500);
            verifyTest(sav.getCurrBalance() == 2500, "Deposit increases balance.");

            boolean wasRefused = false;
            try {
                svc.doWithdraw(sav.getAccNum(), 10000);
            } catch (InsufficientFundsException ex) {
                wasRefused = true;
            }
            verifyTest(wasRefused, "InsufficientFundsException triggered on overdraft.");

            Account fd = svc.openNewAccount(3, "FdClient", 5000);
            boolean isFdBlocked = false;
            try {
                svc.doWithdraw(fd.getAccNum(), 100);
            } catch (InsufficientFundsException ex) {
                isFdBlocked = true;
            }
            verifyTest(isFdBlocked, "FD withdrawal blocked correctly.");

            Account sharedAcc = svc.openNewAccount(1, "ThreadUser", 1000);
            svc.doTransfer(sav.getAccNum(), sharedAcc.getAccNum(), 500);
            verifyTest(sav.getCurrBalance() == 2000 && sharedAcc.getCurrBalance() == 1500,
                    "Funds transferred successfully.");

            Thread[] thrArr = new Thread[5];
            for (int k = 0; k < thrArr.length; k++) {
                thrArr[k] = new Thread(new AccountWorker(sharedAcc, 200, 10));
                thrArr[k].start();
            }
            for (Thread thr : thrArr) {
                thr.join();
            }
            verifyTest(sharedAcc.getCurrBalance() == 11500, "Concurrency lock works.");

            repository.delete(sav.getAccNum());
            repository.delete(fd.getAccNum());
            repository.delete(sharedAcc.getAccNum());
            System.out.println("[INFO] Diagnostics completed.\n");
        } catch (Exception ex) {
            System.out.println("[FAIL] Diagnostics error: " + ex.getMessage());
        } finally {
            svc.setLoggingActive(true);
        }
    }

    private static List<Command> generateBatch(String tgt) throws InterruptedException {
        final String[] rawCmds = {
                "DEPOSIT " + tgt + " 250",
                "DEPOSIT " + tgt + " 350",
                "WITHDRAW " + tgt + " 100"
        };

        final TransactionBuffer txBuf = new TransactionBuffer(2);
        final List<Command> parsedList = new ArrayList<>();

        Thread prodThread = new Thread(() -> {
            try {
                for (String cmdLine : rawCmds) {
                    txBuf.produce(cmdLine);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consThread = new Thread(() -> {
            try {
                for (int m = 0; m < rawCmds.length; m++) {
                    Command cmdObj = CommandParser.parse(txBuf.consume());
                    if (cmdObj != null) {
                        parsedList.add(cmdObj);
                    }
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        });

        prodThread.start();
        consThread.start();
        prodThread.join();
        consThread.join();
        return parsedList;
    }

    public static void main(String[] args) {
        BankInfo bankInfo = new BankInfo("Charusat National Bank", "Changa Branch");
        System.out.println("Mini Bank Service - " + bankInfo.name() + " (" + bankInfo.branch() + ")");

        Repository<Account> dbRepo = new Repository<>();
        Account[] loadedArr = StatePersister.load(PATH_DATA);
        if (loadedArr.length > 0) {
            dbRepo.saveAll(loadedArr);
            Account.restoreCounter(loadedArr);
            System.out.println("Loaded " + loadedArr.length + " accounts from storage.");
        } else {
            SavingsAccount s1 = new SavingsAccount("Vedant", 6000, 1500);
            CurrentAccount c1 = new CurrentAccount("Aarav", 9000, 4000);
            dbRepo.saveAll(s1, c1);
            System.out.println("Started with default accounts.");
        }

        BankService svc = new BankService(dbRepo);
        runDiagnostics(svc, dbRepo);

        Scanner inputScanner = new Scanner(System.in);
        boolean isAppRunning = true;

        while (isAppRunning) {
            displayMenu();
            if (!inputScanner.hasNextInt()) {
                if (!inputScanner.hasNext()) {
                    break;
                }
                System.out.println("Invalid input. Use numbers.");
                inputScanner.next();
                continue;
            }

            int sel = inputScanner.nextInt();
            inputScanner.nextLine();

            MenuOption menuOpt = switch (sel) {
                case 1 -> MenuOption.OPEN_ACCOUNT;
                case 2 -> MenuOption.DEPOSIT;
                case 3 -> MenuOption.WITHDRAW;
                case 4 -> MenuOption.TRANSFER;
                case 5 -> MenuOption.STATEMENT;
                case 6 -> MenuOption.LIST_ACCOUNTS;
                case 7 -> MenuOption.BATCH_PROCESS;
                case 8 -> MenuOption.RUN_REPORT;
                case 9 -> MenuOption.EXIT;
                default -> null;
            };

            if (menuOpt == null) {
                System.out.println("Choice must be 1-9.");
                continue;
            }

            try {
                switch (menuOpt) {
                    case OPEN_ACCOUNT -> {
                        int tType = (int) getNumInput(inputScanner, "\nType (1=Sav, 2=Cur, 3=FD): ");
                        String cName = getStrInput(inputScanner, "Customer Name: ");
                        long startBal = getNumInput(inputScanner, "Starting Balance: ");
                        Account newAcc = svc.openNewAccount(tType, cName, startBal);
                        System.out.println("Created: " + newAcc);
                    }
                    case DEPOSIT -> {
                        String aNum = getStrInput(inputScanner, "Account No: ").toUpperCase();
                        long depVal = getNumInput(inputScanner, "Amount to Deposit: ");
                        svc.doDeposit(aNum, depVal);
                        System.out.println("Rs. " + depVal + " deposited.");
                    }
                    case WITHDRAW -> {
                        String aNum = getStrInput(inputScanner, "Account No: ").toUpperCase();
                        long wdVal = getNumInput(inputScanner, "Amount to Withdraw: ");
                        svc.doWithdraw(aNum, wdVal);
                        System.out.println("Rs. " + wdVal + " withdrawn.");
                    }
                    case TRANSFER -> {
                        String src = getStrInput(inputScanner, "From Account: ").toUpperCase();
                        String dst = getStrInput(inputScanner, "To Account: ").toUpperCase();
                        long trVal = getNumInput(inputScanner, "Transfer Amount: ");
                        svc.doTransfer(src, dst, trVal);
                        System.out.println("Transferred Rs. " + trVal + ".");
                    }
                    case STATEMENT -> {
                        String aNum = getStrInput(inputScanner, "Account No: ").toUpperCase();
                        System.out.println(svc.generateStatement(aNum));
                    }
                    case LIST_ACCOUNTS -> {
                        System.out.println("\n--- All Accounts ---");
                        List<Account> allList = svc.getAllAccounts();
                        if (allList.isEmpty()) {
                            System.out.println("No accounts exist.");
                        } else {
                            allList.forEach(System.out::println);
                            System.out.println("Total Holdings: Rs. " + svc.calcTotalHoldings());
                        }
                    }
                    case BATCH_PROCESS -> {
                        List<Account> curAccs = svc.getAllAccounts();
                        if (curAccs.isEmpty()) {
                            System.out.println("Need an account first.");
                            break;
                        }
                        String sId = "BATCH-RUN-" + System.currentTimeMillis();
                        try (BankAuditSession aud = new BankAuditSession(sId)) {
                            String targetAcc = curAccs.get(0).getAccNum();
                            System.out.println("Loading queue...");
                            List<Command> batchCmds = generateBatch(targetAcc);
                            System.out.println("Running on pool...");
                            TransactionProcessor txProc = new TransactionProcessor(4);
                            svc.executeBatch(txProc, batchCmds);
                            txProc.stop();
                            System.out.println("Done: " + batchCmds.size() + " commands on " + targetAcc + ".");
                        }
                    }
                    case RUN_REPORT -> {
                        System.out.println("\nGenerating Daily Report:");
                        ReportGenerator.generateReport(DIR_LOGS, PATH_REPORT);
                    }
                    case EXIT -> {
                        List<Account> fullList = dbRepo.findAll();
                        StatePersister.save(fullList.toArray(new Account[0]), PATH_DATA);
                        System.out.println("Data saved to " + PATH_DATA);
                        System.out.println("Closing application.");
                        isAppRunning = false;
                    }
                }
            } catch (InsufficientFundsException ex) {
                System.out.println("Error: " + ex.getMessage() + " (Deficit: Rs. " + ex.getDeficit() + ")");
            } catch (BankException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (InputMismatchException ex) {
                System.out.println("Please provide a valid whole number.");
                inputScanner.nextLine();
            } catch (Exception ex) {
                System.out.println("System fault: " + ex.getMessage());
            }
        }
        inputScanner.close();
    }
}