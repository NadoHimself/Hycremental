package de.ageofflair.hycremental.commands;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.gui.PrestigeGUI;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Prestige Command - Handle prestige, ascension, and rebirth
 * 
 * Commands:
 * /prestige - Open prestige menu
 * /prestige confirm - Confirm prestige
 * /ascend - Open ascension menu
 * /ascend confirm - Confirm ascension
 * /rebirth - Open rebirth menu
 * /rebirth confirm - Confirm rebirth
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class PrestigeCommand {
    
    private final Hycremental plugin;
    private final PrestigeGUI prestigeGUI;
    
    // Pending confirmations (UUID -> Type)
    private final Map<UUID, String> pendingConfirmations = new HashMap<>();
    
    public PrestigeCommand(Hycremental plugin) {
        this.plugin = plugin;
        this.prestigeGUI = new PrestigeGUI(plugin);
    }
    
    /**
     * Execute prestige command
     */
    public void executePrestige(Object player, String[] args) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(getPlayerUUID(player));
        if (playerData == null) {
            sendMessage(player, "§cError loading player data!");
            return;
        }
        
        // No arguments - open GUI
        if (args.length == 0) {
            prestigeGUI.openPrestigeMenu(player);
            return;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "confirm":
                confirmPrestige(player, playerData);
                break;
                
            case "info":
                showPrestigeInfo(player, playerData);
                break;
                
            case "cancel":
                cancelConfirmation(player);
                break;
                
            default:
                sendMessage(player, "§cUsage: /prestige [confirm|info|cancel]");
        }
    }
    
    /**
     * Execute ascension command
     */
    public void executeAscension(Object player, String[] args) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(getPlayerUUID(player));
        if (playerData == null) {
            sendMessage(player, "§cError loading player data!");
            return;
        }
        
        // No arguments - show info
        if (args.length == 0) {
            showAscensionInfo(player, playerData);
            return;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "confirm":
                confirmAscension(player, playerData);
                break;
                
            case "info":
                showAscensionInfo(player, playerData);
                break;
                
            case "cancel":
                cancelConfirmation(player);
                break;
                
            default:
                sendMessage(player, "§cUsage: /ascend [confirm|info|cancel]");
        }
    }
    
    /**
     * Execute rebirth command
     */
    public void executeRebirth(Object player, String[] args) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(getPlayerUUID(player));
        if (playerData == null) {
            sendMessage(player, "§cError loading player data!");
            return;
        }
        
        // No arguments - show warning
        if (args.length == 0) {
            showRebirthWarning(player, playerData);
            return;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "confirm":
                confirmRebirth(player, playerData);
                break;
                
            case "info":
                showRebirthInfo(player, playerData);
                break;
                
            case "cancel":
                cancelConfirmation(player);
                break;
                
            default:
                sendMessage(player, "§cUsage: /rebirth [confirm|info|cancel]");
        }
    }
    
    /**
     * Confirm prestige
     */
    private void confirmPrestige(Object player, PlayerData playerData) {
        // Calculate cost
        BigDecimal cost = calculatePrestigeCost(playerData.getPrestigeLevel());
        
        // Check requirements
        if (!playerData.hasEssence(cost)) {
            sendMessage(player, "§cInsufficient Essence! Need: " + NumberFormatter.format(cost));
            return;
        }
        
        // Perform prestige
        int oldPrestige = playerData.getPrestigeLevel();
        
        // Reset
        playerData.prestige();
        
        // Remove generators (keep Tier 1)
        plugin.getGeneratorManager().resetPlayerGenerators(playerData.getUuid(), false);
        
        // Reset island upgrades
        // TODO: Reset island size back to default
        
        // Announce
        sendMessage(player, "§7§m─────────────────────────────");
        sendMessage(player, "§6§l☆ PRESTIGE! ☆");
        sendMessage(player, "");
        sendMessage(player, "§7You have prestiged from §6" + oldPrestige + " §7to §6" + playerData.getPrestigeLevel() + "§7!");
        sendMessage(player, "");
        sendMessage(player, "§7New Multiplier: §e" + String.format("%.2f", playerData.calculateTotalMultiplier()) + "x");
        sendMessage(player, "§7§m─────────────────────────────");
        
        // Broadcast to server
        broadcastMessage("§6§l" + playerData.getUsername() + " §7has prestiged to §6Prestige " + playerData.getPrestigeLevel() + "§7!");
        
        playSound(player, "LEVELUP");
    }
    
    /**
     * Confirm ascension
     */
    private void confirmAscension(Object player, PlayerData playerData) {
        int requiredPrestige = 50 + (playerData.getAscensionLevel() * 50);
        
        // Check requirements
        if (playerData.getPrestigeLevel() < requiredPrestige) {
            sendMessage(player, "§cYou need Prestige " + requiredPrestige + " to ascend!");
            return;
        }
        
        // Perform ascension
        int oldAscension = playerData.getAscensionLevel();
        
        // Reset everything
        playerData.ascend();
        playerData.setPrestigeLevel(0);
        playerData.setEssence(BigDecimal.ZERO);
        
        // Remove all generators
        plugin.getGeneratorManager().resetPlayerGenerators(playerData.getUuid(), true);
        
        // Announce
        sendMessage(player, "§7§m─────────────────────────────");
        sendMessage(player, "§5§l★ ASCENSION! ★");
        sendMessage(player, "");
        sendMessage(player, "§7You have ascended from §5" + oldAscension + " §7to §5" + playerData.getAscensionLevel() + "§7!");
        sendMessage(player, "");
        sendMessage(player, "§7New Multiplier: §d" + String.format("%.2f", playerData.calculateTotalMultiplier()) + "x");
        sendMessage(player, "§7You gained §d1 Ascension Point§7!");
        sendMessage(player, "§7§m─────────────────────────────");
        
        // Broadcast to server
        broadcastMessage("§5§l" + playerData.getUsername() + " §7has ascended to §5Ascension " + playerData.getAscensionLevel() + "§7!");
        
        playSound(player, "WITHER_SPAWN");
    }
    
    /**
     * Confirm rebirth
     */
    private void confirmRebirth(Object player, PlayerData playerData) {
        // Check requirements
        if (playerData.getAscensionLevel() < 10) {
            sendMessage(player, "§cYou need Ascension 10 to rebirth!");
            return;
        }
        
        // Perform rebirth
        int oldRebirth = playerData.getRebirthCount();
        
        // Reset EVERYTHING
        playerData.rebirth();
        
        // Remove all generators
        plugin.getGeneratorManager().resetPlayerGenerators(playerData.getUuid(), true);
        
        // Reset island
        playerData.setIslandSize(20);
        playerData.setGeneratorSlots(10);
        
        // Announce
        sendMessage(player, "§7§m─────────────────────────────");
        sendMessage(player, "§c§l✦ REBIRTH! ✦");
        sendMessage(player, "");
        sendMessage(player, "§7You have been reborn! §c" + oldRebirth + " §7→ §c" + playerData.getRebirthCount());
        sendMessage(player, "");
        sendMessage(player, "§7New Multiplier: §c" + String.format("%.2f", playerData.calculateTotalMultiplier()) + "x");
        sendMessage(player, "§7All Essence gains are now §c2x §7permanent!");
        sendMessage(player, "§7§m─────────────────────────────");
        
        // Broadcast to entire server
        broadcastMessage("§c§l✦ " + playerData.getUsername() + " HAS BEEN REBORN! ✦");
        broadcastMessage("§cRebirth Count: " + playerData.getRebirthCount());
        
        playSound(player, "DRAGON_DEATH");
    }
    
    /**
     * Show prestige info
     */
    private void showPrestigeInfo(Object player, PlayerData playerData) {
        BigDecimal cost = calculatePrestigeCost(playerData.getPrestigeLevel());
        
        sendMessage(player, "§7§m─────────────────────────────");
        sendMessage(player, "§6§lPrestige Information");
        sendMessage(player, "");
        sendMessage(player, "§7Current Prestige: §6" + playerData.getPrestigeLevel());
        sendMessage(player, "§7Next Prestige: §6" + (playerData.getPrestigeLevel() + 1));
        sendMessage(player, "");
        sendMessage(player, "§7Required Essence: §a" + NumberFormatter.format(cost));
        sendMessage(player, "§7Your Essence: §a" + NumberFormatter.format(playerData.getEssence()));
        sendMessage(player, "");
        sendMessage(player, "§7Reward: §e+10% Global Multiplier");
        sendMessage(player, "§7Current Multiplier: §e" + String.format("%.2f", playerData.calculateTotalMultiplier()) + "x");
        sendMessage(player, "§7§m─────────────────────────────");
        sendMessage(player, "§eType /prestige confirm to prestige!");
    }
    
    /**
     * Show ascension info
     */
    private void showAscensionInfo(Object player, PlayerData playerData) {
        int requiredPrestige = 50 + (playerData.getAscensionLevel() * 50);
        boolean canAscend = playerData.getPrestigeLevel() >= requiredPrestige;
        
        sendMessage(player, "§7§m─────────────────────────────");
        sendMessage(player, "§5§lAscension Information");
        sendMessage(player, "");
        sendMessage(player, "§7Current Ascension: §5" + playerData.getAscensionLevel());
        sendMessage(player, "§7Next Ascension: §5" + (playerData.getAscensionLevel() + 1));
        sendMessage(player, "");
        sendMessage(player, "§7Required Prestige: " + (canAscend ? "§a" : "§c") + requiredPrestige);
        sendMessage(player, "§7Your Prestige: " + (canAscend ? "§a" : "§c") + playerData.getPrestigeLevel());
        sendMessage(player, "");
        sendMessage(player, "§7Reward: §d+50% Permanent Multiplier");
        sendMessage(player, "§7Current Multiplier: §d" + String.format("%.2f", playerData.calculateTotalMultiplier()) + "x");
        sendMessage(player, "§7§m─────────────────────────────");
        
        if (canAscend) {
            sendMessage(player, "§dType /ascend confirm to ascend!");
        } else {
            sendMessage(player, "§cYou do not meet the requirements yet!");
        }
    }
    
    /**
     * Show rebirth warning
     */
    private void showRebirthWarning(Object player, PlayerData playerData) {
        boolean canRebirth = playerData.getAscensionLevel() >= 10;
        
        sendMessage(player, "§7§m─────────────────────────────");
        sendMessage(player, "§c§lREBIRTH WARNING");
        sendMessage(player, "");
        sendMessage(player, "§c§lTHIS WILL RESET EVERYTHING!");
        sendMessage(player, "");
        sendMessage(player, "§7Current Rebirth: §c" + playerData.getRebirthCount());
        sendMessage(player, "§7Next Rebirth: §c" + (playerData.getRebirthCount() + 1));
        sendMessage(player, "");
        sendMessage(player, "§7Required Ascension: " + (canRebirth ? "§a" : "§c") + "10");
        sendMessage(player, "§7Your Ascension: " + (canRebirth ? "§a" : "§c") + playerData.getAscensionLevel());
        sendMessage(player, "");
        sendMessage(player, "§7Reward: §c2x ALL Essence (Permanent)");
        sendMessage(player, "§7§m─────────────────────────────");
        
        if (canRebirth) {
            sendMessage(player, "§c§lType /rebirth confirm if you're ABSOLUTELY SURE!");
        } else {
            sendMessage(player, "§cYou do not meet the requirements yet!");
        }
    }
    
    /**
     * Show rebirth info
     */
    private void showRebirthInfo(Object player, PlayerData playerData) {
        showRebirthWarning(player, playerData);
    }
    
    /**
     * Cancel pending confirmation
     */
    private void cancelConfirmation(Object player) {
        UUID uuid = (UUID) getPlayerUUID(player);
        if (pendingConfirmations.remove(uuid) != null) {
            sendMessage(player, "§aConfirmation cancelled.");
        } else {
            sendMessage(player, "§cNo pending confirmation.");
        }
    }
    
    /**
     * Calculate prestige cost
     */
    private BigDecimal calculatePrestigeCost(int currentPrestige) {
        double baseCost = 1_000_000;
        double multiplier = Math.pow(1.5, currentPrestige);
        return BigDecimal.valueOf(baseCost * multiplier);
    }
    
    // Utility methods
    
    private Object getPlayerUUID(Object player) {
        return null; // TODO
    }
    
    private void sendMessage(Object player, String message) {
        plugin.getLogger().info("[Command] " + message);
        // TODO: player.sendMessage(message);
    }
    
    private void broadcastMessage(String message) {
        plugin.getLogger().info("[Broadcast] " + message);
        // TODO: server.broadcastMessage(message);
    }
    
    private void playSound(Object player, String sound) {
        // TODO: player.playSound(sound);
    }
}
