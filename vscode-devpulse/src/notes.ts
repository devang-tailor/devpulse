import * as vscode from 'vscode';
import * as path from 'path';

export class QuickNotesManager {
  constructor(private context: vscode.ExtensionContext) {}

  async openNote() {
    const workspaceFolder = vscode.workspace.workspaceFolders?.[0];
    if (!workspaceFolder) {
      vscode.window.showWarningMessage('Open a workspace to use Quick Notes.');
      return;
    }

    // Use a .devpulse-notes.md file in the workspace root
    const notePath = path.join(workspaceFolder.uri.fsPath, '.devpulse-notes.md');
    const noteUri = vscode.Uri.file(notePath);

    try {
      await vscode.workspace.fs.stat(noteUri);
    } catch {
      // File doesn't exist, create it
      const initial = `# DevPulse Quick Notes\n\n> Per-project scratchpad. This file is auto-created.\n\n---\n\n`;
      await vscode.workspace.fs.writeFile(noteUri, Buffer.from(initial, 'utf-8'));
    }

    const doc = await vscode.workspace.openTextDocument(noteUri);
    await vscode.window.showTextDocument(doc, { preview: false });
  }
}
