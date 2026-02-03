package de.ageofflair.hycremental.generators;

import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a single Generator instance placed on an island
 * Generators produce Essence over time and can be upgraded
 */
@Data
public class Generator {
    
    // Unique Identifier
    private final UUID generatorId;
    private final UUID ownerId;
    private final UUID islandId;
    
    // Generator Properties
    private GeneratorType type;
    private GeneratorQuality quality;
    private int level;
    private int tier;
    
    // Location
    private int x;
    private int y;
    private int z;
    private String worldName;
    
    // Production
    private BigDecimal baseProduction;
    private long lastTickTime;
    private long createdAt;
    
    // Upgrades
    private List<GeneratorEnchantment> enchantments;
    private boolean autoCollectEnabled;
    
    // Statistics
    private BigDecimal totalProduced;
    private long totalTicks;
    
    public Generator(UUID ownerId, UUID islandId, GeneratorType type, int x, int y, int z, String worldName) {
        this.generatorId = UUID.randomUUID();
        this.ownerId = ownerId;
        this.islandId = islandId;
        this.type = type;
        this.quality = GeneratorQuality.COMMON;
        this.level = 1;
        this.tier = type.getTier();
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
        this.baseProduction = type.getBaseProduction();
        this.lastTickTime = System.currentTimeMillis();
        this.createdAt = System.currentTimeMillis();
        this.enchantments = new ArrayList<>();
        this.autoCollectEnabled = false;
        this.totalProduced = BigDecimal.ZERO;
        this.totalTicks = 0;
    }
    
    /**
     * Calculate current production rate with all multipliers applied
     */
    public BigDecimal calculateProduction() {
        BigDecimal production = baseProduction;
        
        // Level Multiplier (5% per level)
        double levelMultiplier = 1.0 + (level * 0.05);
        production = production.multiply(BigDecimal.valueOf(levelMultiplier));
        
        // Quality Multiplier
        production = production.multiply(BigDecimal.valueOf(quality.getMultiplier()));
        
        // Enchantment Multipliers
        for (GeneratorEnchantment enchant : enchantments) {
            production = production.multiply(BigDecimal.valueOf(enchant.getMultiplier()));
        }
        
        return production;
    }
    
    /**
     * Calculate upgrade cost for next level
     */
    public BigDecimal calculateUpgradeCost() {
        // Formula: BaseCost * (1.15 ^ level)
        BigDecimal baseCost = type.getBaseCost();
        return baseCost.multiply(BigDecimal.valueOf(Math.pow(1.15, level)));
    }
    
    /**
     * Upgrade generator to next level
     */
    public void upgrade() {
        if (level < 100) {
            level++;
        }
    }
    
    /**
     * Add enchantment to generator
     */
    public boolean addEnchantment(GeneratorEnchantment enchantment) {
        if (enchantments.size() < 3 && !hasEnchantment(enchantment)) {
            enchantments.add(enchantment);
            return true;
        }
        return false;
    }
    
    /**
     * Check if generator has specific enchantment
     */
    public boolean hasEnchantment(GeneratorEnchantment enchantment) {
        return enchantments.contains(enchantment);
    }
    
    /**
     * Produce essence based on time elapsed
     */
    public BigDecimal produce() {
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - lastTickTime) / 1000;
        
        if (elapsedSeconds <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal production = calculateProduction();
        BigDecimal produced = production.multiply(BigDecimal.valueOf(elapsedSeconds));
        
        totalProduced = totalProduced.add(produced);
        totalTicks++;
        lastTickTime = currentTime;
        
        return produced;
    }
    
    /**
     * Get age of generator in hours
     */
    public long getAgeInHours() {
        return (System.currentTimeMillis() - createdAt) / (1000 * 60 * 60);
    }
    
    /**
     * Get location as formatted string
     */
    public String getLocationString() {
        return String.format("%s (%d, %d, %d)", worldName, x, y, z);
    }
}