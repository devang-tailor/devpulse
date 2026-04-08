import * as vscode from 'vscode';
import * as path from 'path';

interface BookmarkData {
  filePath: string;
  line: number;
  column: number;
  label: string;
  context: string; // surrounding code
  createdAt: number;
}

export class BookmarkItem extends vscode.TreeItem {
  constructor(
    public readonly bookmark: BookmarkData,
    public readonly collapsibleState: vscode.TreeItemCollapsibleState
  ) {
    super(
      `${path.basename(bookmark.filePath)}:${bookmark.line + 1}`,
      collapsibleState
    );
    this.description = bookmark.label || bookmark.context.trim().substring(0, 50);
    this.tooltip = `${bookmark.filePath}\nLine ${bookmark.line + 1}, Col ${bookmark.column + 1}\n\n${bookmark.context}`;
    this.resourceUri = vscode.Uri.file(bookmark.filePath);
    this.iconPath = new vscode.ThemeIcon('bookmark');
    this.command = {
      command: 'vscode.open',
      title: 'Go to Bookmark',
      arguments: [
        vscode.Uri.file(bookmark.filePath),
        {
          selection: new vscode.Range(bookmark.line, bookmark.column, bookmark.line, bookmark.column)
        } as vscode.TextDocumentShowOptions
      ]
    };
    this.contextValue = 'bookmark';
  }
}

export class BookmarkTreeProvider implements vscode.TreeDataProvider<BookmarkItem> {
  private _onDidChangeTreeData = new vscode.EventEmitter<BookmarkItem | undefined>();
  readonly onDidChangeTreeData = this._onDidChangeTreeData.event;

  private storageKey = 'devpulse.bookmarks';
  private bookmarks: BookmarkData[] = [];

  constructor(private context: vscode.ExtensionContext) {
    this.loadBookmarks();
  }

  private loadBookmarks() {
    this.bookmarks = this.context.globalState.get<BookmarkData[]>(this.storageKey, []);
  }

  private saveBookmarks() {
    this.context.globalState.update(this.storageKey, this.bookmarks);
  }

  getTreeItem(element: BookmarkItem): vscode.TreeItem {
    return element;
  }

  getChildren(): BookmarkItem[] {
    return this.bookmarks.map(b =>
      new BookmarkItem(b, vscode.TreeItemCollapsibleState.None)
    );
  }

  async addBookmark() {
    const editor = vscode.window.activeTextEditor;
    if (!editor) {
      vscode.window.showWarningMessage('Open a file to bookmark a line.');
      return;
    }

    const pos = editor.selection.active;
    const doc = editor.document;
    const filePath = doc.fileName;
    const contextLine = doc.lineAt(pos.line).text;

    // Ask for a label
    const label = await vscode.window.showInputBox({
      prompt: 'Bookmark label (optional)',
      placeHolder: 'e.g., "Handle error case here"'
    });
    if (label === undefined) return; // cancelled

    // Avoid duplicates
    const exists = this.bookmarks.some(
      b => b.filePath === filePath && b.line === pos.line
    );
    if (exists) {
      vscode.window.showInformationMessage('This line is already bookmarked.');
      return;
    }

    this.bookmarks.push({
      filePath,
      line: pos.line,
      column: pos.character,
      label: label || '',
      context: contextLine,
      createdAt: Date.now()
    });

    this.saveBookmarks();
    this._onDidChangeTreeData.fire(undefined);
    vscode.window.showInformationMessage(`Bookmarked ${path.basename(filePath)}:${pos.line + 1}`);
  }

  removeBookmark(item: BookmarkItem) {
    const idx = this.bookmarks.findIndex(
      b => b.filePath === item.bookmark.filePath && b.line === item.bookmark.line
    );
    if (idx >= 0) {
      this.bookmarks.splice(idx, 1);
      this.saveBookmarks();
      this._onDidChangeTreeData.fire(undefined);
    }
  }

  getBookmarkCount(): number {
    return this.bookmarks.length;
  }

  getBookmarks(): BookmarkData[] {
    return this.bookmarks;
  }
}
