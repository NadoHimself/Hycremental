package de.ageofflair.hycremental.gui;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.generators.Generator;
import de.ageofflair.hycremental.generators.GeneratorQuality;
import de.ageofflair.hycremental.generators.GeneratorEnchantment;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Upgrade GUI - Interface for upgrading generators
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class UpgradeGUI {
    
    private final Hycremental plugin;
    private static final int GUI_SIZE = 45; // 5 rows
    
    public UpgradeGUI(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Open upgrade GUI for generator
     * @param player Player to open GUI for
     * @param generator Generator to upgrade
     */
    public void openUpgradeGUI(Object player, Generator generator) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(getPlayerUUID(player));
        if (playerData == null) {
            sendMessage(player, "§cError loading player data!");
            return;
        }
        
        // Create GUI
        // TODO: GUI inventory = createInventory("§6§lUpgrade Generator", GUI_SIZE);
        
        // Generator info item
        addGeneratorInfo(null, 4, generator);
        
        // Level upgrade
        addLevelUpgrade(null, 20, generator, playerData);
        
        // Quality upgrade
        addQualityUpgrade(null, 22, generator, playerData);
        
        // Enchantment menu
        addEnchantmentMenu(null, 24, generator, playerData);
        
        // Max level button
        addMaxLevelButton(null, 29, generator, playerData);
        
        // Sell generator
        addSellButton(null, 33, generator, playerData);
        
        // Back button
        addBackButton(null, 40);
        
        // Open GUI
        // TODO: player.openInventory(inventory);
    }
    
    /**
     * Add generator info display
     */
    private void addGeneratorInfo(Object inventory, int slot, Generator generator) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Type: §e" + generator.getType().getDisplayName());
        lore.add("§7Tier: §e" + generator.getType().getTier());
        lore.add("");
        lore.add("§7Level: §a" + generator.getLevel() + "§7/§a100");
        lore.add("§7Quality: " + generator.getQuality().getColor() + generator.getQuality().getDisplayName());
        lore.add("");
        lore.add("§7Base Production: §a" + NumberFormatter.format(generator.getType().getBaseProduction()) + " Essence/s");
        lore.add("§7Current Production: §a" + NumberFormatter.format(generator.calculateProduction()) + " Essence/s");
        lore.add("");
        
        // Enchantments
        if (!generator.getEnchantments().isEmpty()) {
            lore.add("§7Enchantments:");
            for (GeneratorEnchantment ench : generator.getEnchantments()) {
                lore.add("§8 • " + ench.getColor() + ench.getDisplayName());
            }
        }
        
        // TODO: Create item and add to inventory
    }
    
    /**
     * Add level upgrade button
     */
    private void addLevelUpgrade(Object inventory, int slot, Generator generator, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        
        int currentLevel = generator.getLevel();
        int maxLevel = 100;
        
        if (currentLevel >= maxLevel) {
            lore.add("");
            lore.add("§c§lMAX LEVEL REACHED");
            lore.add("");
            // TODO: Add item with barrier material
            return;
        }
        
        BigDecimal cost = generator.calculateLevelUpgradeCost();
        boolean canAfford = playerData.hasEssence(cost);
        
        lore.add("");
        lore.add("§7Current Level: §a" + currentLevel);
        lore.add("§7Next Level: §a" + (currentLevel + 1));
        lore.add("");
        lore.add("§7Production Boost: §e+5%");
        lore.add("");
        String costColor = canAfford ? "§a" : "§c";
        lore.add("§7Cost: " + costColor + NumberFormatter.format(cost) + " Essence");
        lore.add("");
        
        if (canAfford) {
            lore.add("§e§l» Left-Click to upgrade +1 «");
            lore.add("§e§l» Right-Click to upgrade +10 «");
        } else {
            lore.add("§c§l✘ Insufficient Essence");
        }
        
        // TODO: Create item (experience bottle or similar)
    }
    
    /**
     * Add quality upgrade button
     */
    private void addQualityUpgrade(Object inventory, int slot, Generator generator, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        
        GeneratorQuality currentQuality = generator.getQuality();
        GeneratorQuality nextQuality = currentQuality.getNext();
        
        if (nextQuality == null) {
            lore.add("");
            lore.add("§d§lLEGENDARY QUALITY");
            lore.add("§7Already at maximum quality!");
            lore.add("");
            // TODO: Add item with nether star
            return;
        }
        
        BigDecimal cost = generator.calculateQualityUpgradeCost();
        boolean canAfford = playerData.hasEssence(cost);
        
        lore.add("");
        lore.add("§7Current: " + currentQuality.getColor() + currentQuality.getDisplayName() + "§7 (" + currentQuality.getMultiplier() + "x)");
        lore.add("§7Next: " + nextQuality.getColor() + nextQuality.getDisplayName() + "§7 (" + nextQuality.getMultiplier() + "x)");
        lore.add("");
        lore.add("§7Production Multiplier: " + nextQuality.getColor() + nextQuality.getMultiplier() + "x");
        lore.add("");
        String costColor = canAfford ? "§a" : "§c";
        lore.add("§7Cost: " + costColor + NumberFormatter.format(cost) + " Essence");
        lore.add("");
        
        if (canAfford) {
            lore.add("§e§l» Click to upgrade quality «");
        } else {
            lore.add("§c§l✘ Insufficient Essence");
        }
        
        // TODO: Create item (diamond or emerald)
    }
    
    /**
     * Add enchantment menu button
     */
    private void addEnchantmentMenu(Object inventory, int slot, Generator generator, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Current Enchantments: §e" + generator.getEnchantments().size() + "§7/§e3");
        lore.add("");
        
        if (!generator.getEnchantments().isEmpty()) {
            for (GeneratorEnchantment ench : generator.getEnchantments()) {
                lore.add("§8 • " + ench.getColor() + ench.getDisplayName());
            }
            lore.add("");
        }
        
        lore.add("§e§l» Click to manage enchantments «");
        
        // TODO: Create item (enchanted book)
    }
    
    /**
     * Add max level button
     */
    private void addMaxLevelButton(Object inventory, int slot, Generator generator, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        
        int currentLevel = generator.getLevel();
        int levelsToMax = 100 - currentLevel;
        
        if (levelsToMax <= 0) {
            return; // Already max level
        }
        
        BigDecimal totalCost = generator.calculateMaxLevelCost();
        boolean canAfford = playerData.hasEssence(totalCost);
        
        lore.add("");
        lore.add("§7Upgrade to Level 100");
        lore.add("§7Levels to upgrade: §e" + levelsToMax);
        lore.add("");
        String costColor = canAfford ? "§a" : "§c";
        lore.add("§7Total Cost: " + costColor + NumberFormatter.format(totalCost) + " Essence");
        lore.add("");
        
        if (canAfford) {
            lore.add("§e§l» Click to max level «");
        } else {
            lore.add("§c§l✘ Insufficient Essence");
        }
        
        // TODO: Create item (golden apple)
    }
    
    /**
     * Add sell generator button
     */
    private void addSellButton(Object inventory, int slot, Generator generator, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        
        BigDecimal sellPrice = generator.calculateSellPrice();
        
        lore.add("");
        lore.add("§7Sell this generator for:");
        lore.add("§a" + NumberFormatter.format(sellPrice) + " Essence");
        lore.add("");
        lore.add("§c§lWARNING: This cannot be undone!");
        lore.add("");
        lore.add("§c§l» Click to sell «");
        
        // TODO: Create item (red concrete or TNT)
    }
    
    /**
     * Add back button
     */
    private void addBackButton(Object inventory, int slot) {
        // TODO: Create back arrow item
    }
    
    /**
     * Handle upgrade click
     */
    public void handleClick(Object player, int slot, Generator generator, boolean isLeftClick) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(getPlayerUUID(player));
        if (playerData == null) return;
        
        switch (slot) {
            case 20: // Level upgrade
                int levels = isLeftClick ? 1 : 10;
                upgradeLevels(player, generator, playerData, levels);
                break;
                
            case 22: // Quality upgrade
                upgradeQuality(player, generator, playerData);
                break;
                
            case 24: // Enchantments
                openEnchantmentMenu(player, generator);
                break;
                
            case 29: // Max level
                maxLevel(player, generator, playerData);
                break;
                
            case 33: // Sell
                sellGenerator(player, generator, playerData);
                break;
                
            case 40: // Back
                closeGUI(player);
                break;
        }
    }
    
    private void upgradeLevels(Object player, Generator generator, PlayerData playerData, int levels) {
        BigDecimal cost = generator.calculateLevelUpgradeCost(levels);
        
        if (!playerData.hasEssence(cost)) {
            sendMessage(player, "§cInsufficient Essence!");
            playSound(player, "ERROR");
            return;
        }
        
        playerData.removeEssence(cost);
        generator.addLevels(levels);
        
        sendMessage(player, "§a§lUpgraded! §7Now level " + generator.getLevel());
        playSound(player, "SUCCESS");
        
        openUpgradeGUI(player, generator);
    }
    
    private void upgradeQuality(Object player, Generator generator, PlayerData playerData) {
        BigDecimal cost = generator.calculateQualityUpgradeCost();
        
        if (!playerData.hasEssence(cost)) {
            sendMessage(player, "§cInsufficient Essence!");
            playSound(player, "ERROR");
            return;
        }
        
        GeneratorQuality newQuality = generator.getQuality().getNext();
        if (newQuality == null) {
            sendMessage(player, "§cAlready at maximum quality!");
            return;
        }
        
        playerData.removeEssence(cost);
        generator.setQuality(newQuality);
        
        sendMessage(player, "§a§lQuality Upgraded! §7Now " + newQuality.getColor() + newQuality.getDisplayName());
        playSound(player, "SUCCESS");
        
        openUpgradeGUI(player, generator);
    }
    
    private void maxLevel(Object player, Generator generator, PlayerData playerData) {
        BigDecimal cost = generator.calculateMaxLevelCost();
        
        if (!playerData.hasEssence(cost)) {
            sendMessage(player, "§cInsufficient Essence!");
            playSound(player, "ERROR");
            return;
        }
        
        playerData.removeEssence(cost);
        generator.setLevel(100);
        
        sendMessage(player, "§a§lMAX LEVEL! §7Generator is now level 100");
        playSound(player, "LEVELUP");
        
        openUpgradeGUI(player, generator);
    }
    
    private void sellGenerator(Object player, Generator generator, PlayerData playerData) {
        BigDecimal sellPrice = generator.calculateSellPrice();
        
        playerData.addEssence(sellPrice);
        plugin.getGeneratorManager().removeGenerator(generator);
        
        sendMessage(player, "§a§lSold! §7Received " + NumberFormatter.format(sellPrice) + " Essence");
        playSound(player, "SUCCESS");
        
        closeGUI(player);
    }
    
    private void openEnchantmentMenu(Object player, Generator generator) {
        // TODO: Open enchantment sub-menu
        sendMessage(player, "§7Enchantment menu coming soon!");
    }
    
    // Utility methods
    
    private Object getPlayerUUID(Object player) {
        return null; // TODO
    }
    
    private void sendMessage(Object player, String message) {
        plugin.getLogger().info("[GUI] " + message);
    }
    
    private void playSound(Object player, String sound) {
        // TODO
    }
    
    private void closeGUI(Object player) {
        // TODO: player.closeInventory();
    }
}
