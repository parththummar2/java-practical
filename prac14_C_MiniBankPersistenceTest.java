// JUnit 5 test source. Requires junit-jupiter-api on the project classpath.
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
class prac14_C_MiniBankPersistenceTest {
    @Test
    void savesAndReloadsDataUnchanged() throws Exception {
        ArrayList<String> before = new ArrayList<>();
        before.add("Yug:1000");
        before.add("Riya:1500");
        File file = File.createTempFile("minibank", ".dat");
        try {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(before);
            }
            ArrayList<String> after;
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                @SuppressWarnings("unchecked")
                ArrayList<String> loaded = (ArrayList<String>) in.readObject();
                after = loaded;
            }
            assertEquals(before, after);
        } finally { file.delete(); }
    }
}
