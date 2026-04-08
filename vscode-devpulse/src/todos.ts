import * as vscode from 'vscode';

interface TodoItem {
  pattern: string;     // TODO, FIXME, etc.
  filePath: string;
  line: number;
  text: string;
  context: string;     // the full comment line
}

export class TodoTreeItem extends vscode.TreeItem {
  constructor(
    public readonly todo: TodoItem,
    public readonly collapsibleState: vscode.TreeItemCollapsibleState
  ) {
    super(`${todo.pattern}: ${todo.text}`, collapsibleState);
    this.description = `${todo.filePath.split('/').pop()}:${todo.line + 1}`;
    this.tooltip = `${todo.filePath}\nLine ${todo.line + 1}\n\n${todo.context}`;

    const iconMap: Record<string, string> = {
      TODO: 'check',
      FIXME: 'warning',
      HACK: 'flame',
      XXX: 'alert',
      BUG: 'bug',
      NOTE: 'note',
    };
    this.iconPath = new vscode.ThemeIcon(iconMap[todo.pattern] || 'circle-filled');

    this.resourceUri = vscode.Uri.file(todo.filePath);
    this.command = {
      command: 'vscode.open',
      title: 'Go to TODO',
      arguments: [
        vscode.Uri.file(todo.filePath),
        {
          selection: new vscode.Range(todo.line, 0, todo.line, todo.text.length)
        } as vscode.TextDocumentShowOptions
      ]
    };
  }
}

export class TodoTreeProvider implements vscode.TreeDataProvider<TodoTreeItem> {
  private _onDidChangeTreeData = new vscode.EventEmitter<TodoTreeItem | undefined>();
  readonly onDidChangeTreeData = this._onDidChangeTreeData.event;

  private items: TodoItem[] = [];
  private isScanning = false;

  constructor(private context: vscode.ExtensionContext) {}

  getTreeItem(element: TodoTreeItem): vscode.TreeItem {
    return element;
  }

  async getChildren(): Promise<TodoTreeItem[]> {
    return this.items.map(item =>
      new TodoTreeItem(item, vscode.TreeItemCollapsibleState.None)
    );
  }

  async refresh() {
    if (this.isScanning) return;
    this.isScanning = true;

    try {
      const config = vscode.workspace.getConfiguration('devpulse');
      const patterns = config.get<string[]>('todoPatterns', ['TODO', 'FIXME', 'HACK', 'XXX', 'BUG', 'NOTE']);
      const excludeGlobs = config.get<string[]>('excludePatterns', [
        '**/node_modules/**', '**/dist/**', '**/build/**', '**/.git/**'
      ]);

      const excludePattern = `{${excludeGlobs.join(',')}}`;
      const files = await vscode.workspace.findFiles('**/*.*', excludePattern, 2000);

      const patternRegex = new RegExp(`\\b(${patterns.join('|')})\\b`, 'i');
      const results: TodoItem[] = [];

      for (const fileUri of files) {
        try {
          const doc = await vscode.workspace.openTextDocument(fileUri);
          const text = doc.getText();
          const lines = text.split('\n');

          for (let i = 0; i < lines.length; i++) {
            const line = lines[i];
            const match = patternRegex.exec(line);
            if (match) {
              const tag = match[1].toUpperCase();
              // Extract text after the tag
              const afterTag = line.substring(line.indexOf(match[0]) + match[0].length).trim();
              results.push({
                pattern: tag,
                filePath: fileUri.fsPath,
                line: i,
                text: afterTag || '(no description)',
                context: line.trim()
              });
            }
          }
        } catch {
          // skip unreadable files
        }
      }

      this.items = results.sort((a, b) => {
        const priority: Record<string, number> = { BUG: 0, FIXME: 1, HACK: 2, XXX: 3, TODO: 4, NOTE: 5 };
        const p = (priority[a.pattern] ?? 99) - (priority[b.pattern] ?? 99);
        if (p !== 0) return p;
        const f = a.filePath.localeCompare(b.filePath);
        if (f !== 0) return f;
        return a.line - b.line;
      });

      this._onDidChangeTreeData.fire(undefined);
    } finally {
      this.isScanning = false;
    }
  }

  getTodoCount(): number {
    return this.items.length;
  }

  getCountsByType(): Record<string, number> {
    const counts: Record<string, number> = {};
    for (const item of this.items) {
      counts[item.pattern] = (counts[item.pattern] || 0) + 1;
    }
    return counts;
  }
}
