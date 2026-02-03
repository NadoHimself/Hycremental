package de.ageofflair.hycremental.gui;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Prestige GUI - Prestige/Ascension/Rebirth Preview
 */
public class PrestigeGUI {
    
    private final Hycremental plugin;
    
    public PrestigeGUI(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    public void openPrestigeMenu(Player player) {
        UUID uuid = player.getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            player.sendMessage(Message.raw("§cError loading player data!"));
            return;
        }
        
        sendTextBasedPrestigeMenu(player, playerData);
    }
    
    public void openAscensionMenu(Player player) {
        UUID uuid = player.getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            return;
        }
        
        sendTextBasedAscensionMenu(player, playerData);
    }
    
    public void openRebirthMenu(Player player) {
        UUID uuid = player.getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            return;
        }
        
        sendTextBasedRebirthMenu(player, playerData);
    }
    
    private void sendTextBasedPrestigeMenu(Player player, PlayerData playerData) {
        BigDecimal cost = plugin.getPrestigeManager().calculatePrestigeCost(playerData.getPrestigeLevel());
        boolean canPrestige = plugin.getPrestigeManager().canPrestige(playerData);
        
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
        player.sendMessage(Message.raw("§6§l       PRESTIGE"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Current Prestige: §6" + playerData.getPrestigeLevel()));
        player.sendMessage(Message.raw("§7Next Prestige: §6" + (playerData.getPrestigeLevel() + 1)));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Required Essence: " + (canPrestige ? "§a" : "§c") + NumberFormatter.format(cost)));
        player.sendMessage(Message.raw("§7Your Essence: " + (canPrestige ? "§a" : "§c") + NumberFormatter.format(playerData.getEssence())));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7§lBenefits:"));
        player.sendMessage(Message.raw("§8 • §7+10% Permanent Multiplier"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Current Multiplier: §e" + String.format("%.2fx", playerData.calculateTotalMultiplier())));
        player.sendMessage(Message.raw(""));
        
        if (canPrestige) {
            player.sendMessage(Message.raw("§a§lType /prestige confirm to prestige!"));
        } else {
            player.sendMessage(Message.raw("§cYou need more Essence to prestige!"));
        }
        
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
    }
    
    private void sendTextBasedAscensionMenu(Player player, PlayerData playerData) {
        int requiredPrestige = 50 + (playerData.getAscensionLevel() * 50);
        boolean canAscend = plugin.getPrestigeManager().canAscend(playerData);
        
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§5§l─────────────────────────────"));
        player.sendMessage(Message.raw("§5§l     ★ ASCENSION ★"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Current Ascension: §5" + playerData.getAscensionLevel()));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Required Prestige: " + (canAscend ? "§a" : "§c") + requiredPrestige));
        player.sendMessage(Message.raw("§7Your Prestige: " + (canAscend ? "§a" : "§c") + playerData.getPrestigeLevel()));
        player.sendMessage(Message.raw(""));
        
        if (canAscend) {
            player.sendMessage(Message.raw("§d§lType /ascend confirm to ascend!"));
        } else {
            player.sendMessage(Message.raw("§cYou need Prestige " + requiredPrestige + " to ascend!"));
        }
        
        player.sendMessage(Message.raw("§5§l─────────────────────────────"));
    }
    
    private void sendTextBasedRebirthMenu(Player player, PlayerData playerData) {
        boolean canRebirth = plugin.getPrestigeManager().canRebirth(playerData);
        
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§c§l─────────────────────────────"));
        player.sendMessage(Message.raw("§c§l    ✦ REBIRTH WARNING ✦"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§c§lTHIS WILL RESET EVERYTHING!"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Current Rebirth: §c" + playerData.getRebirthCount()));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Required Ascension: " + (canRebirth ? "§a" : "§c") + "10"));
        player.sendMessage(Message.raw("§7Your Ascension: " + (canRebirth ? "§a" : "§c") + playerData.getAscensionLevel()));
        player.sendMessage(Message.raw(""));
        
        if (canRebirth) {
            player.sendMessage(Message.raw("§c§lType /rebirth confirm if you're SURE!"));
        } else {
            player.sendMessage(Message.raw("§cYou need Ascension 10 to rebirth!"));
        }
        
        player.sendMessage(Message.raw("§c§l─────────────────────────────"));
    }
}
