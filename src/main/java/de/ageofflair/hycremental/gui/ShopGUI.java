package de.ageofflair.hycremental.gui;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.generators.GeneratorType;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Shop GUI - Generator Shop Interface
 * 
 * Uses Hytale's PageManager system for custom UI
 * 
 * TODO: Create UI file at Common/UI/Custom/shop_gui.ui
 * - Grid layout with generator icons
 * - Price display under each generator
 * - Lock icon for locked generators (prestige requirement)
 * - Click handler for purchases
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class ShopGUI {
    
    private final Hycremental plugin;
    
    // Page IDs for Hytale UI system
    private static final String SHOP_PAGE_ID = "hycremental:shop";
    
    public ShopGUI(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Open shop for player
     */
    public void openShop(Player player) {
        UUID uuid = player.getComponent(PlayerRef.class).getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            player.sendMessage(Message.raw("§cError loading player data!"));
            return;
        }
        
        // TODO: Implement with Hytale PageManager API
        // 
        // Open custom shop page:
        // player.getPageManager().openCustomPage(PageId.of(SHOP_PAGE_ID));
        // 
        // The shop_gui.ui file should contain:
        // - Grid of generator type buttons
        // - Each button shows:
        //   * Generator icon/sprite
        //   * Display name
        //   * Cost
        //   * Locked/Unlocked status
        //   * Production rate preview
        // 
        // Button click handler:
        // - Check if player can afford
        // - Check prestige requirement
        // - Purchase generator
        // - Give generator item to player
        
        // Temporary fallback: Send text-based shop
        sendTextBasedShop(player, playerData);
    }
    
    /**
     * Handle generator purchase (called from UI button click)
     */
    public void handlePurchase(Player player, GeneratorType type) {
        UUID uuid = player.getComponent(PlayerRef.class).getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            return;
        }
        
        // Check prestige requirement
        if (playerData.getPrestigeLevel() < type.getRequiredPrestige()) {
            player.sendMessage(Message.raw("§cRequires Prestige " + type.getRequiredPrestige() + "!"));
            // TODO: Play error sound with Hytale Audio API
            return;
        }
        
        // Check cost
        BigDecimal cost = type.getCost();
        if (!playerData.hasEssence(cost)) {
            player.sendMessage(Message.raw("§cInsufficient Essence!"));
            player.sendMessage(Message.raw("§7Need: §a" + NumberFormatter.format(cost)));
            player.sendMessage(Message.raw("§7Have: §c" + NumberFormatter.format(playerData.getEssence())));
            // TODO: Play error sound
            return;
        }
        
        // Check generator slots
        int currentGenerators = plugin.getGeneratorManager().getPlayerGeneratorCount(uuid);
        if (currentGenerators >= playerData.getGeneratorSlots()) {
            player.sendMessage(Message.raw("§cNo generator slots available!"));
            player.sendMessage(Message.raw("§7Upgrade with §e/island slots"));
            // TODO: Play error sound
            return;
        }
        
        // Process purchase
        playerData.removeEssence(cost);
        playerData.incrementGeneratorsPurchased();
        plugin.getPlayerDataManager().savePlayerData(playerData);
        
        // TODO: Give generator item to player with Hytale Inventory API
        // ItemStack generatorItem = createGeneratorItem(type);
        // player.getInventory().addItemStack(generatorItem);
        
        // Success feedback
        player.sendMessage(Message.raw("§a§l✓ Purchased! §7" + type.getDisplayName()));
        player.sendMessage(Message.raw("§7Place it on your island to start producing!"));
        
        // TODO: Play success sound
        // TODO: Particle effect
        
        // Refresh GUI
        openShop(player);
    }
    
    /**
     * Temporary text-based shop (until UI is implemented)
     */
    private void sendTextBasedShop(Player player, PlayerData playerData) {
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
        player.sendMessage(Message.raw("§6§l       Generator Shop"));
        player.sendMessage(Message.raw("§7Your Balance: §a" + NumberFormatter.format(playerData.getEssence())));
        player.sendMessage(Message.raw(""));
        
        for (GeneratorType type : GeneratorType.values()) {
            boolean locked = playerData.getPrestigeLevel() < type.getRequiredPrestige();
            String status = locked ? "§c§l✗ LOCKED" : "§a§l✓";
            
            player.sendMessage(Message.raw(
                status + " " + type.getDisplayName() + " §7- " + 
                "§a" + NumberFormatter.format(type.getCost()) + " Essence"
            ));
            
            if (locked) {
                player.sendMessage(Message.raw("§8   Requires Prestige " + type.getRequiredPrestige()));
            } else {
                player.sendMessage(Message.raw("§8   Production: " + NumberFormatter.format(type.getBaseProduction()) + " Essence/s"));
            }
        }
        
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§eUse /gen buy <type> to purchase"));
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
        player.sendMessage(Message.raw(""));
    }
}
