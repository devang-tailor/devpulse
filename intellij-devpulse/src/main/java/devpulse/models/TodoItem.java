package devpulse.models;

public class TodoItem implements Comparable<TodoItem> {
    public String pattern;    // TODO, FIXME, etc.
    public String filePath;
    public int line;          // 0-based
    public String text;       // description after the tag
    public String context;    // full line

    private static final java.util.Map<String, Integer> PRIORITY = java.util.Map.of(
        "BUG", 0, "FIXME", 1, "HACK", 2, "XXX", 3, "TODO", 4, "NOTE", 5
    );

    public TodoItem(String pattern, String filePath, int line, String text, String context) {
        this.pattern = pattern;
        this.filePath = filePath;
        this.line = line;
        this.text = text;
        this.context = context;
    }

    @Override
    public int compareTo(TodoItem other) {
        int p1 = PRIORITY.getOrDefault(this.pattern, 99);
        int p2 = PRIORITY.getOrDefault(other.pattern, 99);
        int cmp = Integer.compare(p1, p2);
        if (cmp != 0) return cmp;
        return this.filePath.compareTo(other.filePath);
    }

    public String getFileName() {
        int idx = filePath.lastIndexOf('/');
        return idx >= 0 ? filePath.substring(idx + 1) : filePath;
    }
}
