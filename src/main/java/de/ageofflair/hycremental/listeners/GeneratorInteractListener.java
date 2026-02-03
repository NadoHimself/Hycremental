package de.ageofflair.hycremental.listeners;

import com.hypixel.hytale.server.core.event.EventHandler;
import com.hypixel.hytale.server.core.event.player.PlayerInteractEvent;
import com.hypixel.hytale.server.player.ServerPlayer;
import com.hypixel.hytale.world.block.Block;
import de.ageofflair.hycremental.Hycremental;

/**
 * Generator Interact Listener - Handles generator right-click interactions
 */
public class GeneratorInteractListener {
    
    private final Hycremental plugin;
    
    public GeneratorInteractListener(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ServerPlayer player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        
        if (clickedBlock == null) return;
        
        // TODO: Check if clicked block is a generator
        // TODO: Open generator UI or collect resources
        
        player.sendMessage("§7You interacted with a block!");
    }
}
