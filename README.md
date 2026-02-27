# Chess

A Java implementation of the classic board game **Chess**. This project includes the game logic, rules enforcement, and a simple command-line interface for playing against another human or testing move generation.

## 🔧 Features

- Full implementation of chess rules, including:
  - Legal move generation for all piece types
  - Check, checkmate, and stalemate detection
  - Castling, en passant, and pawn promotion
- Modular architecture allowing easy extension (AI opponents, GUI, network play)
- Unit tests for core game mechanics

## 📁 Project Structure

```
chess/
├── src/           # Java source files
├── test/          # Unit tests
├── README.md      # Project documentation
└── build.gradle   # Build configuration (if using Gradle)
```

> Adjust structure details to match your build system (Maven, Gradle, etc.).

## 🚀 Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/chess.git
   cd chess
   ```

2. **Build the project**
   ```bash
   ./gradlew build        # Gradle
   # or
   mvn compile            # Maven
   ```

3. **Run the game**
   ```bash
   java -jar build/libs/chess.jar
   ```

4. **Run tests**
   ```bash
   ./gradlew test
   # or
   mvn test
   ```

## 📚 Usage

Once running, follow on-screen instructions to enter moves using algebraic notation (e.g., `e2e4`).

## 🎯 Roadmap

- [ ] Add computer opponent (AI)
- [ ] Implement graphical user interface (JavaFX/Swing)
- [ ] Enable networked multiplayer
- [ ] Mobile version using LibGDX or similar

## 🤝 Contributing

Contributions are welcome! Please open an issue or submit a pull request with your changes. Be sure to update tests when adding new features.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


---

*Generated with help from GitHub Copilot.*