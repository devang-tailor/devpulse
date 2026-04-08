# DevPulse — VS Code Developer Toolkit

A single sidebar panel that replaces scattered productivity tools: **bookmarks, snippet vault, TODO aggregator, and quick notes** — all in one place.

## Features

### 🔖 Smart Bookmarks
- Bookmark any line with `Ctrl+Shift+B` (Cmd+Shift+B on Mac)
- Add optional labels to remember *why* you bookmarked
- One-click navigation back to bookmarked lines
- Stored globally — persists across sessions

### 📋 TODO Aggregator
- Automatically scans your workspace for `TODO`, `FIXME`, `HACK`, `XXX`, `BUG`, `NOTE` comments
- Prioritized display (BUG → FIXME → HACK → XXX → TODO → NOTE)
- Auto-refreshes on file save
- Configurable patterns and exclusions

### 📦 Snippet Vault
- Save selected code as a reusable snippet with `Ctrl+Shift+S`
- **Variable interpolation**: use `${{varName}}` in snippets — you'll be prompted for values on insert
- Usage tracking (most-used snippets bubble up)
- Tag and describe snippets for easy discovery

### 📝 Quick Notes
- Per-workspace scratchpad via `Ctrl+Shift+N`
- Creates a `.devpulse-notes.md` file in your project root
- Perfect for context you need to survive between sessions

### ⚡ Dashboard
- At-a-glance counts for bookmarks, TODOs, snippets
- Quick action buttons for common tasks
- TODO breakdown by type with color-coded badges

## Installation

```bash
# From source
cd plugins/vscode-devpulse
npm install
npm run compile
# Then press F5 in VS Code to launch Extension Development Host
```

## Configuration

```json
{
  "devpulse.todoPatterns": ["TODO", "FIXME", "HACK", "XXX", "BUG", "NOTE"],
  "devpulse.excludePatterns": ["**/node_modules/**", "**/dist/**"],
  "devpulse.snippetVariableDelimiter": "${{}}"
}
```

## Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl+Shift+B` | Bookmark current line |
| `Ctrl+Shift+S` | Save selection as snippet |
| `Ctrl+Shift+N` | Open quick notes |

## License

MIT
