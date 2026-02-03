package de.ageofflair.hycremental.generators;

import lombok.Getter;
import java.math.BigDecimal;

/**
 * Enum representing all generator tiers and types
 * Tier 1-3: Basic (Early Game)
 * Tier 4-6: Advanced (Mid Game)
 * Tier 7-9: Elite (Late Game)
 * Tier 10-12: Mythic (Endgame)
 */
@Getter
public enum GeneratorType {
    
    // TIER 1-3: Basic Generators
    STONE_GENERATOR(1, "Stone Generator", new BigDecimal("1"), new BigDecimal("500"), "STONE"),
    COAL_GENERATOR(2, "Coal Generator", new BigDecimal("5"), new BigDecimal("5000"), "COAL_ORE"),
    IRON_GENERATOR(3, "Iron Generator", new BigDecimal("25"), new BigDecimal("50000"), "IRON_ORE"),
    
    // TIER 4-6: Advanced Generators (Prestige 1+ Required)
    GOLD_GENERATOR(4, "Gold Generator", new BigDecimal("100"), new BigDecimal("500000"), "GOLD_ORE"),
    DIAMOND_GENERATOR(5, "Diamond Generator", new BigDecimal("500"), new BigDecimal("5000000"), "DIAMOND_ORE"),
    EMERALD_GENERATOR(6, "Emerald Generator", new BigDecimal("2500"), new BigDecimal("50000000"), "EMERALD_ORE"),
    
    // TIER 7-9: Elite Generators (Prestige 10+ Required)
    NETHERITE_GENERATOR(7, "Netherite Generator", new BigDecimal("10000"), new BigDecimal("500000000"), "NETHERITE_BLOCK"),
    ESSENCE_CRYSTAL_GENERATOR(8, "Essence Crystal Generator", new BigDecimal("50000"), new BigDecimal("5000000000"), "AMETHYST_BLOCK"),
    QUANTUM_GENERATOR(9, "Quantum Generator", new BigDecimal("250000"), new BigDecimal("50000000000"), "END_STONE"),
    
    // TIER 10-12: Mythic Generators (Ascension Required)
    REALITY_FORGE(10, "Reality Forge", new BigDecimal("1000000"), new BigDecimal("1000000000000"), "BEACON"),
    VOID_REACTOR(11, "Void Reactor", new BigDecimal("10000000"), new BigDecimal("100000000000000"), "END_PORTAL_FRAME"),
    DIVINE_NEXUS(12, "Divine Nexus", new BigDecimal("100000000"), new BigDecimal("10000000000000000"), "DRAGON_EGG");
    
    private final int tier;
    private final String displayName;
    private final BigDecimal baseProduction; // Essence per second
    private final BigDecimal baseCost;
    private final String blockType;
    
    GeneratorType(int tier, String displayName, BigDecimal baseProduction, BigDecimal baseCost, String blockType) {
        this.tier = tier;
        this.displayName = displayName;
        this.baseProduction = baseProduction;
        this.baseCost = baseCost;
        this.blockType = blockType;
    }
    
    /**
     * Get generator type by tier level
     */
    public static GeneratorType getByTier(int tier) {
        for (GeneratorType type : values()) {
            if (type.tier == tier) {
                return type;
            }
        }
        return STONE_GENERATOR;
    }
    
    /**
     * Check if generator requires prestige
     */
    public boolean requiresPrestige() {
        return tier >= 4;
    }
    
    /**
     * Get required prestige level
     */
    public int getRequiredPrestige() {
        if (tier <= 3) return 0;
        if (tier == 4) return 1;
        if (tier == 5) return 3;
        if (tier == 6) return 5;
        if (tier == 7) return 10;
        if (tier == 8) return 20;
        if (tier == 9) return 35;
        if (tier == 10) return 50;
        if (tier == 11) return 100;
        if (tier == 12) return 200;
        return 0;
    }
    
    /**
     * Check if generator requires ascension
     */
    public boolean requiresAscension() {
        return tier >= 10;
    }
    
    /**
     * Get required ascension level
     */
    public int getRequiredAscension() {
        if (tier == 10) return 1;
        if (tier == 11) return 3;
        if (tier == 12) return 10;
        return 0;
    }
    
    /**
     * Get next tier generator
     */
    public GeneratorType getNextTier() {
        int nextTier = this.tier + 1;
        if (nextTier > 12) {
            return this;
        }
        return getByTier(nextTier);
    }
    
    /**
     * Check if this is max tier
     */
    public boolean isMaxTier() {
        return tier == 12;
    }
}