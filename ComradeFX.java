import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;
import java.util.Random;
import java.io.File;

public class ComradeFX extends Application {
    /**
     * Main game class for Comrade FX.
     * Handles game initialization, main loop, rendering, and input.
     * Uses JavaFX for graphics and input.
     */
    
    // Game states
    public enum GameState {
        TITLE, SELECT, PLAYING, PAUSED, GAME_OVER
    }
    
    private GameState currentState = GameState.TITLE;
    
    // Game engine and entities
    private GameEngine gameEngine;
    
    // Image assets
    private Image mig29, su27, f15, f16, euf, f18, f117, b2, b52, sea, snd, stp, wd;
    private Image explosionImg;
    private Random rnd = new Random();
    
    // Sound system variables
    private MediaPlayer backgroundMusic;
    private AudioClip shootSound;
    private AudioClip explosionSound;
    private AudioClip enemyHitSound;
    private AudioClip enemyHitMediumSound;
    private AudioClip enemyHitLargeSound;
    private AudioClip bossHitSound;
    private AudioClip bossDefeatSound;
    private AudioClip playerHitSound;
    private AudioClip powerupSound;
    private AudioClip bonusSound;
    private AudioClip gameOverSound;
    private AudioClip levelCompleteSound;
    private boolean soundEnabled = true;
    private boolean musicEnabled = true;
    
    // Aircraft selection
    private int selectedAircraft = 0; // 0 = MiG-29, 1 = Su-27

    // Game display size
    private static final int GAME_WIDTH = 360;
    private static final int GAME_HEIGHT = 390;

    // Map scrolling offset
    private int mapScrollY = MapData.getMapLength() * TILE_SIZE - GAME_HEIGHT;
    private static final int TILE_SIZE = 16; // Each map tile is 16x16 pixels
    private static final int MAP_SCROLL_SPEED = 1; // pixels per frame
    private static final int ENEMY_SPEED = 2; // vertical or horizontal enemy speed
    private static final int ENEMY_BULLET_SPEED = 2; // enemy bullet vertical speed (slower)

    @Override
    public void start(Stage primaryStage) {
    /**
     * JavaFX entry point. Sets up the game window, canvas, event handlers, and starts the game loop.
     * @param primaryStage The main window for the JavaFX application.
     */
        Canvas canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        loadImages();
        initializeSound();
        
        // Initialize game engine
        gameEngine = new GameEngine(GAME_WIDTH, GAME_HEIGHT);
        gameEngine.getPlayer().setAircraft(0, mig29); // Default aircraft

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);

        // Key event handlers
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);

        // Game loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGame();
                drawGame(gc);
            }
        };
        timer.start();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Comrade FX");
        primaryStage.show();
        canvas.requestFocus();
    }

    private void loadImages() {
    /**
     * Loads all required image assets for the game from the resources directory.
     * Images include player, enemies, map tiles, and explosion effects.
     */
        // Adjusted file paths to use the resources directory
        mig29 = new Image("file:resources/mig29.gif");
        su27 = new Image("file:resources/su27.gif");
        f15 = new Image("file:resources/f15.gif");
        f16 = new Image("file:resources/f16.gif");
        euf = new Image("file:resources/euf.gif");
        f18 = new Image("file:resources/f18.gif");
        f117 = new Image("file:resources/f117.gif");
        b2 = new Image("file:resources/b2.gif");
        b52 = new Image("file:resources/b52.gif");
        sea = new Image("file:resources/sea.gif");
        snd = new Image("file:resources/snd.gif");
        stp = new Image("file:resources/stp.gif");
        wd = new Image("file:resources/wd.gif");
        explosionImg = new Image("file:resources/explosion.gif");
    }
    
    /**
     * Initializes the sound system with background music and sound effects.
     * Uses placeholder sounds since we don't have actual audio files.
     */
    private void initializeSound() {
        try {
            // Load actual sound files from resources directory
            shootSound = new AudioClip("file:resources/shoot.wav");
            explosionSound = new AudioClip("file:resources/explosion.wav");
            enemyHitSound = new AudioClip("file:resources/enemy_hit.wav");
            enemyHitMediumSound = new AudioClip("file:resources/enemy_hit_medium.wav");
            enemyHitLargeSound = new AudioClip("file:resources/enemy_hit_large.wav");
            bossHitSound = new AudioClip("file:resources/boss_hit.wav");
            bossDefeatSound = new AudioClip("file:resources/boss_defeat.wav");
            playerHitSound = new AudioClip("file:resources/player_hit.wav");
            powerupSound = new AudioClip("file:resources/powerup.wav");
            bonusSound = new AudioClip("file:resources/bonus.wav");
            gameOverSound = new AudioClip("file:resources/game_over.wav");
            levelCompleteSound = new AudioClip("file:resources/level_complete.wav");
            
            // Set volume for sound effects
            if (shootSound != null) shootSound.setVolume(0.3);
            if (explosionSound != null) explosionSound.setVolume(0.4);
            if (enemyHitSound != null) enemyHitSound.setVolume(0.3);
            if (enemyHitMediumSound != null) enemyHitMediumSound.setVolume(0.3);
            if (enemyHitLargeSound != null) enemyHitLargeSound.setVolume(0.3);
            if (bossHitSound != null) bossHitSound.setVolume(0.4);
            if (bossDefeatSound != null) bossDefeatSound.setVolume(0.5);
            if (playerHitSound != null) playerHitSound.setVolume(0.4);
            if (powerupSound != null) powerupSound.setVolume(0.4);
            if (bonusSound != null) bonusSound.setVolume(0.4);
            if (gameOverSound != null) gameOverSound.setVolume(0.5);
            if (levelCompleteSound != null) levelCompleteSound.setVolume(0.4);
            
            // Initialize background music (commented out for now)
            // Media backgroundMedia = new Media(new File("resources/background_music.mp3").toURI().toString());
            // backgroundMusic = new MediaPlayer(backgroundMedia);
            // backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
            // backgroundMusic.setVolume(0.3);
            
            System.out.println("Sound system initialized successfully!");
            
        } catch (Exception e) {
            System.out.println("Sound initialization failed: " + e.getMessage());
            System.out.println("Make sure you have generated the sound files using the Python script!");
            // Continue without sound if files are missing
        }
    }
    
    /**
     * Plays a sound effect if sound is enabled.
     */
    private void playSound(AudioClip sound) {
        if (soundEnabled && sound != null) {
            try {
                sound.play();
            } catch (Exception e) {
                System.out.println("Sound playback error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Starts background music if music is enabled.
     */
    private void startBackgroundMusic() {
        if (musicEnabled && backgroundMusic != null) {
            try {
                backgroundMusic.play();
            } catch (Exception e) {
                // Ignore music errors
            }
        }
    }
    
    /**
     * Stops background music.
     */
    private void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            try {
                backgroundMusic.stop();
            } catch (Exception e) {
                // Ignore music errors
            }
        }
    }

    /**
     * Handles key press events for player movement and firing.
     * @param e The KeyEvent triggered by a key press.
     */
    private void handleKeyPressed(KeyEvent e) {
        switch (currentState) {
            case TITLE:
                if (e.getCode() == KeyCode.S) {
                    currentState = GameState.SELECT;
                }
                break;
                
            case SELECT:
                if (e.getCode() == KeyCode.DIGIT1 || e.getCode() == KeyCode.NUMPAD1) {
                    gameEngine.getPlayer().setAircraft(0, mig29);
                    currentState = GameState.PLAYING;
                    resetGame();
                    startBackgroundMusic();
                    playSound(powerupSound);
                } else if (e.getCode() == KeyCode.DIGIT2 || e.getCode() == KeyCode.NUMPAD2) {
                    gameEngine.getPlayer().setAircraft(1, su27);
                    currentState = GameState.PLAYING;
                    resetGame();
                    startBackgroundMusic();
                    playSound(powerupSound);
                }
                break;
                
            case PLAYING:
                if (e.getCode() == KeyCode.P) {
                    currentState = GameState.PAUSED;
                    stopBackgroundMusic();
                } else if (e.getCode() == KeyCode.M) {
                    // Toggle music
                    musicEnabled = !musicEnabled;
                    if (musicEnabled) {
                        startBackgroundMusic();
                    } else {
                        stopBackgroundMusic();
                    }
                } else if (e.getCode() == KeyCode.N) {
                    // Toggle sound effects
                    soundEnabled = !soundEnabled;
                } else if (!gameEngine.getPlayer().isExploding()) {
                    if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) 
                        gameEngine.getPlayer().setMovingRight(true);
                    if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) 
                        gameEngine.getPlayer().setMovingLeft(true);
                    if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) 
                        gameEngine.getPlayer().setMovingUp(true);
                    if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) 
                        gameEngine.getPlayer().setMovingDown(true);
                    if (e.getCode() == KeyCode.SPACE) {
                        if (gameEngine.firePlayerBullet()) {
                            playSound(shootSound);
                        }
                    }
                }
                break;
                
            case PAUSED:
                if (e.getCode() == KeyCode.S) {
                    currentState = GameState.PLAYING;
                    startBackgroundMusic();
                }
                break;
                
            case GAME_OVER:
                if (e.getCode() == KeyCode.S) {
                    currentState = GameState.SELECT;
                }
                break;
        }
    }

    /**
     * Handles key release events to stop player movement.
     * @param e The KeyEvent triggered by a key release.
     */
    private void handleKeyReleased(KeyEvent e) {
        if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) 
            gameEngine.getPlayer().setMovingRight(false);
        if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) 
            gameEngine.getPlayer().setMovingLeft(false);
        if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) 
            gameEngine.getPlayer().setMovingUp(false);
        if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) 
            gameEngine.getPlayer().setMovingDown(false);
    }

    /**
     * Updates the game state each frame: player/enemy movement, ammo, collisions, and explosions.
     */
    private void updateGame() {
        switch (currentState) {
            case TITLE:
            case SELECT:
            case PAUSED:
            case GAME_OVER:
                // No game logic updates in these states
                break;
                
            case PLAYING:
                updateMapScroll();
                
                // Update game engine
                gameEngine.update(GAME_WIDTH, GAME_HEIGHT);
                
                // Check for game over
                if (!gameEngine.getPlayer().isAlive()) {
                    currentState = GameState.GAME_OVER;
                    playSound(gameOverSound);
                }
                
                // Check for boss defeat
                if (gameEngine.getBoss().isExploding()) {
                    gameEngine.setBossScore(gameEngine.getBossScore());
                    playSound(bossDefeatSound);
                    playSound(levelCompleteSound);
                }
                
                // Play bonus sound for score milestones
                if (gameEngine.getScore() % 1000 == 0 && gameEngine.getScore() > 0) {
                    playSound(bonusSound);
                }
                break;
        }
    }
    
    /**
     * Resets the game state for a new game.
     */
    private void resetGame() {
        // Reset game engine
        gameEngine.resetGame();
        
        // Reset map scroll
        mapScrollY = MapData.getMapLength() * TILE_SIZE - GAME_HEIGHT;
    }



    /**
     * Draws the scrolling map background using the map data and tile images.
     * @param gc The GraphicsContext to draw on.
     */
    private void drawMap(GraphicsContext gc) {
        int mapRows = MapData.getMapLength();
        int mapCols = 24; // MapData is always 24 columns wide
        int tilesX = (int) Math.ceil((double) GAME_WIDTH / TILE_SIZE);
        int tilesY = (int) Math.ceil((double) GAME_HEIGHT / TILE_SIZE) + 1; // +1 for partial tile at bottom
        int mapPixelHeight = mapRows * TILE_SIZE;

        // Calculate which row of the map is at the top of the screen
        int scrollOffset = mapScrollY % mapPixelHeight;
        int firstRow = scrollOffset / TILE_SIZE;
        int yOffset = -(scrollOffset % TILE_SIZE);

        for (int row = 0; row < tilesY; row++) {
            int mapRow = (firstRow + row) % mapRows;
            for (int col = 0; col < tilesX; col++) {
                int tile = MapData.MapDataReturn(mapRow, col % mapCols);
                Image tileImg = null;
                switch (tile) {
                    case 0: tileImg = sea; break;
                    case 1: tileImg = snd; break;
                    case 2: tileImg = stp; break;
                    case 3: tileImg = wd; break;
                }
                if (tileImg != null) {
                    gc.drawImage(tileImg, col * TILE_SIZE, yOffset + row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }

    /**
     * Scrolls the map background by a fixed speed, looping when reaching the end.
     */
    private void updateMapScroll() {
        // Scroll the map by MAP_SCROLL_SPEED pixels per frame
        mapScrollY -= MAP_SCROLL_SPEED;
        int mapPixelHeight = MapData.getMapLength() * TILE_SIZE;
        if (mapScrollY < 0) {
            mapScrollY += mapPixelHeight;
        }
    }

    /**
     * Draws all game elements: map, player, enemies, bullets, explosions, and score.
     * @param gc The GraphicsContext to draw on.
     */
    private void drawGame(GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.BLACK);
        gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        
        switch (currentState) {
            case TITLE:
                drawTitleScreen(gc);
                break;
                
            case SELECT:
                drawSelectScreen(gc);
                break;
                
            case PLAYING:
                drawPlayingScreen(gc);
                break;
                
            case PAUSED:
                drawPlayingScreen(gc);
                drawPauseOverlay(gc);
                break;
                
            case GAME_OVER:
                drawGameOverScreen(gc);
                break;
        }
    }
    
    /**
     * Draws the title screen.
     */
    private void drawTitleScreen(GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.GREEN);
        gc.setFont(javafx.scene.text.Font.font("Arial", 24));
        gc.fillText("COMRADE", GAME_WIDTH / 2 - 60, GAME_HEIGHT / 2 - 50);
        gc.setFont(javafx.scene.text.Font.font("Arial", 16));
        gc.fillText("Press S to Start", GAME_WIDTH / 2 - 60, GAME_HEIGHT / 2);
        gc.setFont(javafx.scene.text.Font.font("Arial", 12));
        gc.fillText("(c) 2003 Y&Y FACTORY", GAME_WIDTH / 2 - 70, GAME_HEIGHT - 30);
    }
    
    /**
     * Draws the aircraft selection screen.
     */
    private void drawSelectScreen(GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.GREEN);
        gc.setFont(javafx.scene.text.Font.font("Arial", 18));
        gc.fillText("SELECT AIRCRAFT", GAME_WIDTH / 2 - 80, 80);
        gc.setFont(javafx.scene.text.Font.font("Arial", 14));
        gc.fillText("1: MIG-29 FULCRUM", GAME_WIDTH / 2 - 80, 120);
        gc.fillText("2: SU-27 FLANKER", GAME_WIDTH / 2 - 80, 140);
        
        // Draw aircraft previews
        if (mig29 != null) {
            gc.drawImage(mig29, GAME_WIDTH / 2 - 100, 160, 40, 40);
        }
        if (su27 != null) {
            gc.drawImage(su27, GAME_WIDTH / 2 + 60, 160, 40, 40);
        }
    }
    
    /**
     * Draws the main playing screen.
     */
    private void drawPlayingScreen(GraphicsContext gc) {
        // Draw the scrolling map background
        drawMap(gc);
        
        // Draw UI
        drawUI(gc);
        
        // Draw player
        gameEngine.getPlayer().render(gc, gameEngine.getPlayer().getImage(), explosionImg);
        
        // Draw player bullets
        for (Bullet bullet : gameEngine.getPlayerBullets()) {
            bullet.render(gc);
        }
        
        // Draw enemy bullets
        for (Bullet bullet : gameEngine.getEnemyBullets()) {
            bullet.render(gc);
        }
        
        // Draw enemies
        for (Enemy enemy : gameEngine.getEnemies()) {
            Image enemyImg = getEnemyImage(enemy.getImageType());
            if (enemy.isActive() && !enemy.isExploding()) {
                enemy.setImage(enemyImg);
            }
            enemy.render(gc, explosionImg);
        }
        
        // Draw boss
        Boss boss = gameEngine.getBoss();
        if (boss.isActive() && !boss.isExploding()) {
            Image bossImg = boss.getBossType() == 0 ? b52 : b2;
            boss.setImage(bossImg);
        }
        boss.render(gc, explosionImg);
    }
    
    /**
     * Gets the enemy image based on enemy type.
     */
    private Image getEnemyImage(int imageType) {
        switch (imageType) {
            case 0: return f15;
            case 1: return f16;
            case 2: return euf;
            case 3: return f18;
            case 4: return f117;
            default: return f15;
        }
    }
    
    /**
     * Draws the UI elements (score, health, etc.).
     */
    private void drawUI(GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 12));
        gc.fillText("Score: " + gameEngine.getScore(), 10, 20);
        gc.fillText("Health: " + gameEngine.getPlayer().getHealth() + "/" + gameEngine.getPlayer().getMaxHealth(), 10, 35);
        gc.fillText("Hit Rate: " + gameEngine.getHitRate() + "%", 10, 50);
        gc.fillText("Level: " + gameEngine.getDifficultyLevel(), 10, 65);
        
        // Boss information
        if (gameEngine.getBoss().isActive()) {
            gc.setFill(javafx.scene.paint.Color.RED);
            gc.fillText("BOSS: " + (gameEngine.getBoss().getBossType() == 0 ? "B-52" : "B-2"), 10, 80);
            gc.fillText("Boss HP: " + (gameEngine.getBoss().getHealth()) + "/" + gameEngine.getBoss().getMaxHealth(), 10, 95);
        } else {
            gc.setFill(javafx.scene.paint.Color.YELLOW);
            gc.fillText("Next Boss: " + (gameEngine.getNextBossThreshold() - gameEngine.getBossCounter()) + " enemies", 10, 80);
        }
        
        // Sound controls
        gc.setFont(javafx.scene.text.Font.font("Arial", 10));
        gc.setFill(musicEnabled ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);
        gc.fillText("Music: " + (musicEnabled ? "ON" : "OFF"), GAME_WIDTH - 80, GAME_HEIGHT - 40);
        gc.setFill(soundEnabled ? javafx.scene.paint.Color.GREEN : javafx.scene.paint.Color.RED);
        gc.fillText("Sound: " + (soundEnabled ? "ON" : "OFF"), GAME_WIDTH - 80, GAME_HEIGHT - 25);
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.fillText("M: Toggle Music", GAME_WIDTH - 80, GAME_HEIGHT - 10);
        gc.fillText("N: Toggle Sound", GAME_WIDTH - 80, GAME_HEIGHT + 5);
        
        // Draw health bar
        int barWidth = 200;
        int barHeight = 10;
        int barX = GAME_WIDTH - barWidth - 10;
        int barY = 10;
        
        // Background
        gc.setFill(javafx.scene.paint.Color.DARKRED);
        gc.fillRect(barX, barY, barWidth, barHeight);
        
        // Health
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillRect(barX, barY, (int)((double)gameEngine.getPlayer().getHealth() / gameEngine.getPlayer().getMaxHealth() * barWidth), barHeight);
    }
    
    /**
     * Draws the pause overlay.
     */
    private void drawPauseOverlay(GraphicsContext gc) {
        // Semi-transparent overlay
        gc.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        
        // Pause text
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 24));
        gc.fillText("PAUSED", GAME_WIDTH / 2 - 50, GAME_HEIGHT / 2);
        gc.setFont(javafx.scene.text.Font.font("Arial", 16));
        gc.fillText("Press S to Resume", GAME_WIDTH / 2 - 70, GAME_HEIGHT / 2 + 30);
        gc.setFont(javafx.scene.text.Font.font("Arial", 14));
        gc.fillText("M: Toggle Music", GAME_WIDTH / 2 - 70, GAME_HEIGHT / 2 + 50);
        gc.fillText("N: Toggle Sound", GAME_WIDTH / 2 - 70, GAME_HEIGHT / 2 + 65);
    }
    
    /**
     * Draws the game over screen.
     */
    private void drawGameOverScreen(GraphicsContext gc) {
        // Semi-transparent overlay
        gc.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.8));
        gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        
        // Game over text and stats
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.setFont(javafx.scene.text.Font.font("Arial", 24));
        gc.fillText("GAME OVER", GAME_WIDTH / 2 - 70, GAME_HEIGHT / 2 - 60);
        
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 14));
        gc.fillText("Final Score: " + gameEngine.getScore(), GAME_WIDTH / 2 - 60, GAME_HEIGHT / 2 - 30);
        gc.fillText("Ammo Fired: " + gameEngine.getAmmoFired(), GAME_WIDTH / 2 - 60, GAME_HEIGHT / 2 - 15);
        gc.fillText("Enemies Hit: " + gameEngine.getEnemiesHit(), GAME_WIDTH / 2 - 60, GAME_HEIGHT / 2);
        gc.fillText("Hit Rate: " + gameEngine.getHitRate() + "%", GAME_WIDTH / 2 - 60, GAME_HEIGHT / 2 + 15);
        
        gc.setFont(javafx.scene.text.Font.font("Arial", 16));
        gc.fillText("Press S to Play Again", GAME_WIDTH / 2 - 80, GAME_HEIGHT / 2 + 50);
    }



    /**
     * Main method. Launches the JavaFX application.
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
