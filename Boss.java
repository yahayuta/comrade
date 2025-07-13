import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import java.util.Random;

/**
 * Boss class representing boss aircraft (B-52, B-2).
 * Handles boss movement, health, rendering, and state management.
 */
public class Boss {
    private int x, y;
    private int bossType; // 0 = B-52, 1 = B-2
    private int health;
    private int maxHealth;
    private int direction; // 0 = right, 1 = left
    private boolean active;
    private boolean exploding;
    private int explosionX, explosionY, explosionTimer;
    private Image image;
    
    // Constants
    private static final int B52_WIDTH = 40;
    private static final int B52_HEIGHT = 35;
    private static final int B2_WIDTH = 35;
    private static final int B2_HEIGHT = 20;
    private static final int EXPLOSION_DURATION = 20;
    private static final int MAX_HEALTH = 50;
    
    public Boss() {
        this.active = false;
        this.exploding = false;
        this.explosionTimer = 0;
        this.health = MAX_HEALTH;
        this.maxHealth = MAX_HEALTH;
    }
    
    /**
     * Spawns a new boss of the specified type.
     */
    public void spawn(int bossType, Image image, int gameWidth) {
        this.bossType = bossType;
        this.image = image;
        this.active = true;
        this.exploding = false;
        this.explosionTimer = 0;
        this.health = MAX_HEALTH;
        this.direction = 0;
        
        // Set initial position based on boss type
        if (bossType == 0) { // B-52
            this.x = gameWidth / 2 - 20;
            this.y = -35;
        } else { // B-2
            this.x = gameWidth / 2 - 17;
            this.y = -20;
        }
    }
    
    /**
     * Updates boss position and movement.
     */
    public void update(int gameWidth) {
        if (!active || exploding) {
            return;
        }
        
        // Move down to screen initially
        if (y < 20) {
            y++;
        } else {
            // Side-to-side movement
            if (direction == 0) {
                x++;
                if (x > gameWidth - getWidth()) {
                    direction = 1;
                }
            } else {
                x--;
                if (x < 0) {
                    direction = 0;
                }
            }
        }
    }
    
    /**
     * Renders the boss or its explosion.
     */
    public void render(GraphicsContext gc, Image explosionImage) {
        if (exploding) {
            gc.drawImage(explosionImage, explosionX, explosionY);
        } else if (active && image != null) {
            gc.drawImage(image, x, y);
        }
    }
    
    /**
     * Handles boss taking damage.
     */
    public void takeDamage() {
        if (active && !exploding) {
            health--;
            if (health <= 0) {
                startExplosion();
            }
        }
    }
    
    /**
     * Starts the explosion animation.
     */
    public void startExplosion() {
        if (active && !exploding) {
            exploding = true;
            explosionX = x;
            explosionY = y;
            explosionTimer = 0;
            active = false;
        }
    }
    
    /**
     * Updates explosion animation.
     */
    public void updateExplosion() {
        if (exploding) {
            explosionTimer++;
            if (explosionTimer > EXPLOSION_DURATION) {
                exploding = false;
            }
        }
    }
    
    /**
     * Checks if boss can fire a bullet.
     */
    public boolean canFire() {
        return active && y > 19;
    }
    
    /**
     * Gets the position for boss bullet spawn.
     */
    public int[] getBulletSpawnPosition() {
        if (bossType == 0) { // B-52
            return new int[]{x + 19, y + 35};
        } else { // B-2
            return new int[]{x + 16, y + 20};
        }
    }
    
    /**
     * Resets boss to inactive state.
     */
    public void reset() {
        this.active = false;
        this.exploding = false;
        this.explosionTimer = 0;
        this.health = MAX_HEALTH;
    }
    
    /**
     * Gets the width of the boss based on type.
     */
    public int getWidth() {
        return bossType == 0 ? B52_WIDTH : B2_WIDTH;
    }
    
    /**
     * Gets the height of the boss based on type.
     */
    public int getHeight() {
        return bossType == 0 ? B52_HEIGHT : B2_HEIGHT;
    }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isActive() { return active; }
    public boolean isExploding() { return exploding; }
    public int getBossType() { return bossType; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    
    /**
     * Sets the boss image.
     */
    public void setImage(Image image) {
        this.image = image;
    }
    
    /**
     * Gets the collision bounds for hit detection.
     */
    public boolean intersects(int otherX, int otherY, int otherWidth, int otherHeight) {
        return active && !exploding &&
               x < otherX + otherWidth &&
               x + getWidth() > otherX &&
               y < otherY + otherHeight &&
               y + getHeight() > otherY;
    }
} 