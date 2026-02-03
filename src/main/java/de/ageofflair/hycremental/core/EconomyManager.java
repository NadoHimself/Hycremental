package de.ageofflair.hycremental.core;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Manages the economy (Essence, Gems, Crystals)
 * - Transactions
 * - Currency conversion
 * - Market operations
 * 
 * @author Kielian
 */
public class EconomyManager {
    
    private final Hycremental plugin;
    private final Logger logger;
    
    public EconomyManager(Hycremental plugin) {
        this.plugin = plugin;
        this.logger = plugin.getPluginLogger();
    }
    
    /**
     * Transfer essence between players
     */
    public boolean transferEssence(UUID fromUuid, UUID toUuid, BigDecimal amount) {
        PlayerData from = plugin.getPlayerDataManager().getPlayer(fromUuid);
        PlayerData to = plugin.getPlayerDataManager().getPlayer(toUuid);
        
        if (from == null || to == null) {
            return false;
        }
        
        if (!from.hasEssence(amount)) {
            return false;
        }
        
        from.removeEssence(amount);
        to.addEssence(amount);
        
        logger.info(from.getUsername() + " transferred " + amount + " Essence to " + to.getUsername());
        return true;
    }
    
    /**
     * Award essence to player
     */
    public void giveEssence(UUID uuid, BigDecimal amount) {
        PlayerData player = plugin.getPlayerDataManager().getPlayer(uuid);
        if (player != null) {
            player.addEssence(amount);
        }
    }
    
    /**
     * Award gems to player
     */
    public void giveGems(UUID uuid, long amount) {
        PlayerData player = plugin.getPlayerDataManager().getPlayer(uuid);
        if (player != null) {
            player.addGems(amount);
        }
    }
    
    /**
     * Award crystals to player
     */
    public void giveCrystals(UUID uuid, int amount) {
        PlayerData player = plugin.getPlayerDataManager().getPlayer(uuid);
        if (player != null) {
            player.addCrystals(amount);
        }
    }
    
    /**
     * Process a purchase
     */
    public boolean purchase(UUID uuid, BigDecimal essenceCost, long gemsCost, int crystalsCost) {
        PlayerData player = plugin.getPlayerDataManager().getPlayer(uuid);
        if (player == null) {
            return false;
        }
        
        // Check if player has enough currency
        if (!player.hasEssence(essenceCost) || 
            !player.hasGems(gemsCost) || 
            !player.hasCrystals(crystalsCost)) {
            return false;
        }
        
        // Deduct costs
        player.removeEssence(essenceCost);
        player.removeGems(gemsCost);
        player.removeCrystals(crystalsCost);
        
        return true;
    }
}