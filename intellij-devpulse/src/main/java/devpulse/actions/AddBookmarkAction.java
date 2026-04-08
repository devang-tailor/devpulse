package devpulse.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import devpulse.models.Bookmark;
import devpulse.services.BookmarkService;
import org.jetbrains.annotations.NotNull;

public class AddBookmarkAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (project == null || editor == null) {
            Messages.showWarningDialog(project, "Open a file to bookmark a line.", "DevPulse");
            return;
        }

        CaretModel caret = editor.getCaretModel();
        int line = caret.getLogicalPosition().line;
        int column = caret.getLogicalPosition().column;

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        VirtualFile virtualFile = psiFile != null ? psiFile.getVirtualFile() : null;
        if (virtualFile == null) return;

        String filePath = virtualFile.getPath();
        String contextLine = editor.getDocument().getText(
            new com.intellij.openapi.editor.LogicalPosition(line, 0),
            new com.intellij.openapi.editor.LogicalPosition(line, Integer.MAX_VALUE)
        );

        String label = Messages.showInputDialog(
            project,
            "Bookmark label (optional):",
            "Add Bookmark",
            null
        );
        if (label == null) return; // cancelled

        BookmarkService service = BookmarkService.getInstance(project);
        service.addBookmark(new Bookmark(filePath, line, column, label, contextLine));

        com.intellij.openapi.ui.Messages.showInfoMessage(
            project,
            "Bookmarked line " + (line + 1),
            "DevPulse"
        );
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(
            e.getProject() != null && e.getData(CommonDataKeys.EDITOR) != null
        );
    }
}
