import javafx.scene.image.Image;

/**
 * PowerUp class represents collectible power-ups that provide various bonuses to the player.
 * Power-ups spawn randomly and have different effects when collected.
 */
public class PowerUp {
    // Power-up types
    public enum PowerUpType {
        RAPID_FIRE,    // Increases fire rate
        TRIPLE_SHOT,   // Fires 3 bullets at once
        SHIELD,        // Temporary invincibility
        HEALTH,        // Restores health
        SCORE_BONUS    // Bonus points
    }
    
    // Power-up properties
    private boolean active;
    private double x, y;
    private PowerUpType type;
    private int animationFrame;
    private int animationCounter;
    private static final int ANIMATION_SPEED = 8; // Frames per animation frame
    
    // Movement
    private double speedY = 1.0;
    
    public PowerUp() {
        reset();
    }
    
    /**
     * Spawns a power-up at the specified location with a random type.
     */
    public void spawn(double x, double y, java.util.Random random) {
        this.x = x;
        this.y = y;
        this.active = true;
        this.animationFrame = 0;
        this.animationCounter = 0;
        
        // Randomly select power-up type
        int typeIndex = random.nextInt(5);
        switch (typeIndex) {
            case 0: type = PowerUpType.RAPID_FIRE; break;
            case 1: type = PowerUpType.TRIPLE_SHOT; break;
            case 2: type = PowerUpType.SHIELD; break;
            case 3: type = PowerUpType.HEALTH; break;
            case 4: type = PowerUpType.SCORE_BONUS; break;
            default: type = PowerUpType.RAPID_FIRE; break;
        }
    }
    
    /**
     * Updates power-up position and animation.
     */
    public void update(int gameWidth, int gameHeight) {
        if (!active) return;
        
        // Move downward
        y += speedY;
        
        // Deactivate if off screen
        if (y > gameHeight) {
            active = false;
        }
        
        // Update animation
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            animationCounter = 0;
            animationFrame = (animationFrame + 1) % 4; // 4 animation frames
        }
    }
    
    /**
     * Checks collision with player.
     */
    public boolean checkCollision(Player player) {
        if (!active) return false;
        
        return (x < player.getX() + 20 && x + 16 > player.getX() &&
                y < player.getY() + 20 && y + 16 > player.getY());
    }
    
    /**
     * Deactivates the power-up (when collected).
     */
    public void collect() {
        active = false;
    }
    
    /**
     * Resets the power-up to inactive state.
     */
    public void reset() {
        active = false;
        x = 0;
        y = 0;
        type = PowerUpType.RAPID_FIRE;
        animationFrame = 0;
        animationCounter = 0;
    }
    
    // Getters
    public boolean isActive() { return active; }
    public double getX() { return x; }
    public double getY() { return y; }
    public PowerUpType getType() { return type; }
    public int getAnimationFrame() { return animationFrame; }
} 