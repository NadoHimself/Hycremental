package de.ageofflair.hycremental.gui;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.generators.GeneratorType;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Shop GUI - Generator Shop Interface
 */
public class ShopGUI {
    
    private final Hycremental plugin;
    
    public ShopGUI(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    public void openShop(Player player) {
        UUID uuid = player.getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            player.sendMessage(Message.raw("§cError loading player data!"));
            return;
        }
        
        sendTextBasedShop(player, playerData);
    }
    
    public void handlePurchase(Player player, GeneratorType type) {
        UUID uuid = player.getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            return;
        }
        
        if (playerData.getPrestigeLevel() < type.getRequiredPrestige()) {
            player.sendMessage(Message.raw("§cRequires Prestige " + type.getRequiredPrestige() + "!"));
            return;
        }
        
        BigDecimal cost = type.getCost();
        if (!playerData.hasEssence(cost)) {
            player.sendMessage(Message.raw("§cInsufficient Essence!"));
            player.sendMessage(Message.raw("§7Need: §a" + NumberFormatter.format(cost)));
            player.sendMessage(Message.raw("§7Have: §c" + NumberFormatter.format(playerData.getEssence())));
            return;
        }
        
        int currentGenerators = plugin.getGeneratorManager().getPlayerGeneratorCount(uuid);
        if (currentGenerators >= playerData.getGeneratorSlots()) {
            player.sendMessage(Message.raw("§cNo generator slots available!"));
            player.sendMessage(Message.raw("§7Upgrade with §e/island slots"));
            return;
        }
        
        playerData.removeEssence(cost);
        playerData.incrementGeneratorsPurchased();
        plugin.getPlayerDataManager().savePlayerData(playerData);
        
        player.sendMessage(Message.raw("§a§l✓ Purchased! §7" + type.getDisplayName()));
        player.sendMessage(Message.raw("§7Place it on your island to start producing!"));
        
        openShop(player);
    }
    
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
