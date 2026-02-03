package de.ageofflair.hycremental.core;

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
 * Economy Manager - Handles currency transactions and leaderboards
 * 
 * Features:
 * - Transaction logging
 * - Leaderboard caching
 * - Currency conversions (Essence, Gems, Crystals)
 * - Economy statistics
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class EconomyManager {
    
    private final Hycremental plugin;
    
    // Leaderboard cache (refreshed every 5 minutes)
    private List<LeaderboardEntry> essenceLeaderboard;
    private long lastLeaderboardUpdate;
    private static final long LEADERBOARD_CACHE_TIME = 300000; // 5 minutes
    
    // Exchange rates
    private static final long ESSENCE_TO_GEMS = 1000000; // 1M Essence = 1 Gem
    private static final int GEMS_TO_CRYSTALS = 1000;    // 1K Gems = 1 Crystal
    
    public EconomyManager(Hycremental plugin) {
        this.plugin = plugin;
        this.essenceLeaderboard = new ArrayList<>();
        this.lastLeaderboardUpdate = 0;
    }
    
    /**
     * Initialize economy manager
     */
    public void initialize() {
        plugin.getLogger().at(Level.INFO).log("Initializing EconomyManager...");
        
        // Load initial leaderboard
        refreshLeaderboard();
        
        // Schedule periodic leaderboard updates
        plugin.getScheduler().executeRepeating(() -> {
            refreshLeaderboard();
        }, 6000L, 6000L); // Every 5 minutes (6000 ticks)
        
        plugin.getLogger().at(Level.INFO).log("EconomyManager initialized");
    }
    
    /**
     * Shutdown economy manager
     */
    public void shutdown() {
        plugin.getLogger().at(Level.INFO).log("Shutting down EconomyManager...");
        essenceLeaderboard.clear();
        plugin.getLogger().at(Level.INFO).log("EconomyManager shut down");
    }
    
    /**
     * Transfer essence between players
     */
    public boolean transferEssence(UUID fromUUID, UUID toUUID, BigDecimal amount) {
        PlayerData from = plugin.getPlayerDataManager().getPlayerData(fromUUID);
        PlayerData to = plugin.getPlayerDataManager().getPlayerData(toUUID);
        
        if (from == null || to == null) {
            return false;
        }
        
        // Check if sender has enough
        if (!from.hasEssence(amount)) {
            return false;
        }
        
        // Process transaction
        from.removeEssence(amount);
        to.addEssence(amount);
        
        // Save both players
        plugin.getPlayerDataManager().savePlayerData(from);
        plugin.getPlayerDataManager().savePlayerData(to);
        
        // Log transaction
        logTransaction(fromUUID, toUUID, "ESSENCE", amount);
        
        plugin.getLogger().at(Level.INFO).log(
            "Transfer: " + from.getUsername() + " -> " + to.getUsername() + ": " + amount + " Essence"
        );
        
        return true;
    }
    
    /**
     * Convert Essence to Gems
     */
    public boolean convertEssenceToGems(PlayerData playerData, BigDecimal essenceAmount) {
        if (!playerData.hasEssence(essenceAmount)) {
            return false;
        }
        
        // Calculate gems (1M Essence = 1 Gem)
        long gems = essenceAmount.divide(BigDecimal.valueOf(ESSENCE_TO_GEMS)).longValue();
        
        if (gems <= 0) {
            return false;
        }
        
        // Remove essence, add gems
        BigDecimal actualEssence = BigDecimal.valueOf(gems * ESSENCE_TO_GEMS);
        playerData.removeEssence(actualEssence);
        playerData.addGems(gems);
        
        plugin.getPlayerDataManager().savePlayerData(playerData);
        
        plugin.getLogger().at(Level.FINE).log(
            playerData.getUsername() + " converted " + actualEssence + " Essence to " + gems + " Gems"
        );
        
        return true;
    }
    
    /**
     * Convert Gems to Crystals
     */
    public boolean convertGemsToCrystals(PlayerData playerData, long gemsAmount) {
        if (playerData.getGems() < gemsAmount) {
            return false;
        }
        
        // Calculate crystals (1K Gems = 1 Crystal)
        int crystals = (int)(gemsAmount / GEMS_TO_CRYSTALS);
        
        if (crystals <= 0) {
            return false;
        }
        
        // Remove gems, add crystals
        long actualGems = crystals * GEMS_TO_CRYSTALS;
        playerData.removeGems(actualGems);
        playerData.addCrystals(crystals);
        
        plugin.getPlayerDataManager().savePlayerData(playerData);
        
        plugin.getLogger().at(Level.FINE).log(
            playerData.getUsername() + " converted " + actualGems + " Gems to " + crystals + " Crystals"
        );
        
        return true;
    }
    
    /**
     * Get essence leaderboard
     */
    public List<LeaderboardEntry> getEssenceLeaderboard() {
        // Refresh if cache is old
        if (System.currentTimeMillis() - lastLeaderboardUpdate > LEADERBOARD_CACHE_TIME) {
            refreshLeaderboard();
        }
        
        return new ArrayList<>(essenceLeaderboard);
    }
    
    /**
     * Refresh leaderboard from database
     */
    private void refreshLeaderboard() {
        String sql = "SELECT username, essence FROM players ORDER BY essence DESC LIMIT 100";
        
        List<LeaderboardEntry> newLeaderboard = new ArrayList<>();
        
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            int rank = 1;
            while (rs.next()) {
                newLeaderboard.add(new LeaderboardEntry(
                    rank++,
                    rs.getString("username"),
                    new BigDecimal(rs.getString("essence"))
                ));
            }
            
            essenceLeaderboard = newLeaderboard;
            lastLeaderboardUpdate = System.currentTimeMillis();
            
        } catch (SQLException e) {
            plugin.getLogger().at(Level.SEVERE).log("Failed to refresh leaderboard: " + e.getMessage());
        }
    }
    
    /**
     * Get player rank on leaderboard
     */
    public int getPlayerRank(UUID playerUUID) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(playerUUID);
        if (playerData == null) {
            return -1;
        }
        
        String sql = "SELECT COUNT(*) + 1 as rank FROM players WHERE essence > (SELECT essence FROM players WHERE uuid = ?)";
        
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("rank");
            }
            
        } catch (SQLException e) {
            plugin.getLogger().at(Level.WARNING).log("Failed to get player rank: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Log transaction to database
     */
    private void logTransaction(UUID fromUUID, UUID toUUID, String type, BigDecimal amount) {
        String sql = "INSERT INTO transactions (from_uuid, to_uuid, type, amount, timestamp) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, fromUUID.toString());
            stmt.setString(2, toUUID.toString());
            stmt.setString(3, type);
            stmt.setString(4, amount.toPlainString());
            stmt.setLong(5, System.currentTimeMillis());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().at(Level.WARNING).log("Failed to log transaction: " + e.getMessage());
        }
    }
    
    /**
     * Get economy statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        String sql = "SELECT SUM(essence) as total_essence, SUM(gems) as total_gems, SUM(crystals) as total_crystals FROM players";
        
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                stats.put("total_essence", new BigDecimal(rs.getString("total_essence")));
                stats.put("total_gems", rs.getLong("total_gems"));
                stats.put("total_crystals", rs.getInt("total_crystals"));
            }
            
        } catch (SQLException e) {
            plugin.getLogger().at(Level.WARNING).log("Failed to get economy stats: " + e.getMessage());
        }
        
        stats.put("leaderboard_entries", essenceLeaderboard.size());
        stats.put("last_update", lastLeaderboardUpdate);
        
        return stats;
    }
    
    /**
     * Leaderboard Entry
     */
    public static class LeaderboardEntry {
        private final int rank;
        private final String username;
        private final BigDecimal essence;
        
        public LeaderboardEntry(int rank, String username, BigDecimal essence) {
            this.rank = rank;
            this.username = username;
            this.essence = essence;
        }
        
        public int getRank() { return rank; }
        public String getUsername() { return username; }
        public BigDecimal getEssence() { return essence; }
    }
}
