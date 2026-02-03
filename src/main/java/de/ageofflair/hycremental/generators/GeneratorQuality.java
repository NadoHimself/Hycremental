package de.ageofflair.hycremental.generators;

import lombok.Getter;

/**
 * Quality tiers for generators affecting production multiplier
 * Higher quality = higher production
 */
@Getter
public enum GeneratorQuality {
    
    COMMON("§fCommon", 1.0, 0.0),
    UNCOMMON("§aUncommon", 1.5, 0.25),
    RARE("§9Rare", 2.5, 0.50),
    EPIC("§5Epic", 5.0, 1.0),
    LEGENDARY("§6Legendary", 10.0, 2.0);
    
    private final String displayName;
    private final double multiplier;
    private final double dropQualityBonus;
    
    GeneratorQuality(String displayName, double multiplier, double dropQualityBonus) {
        this.displayName = displayName;
        this.multiplier = multiplier;
        this.dropQualityBonus = dropQualityBonus;
    }
    
    /**
     * Get upgrade cost multiplier for this quality
     */
    public double getUpgradeCostMultiplier() {
        switch (this) {
            case UNCOMMON: return 2.0;
            case RARE: return 5.0;
            case EPIC: return 15.0;
            case LEGENDARY: return 50.0;
            default: return 1.0;
        }
    }
    
    /**
     * Get next quality tier
     */
    public GeneratorQuality getNextQuality() {
        switch (this) {
            case COMMON: return UNCOMMON;
            case UNCOMMON: return RARE;
            case RARE: return EPIC;
            case EPIC: return LEGENDARY;
            default: return this;
        }
    }
    
    /**
     * Check if this is max quality
     */
    public boolean isMaxQuality() {
        return this == LEGENDARY;
    }
    
    /**
     * Get rarity weight for random drops (lower = rarer)
     */
    public int getRarityWeight() {
        switch (this) {
            case COMMON: return 60;
            case UNCOMMON: return 25;
            case RARE: return 10;
            case EPIC: return 4;
            case LEGENDARY: return 1;
            default: return 60;
        }
    }
}