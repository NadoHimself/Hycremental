# Hycremental 🌟

**Das ultimative Incremental Generator Tycoon Gamemode für Hytale**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.java.net/)
[![Status](https://img.shields.io/badge/Status-Alpha%20Development-red.svg)](https://github.com/NadoHimself/Hycremental)

---

## 🎮 Was ist Hycremental?

Hycremental ist ein vollständiger Tycoon-Gamemode für Hytale, der Incremental/Clicker-Mechaniken mit kompetitivem Multiplayer-Gameplay verbindet. Baue dein Generator-Imperium auf einer privaten Insel, verdiene Essence durch Mining und Automation, und klettere durch Prestige-Systeme zu unglaublicher Power!

### ✨ Core Features

- **🏝️ Private Island System** - Eigene Insel mit bis zu 100x100 Chunks
- **⚙️ 12 Generator-Tiers** - Von Stone bis Divine Nexus
- **📈 Prestige System** - Infinite Progression mit Multipliers
- **🚀 Ascension & Rebirth** - Meta-Progression für Endgame
- **💰 Multi-Currency Economy** - Essence, Gems, Crystals
- **🏪 Marketplace & Trading** - Spieler-zu-Spieler Handel
- **🏆 10+ Leaderboards** - Kompetitive Rankings
- **👥 Team System** - Spiele mit Freunden
- **🎨 Cosmetics** - 15+ Island Themes, Generator Skins
- **🎯 Events & Achievements** - Über 100 Achievements

---

## 🛠️ Technical Stack

- **Language:** Java 25
- **Build Tool:** Gradle 8.x
- **Framework:** Hytale Plugin API
- **Database:** PostgreSQL / MySQL
- **Libraries:** HikariCP, Gson, BigMath

---

## 📦 Installation

### Voraussetzungen
- Hytale Server (Version 1.0+)
- Java 25 SDK
- PostgreSQL oder MySQL Datenbank

### Setup

1. **Download** das Plugin von [Releases](https://github.com/NadoHimself/Hycremental/releases)
2. **Platziere** die JAR-Datei in `plugins/` Ordner
3. **Konfiguriere** die `config.yml` mit deinen Datenbank-Credentials
4. **Starte** den Server
5. **Fertig!** 🎉

---

## 🎯 Gameplay Loop

```
1. Mine Essence Blocks (Manual)
   ↓
2. Buy Generators (Automation)
   ↓
3. Upgrade Generators (Power)
   ↓
4. Prestige (Multipliers)
   ↓
5. Unlock Higher Tiers
   ↓
6. Ascend (Permanent Perks)
   ↓
7. Rebirth (Ultimate Power)
   ↓
8. Repeat & Dominate!
```

---

## 📊 Generator Tiers

| Tier | Name | Production | Unlock |
|------|------|------------|--------|
| 1 | Stone Generator | 1 E/s | Start |
| 2 | Coal Generator | 5 E/s | 5k Essence |
| 3 | Iron Generator | 25 E/s | 50k Essence |
| 4 | Gold Generator | 100 E/s | Prestige 1 |
| 5 | Diamond Generator | 500 E/s | Prestige 3 |
| 6 | Emerald Generator | 2.5k E/s | Prestige 5 |
| 7 | Netherite Generator | 10k E/s | Prestige 10 |
| 8 | Essence Crystal | 50k E/s | Prestige 20 |
| 9 | Quantum Generator | 250k E/s | Prestige 35 |
| 10 | Reality Forge | 1M E/s | Prestige 50 |
| 11 | Void Reactor | 10M E/s | Prestige 100 |
| 12 | Divine Nexus | 100M E/s | Prestige 200 |

---

## 🚀 Development Roadmap

### Phase 1: Foundation (Woche 1-2) ✅ In Progress
- [x] Repository Setup
- [x] Project Structure
- [ ] Core Plugin Systems
- [ ] Database Integration
- [ ] Basic Commands

### Phase 2: Generator Automation (Woche 3-4)
- [ ] Generator Tick System
- [ ] Upgrade Mechanics
- [ ] Island System
- [ ] Collection Methods

### Phase 3: Progression (Woche 5-6)
- [ ] Prestige System
- [ ] Advanced Generators (Tier 4-9)
- [ ] Enchantment System

### Phase 4: Economy (Woche 7-8)
- [ ] Marketplace
- [ ] Trading System
- [ ] Multi-Shop System

### Phase 5+: Advanced Features
- [ ] Leaderboards & Competition
- [ ] Events & Achievements
- [ ] Cosmetics
- [ ] Endgame Content

[Vollständige Roadmap](docs/ROADMAP.md)

---

## 📝 Commands

### Player Commands
```
/essence [balance|stats|pay] - Essence management
/gen [buy|place|upgrade|info] - Generator commands
/island [create|home|invite|kick] - Island management
/prestige [confirm] - Prestige system
/shop [category] - Open shop GUI
/stats - View your statistics
```

### Admin Commands
```
/hyadmin [give|set|reset] - Admin tools
/hyadmin reload - Reload configuration
/hyadmin backup - Create data backup
```

---

## 🤝 Contributing

Beiträge sind willkommen! Bitte lies [CONTRIBUTING.md](CONTRIBUTING.md) für Details.

### Development Setup

```bash
# Repository clonen
git clone https://github.com/NadoHimself/Hycremental.git
cd Hycremental

# Dependencies installieren
./gradlew build

# Tests ausführen
./gradlew test

# Plugin builden
./gradlew shadowJar
```

---

## 📄 License

MIT License - siehe [LICENSE](LICENSE) für Details

---

## 🔗 Links

- **Website:** [ageofflair.de](https://ageofflair.de)
- **Discord:** [Join our Community](https://discord.gg/ageofflair)
- **Documentation:** [Wiki](https://github.com/NadoHimself/Hycremental/wiki)
- **Bug Reports:** [Issues](https://github.com/NadoHimself/Hycremental/issues)

---

## 👨‍💻 Author

**Kielian (NadoHimself)**
- GitHub: [@NadoHimself](https://github.com/NadoHimself)
- Company: Age of Flair
- Website: [ageofflair.de](https://ageofflair.de)

---

## 🌟 Support

Wenn dir Hycremental gefällt, gib dem Projekt einen ⭐️!

---

**Made with ❤️ for the Hytale Community**