package devpulse.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import devpulse.services.BookmarkService;
import org.jetbrains.annotations.NotNull;

public class RemoveBookmarkAction extends AnAction {
    // This action is used programmatically from the tool window's context menu.
    // The filePath and line are passed via the action event's data context.

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;
        // Actual removal is handled by the tool window's list component
        // This action exists for the menu registration
    }
}
