package de.ageofflair.hycremental.data;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

/**
 * Player Data Model - Stores all player progression data
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class PlayerData {
    
    // Player Identity
    private final UUID uuid;
    private String username;
    
    // Currency
    private BigDecimal essence;
    private long gems;
    private int crystals;
    
    // Progression
    private int prestigeLevel;
    private int ascensionLevel;
    private int rebirthCount;
    
    // Statistics
    private BigDecimal lifetimeEssence;
    private long totalBlocksMined;
    private long generatorsPurchased;
    private long prestigeCount;
    
    // Island Data
    private String islandId;
    private int islandSize;
    private int generatorSlots;
    
    // Timestamps
    private long firstJoin;
    private long lastLogin;
    private long lastSave;
    
    // Runtime Data (not saved to database)
    private transient boolean dirty = false;
    private transient Map<String, Object> cache = new HashMap<>();
    
    /**
     * Create new PlayerData for a player
     * @param uuid Player UUID
     * @param username Player username
     */
    public PlayerData(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        
        // Initialize with default values
        this.essence = BigDecimal.ZERO;
        this.gems = 0;
        this.crystals = 0;
        
        this.prestigeLevel = 0;
        this.ascensionLevel = 0;
        this.rebirthCount = 0;
        
        this.lifetimeEssence = BigDecimal.ZERO;
        this.totalBlocksMined = 0;
        this.generatorsPurchased = 0;
        this.prestigeCount = 0;
        
        this.islandId = null;
        this.islandSize = 20; // Default 20x20 chunks
        this.generatorSlots = 10; // Start with 10 slots
        
        long now = System.currentTimeMillis();
        this.firstJoin = now;
        this.lastLogin = now;
        this.lastSave = now;
    }
    
    // Currency Methods
    
    /**
     * Add essence to player
     * @param amount Amount to add
     */
    public void addEssence(BigDecimal amount) {
        this.essence = this.essence.add(amount);
        this.lifetimeEssence = this.lifetimeEssence.add(amount);
        this.dirty = true;
    }
    
    /**
     * Remove essence from player
     * @param amount Amount to remove
     * @return true if successful, false if insufficient funds
     */
    public boolean removeEssence(BigDecimal amount) {
        if (this.essence.compareTo(amount) >= 0) {
            this.essence = this.essence.subtract(amount);
            this.dirty = true;
            return true;
        }
        return false;
    }
    
    /**
     * Check if player has enough essence
     * @param amount Amount to check
     * @return true if player has enough
     */
    public boolean hasEssence(BigDecimal amount) {
        return this.essence.compareTo(amount) >= 0;
    }
    
    /**
     * Add gems to player
     * @param amount Amount to add
     */
    public void addGems(long amount) {
        this.gems += amount;
        this.dirty = true;
    }
    
    /**
     * Remove gems from player
     * @param amount Amount to remove
     * @return true if successful
     */
    public boolean removeGems(long amount) {
        if (this.gems >= amount) {
            this.gems -= amount;
            this.dirty = true;
            return true;
        }
        return false;
    }
    
    /**
     * Add crystals to player
     * @param amount Amount to add
     */
    public void addCrystals(int amount) {
        this.crystals += amount;
        this.dirty = true;
    }
    
    /**
     * Remove crystals from player
     * @param amount Amount to remove
     * @return true if successful
     */
    public boolean removeCrystals(int amount) {
        if (this.crystals >= amount) {
            this.crystals -= amount;
            this.dirty = true;
            return true;
        }
        return false;
    }
    
    // Progression Methods
    
    /**
     * Prestige the player
     */
    public void prestige() {
        this.prestigeLevel++;
        this.prestigeCount++;
        this.essence = BigDecimal.ZERO;
        this.dirty = true;
    }
    
    /**
     * Ascend the player
     */
    public void ascend() {
        this.ascensionLevel++;
        this.dirty = true;
    }
    
    /**
     * Rebirth the player
     */
    public void rebirth() {
        this.rebirthCount++;
        this.prestigeLevel = 0;
        this.ascensionLevel = 0;
        this.essence = BigDecimal.ZERO;
        this.gems = 0;
        this.dirty = true;
    }
    
    // Statistics Methods
    
    /**
     * Increment blocks mined counter
     */
    public void incrementBlocksMined() {
        this.totalBlocksMined++;
        this.dirty = true;
    }
    
    /**
     * Increment generators purchased counter
     */
    public void incrementGeneratorsPurchased() {
        this.generatorsPurchased++;
        this.dirty = true;
    }
    
    // Island Methods
    
    /**
     * Set island ID
     * @param islandId Island UUID
     */
    public void setIslandId(String islandId) {
        this.islandId = islandId;
        this.dirty = true;
    }
    
    /**
     * Upgrade island size
     * @param newSize New size in chunks
     */
    public void upgradeIslandSize(int newSize) {
        this.islandSize = newSize;
        this.dirty = true;
    }
    
    /**
     * Add generator slots
     * @param slots Number of slots to add
     */
    public void addGeneratorSlots(int slots) {
        this.generatorSlots += slots;
        this.dirty = true;
    }
    
    // Utility Methods
    
    /**
     * Update last login timestamp
     */
    public void updateLastLogin() {
        this.lastLogin = System.currentTimeMillis();
        this.dirty = true;
    }
    
    /**
     * Update last save timestamp
     */
    public void updateLastSave() {
        this.lastSave = System.currentTimeMillis();
        this.dirty = false;
    }
    
    /**
     * Calculate total multiplier from progression
     * @return Total multiplier
     */
    public double calculateTotalMultiplier() {
        double multiplier = 1.0;
        
        // Prestige multiplier (10% per prestige)
        multiplier += (prestigeLevel * 0.10);
        
        // Ascension multiplier (50% per ascension)
        multiplier += (ascensionLevel * 0.50);
        
        // Rebirth multiplier (2x per rebirth)
        multiplier *= Math.pow(2, rebirthCount);
        
        return multiplier;
    }
    
    // Getters and Setters
    
    public UUID getUuid() {
        return uuid;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
        this.dirty = true;
    }
    
    public BigDecimal getEssence() {
        return essence;
    }
    
    public void setEssence(BigDecimal essence) {
        this.essence = essence;
        this.dirty = true;
    }
    
    public long getGems() {
        return gems;
    }
    
    public void setGems(long gems) {
        this.gems = gems;
        this.dirty = true;
    }
    
    public int getCrystals() {
        return crystals;
    }
    
    public void setCrystals(int crystals) {
        this.crystals = crystals;
        this.dirty = true;
    }
    
    public int getPrestigeLevel() {
        return prestigeLevel;
    }
    
    public void setPrestigeLevel(int prestigeLevel) {
        this.prestigeLevel = prestigeLevel;
        this.dirty = true;
    }
    
    public int getAscensionLevel() {
        return ascensionLevel;
    }
    
    public void setAscensionLevel(int ascensionLevel) {
        this.ascensionLevel = ascensionLevel;
        this.dirty = true;
    }
    
    public int getRebirthCount() {
        return rebirthCount;
    }
    
    public void setRebirthCount(int rebirthCount) {
        this.rebirthCount = rebirthCount;
        this.dirty = true;
    }
    
    public BigDecimal getLifetimeEssence() {
        return lifetimeEssence;
    }
    
    public void setLifetimeEssence(BigDecimal lifetimeEssence) {
        this.lifetimeEssence = lifetimeEssence;
        this.dirty = true;
    }
    
    public long getTotalBlocksMined() {
        return totalBlocksMined;
    }
    
    public void setTotalBlocksMined(long totalBlocksMined) {
        this.totalBlocksMined = totalBlocksMined;
        this.dirty = true;
    }
    
    public long getGeneratorsPurchased() {
        return generatorsPurchased;
    }
    
    public void setGeneratorsPurchased(long generatorsPurchased) {
        this.generatorsPurchased = generatorsPurchased;
        this.dirty = true;
    }
    
    public long getPrestigeCount() {
        return prestigeCount;
    }
    
    public void setPrestigeCount(long prestigeCount) {
        this.prestigeCount = prestigeCount;
        this.dirty = true;
    }
    
    public String getIslandId() {
        return islandId;
    }
    
    public int getIslandSize() {
        return islandSize;
    }
    
    public void setIslandSize(int islandSize) {
        this.islandSize = islandSize;
        this.dirty = true;
    }
    
    public int getGeneratorSlots() {
        return generatorSlots;
    }
    
    public void setGeneratorSlots(int generatorSlots) {
        this.generatorSlots = generatorSlots;
        this.dirty = true;
    }
    
    public long getFirstJoin() {
        return firstJoin;
    }
    
    public long getLastLogin() {
        return lastLogin;
    }
    
    public long getLastSave() {
        return lastSave;
    }
    
    public boolean isDirty() {
        return dirty;
    }
    
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    public Map<String, Object> getCache() {
        return cache;
    }
    
    @Override
    public String toString() {
        return "PlayerData{" +
                "uuid=" + uuid +
                ", username='" + username + '\'' +
                ", essence=" + essence +
                ", prestigeLevel=" + prestigeLevel +
                ", ascensionLevel=" + ascensionLevel +
                ", rebirthCount=" + rebirthCount +
                '}';
    }
}
