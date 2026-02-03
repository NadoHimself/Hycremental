package de.ageofflair.hycremental.listeners;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.island.IslandData;

import java.util.UUID;
import java.util.logging.Level;

/**
 * Player Join Listener - Handles player joining
 * 
 * Uses Hytale's PlayerReadyEvent instead of PlayerJoinEvent
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class PlayerJoinListener {
    
    private final Hycremental plugin;
    
    public PlayerJoinListener(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Handle player join event
     * Called from main plugin class via PlayerReadyEvent
     * 
     * @param player The player who joined
     */
    public void onPlayerJoin(Player player) {
        UUID uuid = player.getComponent(PlayerRef.class).getUuid();
        String username = player.getName();
        
        plugin.getLogger().at(Level.INFO).log("Player " + username + " joined (UUID: " + uuid + ")");
        
        // Load or create player data
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            // New player - create data
            plugin.getLogger().at(Level.INFO).log("Creating new player data for " + username);
            
            playerData = new PlayerData(uuid, username);
            plugin.getPlayerDataManager().createPlayerData(playerData);
            
            // Create island
            IslandData island = new IslandData(uuid);
            plugin.getIslandManager().createIsland(island);
            
            // Welcome message
            sendWelcomeMessage(player);
            
            // Teleport to island
            // TODO: Implement with Hytale Location API
            // player.teleport(island.getSpawnLocation());
            
        } else {
            // Existing player - update last login
            playerData.setLastLogin(System.currentTimeMillis());
            plugin.getPlayerDataManager().savePlayerData(playerData);
            
            // Welcome back message
            sendWelcomeBackMessage(player, playerData);
        }
        
        // Load player's generators
        plugin.getGeneratorManager().loadPlayerGenerators(uuid);
    }
    
    /**
     * Send welcome message to new player
     */
    private void sendWelcomeMessage(Player player) {
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
        player.sendMessage(Message.raw("§6§l    Welcome to Hycremental!"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Build your generator empire and become"));
        player.sendMessage(Message.raw("§7the richest player on the server!"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§e§l» Commands:"));
        player.sendMessage(Message.raw("§7  /gen - Buy generators"));
        player.sendMessage(Message.raw("§7  /island - Manage your island"));
        player.sendMessage(Message.raw("§7  /essence - Check your balance"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Start by buying your first generator!"));
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
        player.sendMessage(Message.raw(""));
    }
    
    /**
     * Send welcome back message to existing player
     */
    private void sendWelcomeBackMessage(Player player, PlayerData playerData) {
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§e§lWelcome back, " + player.getName() + "!"));
        player.sendMessage(Message.raw(""));
        
        // Show quick stats
        int activeGenerators = plugin.getGeneratorManager().getPlayerGeneratorCount(playerData.getUuid());
        
        player.sendMessage(Message.raw("§7Your Stats:"));
        player.sendMessage(Message.raw("§8 • §7Essence: §a" + playerData.getEssence().toPlainString()));
        player.sendMessage(Message.raw("§8 • §7Generators: §e" + activeGenerators));
        player.sendMessage(Message.raw("§8 • §7Prestige: §6" + playerData.getPrestigeLevel()));
        player.sendMessage(Message.raw(""));
        
        // Check for new features/updates
        if (playerData.getLastLogin() < System.currentTimeMillis() - 86400000L) { // 24 hours
            player.sendMessage(Message.raw("§e§lYou've been away for a while!"));
            player.sendMessage(Message.raw("§7Check out what's new with §e/changelog§7!"));
            player.sendMessage(Message.raw(""));
        }
    }
}
