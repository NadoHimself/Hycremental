package de.ageofflair.hycremental.commands;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.core.PlayerDataManager;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.utils.NumberFormatter;
// import com.hytale.api.command.Command;
// import com.hytale.api.command.CommandExecutor;
// import com.hytale.api.command.CommandSender;
// import com.hytale.api.entity.Player;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command handler for Essence operations
 * /essence [balance|stats|pay|set|add]
 */
public class EssenceCommand { // implements CommandExecutor {
    
    private final Hycremental plugin;
    private final PlayerDataManager playerDataManager;
    
    public EssenceCommand(Hycremental plugin) {
        this.plugin = plugin;
        this.playerDataManager = plugin.getPlayerDataManager();
    }
    
    // @Override
    public boolean onCommand(Object sender, Object command, String label, String[] args) {
        /*
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            // Default: show balance
            showBalance(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "balance":
            case "bal":
                showBalance(player);
                break;
                
            case "stats":
                showStats(player);
                break;
                
            case "pay":
                if (args.length < 3) {
                    player.sendMessage("§cUsage: /essence pay <player> <amount>");
                    return true;
                }
                payPlayer(player, args[1], args[2]);
                break;
                
            case "set":
                if (!player.hasPermission("hycremental.admin")) {
                    player.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }
                if (args.length < 3) {
                    player.sendMessage("§cUsage: /essence set <player> <amount>");
                    return true;
                }
                setEssence(player, args[1], args[2]);
                break;
                
            case "add":
                if (!player.hasPermission("hycremental.admin")) {
                    player.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }
                if (args.length < 3) {
                    player.sendMessage("§cUsage: /essence add <player> <amount>");
                    return true;
                }
                addEssence(player, args[1], args[2]);
                break;
                
            default:
                sendHelp(player);
                break;
        }
        */
        return true;
    }
    
    /**
     * Show player's Essence balance
     */
    private void showBalance(Object player) { // Player player
        /*
        PlayerData data = playerDataManager.getPlayerData(player.getUniqueId());
        if (data == null) {
            player.sendMessage("§cFailed to load your data!");
            return;
        }
        
        player.sendMessage("");
        player.sendMessage("§6§l=== Your Balance ===");
        player.sendMessage("§7Essence: §e" + NumberFormatter.format(data.getCurrentEssence()));
        player.sendMessage("§7Gems: §b" + NumberFormatter.format(data.getGems()));
        player.sendMessage("§7Crystals: §d" + data.getCrystals());
        player.sendMessage("");
        */
    }
    
    /**
     * Show detailed player statistics
     */
    private void showStats(Object player) { // Player player
        /*
        PlayerData data = playerDataManager.getPlayerData(player.getUniqueId());
        if (data == null) {
            player.sendMessage("§cFailed to load your data!");
            return;
        }
        
        player.sendMessage("");
        player.sendMessage("§6§l=== Your Statistics ===");
        player.sendMessage("§7Current Essence: §e" + NumberFormatter.format(data.getCurrentEssence()));
        player.sendMessage("§7Lifetime Essence: §6" + NumberFormatter.format(data.getLifetimeEssence()));
        player.sendMessage("§7Blocks Mined: §b" + NumberFormatter.format(data.getBlocksMined()));
        player.sendMessage("§7Prestige Level: §d" + data.getPrestigeLevel());
        
        if (data.getAscensionLevel() > 0) {
            player.sendMessage("§7Ascension Level: §b" + data.getAscensionLevel());
        }
        
        if (data.getRebirthCount() > 0) {
            player.sendMessage("§7Rebirths: §c" + data.getRebirthCount());
        }
        
        player.sendMessage("§7Playtime: §e" + formatPlaytime(data.getPlaytimeMinutes()));
        player.sendMessage("");
        */
    }
    
    /**
     * Pay Essence to another player
     */
    private void payPlayer(Object sender, String targetName, String amountStr) { // Player sender
        /*
        PlayerData senderData = playerDataManager.getPlayerData(sender.getUniqueId());
        if (senderData == null) {
            sender.sendMessage("§cFailed to load your data!");
            return;
        }
        
        // Parse amount
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount!");
            return;
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            sender.sendMessage("§cAmount must be positive!");
            return;
        }
        
        // Check if sender has enough
        if (senderData.getCurrentEssence().compareTo(amount) < 0) {
            sender.sendMessage("§cYou don't have enough Essence!");
            return;
        }
        
        // Get target player
        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }
        
        PlayerData targetData = playerDataManager.getPlayerData(target.getUniqueId());
        if (targetData == null) {
            sender.sendMessage("§cFailed to load target player data!");
            return;
        }
        
        // Transfer Essence
        senderData.removeEssence(amount);
        targetData.addEssence(amount);
        
        // Messages
        sender.sendMessage("§aYou sent §e" + NumberFormatter.format(amount) + " Essence §ato " + targetName);
        target.sendMessage("§aYou received §e" + NumberFormatter.format(amount) + " Essence §afrom " + sender.getName());
        */
    }
    
    /**
     * Set player's Essence (Admin only)
     */
    private void setEssence(Object sender, String targetName, String amountStr) {
        /*
        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }
        
        PlayerData targetData = playerDataManager.getPlayerData(target.getUniqueId());
        if (targetData == null) {
            sender.sendMessage("§cFailed to load target player data!");
            return;
        }
        
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount!");
            return;
        }
        
        targetData.setCurrentEssence(amount);
        sender.sendMessage("§aSet " + targetName + "'s Essence to " + NumberFormatter.format(amount));
        target.sendMessage("§aYour Essence has been set to " + NumberFormatter.format(amount));
        */
    }
    
    /**
     * Add Essence to player (Admin only)
     */
    private void addEssence(Object sender, String targetName, String amountStr) {
        /*
        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }
        
        PlayerData targetData = playerDataManager.getPlayerData(target.getUniqueId());
        if (targetData == null) {
            sender.sendMessage("§cFailed to load target player data!");
            return;
        }
        
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount!");
            return;
        }
        
        targetData.addEssence(amount);
        sender.sendMessage("§aAdded " + NumberFormatter.format(amount) + " Essence to " + targetName);
        target.sendMessage("§aYou received " + NumberFormatter.format(amount) + " Essence");
        */
    }
    
    /**
     * Send help message
     */
    private void sendHelp(Object player) { // Player player
        /*
        player.sendMessage("");
        player.sendMessage("§6§l=== Essence Commands ===");
        player.sendMessage("§7/essence balance §8- Show your balance");
        player.sendMessage("§7/essence stats §8- Show detailed statistics");
        player.sendMessage("§7/essence pay <player> <amount> §8- Pay Essence");
        player.sendMessage("");
        */
    }
    
    /**
     * Format playtime in readable format
     */
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