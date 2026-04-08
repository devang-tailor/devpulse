package devpulse.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OpenNotesAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        String basePath = project.getBasePath();
        if (basePath == null) {
            Messages.showWarningDialog(project, "Open a project to use Quick Notes.", "DevPulse");
            return;
        }

        Path notesPath = Paths.get(basePath, ".devpulse-notes.md");

        // Create the notes file if it doesn't exist
        if (!Files.exists(notesPath)) {
            try {
                Files.writeString(notesPath,
                    "# DevPulse Quick Notes\n\n" +
                    "> Per-project scratchpad. This file is auto-created.\n\n---\n\n"
                );
            } catch (IOException ex) {
                Messages.showErrorDialog(project, "Could not create notes file.", "DevPulse");
                return;
            }
        }

        VirtualFile vf = LocalFileSystem.getInstance().refreshAndFindFileByPath(notesPath.toString());
        if (vf != null) {
            vf.refresh(false, false);
            FileEditorManager.getInstance(project).openFile(vf, true);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(e.getProject() != null);
    }
}
