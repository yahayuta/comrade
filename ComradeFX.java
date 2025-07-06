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
    
    // Game variables (adapted from original applet)
    private int myX = (GAME_WIDTH - 20) / 2;
    private int myY = GAME_HEIGHT - 40;
    private int[] enemX = new int[3], enemY = new int[3];
    private int[] ammoX1 = new int[5], ammoY1 = new int[5], ammoX2 = new int[5], ammoY2 = new int[5];
    private boolean[] ammoCond = new boolean[5];
    private boolean[] enemCond = new boolean[3];
    private Image me, mig29, su27, f15, f16, euf, f18, f117, b2, b52, sea, snd, stp, wd;
    private Random rnd = new Random();
    private boolean MOVUP = false, MOVDOWN = false, MOVRIGHT = false, MOVLEFT = false;
    private int enemCount = 0;
    private int[] enmovX = new int[3], enmovY = new int[3];
    private int[] enemovtype = new int[3];
    private int enemtype;
    private int[] enemImgType = new int[3];
    // Enemy bullet variables
    private int[] enamX = new int[6], enamY = new int[6], enamXmov = new int[6], enamYmov = new int[6];
    private boolean[] enamCond = new boolean[6];
    // Explosion variables
    private int[] enexX = new int[3], enexY = new int[3], enexTimer = new int[3];
    private boolean[] enexCond = new boolean[3];
    private int[] myexX = new int[1], myexY = new int[1], myexTimer = new int[1];
    private boolean myexCond = false;
    private Image explosionImg;
    // Score and game statistics
    private int score = 0;
    private int ammoFired = 0;
    private int enemiesHit = 0;
    private int hitRate = 0;
    private int playerHealth = 100;
    private int maxHealth = 100;
    
    // Boss system variables
    private int bossX, bossY; // boss coordinates
    private int bossExplosionX, bossExplosionY, bossExplosionTimer; // boss explosion coordinates
    private int bossHitCount = 0; // boss hit counter
    private int nextBossThreshold = 50; // enemies to defeat before boss spawns
    private int bossCounter = 0; // counter for defeated enemies
    private int bossType; // 0 = B-52, 1 = B-2
    private int bossDirection = 0; // boss movement direction
    private int bossScore = 2000; // score for defeating boss
    private boolean bossActive = false; // boss active flag
    private boolean bossExplosionActive = false; // boss explosion flag
    private boolean bossSmokeActive = false; // boss smoke flag
    private Image bossImage; // current boss image
    
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
        me = mig29; // Default aircraft

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
                    selectedAircraft = 0;
                    me = mig29;
                    currentState = GameState.PLAYING;
                    resetGame();
                    startBackgroundMusic();
                    playSound(powerupSound);
                } else if (e.getCode() == KeyCode.DIGIT2 || e.getCode() == KeyCode.NUMPAD2) {
                    selectedAircraft = 1;
                    me = su27;
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
                } else if (!myexCond) {
                    if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) MOVRIGHT = true;
                    if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) MOVLEFT = true;
                    if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) MOVUP = true;
                    if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) MOVDOWN = true;
                    if (e.getCode() == KeyCode.SPACE) fireAmmo();
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
        if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) MOVRIGHT = false;
        if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) MOVLEFT = false;
        if (e.getCode() == KeyCode.UP || e.getCode() == KeyCode.W) MOVUP = false;
        if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.S) MOVDOWN = false;
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
                // Simple movement logic (scaled for new size)
                int moveStep = 4;
                if (MOVUP) myY -= moveStep;
                if (MOVDOWN) myY += moveStep;
                if (MOVRIGHT) myX += moveStep;
                if (MOVLEFT) myX -= moveStep;
                if (myX < 0) myX = 0;
                if (myX > GAME_WIDTH - 20) myX = GAME_WIDTH - 20;
                if (myY < 0) myY = 0;
                if (myY > GAME_HEIGHT - 20) myY = GAME_HEIGHT - 20;
                // Update ammo positions
                for (int i = 0; i < 5; i++) {
                    if (ammoCond[i]) {
                        ammoY1[i] -= 2; // slower bullet speed
                        ammoY2[i] -= 2;
                        if (ammoY1[i] < 0 || ammoY2[i] < 0) ammoCond[i] = false;
                    }
                }
                // Enemy logic
                makeEnemy();
                moveEnemy();
                enemyFire();
                moveEnemyBullets();
                
                // Boss logic
                makeBoss();
                updateBoss();
                bossFire();
                checkBossCollisions();
                updateBossExplosion();
                
                checkCollisions();
                checkPlayerBossCollision();
                updateExplosions();
                break;
        }
    }
    
    /**
     * Resets the game state for a new game.
     */
    private void resetGame() {
        // Reset player position
        myX = (GAME_WIDTH - 20) / 2;
        myY = GAME_HEIGHT - 40;
        
        // Reset game statistics
        score = 0;
        ammoFired = 0;
        enemiesHit = 0;
        hitRate = 0;
        playerHealth = maxHealth;
        
        // Reset movement flags
        MOVUP = false;
        MOVDOWN = false;
        MOVRIGHT = false;
        MOVLEFT = false;
        
        // Reset player explosion
        myexCond = false;
        
        // Reset enemies
        for (int i = 0; i < 3; i++) {
            enemCond[i] = false;
            enexCond[i] = false;
        }
        
        // Reset bullets
        for (int i = 0; i < 5; i++) {
            ammoCond[i] = false;
        }
        for (int i = 0; i < 6; i++) {
            enamCond[i] = false;
        }
        
        // Reset map scroll
        mapScrollY = MapData.getMapLength() * TILE_SIZE - GAME_HEIGHT;
        enemCount = 0;
        
        // Reset boss variables
        bossActive = false;
        bossExplosionActive = false;
        bossSmokeActive = false;
        bossCounter = 0;
        bossHitCount = 0;
        nextBossThreshold = 50;
        bossScore = 2000;
        bossDirection = 0;
    }

    /**
     * Handles enemy firing logic. Enemies shoot bullets toward the player.
     */
    private void enemyFire() {
        for (int i = 0; i < 3; i++) {
            if (enemCond[i] && enemY[i] > 10 && !enamCond[i]) {
                enamX[i] = enemX[i] + 10;
                enamY[i] = enemY[i] + 25;
                enamXmov[i] = (myX + 10 - enamX[i]) / 35;
                enamYmov[i] = ENEMY_BULLET_SPEED;
                enamCond[i] = true;
            }
        }
    }

    /**
     * Updates the position of enemy bullets and removes them if they leave the screen.
     */
    private void moveEnemyBullets() {
        for (int i = 0; i < 6; i++) {
            if (enamCond[i]) {
                enamX[i] += enamXmov[i];
                enamY[i] += enamYmov[i]; // Use full speed, not divided
                if (enamX[i] > GAME_WIDTH - 2 || enamY[i] > GAME_HEIGHT - 5 || enamX[i] < 0 || enamY[i] < 0) {
                    enamCond[i] = false;
                }
            }
        }
    }

    /**
     * Checks for collisions between player, enemies, and bullets. Handles explosions and scoring.
     */
    private void checkCollisions() {
        // Player hit by enemy bullet
        if (!myexCond) {
            for (int i = 0; i < 6; i++) {
                if (enamCond[i]) {
                    if (enamX[i] > myX && enamX[i] < myX + 19 && enamY[i] > myY && enamY[i] < myY + 19) {
                        enamCond[i] = false;
                        playerHealth -= 10; // Reduce health when hit
                        playSound(playerHitSound);
                        if (playerHealth <= 0) {
                            // Game over
                            myexX[0] = myX;
                            myexY[0] = myY;
                            myexTimer[0] = 0;
                            myexCond = true;
                            playSound(gameOverSound);
                            currentState = GameState.GAME_OVER;
                        }
                    }
                }
            }
        }
        // Player bullet hits enemy
        for (int i = 0; i < 5; i++) {
            if (ammoCond[i]) {
                for (int j = 0; j < 3; j++) {
                    if (enemCond[j] && !enexCond[j]) {
                        if ((ammoX1[i] > enemX[j] && ammoX1[i] < enemX[j] + 20 && ammoY1[i] > enemY[j] && ammoY1[i] < enemY[j] + 25) ||
                            (ammoX2[i] > enemX[j] && ammoX2[i] < enemX[j] + 20 && ammoY2[i] > enemY[j] && ammoY2[i] < enemY[j] + 25)) {
                            enexX[j] = enemX[j];
                            enexY[j] = enemY[j];
                            enexTimer[j] = 0;
                            enexCond[j] = true;
                            enemCond[j] = false;
                            ammoCond[i] = false;
                            score += 100; // Add score for defeating enemy
                            enemiesHit++;
                            bossCounter++; // Increment boss counter
                            
                            // Play bonus sound for score milestones
                            if (score % 1000 == 0 && score > 0) {
                                playSound(bonusSound);
                            }
                            
                            // Play different sounds based on enemy type
                            if (enemImgType[j] == 0 || enemImgType[j] == 1) {
                                // Small enemies (F-15, F-16)
                                playSound(enemyHitSound);
                            } else if (enemImgType[j] == 2 || enemImgType[j] == 3) {
                                // Medium enemies (F-18, F-117)
                                playSound(enemyHitMediumSound);
                            } else {
                                // Large enemies (EUF)
                                playSound(enemyHitLargeSound);
                            }
                            
                            // Update hit rate
                            if (ammoFired > 0) {
                                hitRate = (enemiesHit * 100) / ammoFired;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates explosion animations for both player and enemies.
     * Resets player position after explosion ends.
     */
    private void updateExplosions() {
        // Enemy explosions
        for (int i = 0; i < 3; i++) {
            if (enexCond[i]) {
                enexTimer[i]++;
                if (enexTimer[i] > 15) {
                    enexCond[i] = false;
                }
            }
        }
        // Player explosion
        if (myexCond) {
            myexTimer[0]++;
            if (myexTimer[0] > 20) {
                myexCond = false;
                // Only reset position if not game over
                if (currentState == GameState.PLAYING) {
                    myX = (GAME_WIDTH - 20) / 2;
                    myY = GAME_HEIGHT - 40;
                }
            }
        }
    }

    /**
     * Spawns new enemies at random positions and movement types after a cooldown.
     */
    private void makeEnemy() {
        enemCount++;
        if ((!enemCond[0]) && (!enemCond[1]) && (!enemCond[2]) && (enemCount > 60)) {
            enemCount = 0;
            for (int i = 0; i < 3; i++) {
                if (!enemCond[i]) {
                    enemCond[i] = true;
                    enemImgType[i] = Math.abs(rnd.nextInt()) % 5;
                    enemtype = enemImgType[i];
                    enemovtype[i] = Math.abs(rnd.nextInt()) % 3; // 0: top, 1: left, 2: right
                    if (enemovtype[i] == 0) { // Spawn from top
                        enemY[i] = 0;
                        enemX[i] = rnd.nextInt(GAME_WIDTH - 20);
                        enmovX[i] = 0;
                        enmovY[i] = ENEMY_SPEED;
                    } else if (enemovtype[i] == 1) { // Spawn from left
                        enemX[i] = 0;
                        enemY[i] = rnd.nextInt(GAME_HEIGHT / 2);
                        enmovX[i] = ENEMY_SPEED;
                        enmovY[i] = 0;
                    } else { // Spawn from right
                        enemX[i] = GAME_WIDTH - 20;
                        enemY[i] = rnd.nextInt(GAME_HEIGHT / 2);
                        enmovX[i] = -ENEMY_SPEED;
                        enmovY[i] = 0;
                    }
                }
            }
        } else if (enemCount > 60) {
            enemCount = 0;
        }
    }

    /**
     * Moves active enemies according to their movement type and removes them if off-screen.
     */
    private void moveEnemy() {
        for (int i = 0; i < 3; i++) {
            if (enemCond[i]) {
                enemX[i] += enmovX[i];
                enemY[i] += enmovY[i]; // Use full speed, not divided
                if ((enemY[i] > GAME_HEIGHT - 15) || (enemX[i] > GAME_WIDTH - 20) || (enemX[i] < 0) || (enemY[i] < 0)) {
                    enemCond[i] = false;
                }
            }
        }
    }
    
    /**
     * Creates a boss when enough enemies have been defeated.
     */
    private void makeBoss() {
        if (bossCounter >= nextBossThreshold && !bossActive) {
            bossType = Math.abs(rnd.nextInt()) % 2;
            
            if (bossType == 0) {
                // B-52
                bossX = GAME_WIDTH / 2 - 20;
                bossY = -35;
                bossImage = b52;
            } else {
                // B-2
                bossX = GAME_WIDTH / 2 - 17;
                bossY = -20;
                bossImage = b2;
            }
            
            bossActive = true;
            bossHitCount = 0;
            nextBossThreshold += 50; // Increase threshold for next boss
        }
    }
    
    /**
     * Updates boss position and movement.
     */
    private void updateBoss() {
        if (bossActive) {
            if (bossY < 20) {
                bossY++; // Move down to screen
            } else {
                // Side-to-side movement
                if (bossDirection == 0) {
                    bossX++;
                    if (bossX > GAME_WIDTH - 40) {
                        bossDirection = 1;
                    }
                } else {
                    bossX--;
                    if (bossX < 0) {
                        bossDirection = 0;
                    }
                }
            }
        }
    }
    
    /**
     * Boss fires bullets at the player.
     */
    private void bossFire() {
        if (bossActive && bossY > 19) {
            for (int i = 3; i < 6; i++) {
                if (!enamCond[i]) {
                    if (bossType == 0) {
                        // B-52 bullet
                        enamX[i] = bossX + 19;
                        enamY[i] = bossY + 35;
                    } else {
                        // B-2 bullet
                        enamX[i] = bossX + 16;
                        enamY[i] = bossY + 20;
                    }
                    enamXmov[i] = (int)((Math.abs(rnd.nextInt()) % GAME_WIDTH) / 40);
                    enamYmov[i] = 3;
                    enamCond[i] = true;
                    break;
                }
            }
        }
    }
    
    /**
     * Checks for collisions between player bullets and boss.
     */
    private void checkBossCollisions() {
        if (bossActive) {
            for (int i = 0; i < 5; i++) {
                if (ammoCond[i]) {
                    boolean hit = false;
                    
                    if (bossType == 0) {
                        // B-52 collision box
                        if ((ammoX1[i] > bossX && ammoX1[i] < bossX + 40 && ammoY1[i] > bossY && ammoY1[i] < bossY + 35) ||
                            (ammoX2[i] > bossX && ammoX2[i] < bossX + 40 && ammoY2[i] > bossY && ammoY2[i] < bossY + 35)) {
                            hit = true;
                        }
                    } else {
                        // B-2 collision box
                        if ((ammoX1[i] > bossX && ammoX1[i] < bossX + 35 && ammoY1[i] > bossY && ammoY1[i] < bossY + 20) ||
                            (ammoX2[i] > bossX && ammoX2[i] < bossX + 35 && ammoY2[i] > bossY && ammoY2[i] < bossY + 20)) {
                            hit = true;
                        }
                    }
                    
                    if (hit) {
                        ammoCond[i] = false;
                        bossHitCount++;
                        enemiesHit++;
                        playSound(bossHitSound);
                        // Update hit rate
                        if (ammoFired > 0) {
                            hitRate = (enemiesHit * 100) / ammoFired;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Updates boss explosion animation and handles boss defeat.
     */
    private void updateBossExplosion() {
        if (bossActive && bossHitCount >= 50) {
            // Boss defeated
            bossActive = false;
            bossExplosionActive = true;
            bossExplosionX = bossX;
            bossExplosionY = bossY;
            bossExplosionTimer = 0;
            
            // Clear boss bullets
            for (int i = 3; i < 6; i++) {
                enamCond[i] = false;
            }
            
            // Award score
            score += bossScore;
            bossScore += 1000; // Increase score for next boss
            
            playSound(bossDefeatSound);
            playSound(levelCompleteSound);
            bossHitCount = 0;
        }
        
        if (bossExplosionActive) {
            bossExplosionTimer++;
            if (bossExplosionTimer > 10) {
                bossSmokeActive = true;
            }
            if (bossExplosionTimer > 20) {
                bossExplosionActive = false;
                bossSmokeActive = false;
            }
        }
    }
    
    /**
     * Checks for collision between player and boss.
     */
    private void checkPlayerBossCollision() {
        if (bossActive && !myexCond) {
            boolean collision = false;
            
            if (bossType == 0) {
                // B-52 collision
                if ((myX > bossX && myX < bossX + 40 && myY > bossY && myY < bossY + 35) ||
                    (bossX > myX && bossX < myX + 19 && myY > bossY && myY < bossY + 35) ||
                    (myX > bossX && myX < bossX + 40 && myY + 19 > bossY && myY < bossY) ||
                    (bossX > myX && bossX < myX + 19 && myY + 19 > bossY && myY < bossY)) {
                    collision = true;
                }
            } else {
                // B-2 collision
                if ((myX > bossX && myX < bossX + 35 && myY > bossY && myY < bossY + 20) ||
                    (bossX > myX && bossX < myX + 19 && myY > bossY && myY < bossY + 20) ||
                    (myX > bossX && myX < bossX + 35 && myY + 19 > bossY && myY < bossY) ||
                    (bossX > myX && bossX < myX + 19 && myY + 19 > bossY && myY < bossY)) {
                    collision = true;
                }
            }
            
            if (collision) {
                playerHealth -= 20; // Boss collision does more damage
                playSound(playerHitSound);
                if (playerHealth <= 0) {
                    myexX[0] = myX;
                    myexY[0] = myY;
                    myexTimer[0] = 0;
                    myexCond = true;
                    currentState = GameState.GAME_OVER;
                }
            }
        }
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
        if (!myexCond) {
            gc.drawImage(me, myX, myY);
        } else {
            gc.drawImage(explosionImg, myexX[0], myexY[0]);
        }
        
        // Draw ammo
        gc.setFill(javafx.scene.paint.Color.YELLOW);
        for (int i = 0; i < 5; i++) {
            if (ammoCond[i]) {
                gc.fillRect(ammoX1[i], ammoY1[i], 4, 20);
                gc.fillRect(ammoX2[i], ammoY2[i], 4, 20);
            }
        }
        
        // Draw enemy bullets
        gc.setFill(javafx.scene.paint.Color.PINK);
        for (int i = 0; i < 6; i++) {
            if (enamCond[i]) {
                gc.fillRect(enamX[i], enamY[i], 4, 10);
            }
        }
        
        // Draw enemies with images or explosions
        for (int i = 0; i < 3; i++) {
            if (enexCond[i]) {
                gc.drawImage(explosionImg, enexX[i], enexY[i]);
            } else if (enemCond[i]) {
                Image enemyImg = null;
                switch (enemImgType[i]) {
                    case 0: enemyImg = f15; break;
                    case 1: enemyImg = f16; break;
                    case 2: enemyImg = euf; break;
                    case 3: enemyImg = f18; break;
                    case 4: enemyImg = f117; break;
                }
                if (enemyImg != null) {
                    gc.drawImage(enemyImg, enemX[i], enemY[i]);
                } else {
                    gc.setFill(javafx.scene.paint.Color.RED);
                    gc.fillRect(enemX[i], enemY[i], 40, 50);
                }
            }
        }
        
        // Draw boss
        if (bossActive) {
            gc.drawImage(bossImage, bossX, bossY);
        } else if (bossExplosionActive) {
            gc.drawImage(explosionImg, bossExplosionX, bossExplosionY);
        }
    }
    
    /**
     * Draws the UI elements (score, health, etc.).
     */
    private void drawUI(GraphicsContext gc) {
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 12));
        gc.fillText("Score: " + score, 10, 20);
        gc.fillText("Health: " + playerHealth + "/" + maxHealth, 10, 35);
        gc.fillText("Hit Rate: " + hitRate + "%", 10, 50);
        
        // Boss information
        if (bossActive) {
            gc.setFill(javafx.scene.paint.Color.RED);
            gc.fillText("BOSS: " + (bossType == 0 ? "B-52" : "B-2"), 10, 65);
            gc.fillText("Boss HP: " + (50 - bossHitCount) + "/50", 10, 80);
        } else {
            gc.setFill(javafx.scene.paint.Color.YELLOW);
            gc.fillText("Next Boss: " + (nextBossThreshold - bossCounter) + " enemies", 10, 65);
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
        gc.fillRect(barX, barY, (int)((double)playerHealth / maxHealth * barWidth), barHeight);
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
        gc.fillText("Final Score: " + score, GAME_WIDTH / 2 - 60, GAME_HEIGHT / 2 - 30);
        gc.fillText("Ammo Fired: " + ammoFired, GAME_WIDTH / 2 - 60, GAME_HEIGHT / 2 - 15);
        gc.fillText("Enemies Hit: " + enemiesHit, GAME_WIDTH / 2 - 60, GAME_HEIGHT / 2);
        gc.fillText("Hit Rate: " + hitRate + "%", GAME_WIDTH / 2 - 60, GAME_HEIGHT / 2 + 15);
        
        gc.setFont(javafx.scene.text.Font.font("Arial", 16));
        gc.fillText("Press S to Play Again", GAME_WIDTH / 2 - 80, GAME_HEIGHT / 2 + 50);
    }

    /**
     * Fires player ammo if available, initializing bullet positions.
     */
    private void fireAmmo() {
        for (int i = 0; i < 5; i++) {
            if (!ammoCond[i]) {
                ammoCond[i] = true;
                ammoX1[i] = myX + 2;
                ammoX2[i] = myX + 16;
                ammoY1[i] = myY;
                ammoY2[i] = myY;
                ammoFired++;
                playSound(shootSound);
                break;
            }
        }
    }

    /**
     * Main method. Launches the JavaFX application.
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
