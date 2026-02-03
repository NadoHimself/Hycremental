package de.ageofflair.hycremental;

import de.ageofflair.hycremental.data.DatabaseManager;
import de.ageofflair.hycremental.data.PlayerDataManager;
import de.ageofflair.hycremental.core.GeneratorManager;
import de.ageofflair.hycremental.core.IslandManager;
import de.ageofflair.hycremental.core.EconomyManager;
import de.ageofflair.hycremental.core.PrestigeManager;

import java.util.logging.Logger;

/**
 * Hycremental - Das ultimative Incremental Generator Tycoon Gamemode für Hytale
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 * @since 2026-02-03
 */
public class Hycremental {
    
    private static Hycremental instance;
    private Logger logger;
    
    // Core Managers
    private DatabaseManager databaseManager;
    private PlayerDataManager playerDataManager;
    private GeneratorManager generatorManager;
    private IslandManager islandManager;
    private EconomyManager economyManager;
    private PrestigeManager prestigeManager;
    
    // Plugin State
    private boolean enabled = false;
    
    /**
     * Setup phase - Called before the plugin starts
     * Initialize all managers and load configurations
     */
    public void setup() {
        instance = this;
        logger = Logger.getLogger("Hycremental");
        
        logger.info("═══════════════════════════════════════════");
        logger.info("  Hycremental v1.0.0-ALPHA");
        logger.info("  Initializing...");
        logger.info("═══════════════════════════════════════════");
        
        try {
            // Load Configuration
            logger.info("Loading configuration files...");
            // TODO: ConfigManager.loadConfigs();
            
            // Initialize Database
            logger.info("Connecting to database...");
            this.databaseManager = new DatabaseManager();
            this.databaseManager.connect();
            
            // Initialize Managers
            logger.info("Initializing managers...");
            this.playerDataManager = new PlayerDataManager(databaseManager);
            // TODO: Initialize other managers when they're created
            
            logger.info("Hycremental setup completed successfully!");
            
        } catch (Exception e) {
            logger.severe("Failed to setup Hycremental: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Start phase - Called when the plugin is ready to start
     * Register events, commands, and start background tasks
     */
    public void start() {
        logger.info("═══════════════════════════════════════════");
        logger.info("  Starting Hycremental...");
        logger.info("═══════════════════════════════════════════");
        
        try {
            // Register Event Listeners
            logger.info("Registering event listeners...");
            // TODO: Register events
            
            // Register Commands
            logger.info("Registering commands...");
            // TODO: Register commands
            
            // Start Background Tasks
            logger.info("Starting background tasks...");
            // TODO: Start generator tick system
            // TODO: Start auto-save system
            
            this.enabled = true;
            
            logger.info("═══════════════════════════════════════════");
            logger.info("  Hycremental successfully started!");
            logger.info("  Ready for players to build their empires!");
            logger.info("═══════════════════════════════════════════");
            
        } catch (Exception e) {
            logger.severe("Failed to start Hycremental: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Shutdown phase - Called when the plugin is shutting down
     * Save all data and close connections
     */
    public void shutdown() {
        logger.info("═══════════════════════════════════════════");
        logger.info("  Shutting down Hycremental...");
        logger.info("═══════════════════════════════════════════");
        
        try {
            this.enabled = false;
            
            // Stop Background Tasks
            logger.info("Stopping background tasks...");
            // TODO: Stop scheduled tasks
            
            // Save All Player Data
            logger.info("Saving all player data...");
            if (playerDataManager != null) {
                playerDataManager.saveAllPlayers();
            }
            
            // Close Database Connection
            logger.info("Closing database connection...");
            if (databaseManager != null) {
                databaseManager.disconnect();
            }
            
            logger.info("═══════════════════════════════════════════");
            logger.info("  Hycremental shut down successfully!");
            logger.info("═══════════════════════════════════════════");
            
        } catch (Exception e) {
            logger.severe("Error during shutdown: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get the plugin instance
     * @return Plugin instance
     */
    public static Hycremental getInstance() {
        return instance;
    }
    
    /**
     * Get the logger
     * @return Logger instance
     */
    public Logger getLogger() {
        return logger;
    }
    
    /**
     * Check if plugin is enabled
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    // Getter methods for managers
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    
    public GeneratorManager getGeneratorManager() {
        return generatorManager;
    }
    
    public IslandManager getIslandManager() {
        return islandManager;
    }
    
    public EconomyManager getEconomyManager() {
        return economyManager;
    }
    
    public PrestigeManager getPrestigeManager() {
        return prestigeManager;
    }
}
