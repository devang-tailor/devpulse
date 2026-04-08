# DevPulse — IntelliJ / Android Studio Plugin

A developer productivity toolkit: **bookmarks, snippet vault, TODO aggregator, and quick notes** — all in a unified bottom tool window.

Compatible with IntelliJ IDEA 2023.3+, Android Studio Hedgehog+, and all JetBrains IDEs built on IntelliJ Platform.

## Features

### 🔖 Bookmarks
- Bookmark any line via `Ctrl+Shift+B` or right-click menu
- Add labels to remember *why* you bookmarked
- Double-click to navigate — right-click to remove
- Persisted per-project

### 📋 TODO Aggregator
- Scans your project for `TODO`, `FIXME`, `HACK`, `XXX`, `BUG`, `NOTE` comments
- Prioritized display (BUG → FIXME → HACK → XXX → TODO → NOTE)
- Configurable patterns via Settings → Tools → DevPulse
- Auto-scans on project open

### 📦 Snippet Vault
- Save selected code as a snippet via `Ctrl+Shift+S`
- **Variable interpolation**: use `${{varName}}` — you'll be prompted for values on insert
- Usage tracking and search
- Double-click to insert at cursor

### 📝 Quick Notes
- Per-project scratchpad via `Ctrl+Shift+N`
- Creates a `.devpulse-notes.md` file in the project root
- Context survives between sessions

## Building

```bash
cd plugins/intellij-devpulse

# Requires JDK 17+
./gradlew buildPlugin

# Output: build/distributions/devpulse-1.0.0.zip
```

## Installation

1. Build the plugin (see above)
2. In your IDE: Settings → Plugins → ⚙️ → Install Plugin from Disk
3. Select the `build/distributions/devpulse-1.0.0.zip` file
4. Restart IDE

## Running in Development

```bash
./gradlew runIde    # Launches a sandbox IDE instance with the plugin
```

## Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl+Shift+B` | Bookmark current line |
| `Ctrl+Shift+S` | Save selection as snippet |
| `Ctrl+Shift+N` | Open quick notes |

## Configuration

Settings → Tools → DevPulse:
- **TODO Patterns**: Customize which comment tags to scan for
- **Exclude Patterns**: Glob patterns to skip during scanning

## Project Structure

```
src/main/java/devpulse/
├── actions/           # Menu and toolbar actions
│   ├── AddBookmarkAction.java
│   ├── SaveSnippetAction.java
│   ├── OpenNotesAction.java
│   ├── ScanTodosAction.java
│   ├── RemoveBookmarkAction.java
│   └── OpenDashboardAction.java
├── models/            # Data models
│   ├── Bookmark.java
│   ├── Snippet.java
│   └── TodoItem.java
├── services/          # Business logic & persistence
│   ├── BookmarkService.java
│   ├── SnippetService.java
│   ├── TodoScannerService.java
│   └── DevPulseStartupActivity.java
├── settings/          # Settings UI & state
│   ├── DevPulseSettingsState.java
│   └── DevPulseSettingsConfigurable.java
└── ui/                # Tool window panels
    ├── DevPulseToolWindowFactory.java
    ├── DevPulsePanel.java
    ├── BookmarkListPanel.java
    ├── TodoListPanel.java
    └── SnippetListPanel.java
```

## License

MIT
