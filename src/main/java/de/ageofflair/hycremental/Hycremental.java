package de.ageofflair.hycremental;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import de.ageofflair.hycremental.commands.*;
import de.ageofflair.hycremental.data.DatabaseManager;
import de.ageofflair.hycremental.data.PlayerDataManager;
import de.ageofflair.hycremental.core.EconomyManager;
import de.ageofflair.hycremental.core.GeneratorManager;
import de.ageofflair.hycremental.core.IslandManager;
import de.ageofflair.hycremental.core.PrestigeManager;
import de.ageofflair.hycremental.listeners.*;
import de.ageofflair.hycremental.utils.ConfigManager;

import java.util.concurrent.TimeUnit;
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
    
    public Hycremental(JavaPluginInit init) {
        super(init);
    }
    
    /**
     * Setup phase - Register components, commands, etc.
     */
    @Override
    public void setup() {
        instance = this;
        
        getLogger().info("====================================");
        getLogger().info("   Hycremental v1.0.0-ALPHA");
        getLogger().info("   by NadoHimself (Kielian)");
        getLogger().info("====================================");
        
        // Initialize configuration
        getLogger().info("Loading configuration...");
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        // Register commands
        getLogger().info("Registering commands...");
        registerCommands();
        
        // Register events
        getLogger().info("Registering event listeners...");
        registerEvents();
    }
    
    /**
     * Start phase - Called when server is ready
     */
    @Override
    public void start() {
        // Initialize database
        getLogger().info("Connecting to database...");
        databaseManager = new DatabaseManager();
        if (!databaseManager.connect()) {
            getLogger().severe("Failed to connect to database! Plugin disabled.");
            return;
        }
        databaseManager.initializeTables();
        
        // Initialize managers
        getLogger().info("Initializing managers...");
        playerDataManager = new PlayerDataManager(this);
        playerDataManager.initialize();
        generatorManager = new GeneratorManager(this);
        islandManager = new IslandManager(this);
        economyManager = new EconomyManager(this);
        prestigeManager = new PrestigeManager(this);
        
        // Start background tasks
        getLogger().info("Starting background tasks...");
        startBackgroundTasks();
        
        getLogger().info("Hycremental has been enabled successfully!");
        getLogger().info("====================================");
    }
    
    /**
     * Shutdown phase - Called when plugin is unloaded
     */
    @Override
    public void shutdown() {
        getLogger().info("Shutting down Hycremental...");
        
        // Save all data
        if (playerDataManager != null) {
            getLogger().info("Saving player data...");
            playerDataManager.shutdown();
        }
        
        // Stop generators
        if (generatorManager != null) {
            getLogger().info("Stopping generators...");
            generatorManager.stopAllGenerators();
        }
        
        // Close database
        if (databaseManager != null) {
            getLogger().info("Closing database connection...");
            databaseManager.disconnect();
        }
        
        getLogger().info("Hycremental has been disabled.");
    }
    
    /**
     * Register all commands
     */
    private void registerCommands() {
        getCommandRegistry().registerCommand(new EssenceCommand(this));
        getCommandRegistry().registerCommand(new IslandCommand(this));
        getCommandRegistry().registerCommand(new ShopCommand(this));
        getCommandRegistry().registerCommand(new GeneratorCommand(this));
        getCommandRegistry().registerCommand(new PrestigeCommand(this));
    }
    
    /**
     * Register all event listeners
     */
    private void registerEvents() {
        PlayerJoinListener joinListener = new PlayerJoinListener(this);
        BlockBreakListener breakListener = new BlockBreakListener(this);
        GeneratorPlaceListener placeListener = new GeneratorPlaceListener(this);
        GeneratorInteractListener interactListener = new GeneratorInteractListener(this);
        
        getEventRegistry().register(com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent.class, joinListener::onPlayerConnect);
        getEventRegistry().register(com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent.class, breakListener::onBlockBreak);
        getEventRegistry().register(com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent.class, placeListener::onBlockPlace);
        getEventRegistry().register(com.hypixel.hytale.server.core.event.events.player.PlayerInteractEvent.class, interactListener::onInteract);
    }
    
    /**
     * Start background tasks using HytaleServer.SCHEDULED_EXECUTOR
     */
    private void startBackgroundTasks() {
        // Auto-save task - every 5 minutes
        HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                getLogger().info("Auto-saving player data...");
                if (playerDataManager != null) {
                    playerDataManager.saveAllPlayers();
                }
            } catch (Exception e) {
                getLogger().severe("Error during auto-save: " + e.getMessage());
            }
        }, 300, 300, TimeUnit.SECONDS);
        
        // Generator production task - every second
        HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                if (generatorManager != null) {
                    generatorManager.processAllGenerators();
                }
            } catch (Exception e) {
                getLogger().severe("Error processing generators: " + e.getMessage());
            }
        }, 1, 1, TimeUnit.SECONDS);
        
        // Leaderboard update task - every hour
        HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                getLogger().info("Updating leaderboards...");
                // TODO: Update leaderboards
            } catch (Exception e) {
                getLogger().severe("Error updating leaderboards: " + e.getMessage());
            }
        }, 3600, 3600, TimeUnit.SECONDS);
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
