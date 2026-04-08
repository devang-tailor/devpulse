package devpulse.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import devpulse.models.TodoItem;
import devpulse.services.TodoScannerService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ScanTodosAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        TodoScannerService scanner = project.getService(TodoScannerService.class);
        List<TodoItem> items = scanner.scan(project);

        if (items.isEmpty()) {
            Messages.showInfoMessage(project, "No TODO/FIXME/HACK comments found.", "DevPulse");
        } else {
            // Show a summary
            long bugs = items.stream().filter(i -> i.pattern.equals("BUG")).count();
            long fixmes = items.stream().filter(i -> i.pattern.equals("FIXME")).count();
            long hacks = items.stream().filter(i -> i.pattern.equals("HACK")).count();
            long todos = items.stream().filter(i -> i.pattern.equals("TODO")).count();

            String summary = String.format(
                "Found %d items:\n• %d BUG\n• %d FIXME\n• %d HACK\n• %d TODO\n• %d other",
                items.size(), bugs, fixmes, hacks, todos,
                items.size() - bugs - fixmes - hacks - todos
            );
            Messages.showInfoMessage(project, summary, "DevPulse — TODO Scan");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(e.getProject() != null);
    }
}
