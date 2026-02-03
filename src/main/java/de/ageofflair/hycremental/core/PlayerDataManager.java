package de.ageofflair.hycremental.core;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Manages player data (loading, saving, caching)
 * 
 * @author Kielian
 */
public class PlayerDataManager {
    
    private final Hycremental plugin;
    private final Logger logger;
    private final DatabaseManager database;
    
    // Cache of loaded player data
    private final Map<UUID, PlayerData> playerCache;
    
    public PlayerDataManager(Hycremental plugin) {
        this.plugin = plugin;
        this.logger = plugin.getPluginLogger();
        this.database = plugin.getDatabaseManager();
        this.playerCache = new ConcurrentHashMap<>();
    }
    
    /**
     * Load player data from database or create new
     */
    public PlayerData loadPlayer(UUID uuid, String username) {
        // Check cache first
        if (playerCache.containsKey(uuid)) {
            return playerCache.get(uuid);
        }
        
        logger.info("Loading player data for " + username + " (" + uuid + ")");
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM players WHERE uuid = ?"
             )) {
            
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            PlayerData data;
            if (rs.next()) {
                // Load existing data
                data = new PlayerData(uuid, rs.getString("username"));
                data.setEssence(rs.getBigDecimal("essence"));
                data.setPrestigeLevel(rs.getInt("prestige_level"));
                data.setAscensionLevel(rs.getInt("ascension_level"));
                // Load other fields...
                logger.info(" - Loaded existing player data");
            } else {
                // Create new player
                data = new PlayerData(uuid, username);
                savePlayer(data);
                logger.info(" - Created new player data");
            }
            
            // Add to cache
            playerCache.put(uuid, data);
            return data;
            
        } catch (SQLException e) {
            logger.severe("Failed to load player data for " + username);
            e.printStackTrace();
            // Return new data as fallback
            PlayerData fallback = new PlayerData(uuid, username);
            playerCache.put(uuid, fallback);
            return fallback;
        }
    }
    
    /**
     * Save player data to database
     */
    public void savePlayer(PlayerData data) {
        if (!data.isDirty()) {
            return; // No changes to save
        }
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO players (uuid, username, essence, gems, crystals, " +
                 "prestige_level, ascension_level, rebirth_count, " +
                 "total_essence_earned, blocks_mined, last_save) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                 "ON CONFLICT (uuid) DO UPDATE SET " +
                 "username = EXCLUDED.username, " +
                 "essence = EXCLUDED.essence, " +
                 "gems = EXCLUDED.gems, " +
                 "crystals = EXCLUDED.crystals, " +
                 "prestige_level = EXCLUDED.prestige_level, " +
                 "ascension_level = EXCLUDED.ascension_level, " +
                 "rebirth_count = EXCLUDED.rebirth_count, " +
                 "total_essence_earned = EXCLUDED.total_essence_earned, " +
                 "blocks_mined = EXCLUDED.blocks_mined, " +
                 "last_save = CURRENT_TIMESTAMP"
             )) {
            
            stmt.setString(1, data.getUuid().toString());
            stmt.setString(2, data.getUsername());
            stmt.setBigDecimal(3, data.getEssence());
            stmt.setLong(4, data.getGems());
            stmt.setInt(5, data.getCrystals());
            stmt.setInt(6, data.getPrestigeLevel());
            stmt.setInt(7, data.getAscensionLevel());
            stmt.setInt(8, data.getRebirthCount());
            stmt.setBigDecimal(9, data.getTotalEssenceEarned());
            stmt.setLong(10, data.getBlocksMined());
            
            stmt.executeUpdate();
            data.updateLastSave();
            
        } catch (SQLException e) {
            logger.severe("Failed to save player data for " + data.getUsername());
            e.printStackTrace();
        }
    }
    
    /**
     * Save all cached players
     */
    public void saveAllPlayers() {
        logger.info("Saving all player data...");
        int saved = 0;
        
        for (PlayerData data : playerCache.values()) {
            if (data.isDirty()) {
                savePlayer(data);
                saved++;
            }
        }
        
        logger.info("Saved " + saved + " players.");
    }
    
    /**
     * Unload player from cache
     */
    public void unloadPlayer(UUID uuid) {
        PlayerData data = playerCache.remove(uuid);
        if (data != null && data.isDirty()) {
            savePlayer(data);
        }
    }
    
    /**
     * Get cached player data
     */
    public PlayerData getPlayer(UUID uuid) {
        return playerCache.get(uuid);
    }
    
    /**
     * Check if player is loaded
     */
    public boolean isPlayerLoaded(UUID uuid) {
        return playerCache.containsKey(uuid);
    }
}