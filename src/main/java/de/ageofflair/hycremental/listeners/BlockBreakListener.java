package de.ageofflair.hycremental.listeners;

import com.hypixel.hytale.server.core.event.EventHandler;
import com.hypixel.hytale.server.core.event.block.BlockBreakEvent;
import com.hypixel.hytale.server.player.ServerPlayer;
import com.hypixel.hytale.world.block.Block;
import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;

import java.math.BigDecimal;

/**
 * Block Break Listener - Handles block mining for essence generation
 */
public class BlockBreakListener {
    
    private final Hycremental plugin;
    
    public BlockBreakListener(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ServerPlayer player = event.getPlayer();
        Block block = event.getBlock();
        
        // Get player data
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUUID());
        if (playerData == null) return;
        
        // Calculate essence gain based on block type
        BigDecimal essenceGain = calculateEssenceGain(block, playerData);
        
        // Add essence to player
        playerData.addEssence(essenceGain);
        playerData.incrementBlocksMined();
        
        // Send message every 10 blocks
        if (playerData.getBlocksMined() % 10 == 0) {
            player.sendMessage("§7+" + essenceGain.toPlainString() + " Essence §8(" + playerData.getBlocksMined() + " blocks mined)");
        }
    }
    
    /**
     * Calculate essence gain from block break
     */
    private BigDecimal calculateEssenceGain(Block block, PlayerData playerData) {
        BigDecimal baseAmount = BigDecimal.ONE;
        
        // Apply prestige multiplier
        double prestigeMultiplier = 1.0 + (playerData.getPrestigeLevel() * 0.1);
        
        return baseAmount.multiply(BigDecimal.valueOf(prestigeMultiplier));
    }
}
