package de.ageofflair.hycremental.gui;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.util.UUID;

/**
 * Stats GUI - Player Statistics Display
 */
public class StatsGUI {
    
    private final Hycremental plugin;
    
    public StatsGUI(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    public void openStats(Player player) {
        UUID uuid = player.getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            player.sendMessage(Message.raw("§cError loading player data!"));
            return;
        }
        
        sendTextBasedStats(player, playerData);
    }
    
    private void sendTextBasedStats(Player player, PlayerData playerData) {
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
        player.sendMessage(Message.raw("§6§l   ★ YOUR STATISTICS ★"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7§lProgression:"));
        player.sendMessage(Message.raw("§8 • §7Prestige Level: §6" + playerData.getPrestigeLevel()));
        player.sendMessage(Message.raw("§8 • §7Ascension Level: §5" + playerData.getAscensionLevel()));
        player.sendMessage(Message.raw("§8 • §7Rebirth Count: §c" + playerData.getRebirthCount()));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7§lEconomy:"));
        player.sendMessage(Message.raw("§8 • §7Essence: §e" + NumberFormatter.format(playerData.getEssence())));
        player.sendMessage(Message.raw("§8 • §7Gems: §b" + playerData.getGems()));
        player.sendMessage(Message.raw("§8 • §7Crystals: §d" + playerData.getCrystals()));
        player.sendMessage(Message.raw("§8 • §7Lifetime Essence: §6" + NumberFormatter.format(playerData.getLifetimeEssence())));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7§lGenerators:"));
        player.sendMessage(Message.raw("§8 • §7Generators Purchased: §e" + playerData.getGeneratorsPurchased()));
        player.sendMessage(Message.raw("§8 • §7Total Multiplier: §a" + String.format("%.2fx", playerData.calculateTotalMultiplier())));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7§lActivity:"));
        player.sendMessage(Message.raw("§8 • §7Blocks Mined: §e" + NumberFormatter.format(playerData.getBlocksMined())));
        player.sendMessage(Message.raw("§8 • §7Playtime: §b" + formatPlaytime(playerData.getPlaytimeMinutes())));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
    }
    
    private String formatPlaytime(long minutes) {
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + "d " + (hours % 24) + "h";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        } else {
            return minutes + "m";
        }
    }
}
