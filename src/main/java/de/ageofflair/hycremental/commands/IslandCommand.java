package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandDescriptor;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.island.IslandData;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Island Command - Manage player islands
 * 
 * Commands:
 * /island - Teleport to your island
 * /island info - View island information
 * /island expand - Expand island size
 * /island slots - Upgrade generator slots
 * /island visit <player> - Visit another player's island
 * /island home - Return to your island
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class IslandCommand extends AbstractAsyncCommand {
    
    private final Hycremental plugin;
    
    public IslandCommand(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public CommandDescriptor buildDescriptor() {
        return CommandDescriptor.builder()
            .name("island")
            .description("Manage your island")
            .aliases("is", "isle")
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
        
        // No arguments - teleport home
        if (args.length == 0) {
            teleportToIsland(player, playerData);
            return;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "info":
            case "stats":
                showIslandInfo(player, playerData);
                break;
                
            case "expand":
            case "upgrade":
                expandIsland(player, playerData);
                break;
                
            case "slots":
                upgradeSlots(player, playerData);
                break;
                
            case "visit":
            case "warp":
                visitIsland(player, args);
                break;
                
            case "home":
            case "tp":
                teleportToIsland(player, playerData);
                break;
                
            case "help":
                sendHelp(player);
                break;
                
            default:
                player.sendMessage(Message.raw("§cUnknown subcommand! Use /island help"));
        }
    }
    
    /**
     * Teleport player to their island
     */
    private void teleportToIsland(Player player, PlayerData playerData) {
        IslandData island = plugin.getIslandManager().getIsland(playerData.getUuid());
        
        if (island == null) {
            player.sendMessage(Message.raw("§cYou don't have an island yet!"));
            return;
        }
        
        // TODO: Implement with Hytale Location/Teleport API
        // player.teleport(island.getSpawnLocation());
        
        player.sendMessage(Message.raw("§a§l✓ Teleported! §7Welcome to your island!"));
    }
    
    /**
     * Show island information
     */
    private void showIslandInfo(Player player, PlayerData playerData) {
        IslandData island = plugin.getIslandManager().getIsland(playerData.getUuid());
        
        if (island == null) {
            player.sendMessage(Message.raw("§cYou don't have an island yet!"));
            return;
        }
        
        int activeGenerators = plugin.getGeneratorManager().getPlayerGeneratorCount(playerData.getUuid());
        
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§6§lYour Island"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Size: §e" + playerData.getIslandSize() + "x" + playerData.getIslandSize() + " chunks"));
        player.sendMessage(Message.raw("§7Generator Slots: §e" + activeGenerators + "§7/§e" + playerData.getGeneratorSlots()));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Upgrade Costs:"));
        player.sendMessage(Message.raw("§8 • §7Expand (+2 chunks): §a" + NumberFormatter.format(calculateExpandCost(playerData.getIslandSize()))));
        player.sendMessage(Message.raw("§8 • §7+5 Slots: §a" + NumberFormatter.format(calculateSlotCost(playerData.getGeneratorSlots()))));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
    }
    
    /**
     * Expand island size
     */
    private void expandIsland(Player player, PlayerData playerData) {
        int currentSize = playerData.getIslandSize();
        
        if (currentSize >= 100) {
            player.sendMessage(Message.raw("§cYour island is already at maximum size!"));
            return;
        }
        
        BigDecimal cost = calculateExpandCost(currentSize);
        
        if (!playerData.hasEssence(cost)) {
            player.sendMessage(Message.raw("§cInsufficient Essence! Need: " + NumberFormatter.format(cost)));
            return;
        }
        
        // Process upgrade
        playerData.removeEssence(cost);
        playerData.setIslandSize(currentSize + 2);
        
        player.sendMessage(Message.raw("§a§l✓ Upgraded! §7Island size is now §e" + playerData.getIslandSize() + "x" + playerData.getIslandSize()));
        player.sendMessage(Message.raw("§7Cost: §c-" + NumberFormatter.format(cost) + " Essence"));
    }
    
    /**
     * Upgrade generator slots
     */
    private void upgradeSlots(Player player, PlayerData playerData) {
        int currentSlots = playerData.getGeneratorSlots();
        
        if (currentSlots >= 500) {
            player.sendMessage(Message.raw("§cYou have maximum generator slots!"));
            return;
        }
        
        BigDecimal cost = calculateSlotCost(currentSlots);
        
        if (!playerData.hasEssence(cost)) {
            player.sendMessage(Message.raw("§cInsufficient Essence! Need: " + NumberFormatter.format(cost)));
            return;
        }
        
        // Process upgrade
        playerData.removeEssence(cost);
        playerData.setGeneratorSlots(currentSlots + 5);
        
        player.sendMessage(Message.raw("§a§l✓ Upgraded! §7You now have §e" + playerData.getGeneratorSlots() + " generator slots"));
        player.sendMessage(Message.raw("§7Cost: §c-" + NumberFormatter.format(cost) + " Essence"));
    }
    
    /**
     * Visit another player's island
     */
    private void visitIsland(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Message.raw("§cUsage: /island visit <player>"));
            return;
        }
        
        String targetName = args[1];
        PlayerData targetData = plugin.getPlayerDataManager().getPlayerDataByName(targetName);
        
        if (targetData == null) {
            player.sendMessage(Message.raw("§cPlayer not found!"));
            return;
        }
        
        IslandData island = plugin.getIslandManager().getIsland(targetData.getUuid());
        if (island == null) {
            player.sendMessage(Message.raw("§cThat player doesn't have an island!"));
            return;
        }
        
        // TODO: Implement with Hytale Location/Teleport API
        // player.teleport(island.getVisitorSpawnLocation());
        
        player.sendMessage(Message.raw("§a§l✓ Visiting! §7Welcome to §e" + targetData.getUsername() + "'s §7island!"));
    }
    
    /**
     * Send help message
     */
    private void sendHelp(Player player) {
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§6§lIsland Commands"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§e/island §7- Teleport to your island"));
        player.sendMessage(Message.raw("§e/island info §7- View island information"));
        player.sendMessage(Message.raw("§e/island expand §7- Expand island size"));
        player.sendMessage(Message.raw("§e/island slots §7- Upgrade generator slots"));
        player.sendMessage(Message.raw("§e/island visit <player> §7- Visit an island"));
        player.sendMessage(Message.raw("§e/island home §7- Return to your island"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Aliases: §e/is, /isle"));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
    }
    
    /**
     * Calculate island expand cost
     */
    private BigDecimal calculateExpandCost(int currentSize) {
        // Formula: 10000 * (1.2 ^ (size/2))
        double baseCost = 10000;
        double multiplier = Math.pow(1.2, currentSize / 2.0);
        return BigDecimal.valueOf(baseCost * multiplier);
    }
    
    /**
     * Calculate slot upgrade cost
     */
    private BigDecimal calculateSlotCost(int currentSlots) {
        // Formula: 5000 * (1.15 ^ (slots/5))
        double baseCost = 5000;
        double multiplier = Math.pow(1.15, currentSlots / 5.0);
        return BigDecimal.valueOf(baseCost * multiplier);
    }
}
