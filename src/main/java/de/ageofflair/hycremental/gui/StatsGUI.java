package de.ageofflair.hycremental.gui;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.generators.Generator;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.util.List;
import java.util.UUID;

/**
 * Stats GUI - Player Statistics Display
 * 
 * Uses Hytale's PageManager system for custom UI
 * 
 * TODO: Create UI file at Common/UI/Custom/stats_gui.ui
 * - Tabbed interface (Overview, Generators, Economy, Progression)
 * - Charts/graphs for progress visualization
 * - Leaderboard comparisons
 * - Achievement display
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class StatsGUI {
    
    private final Hycremental plugin;
    
    private static final String STATS_PAGE_ID = "hycremental:stats";
    
    public StatsGUI(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Open stats GUI for player
     */
    public void openStatsGUI(Player player) {
        UUID uuid = player.getComponent(PlayerRef.class).getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            player.sendMessage(Message.raw("§cError loading player data!"));
            return;
        }
        
        // TODO: Implement with Hytale PageManager API
        // 
        // Open custom stats page with tabs:
        // Map<String, Object> data = new HashMap<>();
        // 
        // Overview Tab:
        // data.put("essence", playerData.getEssence().toPlainString());
        // data.put("gems", playerData.getGems());
        // data.put("crystals", playerData.getCrystals());
        // data.put("prestige", playerData.getPrestigeLevel());
        // data.put("ascension", playerData.getAscensionLevel());
        // data.put("rebirth", playerData.getRebirthCount());
        // 
        // Generators Tab:
        // data.put("generator_count", plugin.getGeneratorManager().getPlayerGeneratorCount(uuid));
        // data.put("total_production", calculateTotalProduction(playerData));
        // data.put("generators_purchased", playerData.getGeneratorsPurchased());
        // 
        // Economy Tab:
        // data.put("lifetime_essence", playerData.getLifetimeEssence().toPlainString());
        // data.put("rank", plugin.getEconomyManager().getPlayerRank(uuid));
        // 
        // Progression Tab:
        // data.put("blocks_mined", playerData.getBlocksMined());
        // data.put("play_time", calculatePlayTime(playerData));
        // data.put("multiplier", playerData.calculateTotalMultiplier());
        // 
        // player.getPageManager().openCustomPage(PageId.of(STATS_PAGE_ID), data);
        
        // Temporary fallback: Send text-based stats
        sendTextBasedStats(player, playerData);
    }
    
    /**
     * Calculate total production from all generators
     */
    private double calculateTotalProduction(PlayerData playerData) {
        List<Generator> generators = plugin.getGeneratorManager().getPlayerGenerators(playerData.getUuid());
        double total = 0;
        
        for (Generator gen : generators) {
            total += gen.calculateProduction();
        }
        
        return total * playerData.calculateTotalMultiplier();
    }
    
    /**
     * Calculate play time
     */
    private String calculatePlayTime(PlayerData playerData) {
        long playTimeMs = System.currentTimeMillis() - playerData.getFirstJoin();
        long days = playTimeMs / 86400000;
        long hours = (playTimeMs % 86400000) / 3600000;
        long minutes = (playTimeMs % 3600000) / 60000;
        
        return days + "d " + hours + "h " + minutes + "m";
    }
    
    /**
     * Temporary text-based stats display
     */
    private void sendTextBasedStats(Player player, PlayerData playerData) {
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
        player.sendMessage(Message.raw("§6§l    Your Statistics"));
        player.sendMessage(Message.raw(""));
        
        // Economy Stats
        player.sendMessage(Message.raw("§e§l» Economy:"));
        player.sendMessage(Message.raw("§8 • §7Essence: §a" + NumberFormatter.format(playerData.getEssence())));
        player.sendMessage(Message.raw("§8 • §7Gems: §b" + NumberFormatter.formatLong(playerData.getGems())));
        player.sendMessage(Message.raw("§8 • §7Crystals: §d" + playerData.getCrystals()));
        player.sendMessage(Message.raw("§8 • §7Lifetime Earned: §e" + NumberFormatter.format(playerData.getLifetimeEssence())));
        player.sendMessage(Message.raw("§8 • §7Leaderboard Rank: §6#" + plugin.getEconomyManager().getPlayerRank(playerData.getUuid())));
        player.sendMessage(Message.raw(""));
        
        // Progression Stats
        player.sendMessage(Message.raw("§e§l» Progression:"));
        player.sendMessage(Message.raw("§8 • §7Prestige: §6" + playerData.getPrestigeLevel()));
        player.sendMessage(Message.raw("§8 • §7Ascension: §5" + playerData.getAscensionLevel()));
        player.sendMessage(Message.raw("§8 • §7Rebirth: §c" + playerData.getRebirthCount()));
        player.sendMessage(Message.raw("§8 • §7Total Multiplier: §e" + String.format("%.2fx", playerData.calculateTotalMultiplier())));
        player.sendMessage(Message.raw(""));
        
        // Generator Stats
        int generatorCount = plugin.getGeneratorManager().getPlayerGeneratorCount(playerData.getUuid());
        double totalProduction = calculateTotalProduction(playerData);
        
        player.sendMessage(Message.raw("§e§l» Generators:"));
        player.sendMessage(Message.raw("§8 • §7Active: §e" + generatorCount + "§7/§e" + playerData.getGeneratorSlots()));
        player.sendMessage(Message.raw("§8 • §7Total Production: §a" + NumberFormatter.format(totalProduction) + " Essence/s"));
        player.sendMessage(Message.raw("§8 • §7Lifetime Purchased: §e" + playerData.getGeneratorsPurchased()));
        player.sendMessage(Message.raw(""));
        
        // Estimated Income
        long hourlyIncome = (long)(totalProduction * 3600);
        long dailyIncome = hourlyIncome * 24;
        
        player.sendMessage(Message.raw("§e§l» Estimated Income:"));
        player.sendMessage(Message.raw("§8 • §7Per Minute: §a" + NumberFormatter.formatLong((long)(totalProduction * 60))));
        player.sendMessage(Message.raw("§8 • §7Per Hour: §a" + NumberFormatter.formatLong(hourlyIncome)));
        player.sendMessage(Message.raw("§8 • §7Per Day: §a" + NumberFormatter.formatLong(dailyIncome)));
        player.sendMessage(Message.raw(""));
        
        // Activity Stats
        player.sendMessage(Message.raw("§e§l» Activity:"));
        player.sendMessage(Message.raw("§8 • §7Blocks Mined: §e" + NumberFormatter.formatLong(playerData.getBlocksMined())));
        player.sendMessage(Message.raw("§8 • §7Play Time: §e" + calculatePlayTime(playerData)));
        player.sendMessage(Message.raw("§8 • §7Island Size: §e" + playerData.getIslandSize() + "x" + playerData.getIslandSize()));
        player.sendMessage(Message.raw(""));
        
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
        player.sendMessage(Message.raw(""));
    }
}
