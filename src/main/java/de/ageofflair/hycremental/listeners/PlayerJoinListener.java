package de.ageofflair.hycremental.listeners;

import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.island.IslandData;

/**
 * Player Join Listener - Handles player connection events
 */
public class PlayerJoinListener {
    
    private final Hycremental plugin;
    
    public PlayerJoinListener(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    public void onPlayerConnect(PlayerConnectEvent event) {
        PlayerRef playerRef = event.getPlayerRef();
        Player player = event.getPlayer();
        
        plugin.getLogger().info(playerRef.getUsername() + " joined the server!");
        
        // Load or create player data
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(playerRef.getUuid());
        
        if (playerData == null) {
            // New player - create data
            playerData = new PlayerData(playerRef.getUuid(), playerRef.getUsername());
            plugin.getPlayerDataManager().savePlayerData(playerData);
            
            // Create island for new player
            IslandData island = plugin.getIslandManager().createIsland(new IslandData(playerRef.getUuid()));
            
            playerRef.sendMessage(Message.raw("§6Welcome to Hycremental!"));
            playerRef.sendMessage(Message.raw("§7Your island has been created!"));
            playerRef.sendMessage(Message.raw("§7Type §e/island§7 to teleport to your island."));
        } else {
            // Returning player
            playerData.setLastJoin(System.currentTimeMillis());
            plugin.getPlayerDataManager().savePlayerData(playerData);
            
            playerRef.sendMessage(Message.raw("§6Welcome back to Hycremental!"));
            playerRef.sendMessage(Message.raw("§7Essence: §e" + playerData.getEssence().toPlainString()));
        }
    }
}
