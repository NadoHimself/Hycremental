package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandDescriptor;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Essence Command - Manage Essence currency
 * 
 * Commands:
 * /essence - Check balance
 * /essence pay <player> <amount> - Pay another player
 * /essence top - View top richest players
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class EssenceCommand extends AbstractAsyncCommand {
    
    private final Hycremental plugin;
    
    public EssenceCommand(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public CommandDescriptor buildDescriptor() {
        return CommandDescriptor.builder()
            .name("essence")
            .description("Manage your Essence currency")
            .aliases("ess", "money", "bal", "balance")
            .build();
    }
    
    @Override
    public void run(CommandContext context) {
        if (!context.sender().isPlayer()) {
            context.sender().sendMessage(Message.raw("§cOnly players can use this command!"));
            return;
        }
        
        Player player = context.sender().asPlayer();
        UUID uuid = player.getComponent(PlayerRef.class).getUuid();
        String[] args = context.args();
        
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        if (playerData == null) {
            player.sendMessage(Message.raw("§cError loading player data!"));
            return;
        }
        
        // No arguments - show balance
        if (args.length == 0) {
            showBalance(player, playerData);
            return;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "pay":
            case "send":
                handlePay(player, playerData, args);
                break;
                
            case "top":
            case "baltop":
            case "leaderboard":
                showLeaderboard(player);
                break;
                
            case "help":
                sendHelp(player);
                break;
                
            default:
                player.sendMessage(Message.raw("§cUnknown subcommand! Use /essence help"));
        }
    }
    
    /**
     * Show player balance
     */
    private void showBalance(Player player, PlayerData playerData) {
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§6§lYour Balance"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Essence: §a" + NumberFormatter.format(playerData.getEssence())));
        player.sendMessage(Message.raw("§7Gems: §b" + NumberFormatter.formatLong(playerData.getGems())));
        player.sendMessage(Message.raw("§7Crystals: §d" + playerData.getCrystals()));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Lifetime Earned: §e" + NumberFormatter.format(playerData.getLifetimeEssence())));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
    }
    
    /**
     * Handle pay command
     */
    private void handlePay(Player player, PlayerData senderData, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Message.raw("§cUsage: /essence pay <player> <amount>"));
            return;
        }
        
        String targetName = args[1];
        String amountStr = args[2];
        
        // Parse amount
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr.replace(",", "").replace("k", "000").replace("m", "000000"));
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                player.sendMessage(Message.raw("§cAmount must be positive!"));
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(Message.raw("§cInvalid amount!"));
            return;
        }
        
        // Check if sender has enough
        if (!senderData.hasEssence(amount)) {
            player.sendMessage(Message.raw("§cInsufficient Essence! You need " + NumberFormatter.format(amount)));
            return;
        }
        
        // Get target player
        PlayerData targetData = plugin.getPlayerDataManager().getPlayerDataByName(targetName);
        if (targetData == null) {
            player.sendMessage(Message.raw("§cPlayer not found!"));
            return;
        }
        
        // Prevent self-payment
        if (targetData.getUuid().equals(senderData.getUuid())) {
            player.sendMessage(Message.raw("§cYou cannot pay yourself!"));
            return;
        }
        
        // Process transaction
        senderData.removeEssence(amount);
        targetData.addEssence(amount);
        
        // Save both
        plugin.getPlayerDataManager().savePlayerData(senderData);
        plugin.getPlayerDataManager().savePlayerData(targetData);
        
        // Messages
        player.sendMessage(Message.raw("§a§l✓ Sent! §7You paid §e" + NumberFormatter.format(amount) + " Essence §7to §e" + targetData.getUsername()));
        
        // Notify target if online
        Player targetPlayer = plugin.getServer().getPlayer(targetData.getUuid());
        if (targetPlayer != null) {
            targetPlayer.sendMessage(Message.raw("§a§l✓ Received! §e" + senderData.getUsername() + " §7paid you §e" + NumberFormatter.format(amount) + " Essence"));
        }
    }
    
    /**
     * Show top richest players
     */
    private void showLeaderboard(Player player) {
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§6§lTop Richest Players"));
        player.sendMessage(Message.raw(""));
        
        // TODO: Get from database
        // SELECT username, essence FROM players ORDER BY essence DESC LIMIT 10
        
        player.sendMessage(Message.raw("§7Leaderboard coming soon!"));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
    }
    
    /**
     * Send help message
     */
    private void sendHelp(Player player) {
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§6§lEssence Commands"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§e/essence §7- Check your balance"));
        player.sendMessage(Message.raw("§e/essence pay <player> <amount> §7- Pay a player"));
        player.sendMessage(Message.raw("§e/essence top §7- View richest players"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Aliases: §e/ess, /bal, /money"));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
    }
}
