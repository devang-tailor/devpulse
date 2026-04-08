import * as vscode from 'vscode';
import { BookmarkTreeProvider } from './bookmarks';
import { TodoTreeProvider } from './todos';
import { SnippetTreeProvider } from './snippets';

export class DashboardProvider implements vscode.WebviewViewProvider {
  public static readonly viewType = 'devpulse.dashboard';
  private view?: vscode.WebviewView;
  private disposables: vscode.Disposable[] = [];

  constructor(
    private context: vscode.ExtensionContext,
    private bookmarks: BookmarkTreeProvider,
    private todos: TodoTreeProvider,
    private snippets: SnippetTreeProvider
  ) {}

  dispose() {
    this.disposables.forEach(d => d.dispose());
    this.disposables = [];
  }

  resolveWebviewView(
    webviewView: vscode.WebviewView,
    _context: vscode.WebviewViewResolveContext,
    _token: vscode.CancellationToken
  ) {
    this.view = webviewView;
    webviewView.webview.options = {
      enableScripts: true,
      localResourceRoots: [this.context.extensionUri]
    };

    this.update();

    // Clean up old listeners before adding new ones
    this.dispose();

    // Listen for data changes
    this.disposables.push(
      this.bookmarks.onDidChangeTreeData(() => this.update()),
      this.todos.onDidChangeTreeData(() => this.update()),
      this.snippets.onDidChangeTreeData(() => this.update())
    );

    webviewView.webview.onDidReceiveMessage(msg => {
      switch (msg.command) {
        case 'refreshTodos':
          this.todos.refresh();
          break;
        case 'openNote':
          vscode.commands.executeCommand('devpulse.openNote');
          break;
        case 'addBookmark':
          vscode.commands.executeCommand('devpulse.addBookmark');
          break;
        case 'addSnippet':
          vscode.commands.executeCommand('devpulse.addSnippet');
          break;
      }
    });
  }

  private update() {
    if (!this.view) return;
    this.view.webview.html = this.getHtml();
  }

  private getHtml(): string {
    const bookmarkCount = this.bookmarks.getBookmarkCount();
    const todoCount = this.todos.getTodoCount();
    const todoCounts = this.todos.getCountsByType();
    const snippetCount = this.snippets.getSnippetCount();

    const todoBreakdown = Object.entries(todoCounts)
      .map(([type, count]) => `<span class="badge badge-${type.toLowerCase()}">${type}: ${count}</span>`)
      .join(' ');

    const workspaceName = vscode.workspace.name || 'No workspace';

    return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
      font-family: var(--vscode-font-family);
      color: var(--vscode-foreground);
      background: var(--vscode-sideBar-background);
      padding: 16px;
      font-size: 13px;
    }
    h1 {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 4px;
      color: var(--vscode-foreground);
    }
    .workspace-name {
      font-size: 11px;
      color: var(--vscode-descriptionForeground);
      margin-bottom: 16px;
    }
    .grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 10px;
      margin-bottom: 16px;
    }
    .card {
      background: var(--vscode-editor-background);
      border-radius: 8px;
      padding: 14px;
      border: 1px solid var(--vscode-panel-border);
    }
    .card-title {
      font-size: 11px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      color: var(--vscode-descriptionForeground);
      margin-bottom: 8px;
    }
    .card-value {
      font-size: 28px;
      font-weight: 700;
      color: var(--vscode-textLink-foreground);
    }
    .card-icon {
      font-size: 20px;
      margin-bottom: 4px;
    }
    .actions {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-bottom: 16px;
    }
    button {
      background: var(--vscode-button-background);
      color: var(--vscode-button-foreground);
      border: none;
      border-radius: 4px;
      padding: 6px 12px;
      font-size: 12px;
      cursor: pointer;
      font-family: inherit;
    }
    button:hover {
      background: var(--vscode-button-hoverBackground);
    }
    button.secondary {
      background: var(--vscode-button-secondaryBackground);
      color: var(--vscode-button-secondaryForeground);
    }
    .badges {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-top: 8px;
    }
    .badge {
      font-size: 11px;
      padding: 2px 8px;
      border-radius: 10px;
      background: var(--vscode-badge-background);
      color: var(--vscode-badge-foreground);
    }
    .badge-bug { background: #f44336; color: white; }
    .badge-fixme { background: #ff9800; color: white; }
    .badge-hack { background: #9c27b0; color: white; }
    .badge-todo { background: #2196f3; color: white; }
    .badge-xxx { background: #e91e63; color: white; }
    .badge-note { background: #4caf50; color: white; }
    .section {
      margin-bottom: 16px;
    }
    .section-title {
      font-size: 12px;
      font-weight: 600;
      margin-bottom: 8px;
      color: var(--vscode-foreground);
    }
    .footer {
      text-align: center;
      font-size: 11px;
      color: var(--vscode-descriptionForeground);
      padding-top: 12px;
      border-top: 1px solid var(--vscode-panel-border);
    }
  </style>
</head>
<body>
  <h1>⚡ DevPulse</h1>
  <div class="workspace-name">${workspaceName}</div>

  <div class="grid">
    <div class="card">
      <div class="card-icon">🔖</div>
      <div class="card-title">Bookmarks</div>
      <div class="card-value">${bookmarkCount}</div>
    </div>
    <div class="card">
      <div class="card-icon">📋</div>
      <div class="card-title">TODOs</div>
      <div class="card-value">${todoCount}</div>
      <div class="badges">${todoBreakdown}</div>
    </div>
    <div class="card">
      <div class="card-icon">📦</div>
      <div class="card-title">Snippets</div>
      <div class="card-value">${snippetCount}</div>
    </div>
    <div class="card">
      <div class="card-icon">📝</div>
      <div class="card-title">Notes</div>
      <div class="card-value" style="font-size: 14px; padding-top: 10px;">Quick access</div>
    </div>
  </div>

  <div class="section">
    <div class="section-title">Quick Actions</div>
    <div class="actions">
      <button onclick="vscode.postMessage({command:'addBookmark'})">🔖 Bookmark</button>
      <button onclick="vscode.postMessage({command:'addSnippet'})">📦 Snippet</button>
      <button onclick="vscode.postMessage({command:'openNote'})">📝 Notes</button>
      <button class="secondary" onclick="vscode.postMessage({command:'refreshTodos'})">🔄 Refresh TODOs</button>
    </div>
  </div>

  <div class="footer">DevPulse v1.0 — Stay in the flow</div>

  <script>
    const vscode = acquireVsCodeApi();
  </script>
</body>
</html>`;
  }
}
