package devpulse.models;

public class Bookmark {
    public String filePath;
    public int line;      // 0-based
    public int column;    // 0-based
    public String label;
    public String context; // surrounding code
    public long createdAt;

    public Bookmark(String filePath, int line, int column, String label, String context) {
        this.filePath = filePath;
        this.line = line;
        this.column = column;
        this.label = label;
        this.context = context;
        this.createdAt = System.currentTimeMillis();
    }

    // Default constructor for serialization
    public Bookmark() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bookmark)) return false;
        Bookmark b = (Bookmark) o;
        return line == b.line && filePath.equals(b.filePath);
    }

    @Override
    public int hashCode() {
        return 31 * filePath.hashCode() + line;
    }
}
