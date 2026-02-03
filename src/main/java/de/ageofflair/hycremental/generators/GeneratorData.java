package de.ageofflair.hycremental.generators;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

/**
 * Data Transfer Object for Generator information
 * Used for passing generator data to GUI and other components
 */
@Data
@AllArgsConstructor
public class GeneratorData {
    
    private final Generator generator;
    
    /**
     * Get current production rate per second
     */
    public BigDecimal getCurrentProduction() {
        return generator.calculateProduction();
    }
    
    /**
     * Get cost to upgrade to next level
     */
    public BigDecimal getUpgradeCost() {
        return generator.calculateUpgradeCost();
    }
    
    /**
     * Get current upgrade level
     */
    public int getUpgradeLevel() {
        return generator.getLevel();
    }
    
    /**
     * Get generator type
     */
    public GeneratorType getType() {
        return generator.getType();
    }
    
    /**
     * Get generator quality
     */
    public GeneratorQuality getQuality() {
        return generator.getQuality();
    }
    
    /**
     * Create GeneratorData from Generator instance
     */
    public static GeneratorData from(Generator generator) {
        return new GeneratorData(generator);
    }
}
