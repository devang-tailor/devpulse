package devpulse.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class DevPulseToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        DevPulsePanel panel = new DevPulsePanel(project);
        Content content = ContentFactory.getInstance().createContent(panel, "Dashboard", false);
        toolWindow.getContentManager().addContent(content);

        // Add sub-tabs
        BookmarkListPanel bookmarksPanel = new BookmarkListPanel(project);
        Content bookmarksContent = ContentFactory.getInstance().createContent(bookmarksPanel, "Bookmarks", false);
        toolWindow.getContentManager().addContent(bookmarksContent);

        TodoListPanel todosPanel = new TodoListPanel(project);
        Content todosContent = ContentFactory.getInstance().createContent(todosPanel, "TODOs", false);
        toolWindow.getContentManager().addContent(todosContent);

        SnippetListPanel snippetsPanel = new SnippetListPanel();
        Content snippetsContent = ContentFactory.getInstance().createContent(snippetsPanel, "Snippets", false);
        toolWindow.getContentManager().addContent(snippetsContent);
    }
}
