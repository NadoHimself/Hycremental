package de.ageofflair.hycremental.island;

import java.util.UUID;

/**
 * Island Data - Represents a player's island
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class IslandData {
    
    private final UUID ownerUUID;
    private int centerX;
    private int centerZ;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private int size;
    private long createdAt;
    
    public IslandData(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
        this.size = 20; // Default size
        this.createdAt = System.currentTimeMillis();
    }
    
    // Getters
    public UUID getOwnerUUID() { return ownerUUID; }
    public int getCenterX() { return centerX; }
    public int getCenterZ() { return centerZ; }
    public int getSpawnX() { return spawnX; }
    public int getSpawnY() { return spawnY; }
    public int getSpawnZ() { return spawnZ; }
    public int getSize() { return size; }
    public long getCreatedAt() { return createdAt; }
    
    /**
     * Get island radius (half of size)
     */
    public int getRadius() {
        return size / 2;
    }
    
    // Setters
    public void setCenterX(int centerX) { this.centerX = centerX; }
    public void setCenterZ(int centerZ) { this.centerZ = centerZ; }
    public void setSpawnX(int spawnX) { this.spawnX = spawnX; }
    public void setSpawnY(int spawnY) { this.spawnY = spawnY; }
    public void setSpawnZ(int spawnZ) { this.spawnZ = spawnZ; }
    public void setSize(int size) { this.size = size; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
