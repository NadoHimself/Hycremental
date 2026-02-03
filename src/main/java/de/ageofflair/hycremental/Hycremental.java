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
import de.ageofflair.hycremental.gui.*;
import de.ageofflair.hycremental.listeners.*;
import de.ageofflair.hycremental.utils.ConfigManager;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Hycremental - Incremental/Idle game plugin for Hytale
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 * @since 2026-02-03
 */
public class Hycremental extends JavaPlugin {
    
    private static Hycremental instance;
    
    // Core managers
    private DatabaseManager databaseManager;
    private PlayerDataManager playerDataManager;
    private GeneratorManager generatorManager;
    private IslandManager islandManager;
    private EconomyManager economyManager;
    private PrestigeManager prestigeManager;
    private ConfigManager configManager;
    
    // GUI managers
    private ShopGUI shopGUI;
    private PrestigeGUI prestigeGUI;
    private StatsGUI statsGUI;
    private UpgradeGUI upgradeGUI;
    
    public Hycremental(JavaPluginInit init) {
        super(init);
    }
    
    @Override
    public void setup() {
        instance = this;
        
        getLogger().info("====================================");
        getLogger().info("   Hycremental v1.0.0-ALPHA");
        getLogger().info("   by NadoHimself (Kielian)");
        getLogger().info("====================================");
        
        getLogger().info("Loading configuration...");
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        getLogger().info("Registering commands...");
        registerCommands();
        
        getLogger().info("Registering event listeners...");
        registerEvents();
    }
    
    @Override
    public void start() {
        getLogger().info("Connecting to database...");
        databaseManager = new DatabaseManager();
        if (!databaseManager.connect()) {
            getLogger().severe("Failed to connect to database! Plugin disabled.");
            return;
        }
        databaseManager.initializeTables();
        
        getLogger().info("Initializing managers...");
        playerDataManager = new PlayerDataManager(this);
        playerDataManager.initialize();
        generatorManager = new GeneratorManager(this);
        islandManager = new IslandManager(this);
        economyManager = new EconomyManager(this);
        prestigeManager = new PrestigeManager(this);
        
        getLogger().info("Initializing GUIs...");
        shopGUI = new ShopGUI(this);
        prestigeGUI = new PrestigeGUI(this);
        statsGUI = new StatsGUI(this);
        upgradeGUI = new UpgradeGUI(this);
        
        getLogger().info("Starting background tasks...");
        startBackgroundTasks();
        
        getLogger().info("Hycremental has been enabled successfully!");
        getLogger().info("====================================");
    }
    
    @Override
    public void shutdown() {
        getLogger().info("Shutting down Hycremental...");
        
        if (playerDataManager != null) {
            getLogger().info("Saving player data...");
            playerDataManager.shutdown();
        }
        
        if (generatorManager != null) {
            getLogger().info("Stopping generators...");
            generatorManager.stopAllGenerators();
        }
        
        if (databaseManager != null) {
            getLogger().info("Closing database connection...");
            databaseManager.disconnect();
        }
        
        getLogger().info("Hycremental has been disabled.");
    }
    
    private void registerCommands() {
        // Commands mit Command Interface registrieren
        getCommandRegistry().registerCommand(new EssenceCommand());
        getCommandRegistry().registerCommand(new IslandCommand());
        getCommandRegistry().registerCommand(new ShopCommand());
        getCommandRegistry().registerCommand(new GeneratorCommand());
        getCommandRegistry().registerCommand(new PrestigeCommand());
        
        getLogger().info("Successfully registered 5 commands!");
    }
    
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
    
    private void startBackgroundTasks() {
        // Auto-save every 5 minutes
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
        
        // Process generators every second
        HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                if (generatorManager != null) {
                    generatorManager.processAllGenerators();
                }
            } catch (Exception e) {
                getLogger().severe("Error processing generators: " + e.getMessage());
            }
        }, 1, 1, TimeUnit.SECONDS);
        
        // Update leaderboards every hour
        HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                getLogger().info("Updating leaderboards...");
                // TODO: Implement leaderboard update logic
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
    
    public ShopGUI getShopGUI() {
        return shopGUI;
    }
    
    public PrestigeGUI getPrestigeGUI() {
        return prestigeGUI;
    }
    
    public StatsGUI getStatsGUI() {
        return statsGUI;
    }
    
    public UpgradeGUI getUpgradeGUI() {
        return upgradeGUI;
    }
}
