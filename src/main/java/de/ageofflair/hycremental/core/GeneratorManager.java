package de.ageofflair.hycremental.core;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.generators.Generator;
import de.ageofflair.hycremental.generators.GeneratorType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Generator Manager - Handles all generator operations
 * 
 * Features:
 * - Generator registration and tracking
 * - Automatic production tick system
 * - Location-based lookups
 * - Database persistence
 * - Thread-safe operations with Hytale World API
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class GeneratorManager {
    
    private final Hycremental plugin;
    
    // Active generators (UUID -> Generator)
    private final Map<UUID, Generator> generators;
    
    // Location index for fast lookups ("x,y,z" -> Generator UUID)
    private final Map<String, UUID> locationIndex;
    
    // Player index (Player UUID -> List of Generator UUIDs)
    private final Map<UUID, List<UUID>> playerGenerators;
    
    // Production task ID
    private Object productionTaskId;
    
    public GeneratorManager(Hycremental plugin) {
        this.plugin = plugin;
        this.generators = new ConcurrentHashMap<>();
        this.locationIndex = new ConcurrentHashMap<>();
        this.playerGenerators = new ConcurrentHashMap<>();
    }
    
    /**
     * Initialize generator manager
     */
    public void initialize() {
        plugin.getLogger().at(Level.INFO).log("Initializing GeneratorManager...");
        
        // Load all generators from database
        loadAllGenerators();
        
        // Start production tick system (every second)
        startProductionTick();
        
        plugin.getLogger().at(Level.INFO).log("GeneratorManager initialized with " + generators.size() + " generators");
    }
    
    /**
     * Shutdown generator manager
     */
    public void shutdown() {
        plugin.getLogger().at(Level.INFO).log("Shutting down GeneratorManager...");
        
        // Stop production tick
        if (productionTaskId != null) {
            plugin.getScheduler().cancel(productionTaskId);
        }
        
        // Save all generators
        saveAllGenerators();
        
        // Clear caches
        generators.clear();
        locationIndex.clear();
        playerGenerators.clear();
        
        plugin.getLogger().at(Level.INFO).log("GeneratorManager shut down");
    }
    
    /**
     * Start production tick system
     */
    private void startProductionTick() {
        // Run every second (20 ticks)
        productionTaskId = plugin.getScheduler().executeRepeating(() -> {
            processProduction();
        }, 0L, 20L); // 0 delay, 20 ticks (1 second) interval
    }
    
    /**
     * Process production for all generators
     */
    private void processProduction() {
        if (generators.isEmpty()) {
            return;
        }
        
        long startTime = System.currentTimeMillis();
        int processed = 0;
        
        for (Generator generator : generators.values()) {
            try {
                // Get owner data
                PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(generator.getOwnerUUID());
                if (playerData == null) {
                    continue;
                }
                
                // Calculate production with multipliers
                double production = generator.calculateProduction();
                double withMultiplier = production * playerData.calculateTotalMultiplier();
                
                // Add to player balance
                playerData.addEssence(BigDecimal.valueOf(withMultiplier));
                
                // Update last production time
                generator.setLastProduction(System.currentTimeMillis());
                
                processed++;
                
            } catch (Exception e) {
                plugin.getLogger().at(Level.WARNING).log("Error processing generator " + generator.getUuid() + ": " + e.getMessage());
            }
        }
        
        // Auto-save player data every 30 seconds
        if (System.currentTimeMillis() % 30000 < 1000) {
            plugin.getPlayerDataManager().saveAllPlayers();
        }
        
        long duration = System.currentTimeMillis() - startTime;
        if (duration > 50) { // Log if taking too long
            plugin.getLogger().at(Level.WARNING).log("Production tick took " + duration + "ms for " + processed + " generators");
        }
    }
    
    /**
     * Add generator to manager
     */
    public void addGenerator(Generator generator) {
        generators.put(generator.getUuid(), generator);
        
        // Update location index
        String locKey = getLocationKey(generator.getX(), generator.getY(), generator.getZ());
        locationIndex.put(locKey, generator.getUuid());
        
        // Update player index
        playerGenerators.computeIfAbsent(generator.getOwnerUUID(), k -> new ArrayList<>())
                        .add(generator.getUuid());
        
        // Save to database
        saveGenerator(generator);
        
        plugin.getLogger().at(Level.FINE).log("Added generator: " + generator.getType().name() + " at " + locKey);
    }
    
    /**
     * Remove generator from manager
     */
    public void removeGenerator(Generator generator) {
        generators.remove(generator.getUuid());
        
        // Update location index
        String locKey = getLocationKey(generator.getX(), generator.getY(), generator.getZ());
        locationIndex.remove(locKey);
        
        // Update player index
        List<UUID> playerGens = playerGenerators.get(generator.getOwnerUUID());
        if (playerGens != null) {
            playerGens.remove(generator.getUuid());
        }
        
        // Remove from database
        deleteGenerator(generator.getUuid());
        
        plugin.getLogger().at(Level.FINE).log("Removed generator: " + generator.getUuid());
    }
    
    /**
     * Get generator at specific location
     */
    public Generator getGeneratorAt(int x, int y, int z) {
        String locKey = getLocationKey(x, y, z);
        UUID generatorId = locationIndex.get(locKey);
        return generatorId != null ? generators.get(generatorId) : null;
    }
    
    /**
     * Get all generators owned by player
     */
    public List<Generator> getPlayerGenerators(UUID playerUUID) {
        List<UUID> genUUIDs = playerGenerators.get(playerUUID);
        if (genUUIDs == null || genUUIDs.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Generator> result = new ArrayList<>();
        for (UUID genUUID : genUUIDs) {
            Generator gen = generators.get(genUUID);
            if (gen != null) {
                result.add(gen);
            }
        }
        return result;
    }
    
    /**
     * Get player generator count
     */
    public int getPlayerGeneratorCount(UUID playerUUID) {
        List<UUID> genUUIDs = playerGenerators.get(playerUUID);
        return genUUIDs != null ? genUUIDs.size() : 0;
    }
    
    /**
     * Load player's generators from database
     */
    public void loadPlayerGenerators(UUID playerUUID) {
        String sql = "SELECT * FROM generators WHERE owner_uuid = ?";
        
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();
            
            int loaded = 0;
            while (rs.next()) {
                Generator generator = Generator.fromResultSet(rs);
                addGenerator(generator);
                loaded++;
            }
            
            if (loaded > 0) {
                plugin.getLogger().at(Level.FINE).log("Loaded " + loaded + " generators for player " + playerUUID);
            }
            
        } catch (SQLException e) {
            plugin.getLogger().at(Level.SEVERE).log("Failed to load generators for player " + playerUUID + ": " + e.getMessage());
        }
    }
    
    /**
     * Reset player generators (for prestige/ascension/rebirth)
     */
    public void resetPlayerGenerators(UUID playerUUID, boolean removeAll) {
        List<Generator> playerGens = getPlayerGenerators(playerUUID);
        
        if (removeAll) {
            // Remove all generators
            for (Generator gen : playerGens) {
                removeGenerator(gen);
            }
        } else {
            // Remove only higher tier generators (keep Tier 1)
            for (Generator gen : playerGens) {
                if (gen.getType().getTier() > 1) {
                    removeGenerator(gen);
                }
            }
        }
    }
    
    /**
     * Load all generators from database
     */
    private void loadAllGenerators() {
        String sql = "SELECT * FROM generators";
        
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Generator generator = Generator.fromResultSet(rs);
                
                generators.put(generator.getUuid(), generator);
                
                String locKey = getLocationKey(generator.getX(), generator.getY(), generator.getZ());
                locationIndex.put(locKey, generator.getUuid());
                
                playerGenerators.computeIfAbsent(generator.getOwnerUUID(), k -> new ArrayList<>())
                                .add(generator.getUuid());
            }
            
        } catch (SQLException e) {
            plugin.getLogger().at(Level.SEVERE).log("Failed to load generators: " + e.getMessage());
        }
    }
    
    /**
     * Save all generators to database
     */
    private void saveAllGenerators() {
        for (Generator generator : generators.values()) {
            saveGenerator(generator);
        }
    }
    
    /**
     * Save generator to database
     */
    private void saveGenerator(Generator generator) {
        String sql = "INSERT INTO generators (uuid, owner_uuid, type, x, y, z, level, quality, enchantments, placed_at, last_production) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE level=?, enchantments=?, last_production=?";
        
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, generator.getUuid().toString());
            stmt.setString(2, generator.getOwnerUUID().toString());
            stmt.setString(3, generator.getType().name());
            stmt.setInt(4, generator.getX());
            stmt.setInt(5, generator.getY());
            stmt.setInt(6, generator.getZ());
            stmt.setInt(7, generator.getLevel());
            stmt.setString(8, generator.getQuality().name());
            stmt.setString(9, generator.serializeEnchantments());
            stmt.setLong(10, generator.getPlacedAt());
            stmt.setLong(11, generator.getLastProduction());
            
            // ON DUPLICATE KEY UPDATE
            stmt.setInt(12, generator.getLevel());
            stmt.setString(13, generator.serializeEnchantments());
            stmt.setLong(14, generator.getLastProduction());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().at(Level.SEVERE).log("Failed to save generator " + generator.getUuid() + ": " + e.getMessage());
        }
    }
    
    /**
     * Delete generator from database
     */
    private void deleteGenerator(UUID generatorUUID) {
        String sql = "DELETE FROM generators WHERE uuid = ?";
        
        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, generatorUUID.toString());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().at(Level.SEVERE).log("Failed to delete generator " + generatorUUID + ": " + e.getMessage());
        }
    }
    
    /**
     * Get location key for indexing
     */
    private String getLocationKey(int x, int y, int z) {
        return x + "," + y + "," + z;
    }
    
    /**
     * Get generator statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_generators", generators.size());
        stats.put("active_players", playerGenerators.size());
        
        // Count by type
        Map<GeneratorType, Integer> byType = new HashMap<>();
        for (Generator gen : generators.values()) {
            byType.put(gen.getType(), byType.getOrDefault(gen.getType(), 0) + 1);
        }
        stats.put("by_type", byType);
        
        return stats;
    }
}
