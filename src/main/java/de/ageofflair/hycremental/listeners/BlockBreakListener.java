package de.ageofflair.hycremental.listeners;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Block Break Listener - Handles block mining rewards
 * 
 * Players earn Essence from mining blocks based on:
 * - Block type (stone, coal, iron, etc.)
 * - Player multipliers (prestige, ascension, rebirth)
 * - Random chance for bonus rewards
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class BlockBreakListener {
    
    private final Hycremental plugin;
    private final Random random;
    
    public BlockBreakListener(Hycremental plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }
    
    /**
     * Handle block break event
     * Called from main plugin class via BlockBreakEvent
     * 
     * @param player The player who broke the block
     * @param blockType The type of block broken (from event.getBlock().getType())
     */
    public void onBlockBreak(Player player, String blockType) {
        UUID uuid = player.getComponent(PlayerRef.class).getUuid();
        
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        if (playerData == null) {
            plugin.getLogger().at(Level.WARNING).log("Player data not found for " + player.getName());
            return;
        }
        
        // Get block location
        // TODO: Implement with Hytale Location API
        // Location blockLocation = event.getLocation();
        // Check if in player's island boundary
        
        // Calculate base reward from block type
        BigDecimal baseReward = getBlockReward(blockType);
        
        if (baseReward.compareTo(BigDecimal.ZERO) <= 0) {
            return; // No reward for this block type
        }
        
        // Apply player multipliers
        double totalMultiplier = playerData.calculateTotalMultiplier();
        BigDecimal finalReward = baseReward.multiply(BigDecimal.valueOf(totalMultiplier));
        
        // Random bonus chance (5% for 2x)
        if (random.nextDouble() < 0.05) {
            finalReward = finalReward.multiply(BigDecimal.valueOf(2));
            player.sendMessage(Message.raw("§e§l✦ LUCKY! §7+" + NumberFormatter.format(finalReward) + " Essence §7(2x bonus!)"));
        }
        
        // Add essence
        playerData.addEssence(finalReward);
        playerData.incrementBlocksMined();
        
        // Save periodically (every 10 blocks)
        if (playerData.getBlocksMined() % 10 == 0) {
            plugin.getPlayerDataManager().savePlayerData(playerData);
        }
        
        // Show action bar with reward (configurable)
        if (plugin.getConfig().getBoolean("gameplay.show-mining-rewards", true)) {
            // TODO: Implement with Hytale Action Bar API
            // player.sendActionBar("§a+" + NumberFormatter.format(finalReward) + " Essence");
        }
        
        // Milestone rewards
        checkMiningMilestones(player, playerData);
    }
    
    /**
     * Get base reward for block type
     */
    private BigDecimal getBlockReward(String blockType) {
        // Get from config or use defaults
        switch (blockType.toLowerCase()) {
            case "stone":
            case "cobblestone":
                return BigDecimal.valueOf(1.0);
                
            case "coal_ore":
            case "deepslate_coal_ore":
                return BigDecimal.valueOf(5.0);
                
            case "iron_ore":
            case "deepslate_iron_ore":
                return BigDecimal.valueOf(10.0);
                
            case "copper_ore":
            case "deepslate_copper_ore":
                return BigDecimal.valueOf(8.0);
                
            case "gold_ore":
            case "deepslate_gold_ore":
            case "nether_gold_ore":
                return BigDecimal.valueOf(20.0);
                
            case "redstone_ore":
            case "deepslate_redstone_ore":
                return BigDecimal.valueOf(15.0);
                
            case "lapis_ore":
            case "deepslate_lapis_ore":
                return BigDecimal.valueOf(15.0);
                
            case "diamond_ore":
            case "deepslate_diamond_ore":
                return BigDecimal.valueOf(50.0);
                
            case "emerald_ore":
            case "deepslate_emerald_ore":
                return BigDecimal.valueOf(75.0);
                
            case "nether_quartz_ore":
                return BigDecimal.valueOf(12.0);
                
            case "ancient_debris":
                return BigDecimal.valueOf(100.0);
                
            default:
                return BigDecimal.ZERO;
        }
    }
    
    /**
     * Check and reward mining milestones
     */
    private void checkMiningMilestones(Player player, PlayerData playerData) {
        long blocksMined = playerData.getBlocksMined();
        
        // Check for milestone achievements
        if (blocksMined == 100) {
            rewardMilestone(player, playerData, 100, BigDecimal.valueOf(1000));
        } else if (blocksMined == 1000) {
            rewardMilestone(player, playerData, 1000, BigDecimal.valueOf(10000));
        } else if (blocksMined == 10000) {
            rewardMilestone(player, playerData, 10000, BigDecimal.valueOf(100000));
        } else if (blocksMined == 100000) {
            rewardMilestone(player, playerData, 100000, BigDecimal.valueOf(1000000));
        } else if (blocksMined == 1000000) {
            rewardMilestone(player, playerData, 1000000, BigDecimal.valueOf(10000000));
        }
    }
    
    /**
     * Reward mining milestone
     */
    private void rewardMilestone(Player player, PlayerData playerData, long milestone, BigDecimal bonus) {
        playerData.addEssence(bonus);
        
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
        player.sendMessage(Message.raw("§6§l    MINING MILESTONE!"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7You have mined §e" + NumberFormatter.formatLong(milestone) + " blocks§7!"));
        player.sendMessage(Message.raw("§7Bonus: §a+" + NumberFormatter.format(bonus) + " Essence"));
        player.sendMessage(Message.raw("§6§l─────────────────────────────"));
        player.sendMessage(Message.raw(""));
        
        // TODO: Broadcast to server with Hytale Server API
        // plugin.getServer().broadcast(Message.raw("§6" + player.getName() + " §7reached the §e" + NumberFormatter.formatLong(milestone) + " blocks §7milestone!"));
    }
}
