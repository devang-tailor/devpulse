package devpulse.ui;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import devpulse.models.Bookmark;
import devpulse.services.BookmarkService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class BookmarkListPanel extends JPanel {

    private final Project project;
    private final DefaultListModel<String> listModel;
    private final JBList<String> list;

    public BookmarkListPanel(Project project) {
        super(new BorderLayout());
        this.project = project;
        this.listModel = new DefaultListModel<>();
        this.list = new JBList<>(listModel);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new BookmarkCellRenderer());

        // Double-click to navigate
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int idx = list.getSelectedIndex();
                    if (idx >= 0) navigateToBookmark(idx);
                }
            }
        });

        // Context menu
        JPopupMenu popup = new JPopupMenu();
        JMenuItem removeItem = new JMenuItem("Remove Bookmark");
        removeItem.addActionListener(e -> removeSelectedBookmark());
        popup.add(removeItem);
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
        BookmarkService service = BookmarkService.getInstance(project);
        for (Bookmark b : service.getBookmarks()) {
            String label = b.label != null && !b.label.isBlank()
                ? b.label : b.context.trim().substring(0, Math.min(60, b.context.trim().length()));
            String fileName = new File(b.filePath).getName();
            listModel.addElement(fileName + ":" + (b.line + 1) + " — " + label);
        }
    }

    private void navigateToBookmark(int index) {
        BookmarkService service = BookmarkService.getInstance(project);
        if (index < service.getCount()) {
            Bookmark b = service.getBookmarks().get(index);
            var vf = LocalFileSystem.getInstance().findFileByIoFile(new File(b.filePath));
            if (vf != null) {
                FileEditorManager.getInstance(project).openTextEditor(
                    new OpenFileDescriptor(project, vf, b.line, b.column), true);
            }
        }
    }

    private void removeSelectedBookmark() {
        int idx = list.getSelectedIndex();
        if (idx >= 0) {
            BookmarkService service = BookmarkService.getInstance(project);
            Bookmark b = service.getBookmarks().get(idx);
            service.removeBookmark(b.filePath, b.line);
            refresh();
        }
    }

    private static class BookmarkCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            return this;
        }
    }
}
