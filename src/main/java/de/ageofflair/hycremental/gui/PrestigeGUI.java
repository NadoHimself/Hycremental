package de.ageofflair.hycremental.gui;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Prestige GUI - Preview and confirm prestige/ascension/rebirth
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class PrestigeGUI {
    
    private final Hycremental plugin;
    private static final int GUI_SIZE = 45; // 5 rows
    
    public PrestigeGUI(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Open prestige menu
     * @param player Player to open GUI for
     */
    public void openPrestigeMenu(Object player) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(getPlayerUUID(player));
        if (playerData == null) {
            sendMessage(player, "§cError loading player data!");
            return;
        }
        
        // Create GUI
        // TODO: GUI inventory = createInventory("§5§lProgression Menu", GUI_SIZE);
        
        // Current stats
        addCurrentStats(null, 4, playerData);
        
        // Prestige option
        addPrestigeOption(null, 20, playerData);
        
        // Ascension option
        addAscensionOption(null, 22, playerData);
        
        // Rebirth option
        addRebirthOption(null, 24, playerData);
        
        // Info/Help
        addInfoItem(null, 40);
        
        // TODO: player.openInventory(inventory);
    }
    
    /**
     * Add current stats display
     */
    private void addCurrentStats(Object inventory, int slot, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Current Progression:");
        lore.add("");
        lore.add("§8 • §7Prestige: §6" + playerData.getPrestigeLevel());
        lore.add("§8 • §7Ascension: §5" + playerData.getAscensionLevel());
        lore.add("§8 • §7Rebirth: §c" + playerData.getRebirthCount());
        lore.add("");
        lore.add("§7Total Multiplier: §e" + String.format("%.2f", playerData.calculateTotalMultiplier()) + "x");
        lore.add("");
        
        // TODO: Create item (player head or beacon)
        // meta.setDisplayName("§e§l" + playerData.getUsername());
    }
    
    /**
     * Add prestige option
     */
    private void addPrestigeOption(Object inventory, int slot, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        
        int currentPrestige = playerData.getPrestigeLevel();
        int nextPrestige = currentPrestige + 1;
        
        // Calculate prestige cost
        BigDecimal cost = calculatePrestigeCost(currentPrestige);
        boolean canAfford = playerData.hasEssence(cost);
        
        lore.add("");
        lore.add("§7Current: §6Prestige " + currentPrestige);
        lore.add("§7Next: §6Prestige " + nextPrestige);
        lore.add("");
        
        // Show what will be reset
        lore.add("§c§lWill Reset:");
        lore.add("§8 • §cEssence Balance");
        lore.add("§8 • §cGenerators (Tier 2+)");
        lore.add("§8 • §cIsland Upgrades");
        lore.add("");
        
        // Show what will be kept
        lore.add("§a§lWill Keep:");
        lore.add("§8 • §aPrestige Level");
        lore.add("§8 • §aGems & Crystals");
        lore.add("§8 • §aCosmetics & VIP");
        lore.add("§8 • §aAchievements");
        lore.add("");
        
        // Show rewards
        lore.add("§e§lRewards:");
        lore.add("§8 • §e+10% Global Multiplier");
        
        // Unlock new tiers
        if (nextPrestige == 1) {
            lore.add("§8 • §eUnlock Tier 4 Generators");
        } else if (nextPrestige == 5) {
            lore.add("§8 • §eUnlock Tier 5 Generators");
        } else if (nextPrestige == 10) {
            lore.add("§8 • §eUnlock Tier 6 Generators");
            lore.add("§8 • §eUnlock Auto-Sell Feature");
        } else if (nextPrestige == 50) {
            lore.add("§8 • §eUnlock Tier 8 Generators");
            lore.add("§8 • §eUnlock Ascension System");
        }
        
        lore.add("");
        
        // Cost
        String costColor = canAfford ? "§a" : "§c";
        lore.add("§7Required: " + costColor + NumberFormatter.format(cost) + " Essence");
        lore.add("");
        
        if (canAfford) {
            lore.add("§e§l» Click to prestige «");
        } else {
            lore.add("§c§l✘ Insufficient Essence");
        }
        
        // TODO: Create item (gold block or enchanted golden apple)
        // meta.setDisplayName("§6§lPrestige");
    }
    
    /**
     * Add ascension option
     */
    private void addAscensionOption(Object inventory, int slot, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        
        int currentAscension = playerData.getAscensionLevel();
        int nextAscension = currentAscension + 1;
        int requiredPrestige = 50 + (currentAscension * 50);
        
        boolean canAscend = playerData.getPrestigeLevel() >= requiredPrestige;
        
        lore.add("");
        lore.add("§7Current: §5Ascension " + currentAscension);
        lore.add("§7Next: §5Ascension " + nextAscension);
        lore.add("");
        
        // Requirements
        String reqColor = canAscend ? "§a" : "§c";
        lore.add("§7Required: " + reqColor + "Prestige " + requiredPrestige);
        lore.add("");
        
        // Show what will be reset
        lore.add("§c§lWill Reset:");
        lore.add("§8 • §cEverything from Prestige");
        lore.add("");
        
        // Show what will be kept
        lore.add("§a§lWill Keep:");
        lore.add("§8 • §aAscension Level & Perks");
        lore.add("§8 • §aGems & Crystals");
        lore.add("§8 • §aCosmetics & VIP");
        lore.add("");
        
        // Show rewards
        lore.add("§d§lRewards:");
        lore.add("§8 • §d1 Ascension Point");
        lore.add("§8 • §dUnlock Ascension Perks");
        lore.add("§8 • §d+50% Permanent Multiplier");
        lore.add("");
        
        if (canAscend) {
            lore.add("§d§l» Click to ascend «");
        } else {
            lore.add("§c§l✘ Requirement not met");
        }
        
        // TODO: Create item (nether star or beacon)
        // meta.setDisplayName("§5§lAscension");
    }
    
    /**
     * Add rebirth option
     */
    private void addRebirthOption(Object inventory, int slot, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        
        int currentRebirth = playerData.getRebirthCount();
        int nextRebirth = currentRebirth + 1;
        int requiredAscension = 10;
        
        boolean canRebirth = playerData.getAscensionLevel() >= requiredAscension;
        
        lore.add("");
        lore.add("§7Current: §cRebirth " + currentRebirth);
        lore.add("§7Next: §cRebirth " + nextRebirth);
        lore.add("");
        
        // Requirements
        String reqColor = canRebirth ? "§a" : "§c";
        lore.add("§7Required: " + reqColor + "Ascension " + requiredAscension);
        lore.add("");
        
        // Show what will be reset
        lore.add("§c§lWill Reset:");
        lore.add("§8 • §cABSOLUTELY EVERYTHING");
        lore.add("§8 • §cPrestige Level → 0");
        lore.add("§8 • §cAscension Level → 0");
        lore.add("§8 • §cAll Currency");
        lore.add("");
        
        // Show what will be kept
        lore.add("§a§lWill Keep:");
        lore.add("§8 • §aRebirth Count");
        lore.add("§8 • §aCosmetics & VIP");
        lore.add("");
        
        // Show rewards
        lore.add("§c§lRewards:");
        lore.add("§8 • §c1 Rebirth Token");
        lore.add("§8 • §c2x ALL Essence (Permanent)");
        lore.add("§8 • §cGame-Breaking Bonuses");
        lore.add("");
        
        if (canRebirth) {
            lore.add("§c§l❗ WARNING: THIS IS PERMANENT!");
            lore.add("§c§l» Click to rebirth «");
        } else {
            lore.add("§c§l✘ Requirement not met");
        }
        
        // TODO: Create item (dragon egg or totem)
        // meta.setDisplayName("§c§lRebirth");
    }
    
    /**
     * Add info item
     */
    private void addInfoItem(Object inventory, int slot) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7§lProgression Guide:");
        lore.add("");
        lore.add("§6Prestige §7- Reset to gain multipliers");
        lore.add("§7 and unlock new generator tiers.");
        lore.add("");
        lore.add("§5Ascension §7- Reset everything for");
        lore.add("§7 permanent perks and massive boosts.");
        lore.add("");
        lore.add("§cRebirth §7- Complete reset for");
        lore.add("§7 game-breaking bonuses. Ultimate power!");
        lore.add("");
        
        // TODO: Create item (book or knowledge book)
        // meta.setDisplayName("§e§lProgression Info");
    }
    
    /**
     * Handle prestige GUI click
     */
    public void handleClick(Object player, int slot) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(getPlayerUUID(player));
        if (playerData == null) return;
        
        switch (slot) {
            case 20: // Prestige
                openPrestigeConfirmation(player, playerData);
                break;
                
            case 22: // Ascension
                openAscensionConfirmation(player, playerData);
                break;
                
            case 24: // Rebirth
                openRebirthConfirmation(player, playerData);
                break;
        }
    }
    
    /**
     * Open prestige confirmation
     */
    private void openPrestigeConfirmation(Object player, PlayerData playerData) {
        BigDecimal cost = calculatePrestigeCost(playerData.getPrestigeLevel());
        
        if (!playerData.hasEssence(cost)) {
            sendMessage(player, "§cYou need " + NumberFormatter.format(cost) + " Essence to prestige!");
            playSound(player, "ERROR");
            return;
        }
        
        // TODO: Open confirmation GUI with confirm/cancel buttons
        sendMessage(player, "§eType /prestige confirm to confirm your prestige!");
        closeGUI(player);
    }
    
    /**
     * Open ascension confirmation
     */
    private void openAscensionConfirmation(Object player, PlayerData playerData) {
        int requiredPrestige = 50 + (playerData.getAscensionLevel() * 50);
        
        if (playerData.getPrestigeLevel() < requiredPrestige) {
            sendMessage(player, "§cYou need Prestige " + requiredPrestige + " to ascend!");
            playSound(player, "ERROR");
            return;
        }
        
        // TODO: Open confirmation GUI
        sendMessage(player, "§dType /ascend confirm to confirm your ascension!");
        closeGUI(player);
    }
    
    /**
     * Open rebirth confirmation
     */
    private void openRebirthConfirmation(Object player, PlayerData playerData) {
        if (playerData.getAscensionLevel() < 10) {
            sendMessage(player, "§cYou need Ascension 10 to rebirth!");
            playSound(player, "ERROR");
            return;
        }
        
        // TODO: Open confirmation GUI
        sendMessage(player, "§c§lWARNING: §7Type /rebirth confirm to confirm your rebirth!");
        sendMessage(player, "§cThis will reset EVERYTHING!");
        closeGUI(player);
    }
    
    /**
     * Calculate prestige cost
     */
    private BigDecimal calculatePrestigeCost(int currentPrestige) {
        // Formula: 1M * (1.5 ^ prestige)
        double baseCost = 1_000_000;
        double multiplier = Math.pow(1.5, currentPrestige);
        return BigDecimal.valueOf(baseCost * multiplier);
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
