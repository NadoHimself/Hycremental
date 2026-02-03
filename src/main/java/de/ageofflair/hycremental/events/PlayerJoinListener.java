package de.ageofflair.hycremental.events;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.core.PlayerDataManager;
import de.ageofflair.hycremental.data.PlayerData;
// import com.hytale.api.event.EventHandler;
// import com.hytale.api.event.Listener;
// import com.hytale.api.event.player.PlayerJoinEvent;
// import com.hytale.api.entity.Player;
import java.util.UUID;

/**
 * Handles player join events
 * Loads player data and displays welcome messages
 */
public class PlayerJoinListener { // implements Listener {
    
    private final Hycremental plugin;
    private final PlayerDataManager playerDataManager;
    
    public PlayerJoinListener(Hycremental plugin) {
        this.plugin = plugin;
        this.playerDataManager = plugin.getPlayerDataManager();
    }
    
    // @EventHandler
    public void onPlayerJoin(Object event) { // PlayerJoinEvent event
        /*
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        // Load player data asynchronously
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerData playerData = playerDataManager.loadPlayerData(playerId);
            
            // If player is new, create data
            if (playerData == null) {
                playerData = new PlayerData(playerId, player.getName());
                playerDataManager.savePlayerData(playerData);
                
                // Send welcome message for new players
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    sendWelcomeMessage(player);
                });
            }
            
            // Cache player data
            playerDataManager.cachePlayerData(playerData);
            
            // Send join message
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                sendJoinMessage(player, playerData);
            });
        });
        */
    }
    
    /**
     * Send welcome message to new players
     */
    private void sendWelcomeMessage(Object player) { // Player player
        /*
        player.sendMessage("");
        player.sendMessage("§6§l=== Welcome to Hycremental! ===");
        player.sendMessage("§7Build your generator empire and dominate the leaderboards!");
        player.sendMessage("");
        player.sendMessage("§eGetting Started:");
        player.sendMessage("§7 • Use §b/island create§7 to create your island");
        player.sendMessage("§7 • Mine blocks to gain §6Essence");
        player.sendMessage("§7 • Buy generators with §b/shop");
        player.sendMessage("§7 • Upgrade and prestige to progress");
        player.sendMessage("");
        player.sendMessage("§7Type §b/help§7 for more commands!");
        player.sendMessage("§6§l=============================");
        player.sendMessage("");
        */
    }
    
    /**
     * Send join message showing stats
     */
    private void sendJoinMessage(Object player, PlayerData data) { // Player player
        /*
        if (data.getPrestigeLevel() > 0 || data.getCurrentEssence().compareTo(BigDecimal.ZERO) > 0) {
            player.sendMessage("");
            player.sendMessage("§7Welcome back, §e" + player.getName() + "§7!");
            player.sendMessage("§7Essence: §6" + NumberFormatter.format(data.getCurrentEssence()));
            
            if (data.getPrestigeLevel() > 0) {
                player.sendMessage("§7Prestige: §d" + data.getPrestigeLevel());
            }
            
            if (data.getAscensionLevel() > 0) {
                player.sendMessage("§7Ascension: §b" + data.getAscensionLevel());
            }
            
            player.sendMessage("");
        }
        */
    }
}