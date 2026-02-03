package de.ageofflair.hycremental.listeners;

import com.hypixel.hytale.server.core.event.EventHandler;
import com.hypixel.hytale.server.core.event.block.BlockPlaceEvent;
import com.hypixel.hytale.server.player.ServerPlayer;
import com.hypixel.hytale.world.block.Block;
import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.island.IslandData;

/**
 * Generator Place Listener - Handles generator placement
 */
public class GeneratorPlaceListener {
    
    private final Hycremental plugin;
    
    public GeneratorPlaceListener(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ServerPlayer player = event.getPlayer();
        Block block = event.getBlock();
        
        // Check if player is on their island
        IslandData island = plugin.getIslandManager().getIsland(player.getUUID());
        if (island == null) return;
        
        // TODO: Check if block is a generator block
        // TODO: Register generator at this location
        
        player.sendMessage("§7Block placed at island!");
    }
}
