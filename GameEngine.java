import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.AudioClip;
import java.util.Random;

/**
 * GameEngine class manages the core game logic, entities, and state.
 * Separates game logic from rendering and input handling.
 */
public class GameEngine {
    // Game entities
    private Player player;
    private Enemy[] enemies;
    private Boss boss;
    private Bullet[] playerBullets;
    private Bullet[] enemyBullets;
    private PowerUp[] powerUps;
    
    // Game state
    private int score;
    private int ammoFired;
    private int enemiesHit;
    private int hitRate;
    private int bossCounter;
    private int nextBossThreshold;
    private int bossScore;
    private int enemCount;
    
    // Game constants
    private static final int MAX_ENEMIES = 5; // Increased from 3 to 5
    private static final int MAX_PLAYER_BULLETS = 5;
    private static final int MAX_ENEMY_BULLETS = 8; // Increased from 6 to 8
    private static final int ENEMY_SPAWN_DELAY = 45; // Reduced from 60 to 45 for more frequent spawning
    private static final int INITIAL_BOSS_THRESHOLD = 50;
    private static final int MAX_POWER_UPS = 3;
    private static final int POWER_UP_SPAWN_CHANCE = 200; // 1 in 200 chance per frame
    
    // Enemy firing control
    private int enemyFireCooldown = 0;
    private static final int ENEMY_FIRE_COOLDOWN_MIN = 30; // Minimum frames between enemy shots
    private static final int ENEMY_FIRE_COOLDOWN_MAX = 120; // Maximum frames between enemy shots
    
    // Difficulty progression
    private int difficultyLevel = 1;
    private static final int DIFFICULTY_INCREASE_SCORE = 1000; // Increase difficulty every 1000 points
    
    // Power-up spawning
    private int powerUpSpawnCounter = 0;
    
    // High score system
    private HighScoreManager highScoreManager;
    
    // Game settings
    private GameSettings gameSettings;
    
    // Random number generator
    private Random random;
    
    public GameEngine(int gameWidth, int gameHeight) {
        this.random = new Random();
        
        // Initialize entities
        this.player = new Player((gameWidth - 20) / 2, gameHeight - 40, 100);
        this.enemies = new Enemy[MAX_ENEMIES];
        for (int i = 0; i < MAX_ENEMIES; i++) {
            this.enemies[i] = new Enemy();
        }
        this.boss = new Boss();
        this.playerBullets = new Bullet[MAX_PLAYER_BULLETS];
        for (int i = 0; i < MAX_PLAYER_BULLETS; i++) {
            this.playerBullets[i] = new Bullet();
        }
        this.enemyBullets = new Bullet[MAX_ENEMY_BULLETS];
        for (int i = 0; i < MAX_ENEMY_BULLETS; i++) {
            this.enemyBullets[i] = new Bullet();
        }
        this.powerUps = new PowerUp[MAX_POWER_UPS];
        for (int i = 0; i < MAX_POWER_UPS; i++) {
            this.powerUps[i] = new PowerUp();
        }
        
        // Initialize managers
        this.highScoreManager = new HighScoreManager();
        this.gameSettings = new GameSettings();
        
        resetGame();
    }
    
    /**
     * Updates all game entities and logic.
     */
    public void update(int gameWidth, int gameHeight) {
        // Update player
        player.update(gameWidth, gameHeight);
        
        // Update enemies
        for (Enemy enemy : enemies) {
            enemy.update(gameWidth, gameHeight);
            enemy.updateExplosion();
        }
        
        // Update boss
        boss.update(gameWidth);
        boss.updateExplosion();
        
        // Update bullets
        for (Bullet bullet : playerBullets) {
            bullet.update(gameWidth, gameHeight);
        }
        for (Bullet bullet : enemyBullets) {
            bullet.update(gameWidth, gameHeight);
        }
        
        // Update power-ups
        for (PowerUp powerUp : powerUps) {
            powerUp.update(gameWidth, gameHeight);
        }
        
        // Spawn enemies
        spawnEnemies(gameWidth, gameHeight);
        
        // Spawn power-ups
        spawnPowerUps(gameWidth, gameHeight);
        
        // Spawn boss
        spawnBoss();
        
        // Handle enemy firing
        handleEnemyFiring();
        
        // Handle boss firing
        handleBossFiring();
        
        // Check collisions
        checkCollisions();
        
        // Update hit rate
        if (ammoFired > 0) {
            hitRate = (enemiesHit * 100) / ammoFired;
        }
    }
    
    /**
     * Spawns new enemies when conditions are met.
     */
    private void spawnEnemies(int gameWidth, int gameHeight) {
        enemCount++;
        
        // Check if we should spawn enemies
        if (enemCount > ENEMY_SPAWN_DELAY) {
            // Count active enemies
            int activeEnemies = 0;
            for (Enemy enemy : enemies) {
                if (enemy.isActive()) {
                    activeEnemies++;
                }
            }
            
            // Spawn enemies if we have room (max 3 active at once for balance)
            int maxActiveEnemies = Math.min(3, MAX_ENEMIES);
            if (activeEnemies < maxActiveEnemies) {
                enemCount = 0;
                
                // Determine how many enemies to spawn (1-2 enemies)
                int enemiesToSpawn = random.nextInt(2) + 1;
                
                for (int spawnCount = 0; spawnCount < enemiesToSpawn; spawnCount++) {
                    // Find inactive enemy slot
                    for (Enemy enemy : enemies) {
                        if (!enemy.isActive()) {
                            int moveType = Math.abs(random.nextInt()) % 7; // 0-6 for 7 different patterns
                            int imageType = Math.abs(random.nextInt()) % 5;
                            
                                                    int spawnX, spawnY;
                        if (moveType == 0) { // Spawn from top - straight down
                            spawnX = random.nextInt(gameWidth - 20);
                            spawnY = 0;
                        } else if (moveType == 1) { // Spawn from left - straight right
                            spawnX = 0;
                            spawnY = random.nextInt(gameHeight / 2);
                        } else if (moveType == 2) { // Spawn from right - straight left
                            spawnX = gameWidth - 20;
                            spawnY = random.nextInt(gameHeight / 2);
                        } else if (moveType == 3) { // Zigzag pattern - spawn from top
                            spawnX = random.nextInt(gameWidth - 20);
                            spawnY = 0;
                        } else if (moveType == 4) { // Circle pattern - spawn from top
                            spawnX = random.nextInt(gameWidth - 20);
                            spawnY = 0;
                        } else if (moveType == 5) { // Dive pattern - spawn from top
                            spawnX = random.nextInt(gameWidth - 20);
                            spawnY = 0;
                        } else { // Hover pattern - spawn from top
                            spawnX = random.nextInt(gameWidth - 20);
                            spawnY = random.nextInt(gameHeight / 3);
                        }
                            
                            enemy.spawn(spawnX, spawnY, moveType, imageType, null, gameWidth, gameHeight, random);
                            break; // Spawn one enemy per iteration
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Spawns boss when threshold is reached.
     */
    private void spawnBoss() {
        if (bossCounter >= nextBossThreshold && !boss.isActive()) {
            int bossType = Math.abs(random.nextInt()) % 2;
            boss.spawn(bossType, null, 360); // gameWidth hardcoded for now
            nextBossThreshold += 50;
        }
    }
    
    /**
     * Handles enemy firing logic with random timing and difficulty progression.
     */
    private void handleEnemyFiring() {
        // Update difficulty based on score
        updateDifficulty();
        
        // Update firing cooldown
        if (enemyFireCooldown > 0) {
            enemyFireCooldown--;
        }
        
        // Only allow firing if cooldown is complete
        if (enemyFireCooldown <= 0) {
            // Random chance to fire (increases with difficulty)
            int fireChance = 30 + (difficultyLevel - 1) * 10; // 30% at level 1, 40% at level 2, etc.
            if (random.nextInt(100) < fireChance) {
                // Find enemies that can fire
                for (Enemy enemy : enemies) {
                    if (enemy.canFire() && random.nextInt(100) < 50) { // 50% chance per enemy
                        // Find available bullet slot
                        for (Bullet bullet : enemyBullets) {
                            if (!bullet.isActive()) {
                                int[] spawnPos = enemy.getBulletSpawnPosition();
                                
                                // Add some randomness to the target position
                                int targetX = player.getX() + 10 + random.nextInt(20) - 10; // ±10 pixels
                                int targetY = player.getY() + 10 + random.nextInt(20) - 10; // ±10 pixels
                                
                                bullet.fireEnemyBullet(spawnPos[0], spawnPos[1], targetX, targetY);
                                
                                // Set random cooldown for next shot (decreases with difficulty)
                                int minCooldown = Math.max(10, ENEMY_FIRE_COOLDOWN_MIN - (difficultyLevel - 1) * 5);
                                int maxCooldown = Math.max(60, ENEMY_FIRE_COOLDOWN_MAX - (difficultyLevel - 1) * 10);
                                enemyFireCooldown = minCooldown + random.nextInt(maxCooldown - minCooldown);
                                break;
                            }
                        }
                        break; // Only one enemy fires per frame
                    }
                }
            }
        }
    }
    
    /**
     * Updates difficulty level based on score.
     */
    private void updateDifficulty() {
        int newDifficulty = (score / DIFFICULTY_INCREASE_SCORE) + 1;
        if (newDifficulty > difficultyLevel) {
            difficultyLevel = newDifficulty;
        }
    }
    
    /**
     * Handles boss firing logic with random timing.
     */
    private void handleBossFiring() {
        if (boss.canFire()) {
            // Random chance for boss to fire (20% chance per frame)
            if (random.nextInt(100) < 20) {
                // Find available bullet slot (use slots 3-6 for boss bullets)
                for (int i = 3; i < enemyBullets.length; i++) {
                    if (!enemyBullets[i].isActive()) {
                        int[] spawnPos = boss.getBulletSpawnPosition();
                        
                        // More random target position for boss
                        int targetX = random.nextInt(360); // Random X position
                        int targetY = 390 + random.nextInt(50); // Random Y position below screen
                        
                        enemyBullets[i].fireEnemyBullet(spawnPos[0], spawnPos[1], targetX, targetY);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Spawns power-ups randomly.
     */
    private void spawnPowerUps(int gameWidth, int gameHeight) {
        powerUpSpawnCounter++;
        
        // Check if we should spawn a power-up
        if (powerUpSpawnCounter > POWER_UP_SPAWN_CHANCE) {
            powerUpSpawnCounter = 0;
            
            // Count active power-ups
            int activePowerUps = 0;
            for (PowerUp powerUp : powerUps) {
                if (powerUp.isActive()) {
                    activePowerUps++;
                }
            }
            
            // Spawn power-up if we have room
            if (activePowerUps < MAX_POWER_UPS) {
                for (PowerUp powerUp : powerUps) {
                    if (!powerUp.isActive()) {
                        int spawnX = random.nextInt(gameWidth - 16);
                        int spawnY = 0;
                        powerUp.spawn(spawnX, spawnY, random);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Checks all collision types.
     */
    private void checkCollisions() {
        checkPlayerEnemyBulletCollisions();
        checkPlayerBulletEnemyCollisions();
        checkPlayerBulletBossCollisions();
        checkPlayerBossCollisions();
        checkPlayerPowerUpCollisions();
    }
    
    /**
     * Checks collisions between player and enemy bullets.
     */
    private void checkPlayerEnemyBulletCollisions() {
        if (player.isExploding()) {
            return;
        }
        
        for (Bullet bullet : enemyBullets) {
            if (bullet.isActive() && bullet.intersects(player.getX(), player.getY(), 19, 19)) {
                bullet.deactivate();
                player.takeDamage(10);
            }
        }
    }
    
    /**
     * Checks collisions between player bullets and enemies.
     */
    private void checkPlayerBulletEnemyCollisions() {
        for (Bullet bullet : playerBullets) {
            if (!bullet.isActive() || !bullet.isPlayerBullet()) {
                continue;
            }
            
            for (Enemy enemy : enemies) {
                if (enemy.isActive() && bullet.intersects(enemy.getX(), enemy.getY(), 20, 25)) {
                    bullet.deactivate();
                    enemy.startExplosion();
                    score += 100;
                    enemiesHit++;
                    bossCounter++;
                }
            }
        }
    }
    
    /**
     * Checks collisions between player bullets and boss.
     */
    private void checkPlayerBulletBossCollisions() {
        if (!boss.isActive()) {
            return;
        }
        
        for (Bullet bullet : playerBullets) {
            if (!bullet.isActive() || !bullet.isPlayerBullet()) {
                continue;
            }
            
            if (bullet.intersects(boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight())) {
                bullet.deactivate();
                boss.takeDamage();
                enemiesHit++;
            }
        }
    }
    
    /**
     * Checks collisions between player and boss.
     */
    private void checkPlayerBossCollisions() {
        if (!boss.isActive() || player.isExploding()) {
            return;
        }
        
        if (player.intersects(boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight())) {
            player.takeDamage(20);
        }
    }
    
    /**
     * Checks collisions between player and power-ups.
     */
    private void checkPlayerPowerUpCollisions() {
        if (player.isExploding()) {
            return;
        }
        
        for (PowerUp powerUp : powerUps) {
            if (powerUp.isActive() && powerUp.checkCollision(player)) {
                // Apply power-up effect
                player.applyPowerUp(powerUp.getType());
                
                // Add score bonus if it's a score bonus power-up
                if (powerUp.getType() == PowerUp.PowerUpType.SCORE_BONUS) {
                    score += 500;
                }
                
                // Deactivate power-up
                powerUp.collect();
            }
        }
    }
    
    /**
     * Fires a player bullet if available.
     */
    public boolean firePlayerBullet() {
        if (player.isTripleShotActive()) {
            // Fire three bullets in a spread pattern
            int bulletsFired = 0;
            for (Bullet bullet : playerBullets) {
                if (!bullet.isActive() && bulletsFired < 3) {
                    int offsetX = bulletsFired == 0 ? 2 : (bulletsFired == 1 ? -2 : 6);
                    bullet.firePlayerBullet(player.getX() + offsetX, player.getY());
                    bulletsFired++;
                }
            }
            if (bulletsFired > 0) {
                ammoFired += bulletsFired;
                return true;
            }
        } else {
            // Fire single bullet
            for (Bullet bullet : playerBullets) {
                if (!bullet.isActive()) {
                    bullet.firePlayerBullet(player.getX() + 2, player.getY());
                    ammoFired++;
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Resets the game to initial state.
     */
    public void resetGame() {
        score = 0;
        ammoFired = 0;
        enemiesHit = 0;
        hitRate = 0;
        bossCounter = 0;
        nextBossThreshold = INITIAL_BOSS_THRESHOLD;
        bossScore = 2000;
        enemCount = 0;
        enemyFireCooldown = 0;
        difficultyLevel = 1;
        
        player.reset((360 - 20) / 2, 390 - 40);
        
        for (Enemy enemy : enemies) {
            enemy.reset();
        }
        
        boss.reset();
        
        for (Bullet bullet : playerBullets) {
            bullet.reset();
        }
        
        for (Bullet bullet : enemyBullets) {
            bullet.reset();
        }
        
        for (PowerUp powerUp : powerUps) {
            powerUp.reset();
        }
        
        powerUpSpawnCounter = 0;
    }
    
    // Getters for game state
    public Player getPlayer() { return player; }
    public Enemy[] getEnemies() { return enemies; }
    public Boss getBoss() { return boss; }
    public Bullet[] getPlayerBullets() { return playerBullets; }
    public Bullet[] getEnemyBullets() { return enemyBullets; }
    public int getScore() { return score; }
    public int getAmmoFired() { return ammoFired; }
    public int getEnemiesHit() { return enemiesHit; }
    public int getHitRate() { return hitRate; }
    public int getBossCounter() { return bossCounter; }
    public int getNextBossThreshold() { return nextBossThreshold; }
    public int getBossScore() { return bossScore; }
    public int getDifficultyLevel() { return difficultyLevel; }
    public PowerUp[] getPowerUps() { return powerUps; }
    public HighScoreManager getHighScoreManager() { return highScoreManager; }
    public GameSettings getGameSettings() { return gameSettings; }
    
    /**
     * Sets the boss score when boss is defeated.
     */
    public void setBossScore(int score) {
        this.bossScore = score;
        this.score += score;
    }
} 