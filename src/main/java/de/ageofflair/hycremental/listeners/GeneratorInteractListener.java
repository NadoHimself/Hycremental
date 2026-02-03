package de.ageofflair.hycremental.listeners;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.generators.Generator;
import de.ageofflair.hycremental.gui.UpgradeGUI;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Generator Interact Listener - Handles player interaction with generators
 * 
 * Actions:
 * - Right-click: Open upgrade GUI
 * - Shift + Right-click: Show detailed info
 * - Left-click: Collect accumulated essence (manual mode)
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class GeneratorInteractListener {
    
    private final Hycremental plugin;
    private final UpgradeGUI upgradeGUI;
    
    // Cooldown to prevent spam (UUID -> last interaction time)
    private final Map<UUID, Long> interactionCooldown;
    private static final long COOLDOWN_MS = 500; // 0.5 seconds
    
    public GeneratorInteractListener(Hycremental plugin) {
        this.plugin = plugin;
        this.upgradeGUI = new UpgradeGUI(plugin);
        this.interactionCooldown = new HashMap<>();
    }
    
    /**
     * Handle player interact event
     * Called from main plugin class via PlayerInteractEvent
     * 
     * @param player The player interacting
     * @param x Block X coordinate
     * @param y Block Y coordinate
     * @param z Block Z coordinate
     * @param isRightClick true if right-click, false if left-click
     * @param isSneaking true if player is sneaking (shift)
     * @return true if event should be cancelled
     */
    public boolean onGeneratorInteract(Player player, int x, int y, int z, boolean isRightClick, boolean isSneaking) {
        UUID uuid = player.getComponent(PlayerRef.class).getUuid();
        
        // Check cooldown
        if (isOnCooldown(uuid)) {
            return true; // Cancel event, still on cooldown
        }
        
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        if (playerData == null) {
            plugin.getLogger().at(Level.WARNING).log("Player data not found for " + player.getName());
            return false;
        }
        
        // Find generator at location
        Generator generator = plugin.getGeneratorManager().getGeneratorAt(x, y, z);
        
        if (generator == null) {
            return false; // Not a generator block
        }
        
        // Set cooldown
        interactionCooldown.put(uuid, System.currentTimeMillis());
        
        // Check ownership
        if (!generator.getOwnerUUID().equals(uuid)) {
            player.sendMessage(Message.raw("§cThis generator belongs to " + getOwnerName(generator.getOwnerUUID()) + "!"));
            return true; // Cancel event
        }
        
        // Handle different interaction types
        if (isRightClick) {
            if (isSneaking) {
                // Shift + Right-click: Show detailed info
                showGeneratorInfo(player, generator, playerData);
            } else {
                // Right-click: Open upgrade GUI
                upgradeGUI.openUpgradeGUI(player, generator);
            }
        } else {
            // Left-click: Collect essence (if manual mode enabled)
            if (plugin.getConfig().getBoolean("gameplay.manual-collection", false)) {
                collectEssence(player, generator, playerData);
            } else {
                player.sendMessage(Message.raw("§7Right-click to upgrade this generator!"));
            }
        }
        
        return true; // Cancel event to prevent block breaking
    }
    
    /**
     * Check if player is on interaction cooldown
     */
    private boolean isOnCooldown(UUID uuid) {
        Long lastInteraction = interactionCooldown.get(uuid);
        if (lastInteraction == null) {
            return false;
        }
        
        long timeSince = System.currentTimeMillis() - lastInteraction;
        return timeSince < COOLDOWN_MS;
    }
    
    /**
     * Show detailed generator information
     */
    private void showGeneratorInfo(Player player, Generator generator, PlayerData playerData) {
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§6§lGenerator Information"));
        player.sendMessage(Message.raw(""));
        
        // Basic info
        player.sendMessage(Message.raw("§7Type: " + generator.getType().getDisplayName()));
        player.sendMessage(Message.raw("§7Tier: §e" + generator.getType().getTier()));
        player.sendMessage(Message.raw("§7Quality: " + generator.getQuality().getColor() + generator.getQuality().getDisplayName()));
        player.sendMessage(Message.raw("§7Level: §a" + generator.getLevel() + "§7/§a100"));
        player.sendMessage(Message.raw(""));
        
        // Production stats
        double baseProduction = generator.getType().getBaseProduction();
        double currentProduction = generator.calculateProduction();
        double withMultiplier = currentProduction * playerData.calculateTotalMultiplier();
        
        player.sendMessage(Message.raw("§7Base Production: §e" + NumberFormatter.format(baseProduction) + " Essence/s"));
        player.sendMessage(Message.raw("§7Current Production: §a" + NumberFormatter.format(currentProduction) + " Essence/s"));
        player.sendMessage(Message.raw("§7With Multipliers: §a" + NumberFormatter.format(withMultiplier) + " Essence/s"));
        player.sendMessage(Message.raw(""));
        
        // Efficiency stats
        double efficiency = (currentProduction / baseProduction) * 100.0;
        player.sendMessage(Message.raw("§7Efficiency: §e" + String.format("%.1f", efficiency) + "%"));
        player.sendMessage(Message.raw(""));
        
        // Enchantments
        if (!generator.getEnchantments().isEmpty()) {
            player.sendMessage(Message.raw("§7Enchantments:"));
            generator.getEnchantments().forEach((enchant, level) -> {
                player.sendMessage(Message.raw("§8 • " + enchant.getColor() + enchant.getDisplayName() + " " + level));
            });
            player.sendMessage(Message.raw(""));
        }
        
        // Location
        player.sendMessage(Message.raw("§7Location: §e" + generator.getX() + ", " + generator.getY() + ", " + generator.getZ()));
        player.sendMessage(Message.raw(""));
        
        // Next upgrade cost
        if (generator.getLevel() < 100) {
            BigDecimal upgradeCost = generator.calculateUpgradeCost();
            player.sendMessage(Message.raw("§7Next Upgrade: §a" + NumberFormatter.format(upgradeCost) + " Essence"));
        } else {
            player.sendMessage(Message.raw("§a§lMAX LEVEL!"));
        }
        
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§eRight-click to open upgrade menu"));
        player.sendMessage(Message.raw(""));
    }
    
    /**
     * Collect accumulated essence from generator (manual mode)
     */
    private void collectEssence(Player player, Generator generator, PlayerData playerData) {
        // Calculate accumulated essence since last collection
        long currentTime = System.currentTimeMillis();
        long lastCollection = generator.getLastCollection();
        long timeDiff = currentTime - lastCollection;
        
        if (timeDiff < 1000) { // Less than 1 second
            player.sendMessage(Message.raw("§cNothing to collect yet!"));
            return;
        }
        
        // Calculate essence
        double production = generator.calculateProduction() * playerData.calculateTotalMultiplier();
        double seconds = timeDiff / 1000.0;
        BigDecimal collected = BigDecimal.valueOf(production * seconds);
        
        // Cap at max storage (if implemented)
        // BigDecimal maxStorage = generator.getMaxStorage();
        // if (collected.compareTo(maxStorage) > 0) collected = maxStorage;
        
        // Add to player
        playerData.addEssence(collected);
        generator.setLastCollection(currentTime);
        
        // Visual feedback
        player.sendMessage(Message.raw("§a§l✓ Collected! §7+" + NumberFormatter.format(collected) + " Essence"));
        
        // TODO: Particle effect with Hytale Particle API
        // player.spawnParticle(ParticleType.HAPPY_VILLAGER, generator.getLocation(), 10);
        
        // Save
        plugin.getPlayerDataManager().savePlayerData(playerData);
    }
    
    /**
     * Get owner name from UUID
     */
    private String getOwnerName(UUID ownerUUID) {
        PlayerData ownerData = plugin.getPlayerDataManager().getPlayerData(ownerUUID);
        return ownerData != null ? ownerData.getUsername() : "Unknown";
    }
    
    /**
     * Clean up old cooldown entries (call periodically)
     */
    public void cleanupCooldowns() {
        long now = System.currentTimeMillis();
        interactionCooldown.entrySet().removeIf(entry -> 
            now - entry.getValue() > 60000 // Remove entries older than 1 minute
        );
    }
}
