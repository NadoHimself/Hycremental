package de.ageofflair.hycremental.data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents all data for a player in Hycremental
 * 
 * @author Kielian
 */
public class PlayerData {
    
    // Identity
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
    private BigDecimal totalEssenceEarned;
    private long blocksMined;
    private long generatorsOwned;
    
    // Island Reference
    private UUID islandId;
    
    // Ascension Perks (PerkType -> Level)
    private Map<String, Integer> ascensionPerks;
    
    // Timestamps
    private long firstJoin;
    private long lastLogin;
    private long lastSave;
    
    // Runtime flags
    private boolean dirty; // Has unsaved changes
    
    /**
     * Create new PlayerData for a player
     */
    public PlayerData(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        this.essence = new BigDecimal("1000.00"); // Starting essence
        this.gems = 0;
        this.crystals = 0;
        this.prestigeLevel = 0;
        this.ascensionLevel = 0;
        this.rebirthCount = 0;
        this.totalEssenceEarned = BigDecimal.ZERO;
        this.blocksMined = 0;
        this.generatorsOwned = 0;
        this.ascensionPerks = new HashMap<>();
        this.firstJoin = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
        this.lastSave = System.currentTimeMillis();
        this.dirty = true;
    }
    
    // Essence Methods
    
    public void addEssence(BigDecimal amount) {
        this.essence = this.essence.add(amount);
        this.totalEssenceEarned = this.totalEssenceEarned.add(amount);
        this.dirty = true;
    }
    
    public boolean removeEssence(BigDecimal amount) {
        if (this.essence.compareTo(amount) >= 0) {
            this.essence = this.essence.subtract(amount);
            this.dirty = true;
            return true;
        }
        return false;
    }
    
    public boolean hasEssence(BigDecimal amount) {
        return this.essence.compareTo(amount) >= 0;
    }
    
    public void setEssence(BigDecimal essence) {
        this.essence = essence;
        this.dirty = true;
    }
    
    // Gems Methods
    
    public void addGems(long amount) {
        this.gems += amount;
        this.dirty = true;
    }
    
    public boolean removeGems(long amount) {
        if (this.gems >= amount) {
            this.gems -= amount;
            this.dirty = true;
            return true;
        }
        return false;
    }
    
    public boolean hasGems(long amount) {
        return this.gems >= amount;
    }
    
    // Crystals Methods
    
    public void addCrystals(int amount) {
        this.crystals += amount;
        this.dirty = true;
    }
    
    public boolean removeCrystals(int amount) {
        if (this.crystals >= amount) {
            this.crystals -= amount;
            this.dirty = true;
            return true;
        }
        return false;
    }
    
    public boolean hasCrystals(int amount) {
        return this.crystals >= amount;
    }
    
    // Progression Methods
    
    public void incrementPrestige() {
        this.prestigeLevel++;
        this.dirty = true;
    }
    
    public void incrementAscension() {
        this.ascensionLevel++;
        this.dirty = true;
    }
    
    public void incrementRebirth() {
        this.rebirthCount++;
        this.dirty = true;
    }
    
    // Ascension Perks
    
    public int getAscensionPerkLevel(String perkType) {
        return ascensionPerks.getOrDefault(perkType, 0);
    }
    
    public void setAscensionPerkLevel(String perkType, int level) {
        ascensionPerks.put(perkType, level);
        this.dirty = true;
    }
    
    public void incrementAscensionPerk(String perkType) {
        int currentLevel = getAscensionPerkLevel(perkType);
        setAscensionPerkLevel(perkType, currentLevel + 1);
    }
    
    // Statistics
    
    public void incrementBlocksMined() {
        this.blocksMined++;
        this.dirty = true;
    }
    
    public void updateLastLogin() {
        this.lastLogin = System.currentTimeMillis();
        this.dirty = true;
    }
    
    public void updateLastSave() {
        this.lastSave = System.currentTimeMillis();
        this.dirty = false; // Clear dirty flag after save
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
    
    public long getGems() {
        return gems;
    }
    
    public int getCrystals() {
        return crystals;
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
    
    public BigDecimal getTotalEssenceEarned() {
        return totalEssenceEarned;
    }
    
    public long getBlocksMined() {
        return blocksMined;
    }
    
    public long getGeneratorsOwned() {
        return generatorsOwned;
    }
    
    public void setGeneratorsOwned(long count) {
        this.generatorsOwned = count;
        this.dirty = true;
    }
    
    public UUID getIslandId() {
        return islandId;
    }
    
    public void setIslandId(UUID islandId) {
        this.islandId = islandId;
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
    
    @Override
    public String toString() {
        return "PlayerData{" +
                "uuid=" + uuid +
                ", username='" + username + '\'' +
                ", essence=" + essence +
                ", prestige=" + prestigeLevel +
                ", ascension=" + ascensionLevel +
                '}';
    }
}