package de.ageofflair.hycremental.listeners;

import com.hypixel.hytale.server.core.event.EventHandler;
import com.hypixel.hytale.server.core.event.PlayerReadyEvent;
import com.hypixel.hytale.server.player.ServerPlayer;
import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.island.IslandData;

import java.util.logging.Level;

/**
 * Player Join Listener - Handles player join events
 */
public class PlayerJoinListener {
    
    private final Hycremental plugin;
    
    public PlayerJoinListener(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerReady(PlayerReadyEvent event) {
        ServerPlayer player = event.getPlayer();
        
        plugin.getLogger().log(Level.INFO, player.getName() + " joined the server!");
        
        // Load or create player data
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUUID());
        
        if (playerData == null) {
            // New player - create data
            playerData = new PlayerData(player.getUUID(), player.getName());
            plugin.getPlayerDataManager().savePlayerData(playerData);
            
            // Create island for new player
            IslandData island = plugin.getIslandManager().createIsland(new IslandData(player.getUUID()));
            
            player.sendMessage("§6Welcome to Hycremental!");
            player.sendMessage("§7Your island has been created!");
            player.sendMessage("§7Type §e/island§7 to teleport to your island.");
        } else {
            // Returning player
            playerData.setLastJoin(System.currentTimeMillis());
            plugin.getPlayerDataManager().savePlayerData(playerData);
            
            player.sendMessage("§6Welcome back to Hycremental!");
            player.sendMessage("§7Essence: §e" + playerData.getEssence().toPlainString());
        }
    }
}
