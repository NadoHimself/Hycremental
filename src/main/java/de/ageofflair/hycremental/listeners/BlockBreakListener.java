package de.ageofflair.hycremental.listeners;

import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.math.vector.Vector3i;
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
    
    public void onBlockBreak(BreakBlockEvent event) {
        // Get player from component holder
        if (!(event.getHolder().toEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getHolder().toEntity();
        Vector3i blockPos = event.getTargetBlock();
        
        // Get player data
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUuid());
        if (playerData == null) return;
        
        // Calculate essence gain
        BigDecimal essenceGain = calculateEssenceGain(playerData);
        
        // Add essence to player
        playerData.addEssence(essenceGain);
        playerData.incrementBlocksMined();
        
        // Send message every 10 blocks
        if (playerData.getBlocksMined() % 10 == 0) {
            player.sendMessage(Message.raw("§7+" + essenceGain.toPlainString() + " Essence §8(" + playerData.getBlocksMined() + " blocks mined)"));
        }
    }
    
    /**
     * Calculate essence gain from block break
     */
    private BigDecimal calculateEssenceGain(PlayerData playerData) {
        BigDecimal baseAmount = BigDecimal.ONE;
        
        // Apply prestige multiplier
        double prestigeMultiplier = 1.0 + (playerData.getPrestigeLevel() * 0.1);
        
        return baseAmount.multiply(BigDecimal.valueOf(prestigeMultiplier));
    }
}
