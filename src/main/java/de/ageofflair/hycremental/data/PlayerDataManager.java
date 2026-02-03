package de.ageofflair.hycremental.data;

import de.ageofflair.hycremental.Hycremental;

import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Player Data Manager - Manages player data loading/saving
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class PlayerDataManager {
    
    private final Hycremental plugin;
    private final Map<UUID, PlayerData> playerDataCache;
    
    public PlayerDataManager(Hycremental plugin) {
        this.plugin = plugin;
        this.playerDataCache = new ConcurrentHashMap<>();
    }
    
    /**
     * Initialize manager
     */
    public void initialize() {
        plugin.getLogger().at(Level.INFO).log("PlayerDataManager initialized");
    }
    
    /**
     * Shutdown manager
     */
    public void shutdown() {
        saveAllPlayers();
        playerDataCache.clear();
        plugin.getLogger().at(Level.INFO).log("PlayerDataManager shut down");
    }
    
    /**
     * Get player data from cache or load from database
     */
    public PlayerData getPlayerData(UUID uuid) {
        return playerDataCache.computeIfAbsent(uuid, this::loadPlayerData);
    }
    
    /**
     * Load player data from database
     */
    private PlayerData loadPlayerData(UUID uuid) {
        String sql = "SELECT * FROM players WHERE uuid = ?";
        
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return PlayerData.fromResultSet(rs);
            }
            
        } catch (SQLException e) {
            plugin.getLogger().at(Level.SEVERE).log("Failed to load player data: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Save player data to database
     */
    public void savePlayerData(PlayerData data) {
        String sql = "INSERT INTO players (uuid, username, essence, gems, crystals, " +
                     "prestige_level, ascension_level, rebirth_count, island_size, " +
                     "generator_slots, blocks_mined, generators_purchased, lifetime_essence, " +
                     "first_join, last_join) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE username=?, essence=?, gems=?, crystals=?, " +
                     "prestige_level=?, ascension_level=?, rebirth_count=?, island_size=?, " +
                     "generator_slots=?, blocks_mined=?, generators_purchased=?, lifetime_essence=?, last_join=?";
        
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // INSERT values
            stmt.setString(1, data.getUuid().toString());
            stmt.setString(2, data.getUsername());
            stmt.setString(3, data.getEssence().toPlainString());
            stmt.setLong(4, data.getGems());
            stmt.setInt(5, data.getCrystals());
            stmt.setInt(6, data.getPrestigeLevel());
            stmt.setInt(7, data.getAscensionLevel());
            stmt.setInt(8, data.getRebirthCount());
            stmt.setInt(9, data.getIslandSize());
            stmt.setInt(10, data.getGeneratorSlots());
            stmt.setLong(11, data.getBlocksMined());
            stmt.setLong(12, data.getGeneratorsPurchased());
            stmt.setString(13, data.getLifetimeEssence().toPlainString());
            stmt.setLong(14, data.getFirstJoin());
            stmt.setLong(15, data.getLastJoin());
            
            // UPDATE values
            stmt.setString(16, data.getUsername());
            stmt.setString(17, data.getEssence().toPlainString());
            stmt.setLong(18, data.getGems());
            stmt.setInt(19, data.getCrystals());
            stmt.setInt(20, data.getPrestigeLevel());
            stmt.setInt(21, data.getAscensionLevel());
            stmt.setInt(22, data.getRebirthCount());
            stmt.setInt(23, data.getIslandSize());
            stmt.setInt(24, data.getGeneratorSlots());
            stmt.setLong(25, data.getBlocksMined());
            stmt.setLong(26, data.getGeneratorsPurchased());
            stmt.setString(27, data.getLifetimeEssence().toPlainString());
            stmt.setLong(28, data.getLastJoin());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().at(Level.SEVERE).log("Failed to save player data: " + e.getMessage());
        }
    }
    
    /**
     * Save all cached player data
     */
    public void saveAllPlayers() {
        for (PlayerData data : playerDataCache.values()) {
            savePlayerData(data);
        }
    }
    
    /**
     * Remove player from cache
     */
    public void unloadPlayer(UUID uuid) {
        PlayerData data = playerDataCache.remove(uuid);
        if (data != null) {
            savePlayerData(data);
        }
    }
}
