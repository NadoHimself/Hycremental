package de.ageofflair.hycremental.core;

import com.hypixel.hytale.server.core.Message;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

/**
 * Prestige Manager - Handles prestige, ascension, and rebirth mechanics
 * 
 * Features:
 * - Prestige calculations and validation
 * - Ascension and rebirth requirements
 * - Global announcements
 * - Leaderboards for prestige levels
 * - Multiplier calculations
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class PrestigeManager {
    
    private final Hycremental plugin;
    
    // Prestige/Ascension/Rebirth leaderboards
    private List<PrestigeLeaderboardEntry> prestigeLeaderboard;
    private List<PrestigeLeaderboardEntry> ascensionLeaderboard;
    private List<PrestigeLeaderboardEntry> rebirthLeaderboard;
    
    private long lastLeaderboardUpdate;
    private static final long LEADERBOARD_CACHE_TIME = 600000; // 10 minutes
    
    public PrestigeManager(Hycremental plugin) {
        this.plugin = plugin;
        this.prestigeLeaderboard = new ArrayList<>();
        this.ascensionLeaderboard = new ArrayList<>();
        this.rebirthLeaderboard = new ArrayList<>();
        this.lastLeaderboardUpdate = 0;
    }
    
    /**
     * Initialize prestige manager
     */
    public void initialize() {
        plugin.getLogger().at(Level.INFO).log("Initializing PrestigeManager...");
        
        // Load initial leaderboards
        refreshLeaderboards();
        
        // Schedule periodic updates
        plugin.getScheduler().executeRepeating(() -> {
            refreshLeaderboards();
        }, 12000L, 12000L); // Every 10 minutes
        
        plugin.getLogger().at(Level.INFO).log("PrestigeManager initialized");
    }
    
    /**
     * Shutdown prestige manager
     */
    public void shutdown() {
        plugin.getLogger().at(Level.INFO).log("Shutting down PrestigeManager...");
        prestigeLeaderboard.clear();
        ascensionLeaderboard.clear();
        rebirthLeaderboard.clear();
        plugin.getLogger().at(Level.INFO).log("PrestigeManager shut down");
    }
    
    /**
     * Calculate prestige cost
     */
    public BigDecimal calculatePrestigeCost(int currentPrestige) {
        // Formula: 1M * (1.5 ^ prestige)
        double baseCost = 1_000_000.0;
        double multiplier = Math.pow(1.5, currentPrestige);
        return BigDecimal.valueOf(baseCost * multiplier);
    }
    
    /**
     * Check if player can prestige
     */
    public boolean canPrestige(PlayerData playerData) {
        BigDecimal cost = calculatePrestigeCost(playerData.getPrestigeLevel());
        return playerData.hasEssence(cost);
    }
    
    /**
     * Perform prestige
     */
    public boolean performPrestige(PlayerData playerData) {
        BigDecimal cost = calculatePrestigeCost(playerData.getPrestigeLevel());
        
        if (!playerData.hasEssence(cost)) {
            return false;
        }
        
        int oldPrestige = playerData.getPrestigeLevel();
        
        // Remove essence and perform prestige
        playerData.prestige();
        
        // Save to database
        plugin.getPlayerDataManager().savePlayerData(playerData);
        
        // Log
        plugin.getLogger().at(Level.INFO).log(
            playerData.getUsername() + " prestiged from " + oldPrestige + " to " + playerData.getPrestigeLevel()
        );
        
        // Broadcast if milestone
        if (playerData.getPrestigeLevel() % 10 == 0 || playerData.getPrestigeLevel() <= 5) {
            broadcastPrestige(playerData);
        }
        
        return true;
    }
    
    /**
     * Check if player can ascend
     */
    public boolean canAscend(PlayerData playerData) {
        int requiredPrestige = 50 + (playerData.getAscensionLevel() * 50);
        return playerData.getPrestigeLevel() >= requiredPrestige;
    }
    
    /**
     * Perform ascension
     */
    public boolean performAscension(PlayerData playerData) {
        if (!canAscend(playerData)) {
            return false;
        }
        
        int oldAscension = playerData.getAscensionLevel();
        
        // Perform ascension
        playerData.ascend();
        playerData.setPrestigeLevel(0);
        playerData.setEssence(BigDecimal.ZERO);
        
        // Save to database
        plugin.getPlayerDataManager().savePlayerData(playerData);
        
        // Reset generators (keep none for ascension)
        plugin.getGeneratorManager().resetPlayerGenerators(playerData.getUuid(), true);
        
        // Log
        plugin.getLogger().at(Level.INFO).log(
            playerData.getUsername() + " ascended from " + oldAscension + " to " + playerData.getAscensionLevel()
        );
        
        // Always broadcast ascensions
        broadcastAscension(playerData);
        
        return true;
    }
    
    /**
     * Check if player can rebirth
     */
    public boolean canRebirth(PlayerData playerData) {
        return playerData.getAscensionLevel() >= 10;
    }
    
    /**
     * Perform rebirth
     */
    public boolean performRebirth(PlayerData playerData) {
        if (!canRebirth(playerData)) {
            return false;
        }
        
        int oldRebirth = playerData.getRebirthCount();
        
        // Perform rebirth
        playerData.rebirth();
        
        // Save to database
        plugin.getPlayerDataManager().savePlayerData(playerData);
        
        // Reset everything
        plugin.getGeneratorManager().resetPlayerGenerators(playerData.getUuid(), true);
        playerData.setIslandSize(20);
        playerData.setGeneratorSlots(10);
        
        // Log
        plugin.getLogger().at(Level.INFO).log(
            playerData.getUsername() + " rebirthed from " + oldRebirth + " to " + playerData.getRebirthCount()
        );
        
        // Always broadcast rebirths
        broadcastRebirth(playerData);
        
        return true;
    }
    
    /**
     * Broadcast prestige to server
     */
    private void broadcastPrestige(PlayerData playerData) {
        // TODO: Implement with Hytale Server broadcast API
        // plugin.getServer().broadcast(
        //     Message.raw("§6§l" + playerData.getUsername() + " §7has prestiged to §6Prestige " + playerData.getPrestigeLevel() + "§7!")
        // );
        
        plugin.getLogger().at(Level.INFO).log(
            "[BROADCAST] " + playerData.getUsername() + " prestiged to Prestige " + playerData.getPrestigeLevel()
        );
    }
    
    /**
     * Broadcast ascension to server
     */
    private void broadcastAscension(PlayerData playerData) {
        // TODO: Implement with Hytale Server broadcast API
        // plugin.getServer().broadcast(
        //     Message.raw("§5§l★ " + playerData.getUsername() + " §7has ascended to §5Ascension " + playerData.getAscensionLevel() + "§7! ★")
        // );
        
        plugin.getLogger().at(Level.INFO).log(
            "[BROADCAST] " + playerData.getUsername() + " ascended to Ascension " + playerData.getAscensionLevel()
        );
    }
    
    /**
     * Broadcast rebirth to server
     */
    private void broadcastRebirth(PlayerData playerData) {
        // TODO: Implement with Hytale Server broadcast API
        // plugin.getServer().broadcast(
        //     Message.raw("§c§l✦ " + playerData.getUsername() + " §7has been §cREBORN§7! (Rebirth " + playerData.getRebirthCount() + ") ✦")
        // );
        
        plugin.getLogger().at(Level.INFO).log(
            "[BROADCAST] " + playerData.getUsername() + " rebirthed to Rebirth " + playerData.getRebirthCount()
        );
    }
    
    /**
     * Get prestige leaderboard
     */
    public List<PrestigeLeaderboardEntry> getPrestigeLeaderboard() {
        if (System.currentTimeMillis() - lastLeaderboardUpdate > LEADERBOARD_CACHE_TIME) {
            refreshLeaderboards();
        }
        return new ArrayList<>(prestigeLeaderboard);
    }
    
    /**
     * Get ascension leaderboard
     */
    public List<PrestigeLeaderboardEntry> getAscensionLeaderboard() {
        if (System.currentTimeMillis() - lastLeaderboardUpdate > LEADERBOARD_CACHE_TIME) {
            refreshLeaderboards();
        }
        return new ArrayList<>(ascensionLeaderboard);
    }
    
    /**
     * Get rebirth leaderboard
     */
    public List<PrestigeLeaderboardEntry> getRebirthLeaderboard() {
        if (System.currentTimeMillis() - lastLeaderboardUpdate > LEADERBOARD_CACHE_TIME) {
            refreshLeaderboards();
        }
        return new ArrayList<>(rebirthLeaderboard);
    }
    
    /**
     * Refresh all leaderboards from database
     */
    private void refreshLeaderboards() {
        refreshPrestigeLeaderboard();
        refreshAscensionLeaderboard();
        refreshRebirthLeaderboard();
        lastLeaderboardUpdate = System.currentTimeMillis();
    }
    
    /**
     * Refresh prestige leaderboard
     */
    private void refreshPrestigeLeaderboard() {
        String sql = "SELECT username, prestige_level FROM players WHERE prestige_level > 0 ORDER BY prestige_level DESC LIMIT 50";
        prestigeLeaderboard = fetchLeaderboard(sql, "prestige_level");
    }
    
    /**
     * Refresh ascension leaderboard
     */
    private void refreshAscensionLeaderboard() {
        String sql = "SELECT username, ascension_level FROM players WHERE ascension_level > 0 ORDER BY ascension_level DESC LIMIT 50";
        ascensionLeaderboard = fetchLeaderboard(sql, "ascension_level");
    }
    
    /**
     * Refresh rebirth leaderboard
     */
    private void refreshRebirthLeaderboard() {
        String sql = "SELECT username, rebirth_count FROM players WHERE rebirth_count > 0 ORDER BY rebirth_count DESC LIMIT 50";
        rebirthLeaderboard = fetchLeaderboard(sql, "rebirth_count");
    }
    
    /**
     * Fetch leaderboard from database
     */
    private List<PrestigeLeaderboardEntry> fetchLeaderboard(String sql, String valueColumn) {
        List<PrestigeLeaderboardEntry> leaderboard = new ArrayList<>();
        
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            int rank = 1;
            while (rs.next()) {
                leaderboard.add(new PrestigeLeaderboardEntry(
                    rank++,
                    rs.getString("username"),
                    rs.getInt(valueColumn)
                ));
            }
            
        } catch (SQLException e) {
            plugin.getLogger().at(Level.SEVERE).log("Failed to refresh leaderboard: " + e.getMessage());
        }
        
        return leaderboard;
    }
    
    /**
     * Get prestige statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        String sql = "SELECT " +
                     "AVG(prestige_level) as avg_prestige, " +
                     "MAX(prestige_level) as max_prestige, " +
                     "AVG(ascension_level) as avg_ascension, " +
                     "MAX(ascension_level) as max_ascension, " +
                     "AVG(rebirth_count) as avg_rebirth, " +
                     "MAX(rebirth_count) as max_rebirth " +
                     "FROM players";
        
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                stats.put("avg_prestige", rs.getDouble("avg_prestige"));
                stats.put("max_prestige", rs.getInt("max_prestige"));
                stats.put("avg_ascension", rs.getDouble("avg_ascension"));
                stats.put("max_ascension", rs.getInt("max_ascension"));
                stats.put("avg_rebirth", rs.getDouble("avg_rebirth"));
                stats.put("max_rebirth", rs.getInt("max_rebirth"));
            }
            
        } catch (SQLException e) {
            plugin.getLogger().at(Level.WARNING).log("Failed to get prestige stats: " + e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Prestige Leaderboard Entry
     */
    public static class PrestigeLeaderboardEntry {
        private final int rank;
        private final String username;
        private final int value;
        
        public PrestigeLeaderboardEntry(int rank, String username, int value) {
            this.rank = rank;
            this.username = username;
            this.value = value;
        }
        
        public int getRank() { return rank; }
        public String getUsername() { return username; }
        public int getValue() { return value; }
    }
}
