# Comrade FX

A modernized JavaFX remake of the classic Java Applet-based shooting game.

![screenshot](resources/screenshot.gif)

## Features
- Player aircraft with movement and shooting
- Enemies spawn from top, left, and right sides
- Enemy bullets and collision detection
- Explosions for player and enemies
- Scrolling, repeating map background using tile images
- Responsive map tile scaling to fit display size
- Score display
- All images are loaded from the `resources/` directory

## Getting Started

### Prerequisites
- Java 11 or later
- JavaFX SDK ([Download here](https://gluonhq.com/products/javafx/))

### Installation & Running
1. Clone this repository:
   ```sh
   git clone https://github.com/yourusername/comrade-fx.git
   cd comrade-fx
   ```
2. Place all `.gif` image files in the `resources/` directory (already done)
3. Compile:
   ```sh
   javac --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml ComradeFX.java MapData.java
   ```
4. Run:
   ```sh
   java --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml ComradeFX
   ```

## Controls
- **Arrow keys** or **WASD**: Move player
- **Space**: Fire

## Project Structure
```
comrade-fx/
├── ComradeFX.java         # Main JavaFX game code
├── MapData.java           # Map data and accessors
├── resources/             # All image files (.gif)
├── .gitignore             # Ignores .class files
└── README.md
```

## Notes
- The player starts at the center-bottom of the screen.
- Enemies and enemy bullets move at independent speeds from the map scroll.
- Map scrolls upward and repeats infinitely.
- Map tiles scale to fit the display size.

## License
[MIT](LICENSE)

---
Original applet code and comments have been translated to English and modernized for JavaFX.

