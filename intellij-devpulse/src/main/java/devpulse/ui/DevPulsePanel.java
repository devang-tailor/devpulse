package devpulse.ui;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import devpulse.services.BookmarkService;
import devpulse.services.SnippetService;
import devpulse.services.TodoScannerService;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DevPulsePanel extends JBPanel<DevPulsePanel> {

    private final Project project;

    public DevPulsePanel(Project project) {
        super(new BorderLayout());
        this.project = project;
        initUI();
    }

    private void initUI() {
        setBorder(JBUI.Borders.empty(16));

        // Title
        JBLabel title = new JBLabel("⚡ DevPulse Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18));

        JBLabel projectName = new JBLabel(project.getName());
        projectName.setForeground(UIManager.getColor("Label.infoForeground"));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(projectName, BorderLayout.SOUTH);
        headerPanel.setBorder(JBUI.Borders.emptyBottom(16));
        add(headerPanel, BorderLayout.NORTH);

        // Stats grid
        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12));
        grid.setBorder(JBUI.Borders.emptyBottom(16));

        BookmarkService bookmarkService = BookmarkService.getInstance(project);
        TodoScannerService todoScanner = project.getService(TodoScannerService.class);
        SnippetService snippetService = SnippetService.getInstance();

        grid.add(createStatCard("🔖", "Bookmarks", String.valueOf(bookmarkService.getCount())));
        grid.add(createStatCard("📋", "TODOs", String.valueOf(todoScanner.getCount())));
        grid.add(createStatCard("📦", "Snippets", String.valueOf(snippetService.getCount())));
        grid.add(createStatCard("📝", "Notes", "Open"));

        add(grid, BorderLayout.CENTER);

        // Action buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        JButton scanBtn = new JButton("🔄 Scan TODOs");
        scanBtn.addActionListener(e -> {
            todoScanner.scan(project);
            refresh();
        });

        JButton notesBtn = new JButton("📝 Quick Notes");
        notesBtn.addActionListener(e -> openNotes());

        JButton refreshBtn = new JButton("↻ Refresh");
        refreshBtn.addActionListener(e -> refresh());

        actions.add(scanBtn);
        actions.add(notesBtn);
        actions.add(refreshBtn);
        add(actions, BorderLayout.SOUTH);
    }

    private JPanel createStatCard(String icon, String title, String value) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1, true),
            JBUI.Borders.empty(12)
        ));

        JBLabel iconLabel = new JBLabel(icon);
        iconLabel.setFont(iconLabel.getFont().deriveFont(24f));

        JBLabel titleLabel = new JBLabel(title);
        titleLabel.setForeground(UIManager.getColor("Label.infoForeground"));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.PLAIN, 11f));

        JBLabel valueLabel = new JBLabel(value);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 28f));
        valueLabel.setForeground(UIManager.getColor("link.foreground"));

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        return card;
    }

    private void openNotes() {
        String basePath = project.getBasePath();
        if (basePath == null) return;
        Path notesPath = Paths.get(basePath, ".devpulse-notes.md");
        if (!Files.exists(notesPath)) {
            try {
                Files.writeString(notesPath,
                    "# DevPulse Quick Notes\n\n> Per-project scratchpad.\n\n---\n\n");
            } catch (Exception ignored) {}
        }
        var vf = LocalFileSystem.getInstance().refreshAndFindFileByPath(notesPath.toString());
        if (vf != null) FileEditorManager.getInstance(project).openFile(vf, true);
    }

    public void refresh() {
        removeAll();
        initUI();
        revalidate();
        repaint();
    }
}
