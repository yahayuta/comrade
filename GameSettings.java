import java.io.*;
import java.util.Properties;

/**
 * GameSettings manages game configuration and user preferences.
 * Settings are saved to and loaded from a properties file.
 */
public class GameSettings {
    private static final String SETTINGS_FILE = "gamesettings.properties";
    private Properties settings;
    
    // Default settings
    private static final String DEFAULT_PLAYER_NAME = "Player";
    private static final boolean DEFAULT_SOUND_ENABLED = true;
    private static final boolean DEFAULT_MUSIC_ENABLED = true;
    private static final double DEFAULT_SOUND_VOLUME = 0.5;
    private static final double DEFAULT_MUSIC_VOLUME = 0.3;
    private static final int DEFAULT_DIFFICULTY = 1;
    private static final boolean DEFAULT_SHOW_FPS = false;
    private static final boolean DEFAULT_SHOW_HITBOXES = false;
    
    public GameSettings() {
        settings = new Properties();
        loadSettings();
    }
    
    /**
     * Loads settings from file.
     */
    private void loadSettings() {
        try (FileInputStream fis = new FileInputStream(SETTINGS_FILE)) {
            settings.load(fis);
        } catch (FileNotFoundException e) {
            // File doesn't exist, use defaults
            setDefaults();
        } catch (IOException e) {
            System.out.println("Error loading settings: " + e.getMessage());
            setDefaults();
        }
    }
    
    /**
     * Saves settings to file.
     */
    private void saveSettings() {
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            settings.store(fos, "Comrade FX Game Settings");
        } catch (IOException e) {
            System.out.println("Error saving settings: " + e.getMessage());
        }
    }
    
    /**
     * Sets default settings.
     */
    private void setDefaults() {
        settings.setProperty("playerName", DEFAULT_PLAYER_NAME);
        settings.setProperty("soundEnabled", String.valueOf(DEFAULT_SOUND_ENABLED));
        settings.setProperty("musicEnabled", String.valueOf(DEFAULT_MUSIC_ENABLED));
        settings.setProperty("soundVolume", String.valueOf(DEFAULT_SOUND_VOLUME));
        settings.setProperty("musicVolume", String.valueOf(DEFAULT_MUSIC_VOLUME));
        settings.setProperty("difficulty", String.valueOf(DEFAULT_DIFFICULTY));
        settings.setProperty("showFPS", String.valueOf(DEFAULT_SHOW_FPS));
        settings.setProperty("showHitboxes", String.valueOf(DEFAULT_SHOW_HITBOXES));
        saveSettings();
    }
    
    // Getter methods
    public String getPlayerName() {
        return settings.getProperty("playerName", DEFAULT_PLAYER_NAME);
    }
    
    public boolean isSoundEnabled() {
        return Boolean.parseBoolean(settings.getProperty("soundEnabled", String.valueOf(DEFAULT_SOUND_ENABLED)));
    }
    
    public boolean isMusicEnabled() {
        return Boolean.parseBoolean(settings.getProperty("musicEnabled", String.valueOf(DEFAULT_MUSIC_ENABLED)));
    }
    
    public double getSoundVolume() {
        return Double.parseDouble(settings.getProperty("soundVolume", String.valueOf(DEFAULT_SOUND_VOLUME)));
    }
    
    public double getMusicVolume() {
        return Double.parseDouble(settings.getProperty("musicVolume", String.valueOf(DEFAULT_MUSIC_VOLUME)));
    }
    
    public int getDifficulty() {
        return Integer.parseInt(settings.getProperty("difficulty", String.valueOf(DEFAULT_DIFFICULTY)));
    }
    
    public boolean isShowFPS() {
        return Boolean.parseBoolean(settings.getProperty("showFPS", String.valueOf(DEFAULT_SHOW_FPS)));
    }
    
    public boolean isShowHitboxes() {
        return Boolean.parseBoolean(settings.getProperty("showHitboxes", String.valueOf(DEFAULT_SHOW_HITBOXES)));
    }
    
    // Setter methods
    public void setPlayerName(String playerName) {
        settings.setProperty("playerName", playerName);
        saveSettings();
    }
    
    public void setSoundEnabled(boolean enabled) {
        settings.setProperty("soundEnabled", String.valueOf(enabled));
        saveSettings();
    }
    
    public void setMusicEnabled(boolean enabled) {
        settings.setProperty("musicEnabled", String.valueOf(enabled));
        saveSettings();
    }
    
    public void setSoundVolume(double volume) {
        settings.setProperty("soundVolume", String.valueOf(volume));
        saveSettings();
    }
    
    public void setMusicVolume(double volume) {
        settings.setProperty("musicVolume", String.valueOf(volume));
        saveSettings();
    }
    
    public void setDifficulty(int difficulty) {
        settings.setProperty("difficulty", String.valueOf(difficulty));
        saveSettings();
    }
    
    public void setShowFPS(boolean show) {
        settings.setProperty("showFPS", String.valueOf(show));
        saveSettings();
    }
    
    public void setShowHitboxes(boolean show) {
        settings.setProperty("showHitboxes", String.valueOf(show));
        saveSettings();
    }
    
    /**
     * Resets all settings to defaults.
     */
    public void resetToDefaults() {
        setDefaults();
    }
} 