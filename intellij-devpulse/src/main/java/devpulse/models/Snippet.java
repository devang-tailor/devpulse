package devpulse.models;

import java.util.List;

public class Snippet {
    public String id;
    public String name;
    public String description;
    public String language;
    public String body;        // may contain ${{varName}} variables
    public List<String> tags;
    public long createdAt;
    public int usageCount;

    public Snippet() {
        this.id = Long.toHexString(System.currentTimeMillis());
        this.createdAt = System.currentTimeMillis();
        this.usageCount = 0;
    }

    public Snippet(String name, String description, String language, String body, List<String> tags) {
        this();
        this.name = name;
        this.description = description;
        this.language = language;
        this.body = body;
        this.tags = tags;
    }
}
