import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;

/**
 * Player class representing the player's aircraft.
 * Handles player movement, health, position, and rendering.
 */
public class Player {
    private int x, y;
    private int health;
    private int maxHealth;
    private Image image;
    private boolean isExploding;
    private int explosionX, explosionY, explosionTimer;
    private int selectedAircraft; // 0 = MiG-29, 1 = Su-27
    
    // Movement flags
    private boolean movingUp, movingDown, movingLeft, movingRight;
    
    // Power-up effects
    private boolean rapidFireActive;
    private boolean tripleShotActive;
    private boolean shieldActive;
    private int rapidFireTimer;
    private int tripleShotTimer;
    private int shieldTimer;
    private static final int POWER_UP_DURATION = 600; // 10 seconds at 60 FPS
    
    // Constants
    private static final int PLAYER_WIDTH = 20;
    private static final int PLAYER_HEIGHT = 20;
    private static final int MOVE_SPEED = 4;
    private static final int EXPLOSION_DURATION = 20;
    
    public Player(int startX, int startY, int maxHealth) {
        this.x = startX;
        this.y = startY;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.isExploding = false;
        this.explosionTimer = 0;
        this.selectedAircraft = 0;
    }
    
    /**
     * Updates player position based on movement flags and screen boundaries.
     */
    public void update(int gameWidth, int gameHeight) {
        if (isExploding) {
            explosionTimer++;
            if (explosionTimer > EXPLOSION_DURATION) {
                isExploding = false;
                explosionTimer = 0;
            }
            return;
        }
        
        // Update power-up timers
        updatePowerUpTimers();
        
        // Handle movement
        if (movingUp) y -= MOVE_SPEED;
        if (movingDown) y += MOVE_SPEED;
        if (movingLeft) x -= MOVE_SPEED;
        if (movingRight) x += MOVE_SPEED;
        
        // Enforce screen boundaries
        if (x < 0) x = 0;
        if (x > gameWidth - PLAYER_WIDTH) x = gameWidth - PLAYER_WIDTH;
        if (y < 0) y = 0;
        if (y > gameHeight - PLAYER_HEIGHT) y = gameHeight - PLAYER_HEIGHT;
    }
    
    /**
     * Renders the player or explosion effect.
     */
    public void render(GraphicsContext gc, Image playerImage, Image explosionImage) {
        if (isExploding) {
            gc.drawImage(explosionImage, explosionX, explosionY);
        } else {
            gc.drawImage(playerImage, x, y);
        }
    }
    
    /**
     * Handles player taking damage.
     */
    public void takeDamage(int damage) {
        if (!isExploding && !shieldActive) {
            health -= damage;
            if (health <= 0) {
                health = 0;
                startExplosion();
            }
        }
    }
    
    /**
     * Starts the explosion animation.
     */
    public void startExplosion() {
        isExploding = true;
        explosionX = x;
        explosionY = y;
        explosionTimer = 0;
    }
    
    /**
     * Resets player to initial state.
     */
    public void reset(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.health = maxHealth;
        this.isExploding = false;
        this.explosionTimer = 0;
        this.movingUp = false;
        this.movingDown = false;
        this.movingLeft = false;
        this.movingRight = false;
        
        // Reset power-ups
        rapidFireActive = false;
        tripleShotActive = false;
        shieldActive = false;
        rapidFireTimer = 0;
        tripleShotTimer = 0;
        shieldTimer = 0;
    }
    
    /**
     * Sets the aircraft type and updates the image.
     */
    public void setAircraft(int aircraftType, Image aircraftImage) {
        this.selectedAircraft = aircraftType;
        this.image = aircraftImage;
    }
    
    // Movement setters
    public void setMovingUp(boolean moving) { this.movingUp = moving; }
    public void setMovingDown(boolean moving) { this.movingDown = moving; }
    public void setMovingLeft(boolean moving) { this.movingLeft = moving; }
    public void setMovingRight(boolean moving) { this.movingRight = moving; }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isExploding() { return isExploding; }
    public boolean isAlive() { return health > 0; }
    public int getSelectedAircraft() { return selectedAircraft; }
    public Image getImage() { return image; }
    
    /**
     * Updates power-up timers and deactivates expired power-ups.
     */
    private void updatePowerUpTimers() {
        if (rapidFireActive) {
            rapidFireTimer++;
            if (rapidFireTimer >= POWER_UP_DURATION) {
                rapidFireActive = false;
                rapidFireTimer = 0;
            }
        }
        
        if (tripleShotActive) {
            tripleShotTimer++;
            if (tripleShotTimer >= POWER_UP_DURATION) {
                tripleShotActive = false;
                tripleShotTimer = 0;
            }
        }
        
        if (shieldActive) {
            shieldTimer++;
            if (shieldTimer >= POWER_UP_DURATION) {
                shieldActive = false;
                shieldTimer = 0;
            }
        }
    }
    
    /**
     * Applies a power-up effect to the player.
     */
    public void applyPowerUp(PowerUp.PowerUpType type) {
        switch (type) {
            case RAPID_FIRE:
                rapidFireActive = true;
                rapidFireTimer = 0;
                break;
            case TRIPLE_SHOT:
                tripleShotActive = true;
                tripleShotTimer = 0;
                break;
            case SHIELD:
                shieldActive = true;
                shieldTimer = 0;
                break;
            case HEALTH:
                health = Math.min(health + 25, maxHealth);
                break;
            case SCORE_BONUS:
                // This will be handled by the game engine
                break;
        }
    }
    
    /**
     * Gets the collision bounds for hit detection.
     */
    public boolean intersects(int otherX, int otherY, int otherWidth, int otherHeight) {
        return x < otherX + otherWidth &&
               x + PLAYER_WIDTH > otherX &&
               y < otherY + otherHeight &&
               y + PLAYER_HEIGHT > otherY;
    }
    
    // Power-up getters
    public boolean isRapidFireActive() { return rapidFireActive; }
    public boolean isTripleShotActive() { return tripleShotActive; }
    public boolean isShieldActive() { return shieldActive; }
} 