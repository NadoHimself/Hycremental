package de.ageofflair.hycremental.gui;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.generators.Generator;
import de.ageofflair.hycremental.generators.GeneratorEnchantment;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Upgrade GUI - Generator Upgrade Interface
 * 
 * Uses Hytale's PageManager system for custom UI
 * 
 * TODO: Create UI file at Common/UI/Custom/upgrade_gui.ui
 * - Generator info display (type, level, quality, production)
 * - Upgrade button with cost
 * - Enchantment slots (up to 5)
 * - Progress bars for upgrade levels
 * - Back button to close
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class UpgradeGUI {
    
    private final Hycremental plugin;
    
    private static final String UPGRADE_PAGE_ID = "hycremental:upgrade";
    
    public UpgradeGUI(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Open upgrade GUI for generator
     */
    public void openUpgradeGUI(Player player, Generator generator) {
        UUID uuid = player.getComponent(PlayerRef.class).getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            player.sendMessage(Message.raw("§cError loading player data!"));
            return;
        }
        
        // Check ownership
        if (!generator.getOwnerUUID().equals(uuid)) {
            player.sendMessage(Message.raw("§cYou don't own this generator!"));
            return;
        }
        
        // TODO: Implement with Hytale PageManager API
        // 
        // Open custom upgrade page:
        // Map<String, Object> data = new HashMap<>();
        // data.put("generator_id", generator.getUuid());
        // data.put("generator_type", generator.getType().getDisplayName());
        // data.put("generator_level", generator.getLevel());
        // data.put("generator_quality", generator.getQuality().getDisplayName());
        // data.put("upgrade_cost", generator.calculateUpgradeCost().toPlainString());
        // 
        // player.getPageManager().openCustomPage(PageId.of(UPGRADE_PAGE_ID), data);
        // 
        // The upgrade_gui.ui file should contain:
        // - Generator 3D model/icon preview
        // - Stats display (level, quality, production)
        // - Upgrade button (level +1)
        // - Enchantment application slots
        // - Progress bar showing level/100
        
        // Temporary fallback: Send text-based upgrade menu
        sendTextBasedUpgradeMenu(player, generator, playerData);
    }
    
    /**
     * Handle level upgrade (called from UI button click)
     */
    public void handleLevelUpgrade(Player player, Generator generator) {
        UUID uuid = player.getComponent(PlayerRef.class).getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            return;
        }
        
        // Check if max level
        if (generator.getLevel() >= 100) {
            player.sendMessage(Message.raw("§cGenerator is already max level!"));
            return;
        }
        
        // Calculate cost
        BigDecimal cost = generator.calculateUpgradeCost();
        
        // Check if player has enough
        if (!playerData.hasEssence(cost)) {
            player.sendMessage(Message.raw("§cInsufficient Essence!"));
            player.sendMessage(Message.raw("§7Need: §a" + NumberFormatter.format(cost)));
            return;
        }
        
        // Process upgrade
        playerData.removeEssence(cost);
        generator.levelUp();
        
        // Save
        plugin.getPlayerDataManager().savePlayerData(playerData);
        
        // Success feedback
        player.sendMessage(Message.raw("§a§l✓ Upgraded! §7Level " + generator.getLevel()));
        player.sendMessage(Message.raw("§7New Production: §a" + NumberFormatter.format(generator.calculateProduction()) + " Essence/s"));
        
        // TODO: Play upgrade sound
        // TODO: Particle effect at generator location
        
        // Refresh GUI
        openUpgradeGUI(player, generator);
    }
    
    /**
     * Handle enchantment application (called from UI)
     */
    public void handleEnchantmentApplication(Player player, Generator generator, GeneratorEnchantment enchantment) {
        UUID uuid = player.getComponent(PlayerRef.class).getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            return;
        }
        
        // Check max enchantments
        if (generator.getEnchantments().size() >= 5) {
            player.sendMessage(Message.raw("§cGenerator has maximum enchantments!"));
            return;
        }
        
        // Check if already has this enchantment
        if (generator.hasEnchantment(enchantment)) {
            // Upgrade enchantment level
            int currentLevel = generator.getEnchantmentLevel(enchantment);
            
            if (currentLevel >= 10) {
                player.sendMessage(Message.raw("§cEnchantment is already max level!"));
                return;
            }
            
            BigDecimal cost = calculateEnchantmentUpgradeCost(enchantment, currentLevel);
            
            if (!playerData.hasEssence(cost)) {
                player.sendMessage(Message.raw("§cInsufficient Essence!"));
                return;
            }
            
            playerData.removeEssence(cost);
            generator.upgradeEnchantment(enchantment);
            
            player.sendMessage(Message.raw("§a§l✓ Upgraded! " + enchantment.getColor() + enchantment.getDisplayName() + " " + (currentLevel + 1)));
            
        } else {
            // Add new enchantment
            BigDecimal cost = enchantment.getBaseCost();
            
            if (!playerData.hasEssence(cost)) {
                player.sendMessage(Message.raw("§cInsufficient Essence!"));
                return;
            }
            
            playerData.removeEssence(cost);
            generator.addEnchantment(enchantment, 1);
            
            player.sendMessage(Message.raw("§a§l✓ Applied! " + enchantment.getColor() + enchantment.getDisplayName() + " I"));
        }
        
        // Save
        plugin.getPlayerDataManager().savePlayerData(playerData);
        
        // Refresh GUI
        openUpgradeGUI(player, generator);
    }
    
    /**
     * Calculate enchantment upgrade cost
     */
    private BigDecimal calculateEnchantmentUpgradeCost(GeneratorEnchantment enchantment, int currentLevel) {
        double baseCost = enchantment.getBaseCost().doubleValue();
        double multiplier = Math.pow(1.5, currentLevel);
        return BigDecimal.valueOf(baseCost * multiplier);
    }
    
    /**
     * Temporary text-based upgrade menu
     */
    private void sendTextBasedUpgradeMenu(Player player, Generator generator, PlayerData playerData) {
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
        player.sendMessage(Message.raw("§6§l    Generator Upgrade"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Type: " + generator.getType().getDisplayName()));
        player.sendMessage(Message.raw("§7Level: §a" + generator.getLevel() + "§7/§a100"));
        player.sendMessage(Message.raw("§7Quality: " + generator.getQuality().getColor() + generator.getQuality().getDisplayName()));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Production: §a" + NumberFormatter.format(generator.calculateProduction()) + " Essence/s"));
        player.sendMessage(Message.raw(""));
        
        if (generator.getLevel() < 100) {
            BigDecimal upgradeCost = generator.calculateUpgradeCost();
            player.sendMessage(Message.raw("§e[Upgrade to Level " + (generator.getLevel() + 1) + "]"));
            player.sendMessage(Message.raw("§7Cost: §a" + NumberFormatter.format(upgradeCost)));
        } else {
            player.sendMessage(Message.raw("§a§lMAX LEVEL!"));
        }
        
        player.sendMessage(Message.raw(""));
        
        if (!generator.getEnchantments().isEmpty()) {
            player.sendMessage(Message.raw("§7Enchantments:"));
            generator.getEnchantments().forEach((enchant, level) -> {
                player.sendMessage(Message.raw("§8 • " + enchant.getColor() + enchant.getDisplayName() + " " + level));
            });
            player.sendMessage(Message.raw(""));
        }
        
        player.sendMessage(Message.raw("§7Right-click generator to open full upgrade GUI"));
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
        player.sendMessage(Message.raw(""));
    }
}
