package de.ageofflair.hycremental.data;

import de.ageofflair.hycremental.generators.Generator;
import lombok.Data;
import java.util.*;

/**
 * Represents an island owned by a player or team
 * Islands contain generators and have upgradeable properties
 */
@Data
public class IslandData {
    
    // Identification
    private final UUID islandId;
    private final UUID ownerId;
    private String islandName;
    
    // Island Properties
    private int sizeX;
    private int sizeZ;
    private int maxGenerators;
    private int maxMembers;
    
    // Location
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private String worldName;
    
    // Generators
    private Map<UUID, Generator> generators;
    private int generatorCount;
    
    // Members & Permissions
    private Set<UUID> members;
    private Set<UUID> admins;
    private Map<UUID, IslandPermission> permissions;
    
    // Upgrades
    private int sizeUpgradeLevel;
    private int generatorSlotUpgradeLevel;
    private int memberSlotUpgradeLevel;
    private int hopperSpeedLevel;
    private boolean chunkLoaderEnabled;
    private boolean teleportPadsUnlocked;
    
    // Statistics
    private long createdAt;
    private long lastActivity;
    private boolean locked;
    
    public IslandData(UUID ownerId, String worldName, int spawnX, int spawnY, int spawnZ) {
        this.islandId = UUID.randomUUID();
        this.ownerId = ownerId;
        this.islandName = "Island";
        this.worldName = worldName;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        
        // Default starter island properties
        this.sizeX = 20;
        this.sizeZ = 20;
        this.maxGenerators = 10;
        this.maxMembers = 1;
        
        // Initialize collections
        this.generators = new HashMap<>();
        this.members = new HashSet<>();
        this.admins = new HashSet<>();
        this.permissions = new HashMap<>();
        
        // Add owner
        this.members.add(ownerId);
        this.admins.add(ownerId);
        
        // Default upgrade levels
        this.sizeUpgradeLevel = 0;
        this.generatorSlotUpgradeLevel = 0;
        this.memberSlotUpgradeLevel = 0;
        this.hopperSpeedLevel = 0;
        this.chunkLoaderEnabled = false;
        this.teleportPadsUnlocked = false;
        
        // Statistics
        this.generatorCount = 0;
        this.createdAt = System.currentTimeMillis();
        this.lastActivity = System.currentTimeMillis();
        this.locked = false;
    }
    
    /**
     * Add generator to island
     */
    public boolean addGenerator(Generator generator) {
        if (generatorCount >= maxGenerators) {
            return false;
        }
        
        generators.put(generator.getGeneratorId(), generator);
        generatorCount++;
        updateActivity();
        return true;
    }
    
    /**
     * Remove generator from island
     */
    public boolean removeGenerator(UUID generatorId) {
        if (generators.remove(generatorId) != null) {
            generatorCount--;
            updateActivity();
            return true;
        }
        return false;
    }
    
    /**
     * Add member to island
     */
    public boolean addMember(UUID playerId) {
        if (members.size() >= maxMembers) {
            return false;
        }
        
        members.add(playerId);
        permissions.put(playerId, IslandPermission.MEMBER);
        updateActivity();
        return true;
    }
    
    /**
     * Remove member from island
     */
    public boolean removeMember(UUID playerId) {
        if (playerId.equals(ownerId)) {
            return false; // Cannot remove owner
        }
        
        members.remove(playerId);
        admins.remove(playerId);
        permissions.remove(playerId);
        updateActivity();
        return true;
    }
    
    /**
     * Check if player is member
     */
    public boolean isMember(UUID playerId) {
        return members.contains(playerId);
    }
    
    /**
     * Check if player is admin
     */
    public boolean isAdmin(UUID playerId) {
        return admins.contains(playerId) || playerId.equals(ownerId);
    }
    
    /**
     * Check if player is owner
     */
    public boolean isOwner(UUID playerId) {
        return playerId.equals(ownerId);
    }
    
    /**
     * Upgrade island size
     */
    public void upgradeSizeLevel() {
        sizeUpgradeLevel++;
        switch (sizeUpgradeLevel) {
            case 1: sizeX = 30; sizeZ = 30; break;
            case 2: sizeX = 50; sizeZ = 50; break;
            case 3: sizeX = 100; sizeZ = 100; break;
        }
        updateActivity();
    }
    
    /**
     * Upgrade generator slots
     */
    public void upgradeGeneratorSlots() {
        generatorSlotUpgradeLevel++;
        maxGenerators += 50; // +50 slots per upgrade
        updateActivity();
    }
    
    /**
     * Upgrade member slots
     */
    public void upgradeMemberSlots() {
        if (maxMembers < 5) {
            memberSlotUpgradeLevel++;
            maxMembers++;
            updateActivity();
        }
    }
    
    /**
     * Check if island has space for generator
     */
    public boolean hasGeneratorSpace() {
        return generatorCount < maxGenerators;
    }
    
    /**
     * Get available generator slots
     */
    public int getAvailableSlots() {
        return maxGenerators - generatorCount;
    }
    
    /**
     * Update last activity timestamp
     */
    public void updateActivity() {
        this.lastActivity = System.currentTimeMillis();
    }
    
    /**
     * Get island age in days
     */
    public long getAgeInDays() {
        return (System.currentTimeMillis() - createdAt) / (1000 * 60 * 60 * 24);
    }
    
    /**
     * Calculate total island value (all generator costs)
     */
    public java.math.BigDecimal calculateTotalValue() {
        return generators.values().stream()
            .map(gen -> gen.getType().getBaseCost())
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
    
    public enum IslandPermission {
        OWNER,
        ADMIN,
        MEMBER,
        VISITOR
    }
}