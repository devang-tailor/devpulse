package devpulse.services;

import com.intellij.openapi.project.Project;
import devpulse.models.TodoItem;
import devpulse.settings.DevPulseSettingsState;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class TodoScannerService {

    private List<TodoItem> cachedResults = new ArrayList<>();
    private long lastScanTime = 0;

    public static TodoScannerService getInstance(@NotNull Project project) {
        return project.getService(TodoScannerService.class);
    }

    public List<TodoItem> scan(@NotNull Project project) {
        DevPulseSettingsState settings = DevPulseSettingsState.getInstance();
        List<String> patterns = settings.todoPatterns;

        // Build a regex that matches any pattern in a comment
        String patternGroup = String.join("|", patterns);
        Pattern regex = Pattern.compile(
            "(?://|/\\*|#|<!--|--|%|;)\\s*(" + patternGroup + ")[\\s:]*(.*)",
            Pattern.CASE_INSENSITIVE
        );

        List<TodoItem> results = new ArrayList<>();
        String basePath = project.getBasePath();
        if (basePath == null) return results;

        Path root = Paths.get(basePath);
        List<String> excludeGlobs = settings.excludePatterns;

        try {
            // Use NIO file walker with glob exclusions
            Files.walk(root, 10)
                .filter(path -> !isExcluded(path, root, excludeGlobs))
                .filter(path -> path.toFile().isFile())
                .filter(path -> isSupportedFile(path.toString()))
                .limit(5000) // safety limit
                .forEach(path -> scanFile(path, regex, results));
        } catch (Exception e) {
            // Silently skip scanning errors
        }

        Collections.sort(results);
        cachedResults = results;
        lastScanTime = System.currentTimeMillis();
        return results;
    }

    private void scanFile(Path path, Pattern regex, List<TodoItem> results) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                java.nio.file.Files.newInputStream(path)))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null && lineNum < 10000) {
                var matcher = regex.matcher(line);
                if (matcher.find()) {
                    String tag = matcher.group(1).toUpperCase();
                    String text = matcher.group(2) != null ? matcher.group(2).trim() : "";
                    if (text.isEmpty()) text = "(no description)";
                    results.add(new TodoItem(tag, path.toString(), lineNum, text, line.trim()));
                }
                lineNum++;
            }
        } catch (Exception e) {
            // skip unreadable files
        }
    }

    private boolean isExcluded(Path path, Path root, List<String> excludeGlobs) {
        String relative = root.relativize(path).toString().replace('\\', '/');
        for (String glob : excludeGlobs) {
            // Simple glob matching
            String regex = glob
                .replace("**/", "(.*/)?")
                .replace("*", "[^/]*")
                .replace("?", ".");
            if (relative.matches(regex)) return true;
            // Also check if any path component matches
            String[] parts = relative.split("/");
            for (String part : parts) {
                if (part.matches(regex)) return true;
            }
        }
        return false;
    }

    private boolean isSupportedFile(String path) {
        // Binary file extensions to skip
        String[] skip = {".class", ".jar", ".war", ".zip", ".tar", ".gz", ".png", ".jpg",
                         ".gif", ".ico", ".svg", ".woff", ".woff2", ".ttf", ".eot",
                         ".mp3", ".mp4", ".avi", ".mov", ".exe", ".dll", ".so", ".dylib"};
        String lower = path.toLowerCase();
        for (String ext : skip) {
            if (lower.endsWith(ext)) return false;
        }
        return true;
    }

    public List<TodoItem> getCachedResults() {
        return cachedResults;
    }

    public int getCount() {
        return cachedResults.size();
    }
}
