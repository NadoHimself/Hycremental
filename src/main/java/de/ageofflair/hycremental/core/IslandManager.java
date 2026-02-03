package de.ageofflair.hycremental.core;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.island.IslandData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Island Manager - Handles player islands
 * 
 * Features:
 * - Island creation and deletion
 * - Island boundary management
 * - Location-based island lookups
 * - Database persistence
 * - Thread-safe operations
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class IslandManager {
    
    private final Hycremental plugin;
    
    // Active islands (Player UUID -> IslandData)
    private final Map<UUID, IslandData> islands;
    
    // Island spacing (blocks between islands)
    private static final int ISLAND_SPACING = 1000;
    
    // Next island position
    private int nextIslandX = 0;
    private int nextIslandZ = 0;
    private static final int ISLANDS_PER_ROW = 10;
    
    public IslandManager(Hycremental plugin) {
        this.plugin = plugin;
        this.islands = new ConcurrentHashMap<>();
    }
    
    /**
     * Initialize island manager
     */
    public void initialize() {
        plugin.getLogger().at(Level.INFO).log("Initializing IslandManager...");
        
        // Load all islands from database
        loadAllIslands();
        
        // Calculate next island position
        calculateNextIslandPosition();
        
        plugin.getLogger().at(Level.INFO).log("IslandManager initialized with " + islands.size() + " islands");
    }
    
    /**
     * Shutdown island manager
     */
    public void shutdown() {
        plugin.getLogger().at(Level.INFO).log("Shutting down IslandManager...");
        
        // Save all islands
        saveAllIslands();
        
        // Clear cache
        islands.clear();
        
        plugin.getLogger().at(Level.INFO).log("IslandManager shut down");
    }
    
    /**
     * Create new island for player
     */
    public IslandData createIsland(IslandData island) {
        // Calculate spawn position
        int centerX = nextIslandX * ISLAND_SPACING;
        int centerZ = nextIslandZ * ISLAND_SPACING;
        
        island.setCenterX(centerX);
        island.setCenterZ(centerZ);
        island.setSpawnX(centerX);
        island.setSpawnY(100); // Default spawn height
        island.setSpawnZ(centerZ);
        
        // Add to cache
        islands.put(island.getOwnerUUID(), island);
        
        // Save to database
        saveIsland(island);
        
        // Update next position
        incrementIslandPosition();
        
        plugin.getLogger().at(Level.INFO).log("Created island for player " + island.getOwnerUUID() + " at " + centerX + ", " + centerZ);
        
        // TODO: Generate island structure with Hytale World API
        // generateIslandStructure(island);
        
        return island;
    }
    
    /**
     * Get island by player UUID
     */
    public IslandData getIsland(UUID playerUUID) {
        return islands.get(playerUUID);
    }
    
    /**
     * Delete island
     */
    public void deleteIsland(UUID playerUUID) {
        IslandData island = islands.remove(playerUUID);
        
        if (island != null) {
            // Remove from database
            deleteIslandFromDB(playerUUID);
            
            // TODO: Reset island chunks with Hytale World API
            // resetIslandChunks(island);
            
            plugin.getLogger().at(Level.INFO).log("Deleted island for player " + playerUUID);
        }
    }
    
    /**
     * Check if location is within island boundary
     */
    public boolean isWithinIslandBounds(UUID playerUUID, int x, int z) {
        IslandData island = islands.get(playerUUID);
        if (island == null) {
            return false;
        }
        
        int centerX = island.getCenterX();
        int centerZ = island.getCenterZ();
        int radius = island.getRadius();
        
        int dx = Math.abs(x - centerX);
        int dz = Math.abs(z - centerZ);
        
        return dx <= radius && dz <= radius;
    }
    
    /**
     * Get island at specific location
     */
    public IslandData getIslandAt(int x, int z) {
        for (IslandData island : islands.values()) {
            if (isWithinIslandBounds(island.getOwnerUUID(), x, z)) {
                return island;
            }
        }
        return null;
    }
    
    /**
     * Expand island size
     */
    public void expandIsland(UUID playerUUID, int newSize) {
        IslandData island = islands.get(playerUUID);
        if (island != null) {
            island.setSize(newSize);
            saveIsland(island);
            
            plugin.getLogger().at(Level.INFO).log("Expanded island for player " + playerUUID + " to size " + newSize);
        }
    }
    
    /**
     * Calculate next island position
     */
    private void calculateNextIslandPosition() {
        int islandCount = islands.size();
        nextIslandX = islandCount % ISLANDS_PER_ROW;
        nextIslandZ = islandCount / ISLANDS_PER_ROW;
    }
    
    /**
     * Increment island position counter
     */
    private void incrementIslandPosition() {
        nextIslandX++;
        if (nextIslandX >= ISLANDS_PER_ROW) {
            nextIslandX = 0;
            nextIslandZ++;
        }
    }
    
    /**
     * Load all islands from database
     */
    private void loadAllIslands() {
        String sql = "SELECT * FROM islands";
        
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                IslandData island = new IslandData(
                    UUID.fromString(rs.getString("owner_uuid"))
                );
                island.setCenterX(rs.getInt("center_x"));
                island.setCenterZ(rs.getInt("center_z"));
                island.setSpawnX(rs.getInt("spawn_x"));
                island.setSpawnY(rs.getInt("spawn_y"));
                island.setSpawnZ(rs.getInt("spawn_z"));
                island.setSize(rs.getInt("size"));
                island.setCreatedAt(rs.getLong("created_at"));
                
                islands.put(island.getOwnerUUID(), island);
            }
            
        } catch (SQLException e) {
            plugin.getLogger().at(Level.SEVERE).log("Failed to load islands: " + e.getMessage());
        }
    }
    
    /**
     * Save all islands to database
     */
    private void saveAllIslands() {
        for (IslandData island : islands.values()) {
            saveIsland(island);
        }
    }
    
    /**
     * Save island to database
     */
    private void saveIsland(IslandData island) {
        String sql = "INSERT INTO islands (owner_uuid, center_x, center_z, spawn_x, spawn_y, spawn_z, size, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE spawn_x=?, spawn_y=?, spawn_z=?, size=?";
        
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, island.getOwnerUUID().toString());
            stmt.setInt(2, island.getCenterX());
            stmt.setInt(3, island.getCenterZ());
            stmt.setInt(4, island.getSpawnX());
            stmt.setInt(5, island.getSpawnY());
            stmt.setInt(6, island.getSpawnZ());
            stmt.setInt(7, island.getSize());
            stmt.setLong(8, island.getCreatedAt());
            
            // ON DUPLICATE KEY UPDATE
            stmt.setInt(9, island.getSpawnX());
            stmt.setInt(10, island.getSpawnY());
            stmt.setInt(11, island.getSpawnZ());
            stmt.setInt(12, island.getSize());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().at(Level.SEVERE).log("Failed to save island for " + island.getOwnerUUID() + ": " + e.getMessage());
        }
    }
    
    /**
     * Delete island from database
     */
    private void deleteIslandFromDB(UUID playerUUID) {
        String sql = "DELETE FROM islands WHERE owner_uuid = ?";
        
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, playerUUID.toString());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().at(Level.SEVERE).log("Failed to delete island for " + playerUUID + ": " + e.getMessage());
        }
    }
    
    /**
     * Generate island structure (placeholder for Hytale World API)
     */
    private void generateIslandStructure(IslandData island) {
        // TODO: Implement with Hytale World Generation API
        // 
        // World world = plugin.getServer().getWorld("skyblock");
        // world.execute(() -> {
        //     int centerX = island.getCenterX();
        //     int centerZ = island.getCenterZ();
        //     int y = 100;
        //     
        //     // Generate starting platform
        //     for (int x = -5; x <= 5; x++) {
        //         for (int z = -5; z <= 5; z++) {
        //             world.setBlock(centerX + x, y, centerZ + z, BlockType.GRASS);
        //         }
        //     }
        //     
        //     // Add starter tree
        //     world.setBlock(centerX, y + 1, centerZ, BlockType.OAK_LOG);
        //     // etc...
        // });
    }
    
    /**
     * Get island statistics
     */
    public Map<String, Object> getStatistics() {
        return Map.of(
            "total_islands", islands.size(),
            "next_position_x", nextIslandX,
            "next_position_z", nextIslandZ
        );
    }
}
