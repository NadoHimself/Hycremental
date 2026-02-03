package de.ageofflair.hycremental.gui;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.generators.GeneratorType;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Shop GUI - Interface for purchasing generators
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class ShopGUI {
    
    private final Hycremental plugin;
    private static final int GUI_SIZE = 54; // 6 rows
    
    public ShopGUI(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Open shop GUI for player
     * @param player Player to open GUI for
     */
    public void openShop(Object player) {
        // TODO: Implement with Hytale GUI API when available
        // This is a placeholder structure
        
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(getPlayerUUID(player));
        if (playerData == null) {
            sendMessage(player, "§cError loading player data!");
            return;
        }
        
        // Create GUI inventory
        // TODO: GUI inventory = createInventory("§6§lGenerator Shop", GUI_SIZE);
        
        // Add generator items to GUI
        int slot = 10;
        for (GeneratorType type : GeneratorType.values()) {
            // Check if player has unlocked this tier
            if (!canPurchase(playerData, type)) {
                continue;
            }
            
            // Create generator item
            // TODO: Create item with proper material and meta
            addGeneratorItem(null, slot, type, playerData);
            
            slot++;
            if (slot == 17) slot = 19; // Skip to next row
            if (slot == 26) slot = 28;
            if (slot == 35) slot = 37;
        }
        
        // Add info item
        addInfoItem(null, 4, playerData);
        
        // Add close button
        addCloseButton(null, 49);
        
        // Open GUI for player
        // TODO: player.openInventory(inventory);
    }
    
    /**
     * Create generator item for shop
     */
    private void addGeneratorItem(Object inventory, int slot, GeneratorType type, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        
        // Generator info
        lore.add("");
        lore.add("§7Tier: §e" + type.getTier());
        lore.add("§7Production: §a" + NumberFormatter.format(type.getBaseProduction()) + " Essence/s");
        lore.add("");
        
        // Cost
        BigDecimal cost = type.getCost();
        boolean canAfford = playerData.hasEssence(cost);
        String costColor = canAfford ? "§a" : "§c";
        lore.add("§7Cost: " + costColor + NumberFormatter.format(cost) + " Essence");
        lore.add("");
        
        // Requirements
        int requiredPrestige = type.getRequiredPrestige();
        if (requiredPrestige > 0) {
            boolean meetsRequirement = playerData.getPrestigeLevel() >= requiredPrestige;
            String reqColor = meetsRequirement ? "§a" : "§c";
            lore.add("§7Required Prestige: " + reqColor + requiredPrestige);
            lore.add("");
        }
        
        // Action
        if (canAfford && playerData.getPrestigeLevel() >= requiredPrestige) {
            lore.add("§e§l» Click to purchase «");
        } else {
            lore.add("§c§l✘ Cannot purchase");
        }
        
        // TODO: Create item and add to inventory
        // ItemStack item = new ItemStack(type.getMaterial());
        // ItemMeta meta = item.getItemMeta();
        // meta.setDisplayName("§6" + type.getDisplayName());
        // meta.setLore(lore);
        // item.setItemMeta(meta);
        // inventory.setItem(slot, item);
    }
    
    /**
     * Add player info item
     */
    private void addInfoItem(Object inventory, int slot, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Balance: §a" + NumberFormatter.format(playerData.getEssence()) + " Essence");
        lore.add("§7Gems: §b" + NumberFormatter.formatLong(playerData.getGems()));
        lore.add("§7Crystals: §d" + playerData.getCrystals());
        lore.add("");
        lore.add("§7Prestige: §6" + playerData.getPrestigeLevel());
        lore.add("§7Ascension: §5" + playerData.getAscensionLevel());
        lore.add("§7Rebirth: §c" + playerData.getRebirthCount());
        lore.add("");
        lore.add("§7Multiplier: §e" + String.format("%.2f", playerData.calculateTotalMultiplier()) + "x");
        
        // TODO: Create info item (player head or info block)
        // ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        // meta.setDisplayName("§e§l" + playerData.getUsername() + "'s Stats");
        // meta.setLore(lore);
    }
    
    /**
     * Add close button
     */
    private void addCloseButton(Object inventory, int slot) {
        // TODO: Create close button item
        // ItemStack item = new ItemStack(Material.BARRIER);
        // meta.setDisplayName("§c§lClose");
    }
    
    /**
     * Handle shop click
     */
    public void handleClick(Object player, int slot, Object item) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(getPlayerUUID(player));
        if (playerData == null) return;
        
        // Get clicked generator type
        GeneratorType type = getGeneratorTypeFromSlot(slot);
        if (type == null) return;
        
        // Check if player can purchase
        if (!canPurchase(playerData, type)) {
            sendMessage(player, "§cYou cannot purchase this generator!");
            playSound(player, "ERROR");
            return;
        }
        
        BigDecimal cost = type.getCost();
        if (!playerData.hasEssence(cost)) {
            sendMessage(player, "§cInsufficient Essence! Need " + NumberFormatter.format(cost));
            playSound(player, "ERROR");
            return;
        }
        
        // Purchase generator
        playerData.removeEssence(cost);
        playerData.incrementGeneratorsPurchased();
        
        // TODO: Give generator item to player
        // giveGeneratorItem(player, type);
        
        sendMessage(player, "§a§lPurchased! §7" + type.getDisplayName() + " for " + NumberFormatter.format(cost) + " Essence");
        playSound(player, "SUCCESS");
        
        // Refresh GUI
        openShop(player);
    }
    
    /**
     * Check if player can purchase generator
     */
    private boolean canPurchase(PlayerData playerData, GeneratorType type) {
        // Check prestige requirement
        if (playerData.getPrestigeLevel() < type.getRequiredPrestige()) {
            return false;
        }
        
        // Check ascension requirement
        if (playerData.getAscensionLevel() < type.getRequiredAscension()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Get generator type from slot
     */
    private GeneratorType getGeneratorTypeFromSlot(int slot) {
        // Map slots to generator types
        int[] validSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31};
        
        int index = -1;
        for (int i = 0; i < validSlots.length; i++) {
            if (validSlots[i] == slot) {
                index = i;
                break;
            }
        }
        
        if (index == -1 || index >= GeneratorType.values().length) {
            return null;
        }
        
        return GeneratorType.values()[index];
    }
    
    // Utility methods (TODO: Implement with Hytale API)
    
    private Object getPlayerUUID(Object player) {
        // TODO: return player.getUUID();
        return null;
    }
    
    private void sendMessage(Object player, String message) {
        // TODO: player.sendMessage(message);
        plugin.getLogger().info("[GUI] " + message);
    }
    
    private void playSound(Object player, String sound) {
        // TODO: player.playSound(sound);
    }
}
