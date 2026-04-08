package devpulse.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import devpulse.models.Snippet;
import devpulse.services.SnippetService;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SaveSnippetAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (project == null || editor == null) {
            Messages.showWarningDialog(project, "Select code to save as snippet.", "DevPulse");
            return;
        }

        SelectionModel selection = editor.getSelectionModel();
        if (!selection.hasSelection()) {
            Messages.showWarningDialog(project, "Select some code first.", "DevPulse");
            return;
        }

        String code = selection.getSelectedText();
        if (code == null || code.isBlank()) return;

        // Detect language safely
        String language = "plaintext";
        try {
            var psiFile = com.intellij.psi.PsiDocumentManager.getInstance(project)
                .getPsiFile(editor.getDocument());
            if (psiFile != null && psiFile.getVirtualFile() != null) {
                language = com.intellij.openapi.fileTypes.FileTypeManager.getInstance()
                    .getFileTypeByFile(psiFile.getVirtualFile()).getName();
            }
        } catch (Exception ignored) {}

        // Collect metadata via dialogs
        String name = Messages.showInputDialog(project, "Snippet name:", "Save Snippet", null);
        if (name == null || name.isBlank()) return;

        String desc = Messages.showInputDialog(project, "Description (optional):", "Save Snippet", null);
        if (desc == null) desc = "";

        String tagsInput = Messages.showInputDialog(project, "Tags (comma-separated):", "Save Snippet", null);
        List<String> tags = tagsInput != null && !tagsInput.isBlank()
            ? Arrays.asList(tagsInput.split("\\s*,\\s*"))
            : List.of();

        Snippet snippet = new Snippet(name, desc, language, code, tags);
        SnippetService.getInstance().addSnippet(snippet);

        Messages.showInfoMessage(project, "Snippet \"" + name + "\" saved.", "DevPulse");
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(
            e.getProject() != null && editor != null && editor.getSelectionModel().hasSelection()
        );
    }
}
