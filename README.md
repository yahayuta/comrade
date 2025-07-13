# Comrade FX

A modernized JavaFX remake of the classic Java Applet-based shooting game "Comrade" with enhanced features including boss battles, sound effects, and improved gameplay mechanics.

![Screenshot](resources/screenshot.png)

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://openjdk.java.net/)
[![JavaFX](https://img.shields.io/badge/JavaFX-24.0.1-blue.svg)](https://openjfx.io/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

## 🎮 Features

### Core Gameplay
- **Vertical Scrolling Shooter**: Classic arcade-style gameplay
- **Multiple Aircraft**: Choose between MiG-29 Fulcrum and Su-27 Flanker
- **Dynamic Enemies**: Various enemy types with different movement patterns
- **Variety of Enemy Movement Patterns**: Enemies can move straight, zigzag, circle, dive, or hover, making each encounter unique and challenging
- **Progressive Difficulty**: Game gets harder as you score more points, with faster and more aggressive enemies
- **Scrolling Terrain**: Multiple terrain types (sea, land, forest, mountains)
- **Collision Detection**: Precise hit detection for bullets and aircraft
- **Power-Up System**: Collectible power-ups with various effects
- **High Score System**: Persistent score tracking with player names
- **Settings System**: Configurable game options and preferences

### Boss Battles
- **B-52 Bomber**: Large, slow-moving boss with high HP
- **B-2 Stealth Bomber**: Smaller, faster boss with unique patterns
- **Progressive Difficulty**: Bosses appear after defeating 50 enemies (increases each time)
- **Boss Health System**: 50 hits required to defeat each boss
- **Boss Rewards**: 2000+ points for defeating bosses

### Sound System
- **Sound Effects**: Shooting, explosions, enemy hits, boss hits, player damage
- **Audio Controls**: Toggle sound effects and music independently
- **Volume Control**: Appropriate volume levels for each sound type
- **Generated Audio**: Python-generated sound files for consistent quality

### Game States & UI
- **Title Screen**: Game introduction and start menu
- **Aircraft Selection**: Choose your fighter with preview
- **Pause System**: Pause/resume functionality
- **Game Over Screen**: Final statistics and restart option
- **Health System**: Player health bar and damage tracking
- **Score Tracking**: Real-time score, hit rate, and statistics
- **Settings Screen**: Configurable game options
- **High Scores Screen**: Persistent score tracking

## 🚀 Getting Started

### Prerequisites

- **Java 11 or later** ([Download here](https://adoptium.net/))
- **JavaFX SDK 24.0.1** ([Download here](https://gluonhq.com/products/javafx/))

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/comrade-fx.git
   cd comrade-fx
   ```

2. **Generate sound files** (optional but recommended):
   ```bash
   pip install numpy scipy
   # For classic-style sounds:
   python generate_sounds.py
   # For authentic Xevious-style sounds:
   python generate_xevious_sounds.py
   ```

3. **Set up JavaFX**:
   - Download JavaFX SDK 24.0.1
   - Extract to `C:\javafx-sdk-24.0.1\` (Windows)
   - Update paths in `compile.bat` and `run.bat` if needed

4. **Compile and run**:
   ```bash
   # Windows
   compile.bat
   run.bat
   
   # Linux/Mac
   javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml,javafx.media *.java
   java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml,javafx.media ComradeFX
   ```

## 🎯 Controls

### Gameplay
- **Arrow Keys** or **WASD**: Move aircraft
- **Space**: Fire bullets
- **P**: Pause/Resume game
- **M**: Toggle background music
- **N**: Toggle sound effects

### Menu Navigation
- **S**: Start game / Resume from pause / Restart after game over
- **1**: Select MiG-29 Fulcrum
- **2**: Select Su-27 Flanker
- **H**: View high scores
- **O**: Open settings
- **ESC**: Return to title from settings/high scores

## 🗺️ Map System

The game features a dynamic scrolling map with different terrain types:

| Tile Type | Terrain | Description |
|-----------|---------|-------------|
| `0` | Sea | Ocean tiles |
| `1` | Land/Sand | Desert terrain |
| `2` | Forest/Steppe | Grassland areas |
| `3` | Mountain/Wooded | Mountainous regions |

The map scrolls upward infinitely and tiles scale to fit the display size.

## 🎵 Sound System

### Generated Sound Effects
You can generate two sets of sound effects:

- **Classic Style** (`generate_sounds.py`):
  - `shoot.wav`: High-pitched beep for firing
  - `explosion.wav`: Noise burst for explosions
  - `enemy_hit.wav`: Medium beep for enemy hits
  - `boss_hit.wav`: Lower beep for boss hits
  - `boss_defeat.wav`: Descending tone for boss defeat
  - `player_hit.wav`: Harsh noise for player damage

- **Xevious Style** (`generate_xevious_sounds.py`):
  - `shoot.wav`: Xevious-style main weapon sound
  - `enemy_hit.wav`: Small enemy destruction
  - `enemy_hit_medium.wav`: Medium enemy destruction
  - `enemy_hit_large.wav`: Large enemy destruction
  - `boss_hit.wav`: Xevious-style boss hit (descending tone)
  - `boss_defeat.wav`: Xevious-style boss defeat (dramatic descending tone)
  - `player_hit.wav`: Xevious-style player hit (harsh filtered noise)
  - `explosion.wav`: Xevious-style explosion (filtered noise)
  - `powerup.wav`: Power-up (ascending tone)
  - `bonus.wav`: Bonus points (chord)
  - `game_over.wav`: Game over (descending chord)
  - `level_complete.wav`: Level complete (ascending chord)

> **Tip:** Run only one of the scripts to generate your preferred sound set. The game will use whatever `.wav` files are present in the `resources/` directory.

### Sound Integration
The game intelligently uses different sounds based on game events:

- **Enemy Destruction**: Different sounds for small (F-15/F-16), medium (F-18/F-117), and large (EUF) enemies
- **Boss Battles**: Special hit and defeat sounds with level completion fanfare
- **Score Milestones**: Bonus sound plays every 1000 points
- **Aircraft Selection**: Power-up sound when choosing your fighter
- **Game Over**: Dramatic sound when player health reaches zero

### Customization
You can replace the generated sound files with your own `.wav` files in the `resources/` directory. The game will automatically load them on startup.

## 🏗️ Project Structure

```
comrade-fx/
├── ComradeFX.java          # Main JavaFX game application
├── GameEngine.java         # Core game logic and entity management
├── Player.java             # Player aircraft and power-up system
├── Enemy.java              # Enemy aircraft with movement patterns
├── Boss.java               # Boss battles and mechanics
├── Bullet.java             # Bullet physics and rendering
├── PowerUp.java            # Power-up system and effects
├── HighScoreManager.java   # High score persistence
├── GameSettings.java       # Settings management
├── MapData.java            # Map data and terrain definitions
├── comrade.java            # Original Java Applet version (legacy)
├── comrade.html            # HTML wrapper for applet version
├── generate_sounds.py      # Python script to generate sound files
├── generate_xevious_sounds.py # Python script to generate Xevious-style sound files
├── resources/              # Game assets
│   ├── *.gif              # Aircraft and terrain images
│   └── *.wav              # Sound effects
├── compile.bat             # Windows compilation script
├── run.bat                 # Windows execution script
├── LICENSE                 # MIT License
└── README.md              # This file
```

## 🔧 Development

### Building from Source

1. **Install dependencies**:
   ```bash
   # Install Python packages for sound generation
   pip install numpy scipy
   ```

2. **Generate assets**:
   ```bash
   python generate_sounds.py
   python generate_xevious_sounds.py
   ```

3. **Compile**:
   ```bash
   # Windows
   compile.bat
   
   # Manual compilation
   javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml,javafx.media *.java
   ```

### Adding New Features

The codebase is structured for easy extension:

- **New Enemies**: Add to `Enemy.java` movement patterns
- **New Bosses**: Extend `Boss.java` boss types
- **New Power-Ups**: Add to `PowerUp.java` power-up types
- **New Sounds**: Add to `initializeSound()` method
- **New Terrain**: Update `MapData.java` and `drawMap()` method
- **New Settings**: Extend `GameSettings.java` configuration options

## 🎮 Game Mechanics

### Power-Up System
- **Rapid Fire (RF)**: Increases fire rate for 10 seconds
- **Triple Shot (TS)**: Fires 3 bullets in a spread pattern for 10 seconds
- **Shield (SH)**: Provides temporary invincibility for 10 seconds
- **Health (HP)**: Restores 25 HP immediately
- **Score Bonus (SB)**: Adds 500 points immediately
- **Spawn Rate**: Power-ups spawn randomly with 1/200 chance per frame
- **Visual Effects**: Animated power-ups with color-coded indicators
- **UI Display**: Active power-ups shown on screen with colored text

### High Score System
- **Persistent Storage**: Scores saved to `highscores.dat` file
- **Top 10 Scores**: Maintains list of highest scores
- **Player Names**: Customizable player names for score entries
- **Date Tracking**: Automatic date stamps for each score
- **Automatic Detection**: Game checks for new high scores on game over
- **High Score Screen**: Dedicated screen showing all top scores

### Settings System
- **Persistent Settings**: Configuration saved to `gamesettings.properties`
- **Player Name**: Customizable player name
- **Sound Controls**: Toggle sound effects and music independently
- **Volume Controls**: Adjustable sound and music volume (0-100%)
- **Difficulty Settings**: Configurable starting difficulty level
- **Display Options**: Toggle FPS counter and hitbox display
- **Settings Screen**: Dedicated screen for all configuration options

### Enemy Movement Patterns
- **Straight Down**: Enemies move directly downward from the top
- **Straight Right/Left**: Enemies enter from the left or right and move horizontally
- **Zigzag**: Enemies move in a zigzag pattern, changing direction periodically
- **Circle**: Enemies move in a circular path while descending
- **Dive**: Enemies dive toward the center of the screen after reaching a certain height
- **Hover**: Enemies hover horizontally, occasionally changing direction and jittering vertically

### Scoring System
- **Regular Enemy**: 100 points
- **Boss Defeat**: 2000+ points (increases each time)
- **Power-Up Score Bonus**: 500 points
- **Hit Rate**: Percentage of successful shots
- **High Score Tracking**: Persistent score storage with player names

### Health System
- **Player Health**: 100 HP
- **Enemy Bullet Damage**: 10 HP
- **Boss Collision Damage**: 20 HP
- **Health Bar**: Visual indicator in top-right corner
- **Health Power-Up**: Restores 25 HP when collected
- **Shield Protection**: Temporary invincibility prevents all damage

### Boss System
- **Spawn Trigger**: Every 50 enemies defeated
- **Boss Health**: 50 hits required
- **Movement Pattern**: Side-to-side after entering screen
- **Bullet Firing**: Uses bullets 3-6 (separate from regular enemies)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the [MIT License](LICENSE) - see the LICENSE file for details.

## 🙏 Acknowledgments

- **Original Game**: Y&Y Factory (2003)
- **JavaFX**: Modern Java graphics framework
- **Sound Generation**: Python with NumPy/SciPy
- **Game Assets**: Preserved from original applet version

## 💾 Data Persistence

The game automatically saves and loads data files:

### High Scores (`highscores.dat`)
- **Format**: Serialized Java objects
- **Content**: Top 10 scores with player names and dates
- **Location**: Game directory
- **Backup**: File is automatically created if missing

### Settings (`gamesettings.properties`)
- **Format**: Java Properties file
- **Content**: All game configuration options
- **Location**: Game directory
- **Default Values**: Automatically set if file is missing

### File Structure
```
comrade-fx/
├── *.java                  # Source code files
├── *.class                 # Compiled Java classes
├── resources/              # Game assets
├── highscores.dat          # High score data (auto-generated)
├── gamesettings.properties # Settings data (auto-generated)
├── compile.bat             # Windows compilation script
├── run.bat                 # Windows execution script
└── README.md              # This file
```

## 📊 Game Statistics

The game tracks various statistics:
- **Score**: Total points earned
- **Ammo Fired**: Total bullets shot
- **Enemies Hit**: Successful hits on enemies
- **Hit Rate**: Accuracy percentage
- **Boss Counter**: Enemies defeated toward next boss

---

**Enjoy the game!** 🎮✈️💥

> *"Comrade FX - Where classic arcade meets modern JavaFX"*

