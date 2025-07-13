import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import java.util.Random;

/**
 * Enemy class representing enemy aircraft.
 * Handles enemy movement, rendering, and state management.
 */
public class Enemy {
    private int x, y;
    private int moveX, moveY;
    private int moveType; // 0: top, 1: left, 2: right, 3: zigzag, 4: circle, 5: dive, 6: hover
    private int imageType; // 0: F-15, 1: F-16, 2: EUF, 3: F-18, 4: F-117
    private boolean active;
    private boolean exploding;
    private int explosionX, explosionY, explosionTimer;
    private Image image;
    
    // Movement pattern variables
    private int patternTimer;
    private int zigzagDirection;
    private int circleRadius;
    private int circleAngle;
    private int circleCenterX;
    private int diveStartY;
    private int hoverTimer;
    private int hoverDirection;
    private Random random;
    
    // Constants
    private static final int ENEMY_WIDTH = 20;
    private static final int ENEMY_HEIGHT = 25;
    private static final int MOVE_SPEED = 2;
    private static final int EXPLOSION_DURATION = 15;
    private static final int ZIGZAG_FREQUENCY = 60; // Frames between direction changes
    private static final int CIRCLE_RADIUS = 30;
    private static final int HOVER_FREQUENCY = 90; // Frames between hover direction changes
    
    public Enemy() {
        this.active = false;
        this.exploding = false;
        this.explosionTimer = 0;
        this.random = new Random();
    }
    
    /**
     * Spawns a new enemy at the specified position with given movement type.
     */
    public void spawn(int x, int y, int moveType, int imageType, Image image, int gameWidth, int gameHeight, Random random) {
        this.x = x;
        this.y = y;
        this.moveType = moveType;
        this.imageType = imageType;
        this.image = image;
        this.active = true;
        this.exploding = false;
        this.explosionTimer = 0;
        
        // Initialize movement pattern variables
        this.patternTimer = 0;
        this.zigzagDirection = random.nextInt(2) * 2 - 1; // -1 or 1
        this.circleRadius = CIRCLE_RADIUS;
        this.circleAngle = random.nextInt(360);
        this.circleCenterX = x;
        this.diveStartY = y;
        this.hoverTimer = 0;
        this.hoverDirection = random.nextInt(2) * 2 - 1; // -1 or 1
        this.random = random; // Use the passed random instance
        
        // Set movement direction based on spawn type
        if (moveType == 0) { // Spawn from top - straight down
            this.moveX = 0;
            this.moveY = MOVE_SPEED;
        } else if (moveType == 1) { // Spawn from left - straight right
            this.moveX = MOVE_SPEED;
            this.moveY = 0;
        } else if (moveType == 2) { // Spawn from right - straight left
            this.moveX = -MOVE_SPEED;
            this.moveY = 0;
        } else if (moveType == 3) { // Zigzag pattern
            this.moveX = MOVE_SPEED * zigzagDirection;
            this.moveY = MOVE_SPEED;
        } else if (moveType == 4) { // Circle pattern
            this.moveX = 0;
            this.moveY = MOVE_SPEED;
        } else if (moveType == 5) { // Dive pattern
            this.moveX = 0;
            this.moveY = MOVE_SPEED;
        } else if (moveType == 6) { // Hover pattern
            this.moveX = MOVE_SPEED * hoverDirection;
            this.moveY = 0;
        }
    }
    
    /**
     * Updates enemy position and checks if it's off-screen.
     */
    public void update(int gameWidth, int gameHeight) {
        if (!active || exploding) {
            return;
        }
        
        patternTimer++;
        
        // Update movement based on pattern type
        switch (moveType) {
            case 0: // Straight down
            case 1: // Straight right
            case 2: // Straight left
                // Simple linear movement
                x += moveX;
                y += moveY;
                break;
                
            case 3: // Zigzag pattern
                updateZigzagMovement(gameWidth);
                break;
                
            case 4: // Circle pattern
                updateCircleMovement(gameWidth, gameHeight);
                break;
                
            case 5: // Dive pattern
                updateDiveMovement(gameWidth, gameHeight);
                break;
                
            case 6: // Hover pattern
                updateHoverMovement(gameWidth, gameHeight);
                break;
        }
        
        // Check if enemy is off-screen
        if (y > gameHeight - 15 || x > gameWidth - 20 || x < 0 || y < 0) {
            active = false;
        }
    }
    
    /**
     * Updates zigzag movement pattern.
     */
    private void updateZigzagMovement(int gameWidth) {
        // Change direction every ZIGZAG_FREQUENCY frames
        if (patternTimer % ZIGZAG_FREQUENCY == 0) {
            zigzagDirection *= -1;
            moveX = MOVE_SPEED * zigzagDirection;
        }
        
        x += moveX;
        y += moveY;
        
        // Bounce off screen edges
        if (x <= 0 || x >= gameWidth - 20) {
            zigzagDirection *= -1;
            moveX = MOVE_SPEED * zigzagDirection;
        }
    }
    
    /**
     * Updates circle movement pattern.
     */
    private void updateCircleMovement(int gameWidth, int gameHeight) {
        // Move down while circling
        y += MOVE_SPEED;
        
        // Calculate circular motion
        circleAngle += 5; // Rotation speed
        if (circleAngle >= 360) {
            circleAngle = 0;
        }
        
        // Calculate new X position based on circle
        double radians = Math.toRadians(circleAngle);
        x = circleCenterX + (int)(circleRadius * Math.cos(radians));
        
        // Keep circle center moving down
        circleCenterX += 0;
    }
    
    /**
     * Updates dive movement pattern.
     */
    private void updateDiveMovement(int gameWidth, int gameHeight) {
        // Start with normal downward movement
        y += MOVE_SPEED;
        
        // After reaching certain height, start diving
        if (y > gameHeight / 3) {
            // Calculate dive angle based on distance from start
            int diveDistance = y - diveStartY;
            int maxDiveDistance = gameHeight - diveStartY;
            double diveRatio = (double)diveDistance / maxDiveDistance;
            
            // Dive towards center of screen
            int targetX = gameWidth / 2;
            int currentDistance = Math.abs(x - targetX);
            
            if (currentDistance > 5) {
                if (x < targetX) {
                    x += MOVE_SPEED;
                } else {
                    x -= MOVE_SPEED;
                }
            }
        }
    }
    
    /**
     * Updates hover movement pattern.
     */
    private void updateHoverMovement(int gameWidth, int gameHeight) {
        // Hover horizontally with occasional vertical movement
        x += moveX;
        
        // Change horizontal direction periodically
        if (patternTimer % HOVER_FREQUENCY == 0) {
            hoverDirection *= -1;
            moveX = MOVE_SPEED * hoverDirection;
        }
        
        // Add slight vertical movement
        if (patternTimer % 30 == 0) {
            y += random.nextInt(3) - 1; // Small random vertical movement
        }
        
        // Bounce off screen edges
        if (x <= 0 || x >= gameWidth - 20) {
            hoverDirection *= -1;
            moveX = MOVE_SPEED * hoverDirection;
        }
    }
    
    /**
     * Renders the enemy or its explosion.
     */
    public void render(GraphicsContext gc, Image explosionImage) {
        if (exploding) {
            gc.drawImage(explosionImage, explosionX, explosionY);
        } else if (active && image != null) {
            gc.drawImage(image, x, y);
        } else if (active) {
            // Fallback rendering if image is null
            gc.setFill(javafx.scene.paint.Color.RED);
            gc.fillRect(x, y, ENEMY_WIDTH, ENEMY_HEIGHT);
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
     * Checks if this enemy can fire a bullet.
     */
    public boolean canFire() {
        return active && y > 10;
    }
    
    /**
     * Gets the position for enemy bullet spawn.
     */
    public int[] getBulletSpawnPosition() {
        return new int[]{x + 10, y + 25};
    }
    
    /**
     * Resets enemy to inactive state.
     */
    public void reset() {
        this.active = false;
        this.exploding = false;
        this.explosionTimer = 0;
    }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isActive() { return active; }
    public boolean isExploding() { return exploding; }
    public int getImageType() { return imageType; }
    public int getMoveType() { return moveType; }
    
    /**
     * Sets the enemy image.
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
               x + ENEMY_WIDTH > otherX &&
               y < otherY + otherHeight &&
               y + ENEMY_HEIGHT > otherY;
    }
} 