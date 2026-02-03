package de.ageofflair.hycremental;

import de.ageofflair.hycremental.data.DatabaseManager;
import de.ageofflair.hycremental.data.PlayerDataManager;
import de.ageofflair.hycremental.core.EconomyManager;
import de.ageofflair.hycremental.core.GeneratorManager;
import de.ageofflair.hycremental.core.IslandManager;
import de.ageofflair.hycremental.core.PrestigeManager;
import de.ageofflair.hycremental.utils.ConfigManager;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hycremental - Incremental/Idle game plugin for Hytale
 * 
 * A complex idle game featuring:
 * - 12-tier generator system with quality and enchantments
 * - Triple currency economy (Essence, Gems, Crystals)
 * - Prestige/Ascension/Rebirth progression systems
 * - Player islands with upgradeable plots
 * - Block mining for passive income
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 * @since 2026-02-03
 */
public class Hycremental {
    
    // Plugin instance
    private static Hycremental instance;
    private final Logger logger;
    
    // Core managers
    private DatabaseManager databaseManager;
    private PlayerDataManager playerDataManager;
    private GeneratorManager generatorManager;
    private IslandManager islandManager;
    private EconomyManager economyManager;
    private PrestigeManager prestigeManager;
    private ConfigManager configManager;
    
    public Hycremental() {
        this.logger = Logger.getLogger("Hycremental");
    }
    
    /**
     * Plugin startup - Called when plugin is loaded
     * TODO: Replace with proper Hytale Plugin API when available
     */
    public void start() {
        instance = this;
        
        logger.log(Level.INFO, "====================================");
        logger.log(Level.INFO, "   Hycremental v1.0.0-ALPHA");
        logger.log(Level.INFO, "   by NadoHimself (Kielian)");
        logger.log(Level.INFO, "====================================");
        
        // Initialize configuration
        logger.log(Level.INFO, "Loading configuration...");
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        // Initialize database
        logger.log(Level.INFO, "Connecting to database...");
        databaseManager = new DatabaseManager();
        if (!databaseManager.connect()) {
            logger.log(Level.SEVERE, "Failed to connect to database! Plugin disabled.");
            return;
        }
        databaseManager.initializeTables();
        
        // Initialize managers
        logger.log(Level.INFO, "Initializing managers...");
        playerDataManager = new PlayerDataManager(this);
        playerDataManager.initialize();
        generatorManager = new GeneratorManager(this);
        islandManager = new IslandManager(this);
        economyManager = new EconomyManager(this);
        prestigeManager = new PrestigeManager(this);
        
        // TODO: Register commands when Hytale Command API is known
        logger.log(Level.INFO, "Commands will be registered when API is available");
        
        // TODO: Register events when Hytale Event API is known
        logger.log(Level.INFO, "Events will be registered when API is available");
        
        logger.log(Level.INFO, "Hycremental has been enabled successfully!");
        logger.log(Level.INFO, "====================================");
    }
    
    /**
     * Plugin shutdown - Called when plugin is unloaded
     */
    public void shutdown() {
        logger.log(Level.INFO, "Shutting down Hycremental...");
        
        // Save all data
        if (playerDataManager != null) {
            logger.log(Level.INFO, "Saving player data...");
            playerDataManager.shutdown();
        }
        
        // Stop generators
        if (generatorManager != null) {
            logger.log(Level.INFO, "Stopping generators...");
            generatorManager.stopAllGenerators();
        }
        
        // Close database
        if (databaseManager != null) {
            logger.log(Level.INFO, "Closing database connection...");
            databaseManager.disconnect();
        }
        
        logger.log(Level.INFO, "Hycremental has been disabled.");
    }
    
    // Getters
    
    public static Hycremental getInstance() {
        return instance;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
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
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
}
