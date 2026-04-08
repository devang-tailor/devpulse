package devpulse.ui;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import devpulse.models.TodoItem;
import devpulse.services.TodoScannerService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Map;

public class TodoListPanel extends JPanel {

    private final Project project;
    private final DefaultListModel<String> listModel;
    private final JBList<String> list;
    private final List<TodoItem> cachedItems = new java.util.ArrayList<>();

    private static final Map<String, String> ICON_MAP = Map.of(
        "BUG", "🐛", "FIXME", "⚠️", "HACK", "🔥", "XXX", "🚨", "TODO", "✅", "NOTE", "📝"
    );

    public TodoListPanel(Project project) {
        super(new BorderLayout());
        this.project = project;
        this.listModel = new DefaultListModel<>();
        this.list = new JBList<>(listModel);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int idx = list.getSelectedIndex();
                    if (idx >= 0 && idx < cachedItems.size()) navigateToTodo(idx);
                }
            }
        });

        add(new JBScrollPane(list), BorderLayout.CENTER);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton scanBtn = new JButton("🔍 Scan Project");
        scanBtn.addActionListener(e -> scan());
        toolbar.add(scanBtn);

        JLabel status = new JLabel("");
        toolbar.add(status);
        add(toolbar, BorderLayout.NORTH);
    }

    public void scan() {
        listModel.clear();
        cachedItems.clear();

        TodoScannerService scanner = project.getService(TodoScannerService.class);
        List<TodoItem> items = scanner.scan(project);
        cachedItems.addAll(items);

        for (TodoItem item : items) {
            String icon = ICON_MAP.getOrDefault(item.pattern, "•");
            listModel.addElement(
                icon + " " + item.pattern + ": " + item.text + "  [" + item.getFileName() + ":" + (item.line + 1) + "]"
            );
        }
    }

    private void navigateToTodo(int index) {
        TodoItem item = cachedItems.get(index);
        var vf = LocalFileSystem.getInstance().findFileByIoFile(new File(item.filePath));
        if (vf != null) {
            FileEditorManager.getInstance(project).openTextEditor(
                new OpenFileDescriptor(project, vf, item.line, 0), true);
        }
    }
}
