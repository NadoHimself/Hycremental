package de.ageofflair.hycremental;

import com.hypixel.hytale.server.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.event.PlayerReadyEvent;
import com.hypixel.hytale.server.core.event.EventHandler;
import com.hypixel.hytale.server.player.ServerPlayer;

import de.ageofflair.hycremental.commands.*;
import de.ageofflair.hycremental.data.DatabaseManager;
import de.ageofflair.hycremental.data.PlayerDataManager;
import de.ageofflair.hycremental.core.EconomyManager;
import de.ageofflair.hycremental.core.GeneratorManager;
import de.ageofflair.hycremental.core.IslandManager;
import de.ageofflair.hycremental.core.PrestigeManager;
import de.ageofflair.hycremental.listeners.*;
import de.ageofflair.hycremental.utils.ConfigManager;

import java.util.logging.Level;

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
public class Hycremental extends JavaPlugin {
    
    // Plugin instance
    private static Hycremental instance;
    
    // Core managers
    private DatabaseManager databaseManager;
    private PlayerDataManager playerDataManager;
    private GeneratorManager generatorManager;
    private IslandManager islandManager;
    private EconomyManager economyManager;
    private PrestigeManager prestigeManager;
    private ConfigManager configManager;
    
    /**
     * Plugin startup - Called when plugin is loaded
     */
    @Override
    public void onEnable() {
        instance = this;
        
        getLogger().log(Level.INFO, "====================================");
        getLogger().log(Level.INFO, "   Hycremental v1.0.0-ALPHA");
        getLogger().log(Level.INFO, "   by NadoHimself (Kielian)");
        getLogger().log(Level.INFO, "====================================");
        
        // Initialize configuration
        getLogger().log(Level.INFO, "Loading configuration...");
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        // Initialize database
        getLogger().log(Level.INFO, "Connecting to database...");
        databaseManager = new DatabaseManager();
        if (!databaseManager.connect()) {
            getLogger().log(Level.SEVERE, "Failed to connect to database! Plugin disabled.");
            return;
        }
        databaseManager.initializeTables();
        
        // Initialize managers
        getLogger().log(Level.INFO, "Initializing managers...");
        playerDataManager = new PlayerDataManager(this);
        playerDataManager.initialize();
        generatorManager = new GeneratorManager(this);
        islandManager = new IslandManager(this);
        economyManager = new EconomyManager(this);
        prestigeManager = new PrestigeManager(this);
        
        // Register commands
        getLogger().log(Level.INFO, "Registering commands...");
        registerCommands();
        
        // Register events
        getLogger().log(Level.INFO, "Registering event listeners...");
        registerEvents();
        
        // Start background tasks
        getLogger().log(Level.INFO, "Starting background tasks...");
        startBackgroundTasks();
        
        getLogger().log(Level.INFO, "Hycremental has been enabled successfully!");
        getLogger().log(Level.INFO, "====================================");
    }
    
    /**
     * Plugin shutdown - Called when plugin is unloaded
     */
    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Shutting down Hycremental...");
        
        // Save all data
        if (playerDataManager != null) {
            getLogger().log(Level.INFO, "Saving player data...");
            playerDataManager.shutdown();
        }
        
        // Stop generators
        if (generatorManager != null) {
            getLogger().log(Level.INFO, "Stopping generators...");
            generatorManager.stopAllGenerators();
        }
        
        // Close database
        if (databaseManager != null) {
            getLogger().log(Level.INFO, "Closing database connection...");
            databaseManager.disconnect();
        }
        
        getLogger().log(Level.INFO, "Hycremental has been disabled.");
    }
    
    /**
     * Register all commands
     */
    private void registerCommands() {
        getCommandManager().register(new EssenceCommand(this));
        getCommandManager().register(new IslandCommand(this));
        getCommandManager().register(new ShopCommand(this));
        getCommandManager().register(new GeneratorCommand(this));
        getCommandManager().register(new PrestigeCommand(this));
    }
    
    /**
     * Register all event listeners
     */
    private void registerEvents() {
        getEventManager().register(new PlayerJoinListener(this));
        getEventManager().register(new BlockBreakListener(this));
        getEventManager().register(new GeneratorPlaceListener(this));
        getEventManager().register(new GeneratorInteractListener(this));
    }
    
    /**
     * Start background tasks using Hytale Scheduler
     */
    private void startBackgroundTasks() {
        // Auto-save task - every 5 minutes (6000 ticks)
        getScheduler().runRepeating(() -> {
            getLogger().log(Level.INFO, "Auto-saving player data...");
            playerDataManager.saveAllPlayers();
        }, 6000, 6000);
        
        // Generator production task - every second (20 ticks)
        getScheduler().runRepeating(() -> {
            generatorManager.processAllGenerators();
        }, 20, 20);
        
        // Leaderboard update task - every hour (72000 ticks)
        getScheduler().runRepeating(() -> {
            getLogger().log(Level.INFO, "Updating leaderboards...");
            // TODO: Update leaderboards
        }, 72000, 72000);
    }
    
    // Getters
    
    public static Hycremental getInstance() {
        return instance;
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
