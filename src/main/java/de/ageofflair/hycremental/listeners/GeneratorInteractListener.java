package de.ageofflair.hycremental.listeners;

import com.hypixel.hytale.server.core.event.events.player.PlayerInteractEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.math.vector.Vector3i;
import de.ageofflair.hycremental.Hycremental;

/**
 * Generator Interact Listener - Handles generator right-click interactions
 */
public class GeneratorInteractListener {
    
    private final Hycremental plugin;
    
    public GeneratorInteractListener(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    public void onInteract(PlayerInteractEvent event) {
        // Get player from component holder
        if (!(event.getHolder().toEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getHolder().toEntity();
        
        // TODO: Check if clicked block is a generator
        // TODO: Open generator UI or collect resources
        
        player.sendMessage(Message.raw("§7You interacted with a block!"));
    }
}
