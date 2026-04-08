package devpulse.ui;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import devpulse.models.Snippet;
import devpulse.services.SnippetService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnippetListPanel extends JPanel {

    private final DefaultListModel<String> listModel;
    private final JBList<String> list;
    private final List<Snippet> cachedSnippets;

    public SnippetListPanel() {
        super(new BorderLayout());
        this.listModel = new DefaultListModel<>();
        this.list = new JBList<>(listModel);
        this.cachedSnippets = new java.util.ArrayList<>();

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double-click to insert
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int idx = list.getSelectedIndex();
                    if (idx >= 0) insertSnippet(idx);
                }
            }
        });

        // Context menu
        JPopupMenu popup = new JPopupMenu();
        JMenuItem insertItem = new JMenuItem("Insert Snippet");
        insertItem.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx >= 0) insertSnippet(idx);
        });
        JMenuItem deleteItem = new JMenuItem("Delete Snippet");
        deleteItem.addActionListener(e -> deleteSelected());
        popup.add(insertItem);
        popup.add(deleteItem);
        list.setComponentPopupMenu(popup);

        add(new JBScrollPane(list), BorderLayout.CENTER);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("↻ Refresh");
        refreshBtn.addActionListener(e -> refresh());
        toolbar.add(refreshBtn);
        add(toolbar, BorderLayout.NORTH);

        refresh();
    }

    public void refresh() {
        listModel.clear();
        cachedSnippets.clear();
        SnippetService service = SnippetService.getInstance();
        for (Snippet s : service.getSnippets()) {
            cachedSnippets.add(s);
            listModel.addElement(s.name + " [" + s.language + "] — used " + s.usageCount + "x");
        }
    }

    private void insertSnippet(int index) {
        if (index >= cachedSnippets.size()) return;
        Snippet snippet = cachedSnippets.get(index);

        // Find any project window with an active editor
        Project project = null;
        Editor editor = null;
        for (Project p : com.intellij.openapi.project.ProjectManager.getInstance().getOpenProjects()) {
            editor = FileEditorManager.getInstance(p).getSelectedTextEditor();
            if (editor != null) { project = p; break; }
        }

        if (editor == null || project == null) {
            Messages.showWarningDialog("Open a file to insert a snippet.", "DevPulse");
            return;
        }

        // Resolve variables
        String body = snippet.body;
        List<String> vars = SnippetService.extractVariables(body);
        Map<String, String> values = new HashMap<>();

        for (String varName : vars) {
            String value = Messages.showInputDialog(project,
                "Value for ${{" + varName + "}}:", "Snippet Variable", null);
            if (value == null) return; // cancelled
            values.put(varName, value);
        }

        String finalBody = SnippetService.getInstance().expandSnippet(snippet, values);

        WriteCommandAction.runWriteCommandAction(project, "Insert DevPulse Snippet", null, () -> {
            int offset = editor.getCaretModel().getOffset();
            editor.getDocument().insertString(offset, finalBody);
        });

        refresh(); // update usage count display
    }

    private void deleteSelected() {
        int idx = list.getSelectedIndex();
        if (idx >= 0 && idx < cachedSnippets.size()) {
            SnippetService.getInstance().removeSnippet(cachedSnippets.get(idx).id);
            refresh();
        }
    }
}
