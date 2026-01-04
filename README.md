![image](https://github.com/user-attachments/assets/7c08e053-c84f-41f8-bc97-f55130100419)

<a href="https://www.paypal.com/paypalme/MicrobotBE?country.x=BE" target="_blank">
  <img src="https://img.shields.io/badge/Donate-%E2%9D%A4-ff69b4?style=for-the-badge">
</a>
<a href="https://www.youtube.com/@themicrobot" target="_blank">
  <img src="https://img.shields.io/badge/YouTube-Subscribe-FF0000?style=for-the-badge&logo=youtube&logoColor=white">
</a>
<a href="https://themicrobot.com" target="_blank">
  <img src="https://img.shields.io/badge/Microbot-Website-0A66C2?style=for-the-badge&logo=google-chrome&logoColor=white">
</a>


# Microbot - Darkness Edition 🌙

**Darkness Edition** is an enhanced fork of Microbot that brings intelligent, adaptive automation to a new level. Built around three foundational principles: **intelligent adaptation** over rigid automation, a **premium dark aesthetic** user experience, and **educational, well-documented code**.

## ✨ What Makes Darkness Edition Different

- **Behavioral Profiling System** - Captures and learns from your natural play patterns during a calibration phase, then applies those patterns to automation for more human-like behavior
- **Intelligence Layer** - Continuously monitors game state and dynamically adjusts script execution based on risk assessment and environmental factors
- **Enhanced Script API** - High-level abstractions that make script development more accessible while automatically incorporating behavioral variation
- **Dark Visual Theme** - A cohesive dark aesthetic with purple accents that communicates sophistication and reduces eye strain

## 🚀 Core Features

### Behavioral Profiling
The system observes user actions during calibration and builds a statistical model of natural behavior patterns including:
- Mouse movement velocity and acceleration curves
- Timing distributions between actions
- Decision-making patterns and common action sequences

### Risk-Aware Intelligence
Dynamic risk assessment considers:
- Nearby player presence and behavior
- Duration of repetitive actions
- Time since last random behavior
- Environmental game conditions

Risk responses range from normal operation at low risk to injecting random human-like behaviors at elevated risk levels.

### Premium Script API
Task-based abstractions for common activities:
- **GatheringTask** - Resource collection with intelligent banking
- **CombatTask** - Combat management with survival prioritization
- **BankingTask** - Reliable inventory management
- **NavigationTask** - Pathfinding with natural movement

## 📚 Documentation

- [Development Roadmap](ROADMAP.md) - Detailed development phases and milestones
- [API Documentation](runelite-client/src/main/java/net/runelite/client/plugins/microbot/api/QUERYABLE_API.md) - Queryable API reference
- [Agent Guide](AGENTS.md) - Instructions for AI coding assistants

## 🔧 Building from Source

\`\`\`bash
# Clone the repository
git clone https://github.com/Darknessownsu/Microbot---Darkness-Edition.git
cd Microbot---Darkness-Edition

# Build the project
./gradlew :buildAll
\`\`\`

## 🤝 Contributing

Contributions are welcome! Please read our contributing guidelines and ensure your code follows the established patterns in the codebase. All Darkness Edition code should reside in the \`net.runelite.client.plugins.microbot.darkness\` namespace.

## 📖 Learning Resource

This project serves both as a functional tool and a learning resource. The code is designed to be educational with comprehensive documentation explaining not just how to accomplish tasks, but why certain approaches work better than others.

## Discord

[![Discord Banner 1](https://discord.com/api/guilds/1087718903985221642/widget.png?style=banner1)](https://discord.gg/zaGrfqFEWE)

If you have any questions, please join our [Discord](https://discord.gg/zaGrfqFEWE) server.

---

*Darkness Edition - Where intelligent automation meets elegant design.*
