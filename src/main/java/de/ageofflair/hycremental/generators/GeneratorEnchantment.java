package de.ageofflair.hycremental.generators;

import lombok.Getter;

/**
 * Enchantments that can be applied to generators
 * Max 3 enchantments per generator
 */
@Getter
public enum GeneratorEnchantment {
    
    EFFICIENCY("Efficiency", "Reduces mining delay by 15%", 1.15),
    FORTUNE("Fortune", "10-30% bonus Essence drops", 1.20),
    LOOTING("Looting", "Chance for rare items", 1.10),
    SPEED("Speed", "20% faster block regeneration", 1.20),
    OVERFLOW("Overflow", "5% chance for double production", 1.05),
    SYNERGY("Synergy", "+2% per nearby generator (Max 50%)", 1.02);
    
    private final String name;
    private final String description;
    private final double multiplier;
    
    GeneratorEnchantment(String name, String description, double multiplier) {
        this.name = name;
        this.description = description;
        this.multiplier = multiplier;
    }
    
    /**
     * Get enchantment cost in Gems
     */
    public int getCost() {
        switch (this) {
            case EFFICIENCY: return 100;
            case FORTUNE: return 200;
            case LOOTING: return 150;
            case SPEED: return 250;
            case OVERFLOW: return 500;
            case SYNERGY: return 300;
            default: return 100;
        }
    }
    
    /**
     * Check if enchantment is stackable
     */
    public boolean isStackable() {
        return this == SYNERGY;
    }
    
    /**
     * Get display name with color
     */
    public String getDisplayName() {
        return "§b" + name;
    }
}