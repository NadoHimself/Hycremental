package de.ageofflair.hycremental.gui;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Prestige GUI - Prestige/Ascension/Rebirth Preview
 * 
 * Uses Hytale's PageManager system for custom UI
 * 
 * TODO: Create UI file at Common/UI/Custom/prestige_gui.ui
 * - Tabbed interface (Prestige, Ascension, Rebirth)
 * - Preview of next level benefits
 * - Cost display
 * - Warning messages for resets
 * - Confirmation buttons
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class PrestigeGUI {
    
    private final Hycremental plugin;
    
    private static final String PRESTIGE_PAGE_ID = "hycremental:prestige";
    
    public PrestigeGUI(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Open prestige menu
     */
    public void openPrestigeMenu(Player player) {
        UUID uuid = player.getComponent(PlayerRef.class).getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            player.sendMessage(Message.raw("§cError loading player data!"));
            return;
        }
        
        // TODO: Implement with Hytale PageManager API
        // 
        // Open custom prestige page with tabs:
        // Map<String, Object> data = new HashMap<>();
        // 
        // Prestige Tab:
        // data.put("current_prestige", playerData.getPrestigeLevel());
        // data.put("next_prestige", playerData.getPrestigeLevel() + 1);
        // data.put("prestige_cost", plugin.getPrestigeManager().calculatePrestigeCost(playerData.getPrestigeLevel()).toPlainString());
        // data.put("current_multiplier", playerData.calculateTotalMultiplier());
        // data.put("new_multiplier", (playerData.getPrestigeLevel() + 1) * 0.1 + 1.0);
        // data.put("can_prestige", plugin.getPrestigeManager().canPrestige(playerData));
        // 
        // Ascension Tab:
        // int requiredPrestige = 50 + (playerData.getAscensionLevel() * 50);
        // data.put("current_ascension", playerData.getAscensionLevel());
        // data.put("required_prestige", requiredPrestige);
        // data.put("can_ascend", plugin.getPrestigeManager().canAscend(playerData));
        // 
        // Rebirth Tab:
        // data.put("current_rebirth", playerData.getRebirthCount());
        // data.put("required_ascension", 10);
        // data.put("can_rebirth", plugin.getPrestigeManager().canRebirth(playerData));
        // 
        // player.getPageManager().openCustomPage(PageId.of(PRESTIGE_PAGE_ID), data);
        
        // Temporary fallback: Send text-based menu
        sendTextBasedPrestigeMenu(player, playerData);
    }
    
    /**
     * Open ascension menu
     */
    public void openAscensionMenu(Player player) {
        UUID uuid = player.getComponent(PlayerRef.class).getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            return;
        }
        
        // TODO: Similar to prestige menu but focused on ascension tab
        sendTextBasedAscensionMenu(player, playerData);
    }
    
    /**
     * Open rebirth menu
     */
    public void openRebirthMenu(Player player) {
        UUID uuid = player.getComponent(PlayerRef.class).getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            return;
        }
        
        // TODO: Similar to prestige menu but focused on rebirth tab with big warning
        sendTextBasedRebirthMenu(player, playerData);
    }
    
    /**
     * Temporary text-based prestige menu
     */
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
        player.sendMessage(Message.raw("§8 • §7Unlock higher tier generators"));
        player.sendMessage(Message.raw("§8 • §7Better generator quality rates"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7§lYou will lose:"));
        player.sendMessage(Message.raw("§8 • §cAll Essence"));
        player.sendMessage(Message.raw("§8 • §cTier 2+ Generators"));
        player.sendMessage(Message.raw("§8 • §aTier 1 Generators kept"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Current Multiplier: §e" + String.format("%.2fx", playerData.calculateTotalMultiplier())));
        player.sendMessage(Message.raw("§7New Multiplier: §a" + String.format("%.2fx", playerData.calculateTotalMultiplier() + 0.1)));
        player.sendMessage(Message.raw(""));
        
        if (canPrestige) {
            player.sendMessage(Message.raw("§a§lType /prestige confirm to prestige!"));
        } else {
            player.sendMessage(Message.raw("§cYou need more Essence to prestige!"));
        }
        
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
        player.sendMessage(Message.raw(""));
    }
    
    /**
     * Temporary text-based ascension menu
     */
    private void sendTextBasedAscensionMenu(Player player, PlayerData playerData) {
        int requiredPrestige = 50 + (playerData.getAscensionLevel() * 50);
        boolean canAscend = plugin.getPrestigeManager().canAscend(playerData);
        
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§5§l─────────────────────────────"));
        player.sendMessage(Message.raw("§5§l     ★ ASCENSION ★"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Current Ascension: §5" + playerData.getAscensionLevel()));
        player.sendMessage(Message.raw("§7Next Ascension: §5" + (playerData.getAscensionLevel() + 1)));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Required Prestige: " + (canAscend ? "§a" : "§c") + requiredPrestige));
        player.sendMessage(Message.raw("§7Your Prestige: " + (canAscend ? "§a" : "§c") + playerData.getPrestigeLevel()));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7§lBenefits:"));
        player.sendMessage(Message.raw("§8 • §d+50% Permanent Multiplier"));
        player.sendMessage(Message.raw("§8 • §dMassive production boost"));
        player.sendMessage(Message.raw("§8 • §dSpecial ascension perks"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7§lYou will lose:"));
        player.sendMessage(Message.raw("§8 • §cAll Prestige Levels (reset to 0)"));
        player.sendMessage(Message.raw("§8 • §cAll Essence"));
        player.sendMessage(Message.raw("§8 • §cALL Generators"));
        player.sendMessage(Message.raw(""));
        
        if (canAscend) {
            player.sendMessage(Message.raw("§d§lType /ascend confirm to ascend!"));
        } else {
            player.sendMessage(Message.raw("§cYou need Prestige " + requiredPrestige + " to ascend!"));
        }
        
        player.sendMessage(Message.raw("§5§l─────────────────────────────"));
        player.sendMessage(Message.raw(""));
    }
    
    /**
     * Temporary text-based rebirth menu
     */
    private void sendTextBasedRebirthMenu(Player player, PlayerData playerData) {
        boolean canRebirth = plugin.getPrestigeManager().canRebirth(playerData);
        
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§c§l─────────────────────────────"));
        player.sendMessage(Message.raw("§c§l    ✦ REBIRTH WARNING ✦"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§c§lTHIS WILL RESET EVERYTHING!"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Current Rebirth: §c" + playerData.getRebirthCount()));
        player.sendMessage(Message.raw("§7Next Rebirth: §c" + (playerData.getRebirthCount() + 1)));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Required Ascension: " + (canRebirth ? "§a" : "§c") + "10"));
        player.sendMessage(Message.raw("§7Your Ascension: " + (canRebirth ? "§a" : "§c") + playerData.getAscensionLevel()));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7§lBenefits:"));
        player.sendMessage(Message.raw("§8 • §c2x ALL Essence (Permanent)"));
        player.sendMessage(Message.raw("§8 • §cGame-breaking multiplier"));
        player.sendMessage(Message.raw("§8 • §cUltimate prestige status"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7§lYou will lose:"));
        player.sendMessage(Message.raw("§8 • §cALL Prestige Levels (reset to 0)"));
        player.sendMessage(Message.raw("§8 • §cALL Ascension Levels (reset to 0)"));
        player.sendMessage(Message.raw("§8 • §cALL Essence"));
        player.sendMessage(Message.raw("§8 • §cALL Generators"));
        player.sendMessage(Message.raw("§8 • §cIsland size reset to 20x20"));
        player.sendMessage(Message.raw("§8 • §cGenerator slots reset to 10"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Current Total Multiplier: §e" + String.format("%.2fx", playerData.calculateTotalMultiplier())));
        player.sendMessage(Message.raw("§7After Rebirth: §c" + String.format("%.2fx", (playerData.getRebirthCount() + 1) * 2.0)));
        player.sendMessage(Message.raw(""));
        
        if (canRebirth) {
            player.sendMessage(Message.raw("§c§lType /rebirth confirm if you're SURE!"));
        } else {
            player.sendMessage(Message.raw("§cYou need Ascension 10 to rebirth!"));
        }
        
        player.sendMessage(Message.raw("§c§l─────────────────────────────"));
        player.sendMessage(Message.raw(""));
    }
}
