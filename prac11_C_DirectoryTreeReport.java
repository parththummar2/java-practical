import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;
public class prac11_C_DirectoryTreeReport {
    public static void main(String[] args) {
        Path root = Paths.get("lab-NN");
        Path report = Paths.get("folder_report.txt");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        try {
            Files.createDirectories(root.resolve("src"));
            Files.createDirectories(root.resolve("data"));
            Files.writeString(root.resolve("src/Main.java"), "// Sample Java source file\\nclass Main {}\\n");
            Files.writeString(root.resolve("data/notes.txt"), "NIO directory traversal practice.\\n");
            Files.writeString(root.resolve("readme.txt"), "Practical 11 - Directory report example.\\n");
            StringBuilder summary = new StringBuilder();
            summary.append("DIRECTORY TREE REPORT\\n");
            summary.append("Root: ").append(root.toAbsolutePath()).append("\\n\\n");
            try (Stream<Path> paths = Files.walk(root)) {
                paths.filter(Files::isRegularFile).sorted(Comparator.comparing(Path::toString)).forEach(path -> {
                    try {
                        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                        String modified = formatter.format(Instant.ofEpochMilli(attrs.lastModifiedTime().toMillis()));
                        summary.append("Name: ").append(root.relativize(path))
                            .append(" | Size: ").append(attrs.size())
                            .append(" bytes | Modified: ").append(modified).append("\\n");
                    } catch (IOException e) { throw new RuntimeException(e); }
                });
            }
            Files.writeString(report, summary.toString());
            System.out.println(summary);
            System.out.println("Report saved to: " + report.toAbsolutePath());
            System.out.println("Directory scan completed successfully.");
        } catch (IOException e) { System.out.println("Directory error: " + e.getMessage()); }
    }
}
