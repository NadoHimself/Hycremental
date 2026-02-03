package de.ageofflair.hycremental;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.PlayerReadyEvent;

import de.ageofflair.hycremental.commands.*;
import de.ageofflair.hycremental.data.DatabaseManager;
import de.ageofflair.hycremental.data.PlayerDataManager;
import de.ageofflair.hycremental.core.EconomyManager;
import de.ageofflair.hycremental.core.GeneratorManager;
import de.ageofflair.hycremental.core.IslandManager;
import de.ageofflair.hycremental.listeners.*;
import de.ageofflair.hycremental.core.PrestigeManager;
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
    
    // Commands
    private EssenceCommand essenceCommand;
    private IslandCommand islandCommand;
    private ShopCommand shopCommand;
    private GeneratorCommand generatorCommand;
    private PrestigeCommand prestigeCommand;
    
    /**
     * Plugin startup - Called when plugin is loaded
     */
    @Override
    public void start() {
        instance = this;
        
        getLogger().at(Level.INFO).log("====================================");
        getLogger().at(Level.INFO).log("   Hycremental v1.0.0-ALPHA");
        getLogger().at(Level.INFO).log("   by NadoHimself (Kielian)");
        getLogger().at(Level.INFO).log("====================================");
        
        // Initialize configuration
        getLogger().at(Level.INFO).log("Loading configuration...");
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        // Initialize database
        getLogger().at(Level.INFO).log("Connecting to database...");
        databaseManager = new DatabaseManager();
        if (!databaseManager.connect()) {
            getLogger().at(Level.SEVERE).log("Failed to connect to database! Plugin disabled.");
            return;
        }
        databaseManager.initializeTables();
        
        // Initialize managers
        getLogger().at(Level.INFO).log("Initializing managers...");
        playerDataManager = new PlayerDataManager(databaseManager);
        generatorManager = new GeneratorManager(this);
        islandManager = new IslandManager(this);
        economyManager = new EconomyManager(this);
        prestigeManager = new PrestigeManager(this);
        
        // Register commands
        getLogger().at(Level.INFO).log("Registering commands...");
        registerCommands();
        
        // Register events
        getLogger().at(Level.INFO).log("Registering event listeners...");
        registerEvents();
        
        // Start background tasks
        getLogger().at(Level.INFO).log("Starting background tasks...");
        startBackgroundTasks();
        
        getLogger().at(Level.INFO).log("Hycremental has been enabled successfully!");
        getLogger().at(Level.INFO).log("====================================");
    }
    
    /**
     * Plugin shutdown - Called when plugin is unloaded
     */
    @Override
    public void shutdown() {
        getLogger().at(Level.INFO).log("Shutting down Hycremental...");
        
        // Save all data
        if (playerDataManager != null) {
            getLogger().at(Level.INFO).log("Saving player data...");
            playerDataManager.saveAllPlayers();
        }
        
        // Stop generators
        if (generatorManager != null) {
            getLogger().at(Level.INFO).log("Stopping generators...");
            generatorManager.stopAllGenerators();
        }
        
        // Close database
        if (databaseManager != null) {
            getLogger().at(Level.INFO).log("Closing database connection...");
            databaseManager.disconnect();
        }
        
        getLogger().at(Level.INFO).log("Hycremental has been disabled.");
    }
    
    /**
     * Register all commands
     */
    private void registerCommands() {
        essenceCommand = new EssenceCommand(this);
        islandCommand = new IslandCommand(this);
        shopCommand = new ShopCommand(this);
        generatorCommand = new GeneratorCommand(this);
        prestigeCommand = new PrestigeCommand(this);
        
        getCommandRegistry().registerCommand(essenceCommand);
        getCommandRegistry().registerCommand(islandCommand);
        getCommandRegistry().registerCommand(shopCommand);
        getCommandRegistry().registerCommand(generatorCommand);
        getCommandRegistry().registerCommand(prestigeCommand);
    }
    
    /**
     * Register all event listeners
     */
    private void registerEvents() {
        // Player ready event (join)
        getEventRegistry().registerGlobal(PlayerReadyEvent.class, this::onPlayerReady);
        
        // Block break event
        BlockBreakListener blockBreakListener = new BlockBreakListener(this);
        getEventRegistry().registerGlobal(com.hypixel.hytale.server.core.event.BlockBreakEvent.class, 
            event -> blockBreakListener.onBlockBreak(event));
        
        // Block place event (generators)
        GeneratorPlaceListener generatorPlaceListener = new GeneratorPlaceListener(this);
        getEventRegistry().registerGlobal(com.hypixel.hytale.server.core.event.BlockPlaceEvent.class,
            event -> generatorPlaceListener.onGeneratorPlace(event));
        
        // Player interact event (generators)
        GeneratorInteractListener generatorInteractListener = new GeneratorInteractListener(this);
        getEventRegistry().registerGlobal(com.hypixel.hytale.server.core.event.PlayerInteractEvent.class,
            event -> generatorInteractListener.onGeneratorInteract(event));
    }
    
    /**
     * Handle player ready event (join)
     */
    private void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        PlayerJoinListener listener = new PlayerJoinListener(this);
        listener.onPlayerJoin(player);
    }
    
    /**
     * Start background tasks using Hytale Scheduler
     */
    private void startBackgroundTasks() {
        // Auto-save task - every 5 minutes
        getScheduler().scheduleRepeating(() -> {
            getLogger().at(Level.INFO).log("Auto-saving player data...");
            playerDataManager.saveAllPlayers();
        }, 300000L, 300000L); // 5 minutes in milliseconds
        
        // Generator production task - every second
        getScheduler().scheduleRepeating(() -> {
            generatorManager.processAllGenerators();
        }, 1000L, 1000L); // 1 second
        
        // Leaderboard update task - every hour
        getScheduler().scheduleRepeating(() -> {
            getLogger().at(Level.INFO).log("Updating leaderboards...");
            // TODO: Update leaderboards
        }, 3600000L, 3600000L); // 1 hour
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
