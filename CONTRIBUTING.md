# Contributing to DevPulse

Thanks for your interest! Here's how to help.

## Ways to Contribute

- 🐛 Report bugs via [Issues](https://github.com/devang-tailor/devpulse/issues)
- 💡 Suggest features via [Issues](https://github.com/devang-tailor/devpulse/issues)
- 🔧 Submit pull requests
- ⭐ Star the repo and share with others

## Development Setup

### VS Code Extension
```bash
cd plugins/vscode-devpulse
npm install
npm run compile
# Press F5 in VS Code to test
```

### IntelliJ Plugin
```bash
cd plugins/intellij-devpulse
./gradlew runIde
```

## Pull Request Guidelines

1. Fork the repo
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes
4. Test both plugins if you changed shared logic
5. Submit a PR with a clear description

## Code Style

- TypeScript: ESLint defaults
- Java: IntelliJ default formatting

## License

By contributing, you agree your code will be licensed under MIT.
