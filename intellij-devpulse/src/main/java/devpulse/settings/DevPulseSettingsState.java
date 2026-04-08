package devpulse.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(
    name = "DevPulseSettings",
    storages = @Storage("DevPulseSettings.xml")
)
public class DevPulseSettingsState implements PersistentStateComponent<DevPulseSettingsState> {

    public List<String> todoPatterns = List.of("TODO", "FIXME", "HACK", "XXX", "BUG", "NOTE");
    public List<String> excludePatterns = List.of(
        "**/node_modules/**", "**/dist/**", "**/build/**", "**/.git/**",
        "**/out/**", "**/.gradle/**", "**/.idea/**"
    );
    public String snippetVariablePattern = "\\$\\{\\{(\\w+)\\}\\}";

    public static DevPulseSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(DevPulseSettingsState.class);
    }

    @Override
    public @Nullable DevPulseSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull DevPulseSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
