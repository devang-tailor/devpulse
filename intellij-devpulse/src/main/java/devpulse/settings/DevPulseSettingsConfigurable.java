package devpulse.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class DevPulseSettingsConfigurable implements Configurable {

    private JPanel mainPanel;
    private JTextField todoPatternsField;
    private JTextField excludePatternsField;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "DevPulse";
    }

    @Override
    public @Nullable JComponent createComponent() {
        DevPulseSettingsState settings = DevPulseSettingsState.getInstance();

        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // TODO patterns
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        mainPanel.add(new JLabel("TODO Patterns (comma-separated):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        todoPatternsField = new JTextField(String.join(", ", settings.todoPatterns), 40);
        mainPanel.add(todoPatternsField, gbc);

        // Exclude patterns
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        mainPanel.add(new JLabel("Exclude Patterns (comma-separated):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        excludePatternsField = new JTextField(String.join(", ", settings.excludePatterns), 40);
        mainPanel.add(excludePatternsField, gbc);

        return mainPanel;
    }

    @Override
    public boolean isModified() {
        DevPulseSettingsState settings = DevPulseSettingsState.getInstance();
        return !todoPatternsField.getText().equals(String.join(", ", settings.todoPatterns))
            || !excludePatternsField.getText().equals(String.join(", ", settings.excludePatterns));
    }

    @Override
    public void apply() {
        DevPulseSettingsState settings = DevPulseSettingsState.getInstance();
        settings.todoPatterns = Arrays.asList(todoPatternsField.getText().split("\\s*,\\s*"));
        settings.excludePatterns = Arrays.asList(excludePatternsField.getText().split("\\s*,\\s*"));
    }

    @Override
    public void reset() {
        DevPulseSettingsState settings = DevPulseSettingsState.getInstance();
        todoPatternsField.setText(String.join(", ", settings.todoPatterns));
        excludePatternsField.setText(String.join(", ", settings.excludePatterns));
    }
}
