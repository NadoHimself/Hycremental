package de.ageofflair.hycremental.listeners;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.generators.Generator;
import de.ageofflair.hycremental.generators.GeneratorType;
import de.ageofflair.hycremental.generators.GeneratorQuality;
import de.ageofflair.hycremental.island.IslandData;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Generator Place Listener - Handles generator placement
 * 
 * When a player places a generator item:
 * - Check if they have available slots
 * - Check if within island boundary
 * - Roll for quality (Common to Divine)
 * - Create and register generator
 * - Start production
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class GeneratorPlaceListener {
    
    private final Hycremental plugin;
    private final Random random;
    
    public GeneratorPlaceListener(Hycremental plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }
    
    /**
     * Handle block place event
     * Called from main plugin class via BlockPlaceEvent
     * 
     * @param player The player placing the block
     * @param blockType The item type being placed
     * @param x Block X coordinate
     * @param y Block Y coordinate
     * @param z Block Z coordinate
     * @return true if generator was placed successfully
     */
    public boolean onGeneratorPlace(Player player, String blockType, int x, int y, int z) {
        UUID uuid = player.getComponent(PlayerRef.class).getUuid();
        
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        if (playerData == null) {
            plugin.getLogger().at(Level.WARNING).log("Player data not found for " + player.getName());
            return false;
        }
        
        // Check if it's a generator item
        GeneratorType type = identifyGeneratorType(blockType);
        if (type == null) {
            return false; // Not a generator
        }
        
        // Check if player owns the island at this location
        IslandData island = plugin.getIslandManager().getIsland(uuid);
        if (island == null) {
            player.sendMessage(Message.raw("§cYou don't have an island!"));
            return true; // Cancel event
        }
        
        // TODO: Check if location is within island boundary
        // if (!island.contains(x, y, z)) {
        //     player.sendMessage(Message.raw("§cYou can only place generators on your island!"));
        //     return true;
        // }
        
        // Check generator slots
        int currentGenerators = plugin.getGeneratorManager().getPlayerGeneratorCount(uuid);
        if (currentGenerators >= playerData.getGeneratorSlots()) {
            player.sendMessage(Message.raw("§cNo generator slots available!"));
            player.sendMessage(Message.raw("§7Upgrade your island with §e/island slots"));
            return true; // Cancel event
        }
        
        // Roll for quality
        GeneratorQuality quality = rollQuality(playerData);
        
        // Create generator
        Generator generator = new Generator(
            UUID.randomUUID(),
            uuid,
            type,
            x, y, z,
            quality
        );
        
        // Register generator
        plugin.getGeneratorManager().addGenerator(generator);
        
        // Notify player
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§a§l✓ Generator Placed!"));
        player.sendMessage(Message.raw("§7Type: " + type.getDisplayName()));
        player.sendMessage(Message.raw("§7Quality: " + quality.getColor() + quality.getDisplayName()));
        player.sendMessage(Message.raw("§7Production: §e" + NumberFormatter.format(generator.calculateProduction()) + " Essence/s"));
        player.sendMessage(Message.raw(""));
        
        // Special message for rare qualities
        if (quality.ordinal() >= GeneratorQuality.RARE.ordinal()) {
            player.sendMessage(Message.raw(quality.getColor() + "§l✦ You got a " + quality.getDisplayName() + " quality generator!"));
            player.sendMessage(Message.raw(""));
        }
        
        // Log
        plugin.getLogger().at(Level.INFO).log(
            player.getName() + " placed a " + quality.name() + " " + type.name() + " generator at " + x + "," + y + "," + z
        );
        
        return false; // Allow event
    }
    
    /**
     * Identify generator type from item/block type
     */
    private GeneratorType identifyGeneratorType(String blockType) {
        // Check if block name contains generator type
        String lower = blockType.toLowerCase();
        
        if (lower.contains("stone_generator")) return GeneratorType.STONE;
        if (lower.contains("coal_generator")) return GeneratorType.COAL;
        if (lower.contains("iron_generator")) return GeneratorType.IRON;
        if (lower.contains("copper_generator")) return GeneratorType.COPPER;
        if (lower.contains("gold_generator")) return GeneratorType.GOLD;
        if (lower.contains("redstone_generator")) return GeneratorType.REDSTONE;
        if (lower.contains("diamond_generator")) return GeneratorType.DIAMOND;
        if (lower.contains("emerald_generator")) return GeneratorType.EMERALD;
        if (lower.contains("netherite_generator")) return GeneratorType.NETHERITE;
        if (lower.contains("crystal_generator")) return GeneratorType.CRYSTAL;
        if (lower.contains("quantum_generator")) return GeneratorType.QUANTUM;
        if (lower.contains("reality_generator")) return GeneratorType.REALITY;
        if (lower.contains("void_generator")) return GeneratorType.VOID;
        if (lower.contains("divine_generator")) return GeneratorType.DIVINE;
        
        return null;
    }
    
    /**
     * Roll for generator quality
     * Better luck with higher prestige/ascension/rebirth
     */
    private GeneratorQuality rollQuality(PlayerData playerData) {
        double roll = random.nextDouble() * 100.0;
        
        // Calculate luck bonus from progression
        double luckBonus = 0;
        luckBonus += playerData.getPrestigeLevel() * 0.1;      // +0.1% per prestige
        luckBonus += playerData.getAscensionLevel() * 1.0;     // +1% per ascension
        luckBonus += playerData.getRebirthCount() * 5.0;       // +5% per rebirth
        
        // Apply luck bonus
        roll += luckBonus;
        
        // Quality thresholds (with luck, better qualities become more likely)
        if (roll >= 99.9) return GeneratorQuality.DIVINE;      // 0.1% base
        if (roll >= 99.0) return GeneratorQuality.LEGENDARY;   // 0.9% base
        if (roll >= 95.0) return GeneratorQuality.EPIC;        // 4% base
        if (roll >= 85.0) return GeneratorQuality.RARE;        // 10% base
        if (roll >= 65.0) return GeneratorQuality.UNCOMMON;    // 20% base
        return GeneratorQuality.COMMON;                         // 65% base
    }
}
