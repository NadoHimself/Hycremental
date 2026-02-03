package de.ageofflair.hycremental.core;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Manages the prestige system
 * - Prestige requirements
 * - Reset logic
 * - Rewards calculation
 * 
 * @author Kielian
 */
public class PrestigeManager {
    
    private final Hycremental plugin;
    private final Logger logger;
    
    // Config values (TODO: Load from config)
    private final BigDecimal BASE_COST = new BigDecimal("1000000"); // 1M
    private final double COST_MULTIPLIER = 1.5;
    
    public PrestigeManager(Hycremental plugin) {
        this.plugin = plugin;
        this.logger = plugin.getPluginLogger();
    }
    
    /**
     * Calculate the cost for next prestige
     */
    public BigDecimal calculatePrestigeCost(int currentPrestige) {
        // Formula: BASE_COST * (MULTIPLIER ^ prestige)
        double multiplier = Math.pow(COST_MULTIPLIER, currentPrestige);
        return BASE_COST.multiply(BigDecimal.valueOf(multiplier));
    }
    
    /**
     * Check if player can prestige
     */
    public boolean canPrestige(UUID uuid) {
        PlayerData player = plugin.getPlayerDataManager().getPlayer(uuid);
        if (player == null) {
            return false;
        }
        
        BigDecimal cost = calculatePrestigeCost(player.getPrestigeLevel());
        return player.hasEssence(cost);
    }
    
    /**
     * Perform prestige for a player
     */
    public boolean prestige(UUID uuid) {
        PlayerData player = plugin.getPlayerDataManager().getPlayer(uuid);
        if (player == null || !canPrestige(uuid)) {
            return false;
        }
        
        logger.info(player.getUsername() + " is prestiging from level " + 
                    player.getPrestigeLevel() + " to " + (player.getPrestigeLevel() + 1));
        
        // Reset essence
        player.setEssence(new BigDecimal("1000")); // Back to starting amount
        
        // TODO: Reset generators
        // TODO: Reset island upgrades
        
        // Increment prestige
        player.incrementPrestige();
        
        // TODO: Apply prestige rewards
        // - Multiplier bonus
        // - Unlock new generators
        
        logger.info(player.getUsername() + " prestiged successfully!");
        return true;
    }
    
    /**
     * Calculate global multiplier from prestige
     */
    public double calculatePrestigeMultiplier(int prestigeLevel) {
        // Example: Each prestige gives +10% bonus
        // Prestige 1 = 1.1x, Prestige 10 = 2.0x, etc.
        return 1.0 + (prestigeLevel * 0.1);
    }
    
    /**
     * Get prestige information for display
     */
    public String getPrestigeInfo(UUID uuid) {
        PlayerData player = plugin.getPlayerDataManager().getPlayer(uuid);
        if (player == null) {
            return "Player not found";
        }
        
        int currentPrestige = player.getPrestigeLevel();
        BigDecimal nextCost = calculatePrestigeCost(currentPrestige);
        double multiplier = calculatePrestigeMultiplier(currentPrestige + 1);
        
        return String.format(
            "Current Prestige: %d\nNext Cost: %s Essence\nNext Multiplier: %.1fx",
            currentPrestige,
            plugin.getNumberFormatter().format(nextCost),
            multiplier
        );
    }
}