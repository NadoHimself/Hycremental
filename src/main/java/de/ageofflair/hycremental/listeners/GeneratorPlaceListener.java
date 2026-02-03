package de.ageofflair.hycremental.listeners;

import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.math.vector.Vector3i;
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
    
    public void onBlockPlace(PlaceBlockEvent event) {
        // Get player from component holder
        if (!(event.getHolder().toEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getHolder().toEntity();
        Vector3i blockPos = event.getTargetBlock();
        
        // Check if player is on their island
        IslandData island = plugin.getIslandManager().getIsland(player.getUuid());
        if (island == null) return;
        
        // TODO: Check if block is a generator block
        // TODO: Register generator at this location
        
        player.sendMessage(Message.raw("§7Block placed at island!"));
    }
}
