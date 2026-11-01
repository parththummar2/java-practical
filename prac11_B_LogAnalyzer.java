import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Stream;
public class prac11_B_LogAnalyzer {
    public static void main(String[] args) {
        Path logDir = Paths.get("logs");
        String keyword = "ERROR";
        try {
            Files.createDirectories(logDir);
            Files.write(logDir.resolve("app.log"), List.of("INFO Application started", "INFO User logged in", "ERROR Database connection failed", "INFO Retrying connection"));
            Files.write(logDir.resolve("error.log"), List.of("WARN Disk space is low", "ERROR File not found", "INFO Request completed"));
            Files.write(logDir.resolve("access.log"), List.of("INFO GET /home", "ERROR HTTP 500 on /api", "INFO GET /profile", "INFO Application stopped", "INFO Shutdown complete"));
            long totalLines = 0, matchingLines = 0;
            System.out.println("Analyzing log files...");
            try (Stream<Path> files = Files.list(logDir)) {
                for (Path file : (Iterable<Path>) files.filter(Files::isRegularFile).sorted()::iterator) {
                    long fileLines = 0, fileMatches = 0;
                    try (var reader = Files.lines(file)) {
                        var iterator = reader.iterator();
                        while (iterator.hasNext()) {
                            String line = iterator.next(); fileLines++;
                            if (line.contains(keyword)) fileMatches++;
                        }
                    }
                    BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                    totalLines += fileLines; matchingLines += fileMatches;
                    System.out.println("File: " + file.getFileName() + " | Size: " + attrs.size() + " bytes | Lines: " + fileLines + " | Matches: " + fileMatches);
                }
            }
            System.out.println("\nKeyword: " + keyword);
            System.out.println("Total lines: " + totalLines);
            System.out.println("Lines containing keyword '" + keyword + "': " + matchingLines);
            System.out.println("Log analysis completed.");
        } catch (IOException e) { System.out.println("File error: " + e.getMessage()); }
    }
}
