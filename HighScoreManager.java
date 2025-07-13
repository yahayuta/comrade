import java.io.*;
import java.util.*;

/**
 * HighScoreManager handles saving and loading high scores to/from a file.
 * Maintains a list of top scores with player names and dates.
 */
public class HighScoreManager {
    private static final String HIGH_SCORE_FILE = "highscores.dat";
    private static final int MAX_HIGH_SCORES = 10;
    
    public static class HighScoreEntry implements Serializable {
        private String playerName;
        private int score;
        private String date;
        
        public HighScoreEntry(String playerName, int score, String date) {
            this.playerName = playerName;
            this.score = score;
            this.date = date;
        }
        
        public String getPlayerName() { return playerName; }
        public int getScore() { return score; }
        public String getDate() { return date; }
    }
    
    private List<HighScoreEntry> highScores;
    
    public HighScoreManager() {
        highScores = new ArrayList<>();
        loadHighScores();
    }
    
    /**
     * Loads high scores from file.
     */
    @SuppressWarnings("unchecked")
    private void loadHighScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGH_SCORE_FILE))) {
            highScores = (List<HighScoreEntry>) ois.readObject();
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, start with empty list
            highScores = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading high scores: " + e.getMessage());
            highScores = new ArrayList<>();
        }
    }
    
    /**
     * Saves high scores to file.
     */
    private void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGH_SCORE_FILE))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            System.out.println("Error saving high scores: " + e.getMessage());
        }
    }
    
    /**
     * Checks if a score qualifies as a high score.
     */
    public boolean isHighScore(int score) {
        if (highScores.size() < MAX_HIGH_SCORES) {
            return true;
        }
        
        // Check if score is higher than the lowest high score
        return score > getLowestHighScore();
    }
    
    /**
     * Adds a new high score entry.
     */
    public void addHighScore(String playerName, int score) {
        if (!isHighScore(score)) {
            return;
        }
        
        String currentDate = java.time.LocalDate.now().toString();
        HighScoreEntry newEntry = new HighScoreEntry(playerName, score, currentDate);
        
        highScores.add(newEntry);
        
        // Sort by score (highest first)
        highScores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        
        // Keep only top scores
        if (highScores.size() > MAX_HIGH_SCORES) {
            highScores = highScores.subList(0, MAX_HIGH_SCORES);
        }
        
        saveHighScores();
    }
    
    /**
     * Gets the lowest high score.
     */
    private int getLowestHighScore() {
        if (highScores.isEmpty()) {
            return 0;
        }
        
        int lowest = Integer.MAX_VALUE;
        for (HighScoreEntry entry : highScores) {
            if (entry.getScore() < lowest) {
                lowest = entry.getScore();
            }
        }
        return lowest;
    }
    
    /**
     * Gets all high scores.
     */
    public List<HighScoreEntry> getHighScores() {
        return new ArrayList<>(highScores);
    }
    
    /**
     * Gets the highest score.
     */
    public int getHighestScore() {
        if (highScores.isEmpty()) {
            return 0;
        }
        return highScores.get(0).getScore();
    }
    
    /**
     * Clears all high scores (for testing).
     */
    public void clearHighScores() {
        highScores.clear();
        saveHighScores();
    }
} 