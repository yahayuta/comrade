# Comrade FX

A modernized JavaFX remake of the classic Java Applet-based shooting game.

## Features
- Player aircraft with movement and shooting
- Enemies spawn from top, left, and right sides
- Enemy bullets and collision detection
- Explosions for player and enemies
- Scrolling map background using tile images
- All images are loaded from the `resources/` directory

## How to Run

1. **Requirements:**
   - Java 11 or later
   - JavaFX SDK (download from https://gluonhq.com/products/javafx/)

2. **Compile:**
   - Place all `.gif` image files in the `resources/` directory (already done)
   - Compile with JavaFX libraries. Example (adjust path to your JavaFX SDK):
     ```sh
     javac --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml ComradeFX.java MapData.java
     ```

3. **Run:**
   - Run with JavaFX libraries. Example:
     ```sh
     java --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml ComradeFX
     ```

## Controls
- Arrow keys or WASD: Move player
- Space: Fire

## Project Structure
- `ComradeFX.java` - Main JavaFX game code
- `MapData.java` - Map data and accessors
- `resources/` - All image files (`.gif`)
- `.gitignore` - Ignores `.class` files

## Notes
- The player starts at the center-bottom of the screen.
- Enemies and enemy bullets move at independent speeds from the map scroll.
- Map scrolls upward, simulating forward movement.

---
Original applet code and comments have been translated to English and modernized for JavaFX.

