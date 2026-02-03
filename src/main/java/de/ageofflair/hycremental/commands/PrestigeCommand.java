package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandDescriptor;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;

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
public class PrestigeCommand extends AbstractAsyncCommand {
    
    private final Hycremental plugin;
    private final PrestigeGUI prestigeGUI;
    
    // Pending confirmations (UUID -> Type)
    private final Map<UUID, String> pendingConfirmations = new HashMap<>();
    
    public PrestigeCommand(Hycremental plugin) {
        this.plugin = plugin;
        this.prestigeGUI = new PrestigeGUI(plugin);
    }
    
    @Override
    public CommandDescriptor buildDescriptor() {
        return CommandDescriptor.builder()
            .name("prestige")
            .description("Prestige to gain permanent bonuses")
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
                cancelConfirmation(player, uuid);
                break;
                
            default:
                player.sendMessage(Message.raw("§cUsage: /prestige [confirm|info|cancel]"));
        }
    }
    
    /**
     * Confirm prestige
     */
    private void confirmPrestige(Player player, PlayerData playerData) {
        // Calculate cost
        BigDecimal cost = calculatePrestigeCost(playerData.getPrestigeLevel());
        
        // Check requirements
        if (!playerData.hasEssence(cost)) {
            player.sendMessage(Message.raw("§cInsufficient Essence! Need: " + NumberFormatter.format(cost)));
            return;
        }
        
        // Perform prestige
        int oldPrestige = playerData.getPrestigeLevel();
        
        // Reset
        playerData.prestige();
        
        // Remove generators (keep Tier 1)
        plugin.getGeneratorManager().resetPlayerGenerators(playerData.getUuid(), false);
        
        // Announce
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§6§l☆ PRESTIGE! ☆"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7You have prestiged from §6" + oldPrestige + " §7to §6" + playerData.getPrestigeLevel() + "§7!"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7New Multiplier: §e" + String.format("%.2f", playerData.calculateTotalMultiplier()) + "x"));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        
        // TODO: Broadcast to server with Hytale Server API
        // plugin.getServer().broadcast(Message.raw("§6§l" + playerData.getUsername() + " §7has prestiged to §6Prestige " + playerData.getPrestigeLevel() + "§7!"));
    }
    
    /**
     * Show prestige info
     */
    private void showPrestigeInfo(Player player, PlayerData playerData) {
        BigDecimal cost = calculatePrestigeCost(playerData.getPrestigeLevel());
        
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§6§lPrestige Information"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Current Prestige: §6" + playerData.getPrestigeLevel()));
        player.sendMessage(Message.raw("§7Next Prestige: §6" + (playerData.getPrestigeLevel() + 1)));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Required Essence: §a" + NumberFormatter.format(cost)));
        player.sendMessage(Message.raw("§7Your Essence: §a" + NumberFormatter.format(playerData.getEssence())));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Reward: §e+10% Global Multiplier"));
        player.sendMessage(Message.raw("§7Current Multiplier: §e" + String.format("%.2f", playerData.calculateTotalMultiplier()) + "x"));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§eType /prestige confirm to prestige!"));
    }
    
    /**
     * Cancel pending confirmation
     */
    private void cancelConfirmation(Player player, UUID uuid) {
        if (pendingConfirmations.remove(uuid) != null) {
            player.sendMessage(Message.raw("§aConfirmation cancelled."));
        } else {
            player.sendMessage(Message.raw("§cNo pending confirmation."));
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
}

/**
 * Ascension Command
 */
class AscensionCommand extends AbstractAsyncCommand {
    
    private final Hycremental plugin;
    private final PrestigeGUI prestigeGUI;
    
    public AscensionCommand(Hycremental plugin) {
        this.plugin = plugin;
        this.prestigeGUI = new PrestigeGUI(plugin);
    }
    
    @Override
    public CommandDescriptor buildDescriptor() {
        return CommandDescriptor.builder()
            .name("ascend")
            .description("Ascend for massive permanent bonuses")
            .aliases("ascension")
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
        
        // No arguments - show info
        if (args.length == 0) {
            showAscensionInfo(player, playerData);
            return;
        }
        
        String subCommand = args[0].toLowerCase();
        
        if (subCommand.equals("confirm")) {
            confirmAscension(player, playerData);
        } else {
            player.sendMessage(Message.raw("§cUsage: /ascend [confirm]"));
        }
    }
    
    private void confirmAscension(Player player, PlayerData playerData) {
        int requiredPrestige = 50 + (playerData.getAscensionLevel() * 50);
        
        if (playerData.getPrestigeLevel() < requiredPrestige) {
            player.sendMessage(Message.raw("§cYou need Prestige " + requiredPrestige + " to ascend!"));
            return;
        }
        
        int oldAscension = playerData.getAscensionLevel();
        
        playerData.ascend();
        playerData.setPrestigeLevel(0);
        playerData.setEssence(BigDecimal.ZERO);
        plugin.getGeneratorManager().resetPlayerGenerators(playerData.getUuid(), true);
        
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§5§l★ ASCENSION! ★"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7You have ascended from §5" + oldAscension + " §7to §5" + playerData.getAscensionLevel() + "§7!"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7New Multiplier: §d" + String.format("%.2f", playerData.calculateTotalMultiplier()) + "x"));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
    }
    
    private void showAscensionInfo(Player player, PlayerData playerData) {
        int requiredPrestige = 50 + (playerData.getAscensionLevel() * 50);
        boolean canAscend = playerData.getPrestigeLevel() >= requiredPrestige;
        
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§5§lAscension Information"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Current Ascension: §5" + playerData.getAscensionLevel()));
        player.sendMessage(Message.raw("§7Required Prestige: " + (canAscend ? "§a" : "§c") + requiredPrestige));
        player.sendMessage(Message.raw("§7Your Prestige: " + (canAscend ? "§a" : "§c") + playerData.getPrestigeLevel()));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Reward: §d+50% Permanent Multiplier"));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        
        if (canAscend) {
            player.sendMessage(Message.raw("§dType /ascend confirm to ascend!"));
        }
    }
}

/**
 * Rebirth Command
 */
class RebirthCommand extends AbstractAsyncCommand {
    
    private final Hycremental plugin;
    
    public RebirthCommand(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public CommandDescriptor buildDescriptor() {
        return CommandDescriptor.builder()
            .name("rebirth")
            .description("Complete reset for game-breaking bonuses")
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
        
        if (args.length == 0) {
            showRebirthWarning(player, playerData);
            return;
        }
        
        if (args[0].equalsIgnoreCase("confirm")) {
            confirmRebirth(player, playerData);
        }
    }
    
    private void confirmRebirth(Player player, PlayerData playerData) {
        if (playerData.getAscensionLevel() < 10) {
            player.sendMessage(Message.raw("§cYou need Ascension 10 to rebirth!"));
            return;
        }
        
        int oldRebirth = playerData.getRebirthCount();
        
        playerData.rebirth();
        plugin.getGeneratorManager().resetPlayerGenerators(playerData.getUuid(), true);
        playerData.setIslandSize(20);
        playerData.setGeneratorSlots(10);
        
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§c§l✦ REBIRTH! ✦"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7You have been reborn! §c" + oldRebirth + " §7→ §c" + playerData.getRebirthCount()));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7New Multiplier: §c" + String.format("%.2f", playerData.calculateTotalMultiplier()) + "x"));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
    }
    
    private void showRebirthWarning(Player player, PlayerData playerData) {
        boolean canRebirth = playerData.getAscensionLevel() >= 10;
        
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§c§lREBIRTH WARNING"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§c§lTHIS WILL RESET EVERYTHING!"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Required Ascension: " + (canRebirth ? "§a" : "§c") + "10"));
        player.sendMessage(Message.raw("§7Your Ascension: " + (canRebirth ? "§a" : "§c") + playerData.getAscensionLevel()));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Reward: §c2x ALL Essence (Permanent)"));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        
        if (canRebirth) {
            player.sendMessage(Message.raw("§c§lType /rebirth confirm if you're ABSOLUTELY SURE!"));
        }
    }
}
