package de.ageofflair.hycremental.gui;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.generators.GeneratorData;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Upgrade GUI - Generator Upgrade Interface
 */
public class UpgradeGUI {
    
    private final Hycremental plugin;
    
    public UpgradeGUI(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    public void openUpgradeMenu(Player player, GeneratorData generator) {
        UUID uuid = player.getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            player.sendMessage(Message.raw("§cError loading player data!"));
            return;
        }
        
        sendTextBasedUpgradeMenu(player, playerData, generator);
    }
    
    private void sendTextBasedUpgradeMenu(Player player, PlayerData playerData, GeneratorData generator) {
        BigDecimal currentProduction = generator.getCurrentProduction();
        BigDecimal upgradeCost = generator.getUpgradeCost();
        int currentLevel = generator.getUpgradeLevel();
        
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
        player.sendMessage(Message.raw("§6§l   Generator Upgrade"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Type: §e" + generator.getType().getDisplayName()));
        player.sendMessage(Message.raw("§7Current Level: §a" + currentLevel));
        player.sendMessage(Message.raw("§7Quality: §b" + generator.getQuality()));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Current Production: §e" + NumberFormatter.format(currentProduction) + "/s"));
        player.sendMessage(Message.raw("§7After Upgrade: §a" + NumberFormatter.format(currentProduction.multiply(BigDecimal.valueOf(1.15))) + "/s"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Upgrade Cost: §e" + NumberFormatter.format(upgradeCost)));
        player.sendMessage(Message.raw("§7Your Balance: §a" + NumberFormatter.format(playerData.getEssence())));
        player.sendMessage(Message.raw(""));
        
        if (playerData.hasEssence(upgradeCost)) {
            player.sendMessage(Message.raw("§a§lClick to upgrade!"));
        } else {
            player.sendMessage(Message.raw("§c§lInsufficient Essence!"));
        }
        
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
    }
}
