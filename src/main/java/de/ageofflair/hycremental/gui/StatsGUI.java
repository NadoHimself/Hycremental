package de.ageofflair.hycremental.gui;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Stats GUI - Display player statistics and progression
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class StatsGUI {
    
    private final Hycremental plugin;
    private static final int GUI_SIZE = 54; // 6 rows
    
    public StatsGUI(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Open stats GUI for player
     * @param player Player to open GUI for
     */
    public void openStats(Object player) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(getPlayerUUID(player));
        if (playerData == null) {
            sendMessage(player, "§cError loading player data!");
            return;
        }
        
        // Create GUI
        // TODO: GUI inventory = createInventory("§6§l" + playerData.getUsername() + "'s Stats", GUI_SIZE);
        
        // Currency Stats
        addCurrencyStats(null, 11, playerData);
        
        // Progression Stats
        addProgressionStats(null, 13, playerData);
        
        // Generator Stats
        addGeneratorStats(null, 15, playerData);
        
        // Mining Stats
        addMiningStats(null, 20, playerData);
        
        // Economy Stats
        addEconomyStats(null, 22, playerData);
        
        // Time Stats
        addTimeStats(null, 24, playerData);
        
        // Multiplier Breakdown
        addMultiplierBreakdown(null, 31, playerData);
        
        // Leaderboard Position
        addLeaderboardPosition(null, 40, playerData);
        
        // Close button
        addCloseButton(null, 49);
        
        // TODO: player.openInventory(inventory);
    }
    
    /**
     * Add currency statistics
     */
    private void addCurrencyStats(Object inventory, int slot, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Balance: §a" + NumberFormatter.format(playerData.getEssence()) + " Essence");
        lore.add("§7Gems: §b" + NumberFormatter.formatLong(playerData.getGems()));
        lore.add("§7Crystals: §d" + playerData.getCrystals());
        lore.add("");
        lore.add("§7Lifetime Essence: §e" + NumberFormatter.format(playerData.getLifetimeEssence()));
        lore.add("");
        
        // TODO: Create item (gold ingot or emerald)
        // meta.setDisplayName("§a§lCurrency");
    }
    
    /**
     * Add progression statistics
     */
    private void addProgressionStats(Object inventory, int slot, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Prestige Level: §6" + playerData.getPrestigeLevel());
        lore.add("§7Total Prestiges: §6" + playerData.getPrestigeCount());
        lore.add("");
        lore.add("§7Ascension Level: §5" + playerData.getAscensionLevel());
        lore.add("§7Rebirth Count: §c" + playerData.getRebirthCount());
        lore.add("");
        lore.add("§7Island Size: §e" + playerData.getIslandSize() + "x" + playerData.getIslandSize() + " chunks");
        lore.add("§7Generator Slots: §e" + playerData.getGeneratorSlots());
        lore.add("");
        
        // TODO: Create item (nether star)
        // meta.setDisplayName("§5§lProgression");
    }
    
    /**
     * Add generator statistics
     */
    private void addGeneratorStats(Object inventory, int slot, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        
        // Get generator stats from manager
        int activeGenerators = 0; // TODO: Get from GeneratorManager
        double totalProduction = 0; // TODO: Calculate total production
        
        lore.add("");
        lore.add("§7Active Generators: §e" + activeGenerators);
        lore.add("§7Total Production: §a" + NumberFormatter.format(totalProduction) + " Essence/s");
        lore.add("");
        lore.add("§7Generators Purchased: §e" + playerData.getGeneratorsPurchased());
        lore.add("");
        
        // Calculate estimated income
        long hourlyIncome = (long)(totalProduction * 3600);
        long dailyIncome = hourlyIncome * 24;
        
        lore.add("§7Estimated Income:");
        lore.add("§8 • §7Per Hour: §a" + NumberFormatter.formatLong(hourlyIncome));
        lore.add("§8 • §7Per Day: §a" + NumberFormatter.formatLong(dailyIncome));
        lore.add("");
        
        // TODO: Create item (hopper or dispenser)
        // meta.setDisplayName("§e§lGenerators");
    }
    
    /**
     * Add mining statistics
     */
    private void addMiningStats(Object inventory, int slot, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Total Blocks Mined: §e" + NumberFormatter.formatLong(playerData.getTotalBlocksMined()));
        lore.add("");
        
        // Calculate averages
        long timePlayed = System.currentTimeMillis() - playerData.getFirstJoin();
        long hoursPlayed = TimeUnit.MILLISECONDS.toHours(timePlayed);
        if (hoursPlayed > 0) {
            long blocksPerHour = playerData.getTotalBlocksMined() / hoursPlayed;
            lore.add("§7Blocks per Hour: §e" + NumberFormatter.formatLong(blocksPerHour));
        }
        lore.add("");
        
        // TODO: Create item (diamond pickaxe)
        // meta.setDisplayName("§b§lMining");
    }
    
    /**
     * Add economy statistics
     */
    private void addEconomyStats(Object inventory, int slot, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Total Essence Earned: §a" + NumberFormatter.format(playerData.getLifetimeEssence()));
        lore.add("");
        
        // TODO: Get transaction stats from database
        int totalTransactions = 0;
        int totalPurchases = 0;
        int totalSales = 0;
        
        lore.add("§7Marketplace:");
        lore.add("§8 • §7Transactions: §e" + totalTransactions);
        lore.add("§8 • §7Purchases: §e" + totalPurchases);
        lore.add("§8 • §7Sales: §e" + totalSales);
        lore.add("");
        
        // TODO: Create item (chest or gold block)
        // meta.setDisplayName("§6§lEconomy");
    }
    
    /**
     * Add time statistics
     */
    private void addTimeStats(Object inventory, int slot, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        
        // First join
        long firstJoin = playerData.getFirstJoin();
        String firstJoinDate = formatDate(firstJoin);
        lore.add("§7First Joined: §e" + firstJoinDate);
        
        // Last login
        long lastLogin = playerData.getLastLogin();
        String lastLoginDate = formatDate(lastLogin);
        lore.add("§7Last Login: §e" + lastLoginDate);
        
        lore.add("");
        
        // Time played
        long timePlayed = System.currentTimeMillis() - firstJoin;
        String timePlayedStr = formatDuration(timePlayed);
        lore.add("§7Total Playtime: §e" + timePlayedStr);
        lore.add("");
        
        // TODO: Create item (clock)
        // meta.setDisplayName("§7§lTime Stats");
    }
    
    /**
     * Add multiplier breakdown
     */
    private void addMultiplierBreakdown(Object inventory, int slot, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Total Multiplier: §e" + String.format("%.2f", playerData.calculateTotalMultiplier()) + "x");
        lore.add("");
        lore.add("§7Breakdown:");
        
        // Base
        lore.add("§8 • §7Base: §f1.00x");
        
        // Prestige
        double prestigeBonus = playerData.getPrestigeLevel() * 0.10;
        if (prestigeBonus > 0) {
            lore.add("§8 • §7Prestige: §6+" + String.format("%.2f", prestigeBonus) + "x");
        }
        
        // Ascension
        double ascensionBonus = playerData.getAscensionLevel() * 0.50;
        if (ascensionBonus > 0) {
            lore.add("§8 • §7Ascension: §5+" + String.format("%.2f", ascensionBonus) + "x");
        }
        
        // Rebirth
        if (playerData.getRebirthCount() > 0) {
            double rebirthMulti = Math.pow(2, playerData.getRebirthCount());
            lore.add("§8 • §7Rebirth: §c" + String.format("%.2f", rebirthMulti) + "x");
        }
        
        lore.add("");
        
        // TODO: Create item (enchanted book)
        // meta.setDisplayName("§d§lMultipliers");
    }
    
    /**
     * Add leaderboard position
     */
    private void addLeaderboardPosition(Object inventory, int slot, PlayerData playerData) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        
        // TODO: Get leaderboard positions from database
        int essenceRank = 0;
        int prestigeRank = 0;
        int generatorRank = 0;
        
        lore.add("§7Your Rankings:");
        lore.add("");
        lore.add("§8 • §7Top Essence: §e#" + essenceRank);
        lore.add("§8 • §7Top Prestige: §6#" + prestigeRank);
        lore.add("§8 • §7Top Producer: §a#" + generatorRank);
        lore.add("");
        lore.add("§e§l» Click to view leaderboards «");
        lore.add("");
        
        // TODO: Create item (podium or trophy)
        // meta.setDisplayName("§6§lLeaderboards");
    }
    
    /**
     * Add close button
     */
    private void addCloseButton(Object inventory, int slot) {
        // TODO: Create close button
    }
    
    /**
     * Handle stats GUI click
     */
    public void handleClick(Object player, int slot) {
        switch (slot) {
            case 40: // Leaderboard
                openLeaderboards(player);
                break;
            case 49: // Close
                closeGUI(player);
                break;
        }
    }
    
    /**
     * Open leaderboards GUI
     */
    private void openLeaderboards(Object player) {
        // TODO: Open leaderboards menu
        sendMessage(player, "§7Leaderboards coming soon!");
    }
    
    /**
     * Format timestamp to readable date
     */
    private String formatDate(long timestamp) {
        // TODO: Use proper date formatting
        long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - timestamp);
        if (days == 0) return "Today";
        if (days == 1) return "Yesterday";
        return days + " days ago";
    }
    
    /**
     * Format duration to readable string
     */
    private String formatDuration(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m");
        
        return sb.length() > 0 ? sb.toString().trim() : "< 1m";
    }
    
    // Utility methods
    
    private Object getPlayerUUID(Object player) {
        return null; // TODO
    }
    
    private void sendMessage(Object player, String message) {
        plugin.getLogger().info("[GUI] " + message);
    }
    
    private void closeGUI(Object player) {
        // TODO: player.closeInventory();
    }
}
