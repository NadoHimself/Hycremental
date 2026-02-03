package de.ageofflair.hycremental.events;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.core.GeneratorManager;
import de.ageofflair.hycremental.generators.Generator;
import de.ageofflair.hycremental.utils.NumberFormatter;
// import com.hytale.api.event.EventHandler;
// import com.hytale.api.event.Listener;
// import com.hytale.api.event.player.PlayerInteractEvent;
// import com.hytale.api.entity.Player;
// import com.hytale.api.block.Block;
import java.util.UUID;

/**
 * Handles player interaction with generators
 * Right-click to view stats, shift-right-click to open upgrade menu
 */
public class GeneratorInteractListener { // implements Listener {
    
    private final Hycremental plugin;
    private final GeneratorManager generatorManager;
    
    public GeneratorInteractListener(Hycremental plugin) {
        this.plugin = plugin;
        this.generatorManager = plugin.getGeneratorManager();
    }
    
    // @EventHandler
    public void onPlayerInteract(Object event) { // PlayerInteractEvent event
        /*
        // Check if right-click on block
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        
        if (block == null) {
            return;
        }
        
        // Check if block is a generator
        Generator generator = generatorManager.getGeneratorAt(
            block.getX(),
            block.getY(),
            block.getZ(),
            block.getWorld().getName()
        );
        
        if (generator == null) {
            return;
        }
        
        event.setCancelled(true);
        
        // If sneaking, open upgrade menu
        if (player.isSneaking()) {
            openUpgradeMenu(player, generator);
        } else {
            // Show generator stats
            showGeneratorStats(player, generator);
        }
        */
    }
    
    /**
     * Show generator statistics to player
     */
    private void showGeneratorStats(Object player, Generator generator) { // Player player
        /*
        player.sendMessage("");
        player.sendMessage("§6§l=== Generator Info ===");
        player.sendMessage("§7Type: §e" + generator.getType().getDisplayName());
        player.sendMessage("§7Tier: §d" + generator.getTier());
        player.sendMessage("§7Level: §b" + generator.getLevel() + "§7/100");
        player.sendMessage("§7Quality: " + generator.getQuality().getDisplayName());
        player.sendMessage("§7Production: §a" + NumberFormatter.format(generator.calculateProduction()) + "/s");
        player.sendMessage("§7Total Produced: §6" + NumberFormatter.format(generator.getTotalProduced()));
        
        if (!generator.getEnchantments().isEmpty()) {
            player.sendMessage("§7Enchantments:");
            generator.getEnchantments().forEach(enchant -> {
                player.sendMessage("§7 • " + enchant.getDisplayName());
            });
        }
        
        player.sendMessage("§7Age: §e" + generator.getAgeInHours() + "h");
        player.sendMessage("");
        player.sendMessage("§7§oShift + Right-Click to upgrade");
        player.sendMessage("");
        */
    }
    
    /**
     * Open upgrade menu GUI
     */
    private void openUpgradeMenu(Object player, Generator generator) { // Player player
        /*
        // TODO: Open upgrade GUI
        player.sendMessage("§eUpgrade menu coming soon!");
        */
    }
}