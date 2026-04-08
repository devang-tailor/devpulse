# DevPulse Usage Guide

## Installation

| IDE | How to Install |
|-----|---------------|
| **VS Code** | Extensions → Search "DevPulse" → Install |
| **Android Studio** | Settings → Plugins → Marketplace → Search "DevPulse" |
| **IntelliJ IDEA** | Settings → Plugins → Marketplace → Search "DevPulse" |
| **Any JetBrains IDE** | Same as IntelliJ |

## 🔖 Bookmarks

**What:** Mark important lines for instant navigation.

**Usage:**
1. Place cursor on any line
2. Press `Ctrl+Shift+B` (`Cmd+Shift+B` on Mac)
3. Enter an optional label
4. Click bookmark in sidebar to jump back

**Use cases:**
- Marking debugging breakpoints
- Flagging code for review
- Quick navigation across files

## 📋 TODO Aggregator

**What:** Finds all TODO/FIXME/HACK/XXX/BUG/NOTE comments in your project.

**Usage:**
1. Open DevPulse sidebar → TODOs tab
2. Auto-scans on save and project open
3. Click to jump to any item

**Supported formats:**
- `// TODO: text` — C, C++, Java, JS, TS, C#, Go, Rust, Swift, Kotlin
- `# TODO: text` — Python, Ruby, Bash, YAML
- `-- TODO: text` — SQL, Haskell, Lua
- `<!-- TODO: text -->` — HTML, XML
- `/* TODO: text */` — CSS

## 📦 Snippet Vault

**What:** Save reusable code with variable placeholders.

**Save a snippet:**
1. Select code in editor
2. Press `Ctrl+Shift+S` (`Cmd+Shift+S` on Mac)
3. Enter name, description, and tags

**Insert a snippet:**
1. Open DevPulse sidebar → Snippets tab
2. Double-click a snippet
3. Fill in `${{variable}}` values when prompted

**Example with variables:**
```
// Save as: React Component
export const ${{Name}} = () => {
  return <div className="${{class}}">${{content}}</div>;
};
```

## 📝 Quick Notes

**What:** Per-project scratchpad as `.devpulse-notes.md`.

**Usage:**
1. Press `Ctrl+Shift+N` (`Cmd+Shift+N` on Mac)
2. Write notes in the opened file
3. Notes persist between sessions

## ⚡ Dashboard

**What:** Overview of all DevPulse data.

**Usage:**
1. Open DevPulse sidebar → Dashboard tab
2. View counts and quick-action buttons
