import * as vscode from 'vscode';

interface SnippetData {
  id: string;
  name: string;
  description: string;
  language: string;
  body: string;        // may contain variables like ${{varName}}
  tags: string[];
  createdAt: number;
  usageCount: number;
}

export class SnippetItem extends vscode.TreeItem {
  constructor(
    public readonly snippet: SnippetData,
    public readonly collapsibleState: vscode.TreeItemCollapsibleState
  ) {
    super(snippet.name, collapsibleState);
    this.description = `${snippet.language} · used ${snippet.usageCount}x`;
    this.tooltip = `${snippet.description}\n\n${snippet.body}`;
    this.iconPath = new vscode.ThemeIcon('code');
    this.contextValue = 'snippet';
  }
}

export class SnippetTreeProvider implements vscode.TreeDataProvider<SnippetItem> {
  private _onDidChangeTreeData = new vscode.EventEmitter<SnippetItem | undefined>();
  readonly onDidChangeTreeData = this._onDidChangeTreeData.event;

  private storageKey = 'devpulse.snippets';
  private snippets: SnippetData[] = [];

  constructor(private context: vscode.ExtensionContext) {
    this.loadSnippets();
  }

  private loadSnippets() {
    this.snippets = this.context.globalState.get<SnippetData[]>(this.storageKey, []);
  }

  private saveSnippets() {
    this.context.globalState.update(this.storageKey, this.snippets);
  }

  getTreeItem(element: SnippetItem): vscode.TreeItem {
    return element;
  }

  getChildren(): SnippetItem[] {
    return this.snippets.map(s =>
      new SnippetItem(s, vscode.TreeItemCollapsibleState.None)
    );
  }

  async addSnippet() {
    const editor = vscode.window.activeTextEditor;
    if (!editor) {
      vscode.window.showWarningMessage('Select code to save as snippet.');
      return;
    }

    const selection = editor.selection;
    if (selection.isEmpty) {
      vscode.window.showWarningMessage('Select some code first.');
      return;
    }

    const code = editor.document.getText(selection);
    const language = editor.document.languageId;

    const name = await vscode.window.showInputBox({
      prompt: 'Snippet name',
      placeHolder: 'e.g., "React useEffect cleanup"'
    });
    if (!name) return;

    const description = await vscode.window.showInputBox({
      prompt: 'Description (optional)',
      placeHolder: 'What this snippet does'
    }) || '';

    const tagsInput = await vscode.window.showInputBox({
      prompt: 'Tags (comma-separated, optional)',
      placeHolder: 'react, hooks, cleanup'
    });
    const tags = tagsInput ? tagsInput.split(',').map(t => t.trim()).filter(Boolean) : [];

    const snippet: SnippetData = {
      id: Date.now().toString(36) + Math.random().toString(36).slice(2, 6),
      name,
      description,
      language,
      body: code,
      tags,
      createdAt: Date.now(),
      usageCount: 0
    };

    this.snippets.push(snippet);
    this.saveSnippets();
    this._onDidChangeTreeData.fire(undefined);
    vscode.window.showInformationMessage(`Snippet "${name}" saved.`);
  }

  async insertSnippet(item: SnippetItem) {
    const editor = vscode.window.activeTextEditor;
    if (!editor) {
      vscode.window.showWarningMessage('Open a file to insert the snippet.');
      return;
    }

    let body = item.snippet.body;

    // Find variables: ${{varName}}
    const varRegex = /\$\{\{(\w+)\}\}/g;
    const variables = new Set<string>();
    let match;
    while ((match = varRegex.exec(body)) !== null) {
      variables.add(match[1]);
    }

    // Prompt for each variable
    if (variables.size > 0) {
      for (const varName of variables) {
        const value = await vscode.window.showInputBox({
          prompt: 'Value for ${{' + varName + '}}',
          placeHolder: varName
        });
        if (value === undefined) return; // cancelled
        body = body.replace(new RegExp(`\\$\\{\\{${varName}\\}\\}`, 'g'), value);
      }
    }

    // Insert at cursor
    await editor.edit(editBuilder => {
      editBuilder.insert(editor.selection.active, body);
    });

    // Update usage count
    const idx = this.snippets.findIndex(s => s.id === item.snippet.id);
    if (idx >= 0) {
      this.snippets[idx].usageCount++;
      this.saveSnippets();
      this._onDidChangeTreeData.fire(undefined);
    }
  }

  getSnippetCount(): number {
    return this.snippets.length;
  }

  getSnippets(): SnippetData[] {
    return this.snippets;
  }
}
