package devpulse.services;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import devpulse.models.Snippet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@State(
    name = "DevPulseSnippets",
    storages = @Storage("DevPulseSnippets.xml")
)
public class SnippetService implements PersistentStateComponent<SnippetService> {

    public List<Snippet> snippets = new ArrayList<>();

    private static final Pattern VAR_PATTERN = Pattern.compile("\\$\\{(\\w+)\\}");

    public static SnippetService getInstance() {
        return com.intellij.openapi.application.ApplicationManager.getApplication().getService(SnippetService.class);
    }

    @Override
    public @Nullable SnippetService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SnippetService state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public void addSnippet(Snippet snippet) {
        snippets.add(snippet);
    }

    public void removeSnippet(String id) {
        snippets.removeIf(s -> s.id.equals(id));
    }

    /**
     * Expand snippet variables. Returns the body with variables replaced.
     * Variables are in ${{varName}} format.
     */
    public String expandSnippet(Snippet snippet, java.util.Map<String, String> values) {
        String body = snippet.body;
        for (var entry : values.entrySet()) {
            body = body.replace("${{" + entry.getKey() + "}}", entry.getValue());
        }
        snippet.usageCount++;
        return body;
    }

    /** Extract variable names from a snippet body */
    public static java.util.List<String> extractVariables(String body) {
        java.util.Set<String> vars = new java.util.LinkedHashSet<>();
        Matcher m = Pattern.compile("\\$\\{\\{(\\w+)\\}\\}").matcher(body);
        while (m.find()) {
            vars.add(m.group(1));
        }
        return new ArrayList<>(vars);
    }

    public List<Snippet> getSnippets() {
        return new ArrayList<>(snippets);
    }

    public int getCount() {
        return snippets.size();
    }
}
