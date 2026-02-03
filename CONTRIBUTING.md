# Contributing to Hycremental

Wir freuen uns über Beiträge zur Entwicklung von Hycremental! Dieses Dokument beschreibt den Prozess für Beiträge.

## Code of Conduct

- Sei respektvoll und konstruktiv
- Hilf anderen Community-Mitgliedern
- Folge den Coding Standards

## Wie kann ich beitragen?

### Bug Reports

1. Nutze die [Issue Templates](https://github.com/NadoHimself/Hycremental/issues/new/choose)
2. Beschreibe das Problem detailliert
3. Füge Steps to Reproduce hinzu
4. Erwähne deine Environment (Java Version, Hytale Version, etc.)

### Feature Requests

1. Erstelle ein Issue mit dem "Feature Request" Template
2. Beschreibe die gewünschte Funktion
3. Erkläre den Use Case
4. Optional: Schläge eine Implementation vor

### Pull Requests

1. **Fork** das Repository
2. **Clone** deinen Fork:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Hycremental.git
   ```
3. **Branch** erstellen:
   ```bash
   git checkout -b feature/amazing-feature
   ```
4. **Changes** machen und committen:
   ```bash
   git add .
   git commit -m "feat: add amazing feature"
   ```
5. **Push** to your fork:
   ```bash
   git push origin feature/amazing-feature
   ```
6. **Pull Request** erstellen auf GitHub

## Development Guidelines

### Code Style

- **Java 21+** Syntax verwenden
- **Lombok** für Boilerplate Code
- **4 Spaces** Indentation
- **CamelCase** für Klassen, **camelCase** für Variablen
- **Javadoc** für Public Methods

### Commit Messages

Nutze [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` - Neue Features
- `fix:` - Bug Fixes
- `docs:` - Documentation Changes
- `style:` - Code Style Changes (formatting)
- `refactor:` - Code Refactoring
- `test:` - Tests hinzufügen/ändern
- `chore:` - Build/Dependencies Updates

Beispiel:
```
feat: add prestige system with multiplier calculation

- Implement prestige reset logic
- Add prestige rewards configuration
- Create prestige GUI
```

### Testing

- Schreibe Unit Tests für neue Features
- Stelle sicher, dass alle Tests durchlaufen:
  ```bash
  ./gradlew test
  ```
- Test Coverage sollte über 70% sein

### Documentation

- Update README.md wenn nötig
- Javadoc für Public APIs
- Kommentiere komplexe Logik

## Project Structure

```
src/main/java/de/ageofflair/hycremental/
├── core/          # Core Systems (Managers)
├── generators/    # Generator Logic
├── data/          # Data Models & Database
├── events/        # Event Listeners
├── commands/      # Command Handlers
├── gui/           # GUI Menus
├── utils/         # Utility Classes
└── api/           # Public API
```

## Need Help?

- Join our [Discord](https://discord.gg/ageofflair)
- Check the [Wiki](https://github.com/NadoHimself/Hycremental/wiki)
- Ask in [Discussions](https://github.com/NadoHimself/Hycremental/discussions)

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

**Thank you for contributing to Hycremental!** 🚀