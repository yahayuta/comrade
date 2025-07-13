import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Bullet class representing projectiles fired by player or enemies.
 * Handles bullet movement, rendering, and collision detection.
 */
public class Bullet {
    private int x, y;
    private int moveX, moveY;
    private boolean active;
    private boolean isPlayerBullet;
    
    // Constants
    private static final int PLAYER_BULLET_WIDTH = 4;
    private static final int PLAYER_BULLET_HEIGHT = 20;
    private static final int ENEMY_BULLET_WIDTH = 4;
    private static final int ENEMY_BULLET_HEIGHT = 10;
    private static final int PLAYER_BULLET_SPEED = 2;
    private static final int ENEMY_BULLET_SPEED = 2;
    
    public Bullet() {
        this.active = false;
    }
    
    /**
     * Fires a player bullet from the specified position.
     */
    public void firePlayerBullet(int x, int y) {
        this.x = x;
        this.y = y;
        this.moveX = 0;
        this.moveY = -PLAYER_BULLET_SPEED;
        this.active = true;
        this.isPlayerBullet = true;
    }
    
    /**
     * Fires an enemy bullet from the specified position with target coordinates.
     */
    public void fireEnemyBullet(int x, int y, int targetX, int targetY) {
        this.x = x;
        this.y = y;
        
        // Calculate direction to target
        int deltaX = targetX - x;
        int deltaY = targetY - y;
        int distance = (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        if (distance > 0) {
            this.moveX = (deltaX * ENEMY_BULLET_SPEED) / distance;
            this.moveY = ENEMY_BULLET_SPEED;
        } else {
            this.moveX = 0;
            this.moveY = ENEMY_BULLET_SPEED;
        }
        
        this.active = true;
        this.isPlayerBullet = false;
    }
    
    /**
     * Updates bullet position and checks if it's off-screen.
     */
    public void update(int gameWidth, int gameHeight) {
        if (!active) {
            return;
        }
        
        x += moveX;
        y += moveY;
        
        // Check if bullet is off-screen
        if (y < 0 || y > gameHeight || x < 0 || x > gameWidth) {
            active = false;
        }
    }
    
    /**
     * Renders the bullet.
     */
    public void render(GraphicsContext gc) {
        if (!active) {
            return;
        }
        
        if (isPlayerBullet) {
            // Player bullets are yellow
            gc.setFill(Color.YELLOW);
            gc.fillRect(x, y, PLAYER_BULLET_WIDTH, PLAYER_BULLET_HEIGHT);
        } else {
            // Enemy bullets are pink
            gc.setFill(Color.PINK);
            gc.fillRect(x, y, ENEMY_BULLET_WIDTH, ENEMY_BULLET_HEIGHT);
        }
    }
    
    /**
     * Deactivates the bullet.
     */
    public void deactivate() {
        this.active = false;
    }
    
    /**
     * Resets bullet to inactive state.
     */
    public void reset() {
        this.active = false;
    }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isActive() { return active; }
    public boolean isPlayerBullet() { return isPlayerBullet; }
    
    /**
     * Gets the width of the bullet.
     */
    public int getWidth() {
        return isPlayerBullet ? PLAYER_BULLET_WIDTH : ENEMY_BULLET_WIDTH;
    }
    
    /**
     * Gets the height of the bullet.
     */
    public int getHeight() {
        return isPlayerBullet ? PLAYER_BULLET_HEIGHT : ENEMY_BULLET_HEIGHT;
    }
    
    /**
     * Gets the collision bounds for hit detection.
     */
    public boolean intersects(int otherX, int otherY, int otherWidth, int otherHeight) {
        return active &&
               x < otherX + otherWidth &&
               x + getWidth() > otherX &&
               y < otherY + otherHeight &&
               y + getHeight() > otherY;
    }
} 