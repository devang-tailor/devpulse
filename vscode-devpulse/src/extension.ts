import * as vscode from 'vscode';
import { BookmarkTreeProvider } from './bookmarks';
import { TodoTreeProvider } from './todos';
import { SnippetTreeProvider } from './snippets';
import { DashboardProvider } from './dashboard';
import { QuickNotesManager } from './notes';

export function activate(context: vscode.ExtensionContext) {
  console.log('DevPulse activated');

  // Register data providers
  const bookmarkProvider = new BookmarkTreeProvider(context);
  const todoProvider = new TodoTreeProvider(context);
  const snippetProvider = new SnippetTreeProvider(context);
  const dashboardProvider = new DashboardProvider(context, bookmarkProvider, todoProvider, snippetProvider);
  const notesManager = new QuickNotesManager(context);

  // Register tree views (track disposables)
  context.subscriptions.push(
    vscode.window.registerTreeDataProvider('devpulse.bookmarks', bookmarkProvider),
    vscode.window.registerTreeDataProvider('devpulse.todos', todoProvider),
    vscode.window.registerTreeDataProvider('devpulse.snippets', snippetProvider),
    vscode.window.registerWebviewViewProvider('devpulse.dashboard', dashboardProvider)
  );

  // Register commands
  context.subscriptions.push(
    vscode.commands.registerCommand('devpulse.addBookmark', () => bookmarkProvider.addBookmark()),
    vscode.commands.registerCommand('devpulse.removeBookmark', (item) => bookmarkProvider.removeBookmark(item)),
    vscode.commands.registerCommand('devpulse.addSnippet', () => snippetProvider.addSnippet()),
    vscode.commands.registerCommand('devpulse.insertSnippet', (item) => snippetProvider.insertSnippet(item)),
    vscode.commands.registerCommand('devpulse.openNote', () => notesManager.openNote()),
    vscode.commands.registerCommand('devpulse.refreshTodos', () => todoProvider.refresh()),
    vscode.commands.registerCommand('devpulse.openDashboard', () => {
      vscode.commands.executeCommand('devpulse.dashboard.focus');
    })
  );

  // Auto-refresh TODOs on file save
  context.subscriptions.push(
    vscode.workspace.onDidSaveTextDocument(() => todoProvider.refresh())
  );

  // Initial scan
  todoProvider.refresh();
}

export function deactivate() {}
