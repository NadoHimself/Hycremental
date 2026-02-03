package de.ageofflair.hycremental.commands;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.generators.Generator;
import de.ageofflair.hycremental.generators.GeneratorType;
import de.ageofflair.hycremental.gui.ShopGUI;
import de.ageofflair.hycremental.gui.UpgradeGUI;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Generator Command - Manage generators
 * 
 * Commands:
 * /gen - Open generator shop
 * /gen buy <type> - Buy a generator
 * /gen upgrade - Upgrade nearest generator
 * /gen info - Info about nearest generator
 * /gen list - List all your generators
 * /gen stats - Generator statistics
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class GeneratorCommand {
    
    private final Hycremental plugin;
    private final ShopGUI shopGUI;
    private final UpgradeGUI upgradeGUI;
    
    public GeneratorCommand(Hycremental plugin) {
        this.plugin = plugin;
        this.shopGUI = new ShopGUI(plugin);
        this.upgradeGUI = new UpgradeGUI(plugin);
    }
    
    /**
     * Execute command
     * @param player Command sender
     * @param args Command arguments
     */
    public void execute(Object player, String[] args) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(getPlayerUUID(player));
        if (playerData == null) {
            sendMessage(player, "§cError loading player data!");
            return;
        }
        
        // No arguments - open shop
        if (args.length == 0) {
            shopGUI.openShop(player);
            return;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "buy":
                handleBuy(player, playerData, args);
                break;
                
            case "upgrade":
                handleUpgrade(player, playerData, args);
                break;
                
            case "info":
                handleInfo(player, playerData);
                break;
                
            case "list":
                handleList(player, playerData);
                break;
                
            case "stats":
                handleStats(player, playerData);
                break;
                
            case "remove":
            case "delete":
                handleRemove(player, playerData);
                break;
                
            case "help":
                sendHelp(player);
                break;
                
            default:
                sendMessage(player, "§cUnknown subcommand! Use /gen help");
        }
    }
    
    /**
     * Handle buy subcommand
     */
    private void handleBuy(Object player, PlayerData playerData, String[] args) {
        if (args.length < 2) {
            sendMessage(player, "§cUsage: /gen buy <type>");
            sendMessage(player, "§eExample: /gen buy stone");
            return;
        }
        
        String typeName = args[1].toUpperCase();
        GeneratorType type;
        
        try {
            type = GeneratorType.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            sendMessage(player, "§cInvalid generator type! Use /gen help");
            return;
        }
        
        // Check prestige requirement
        if (playerData.getPrestigeLevel() < type.getRequiredPrestige()) {
            sendMessage(player, "§cYou need Prestige " + type.getRequiredPrestige() + " to purchase this!");
            return;
        }
        
        // Check cost
        BigDecimal cost = type.getCost();
        if (!playerData.hasEssence(cost)) {
            sendMessage(player, "§cInsufficient Essence! Need: " + NumberFormatter.format(cost));
            return;
        }
        
        // Check generator slots
        int currentGenerators = plugin.getGeneratorManager().getPlayerGeneratorCount(playerData.getUuid());
        if (currentGenerators >= playerData.getGeneratorSlots()) {
            sendMessage(player, "§cNo generator slots available! Upgrade your island!");
            return;
        }
        
        // Purchase
        playerData.removeEssence(cost);
        playerData.incrementGeneratorsPurchased();
        
        // TODO: Give generator item to player
        // giveGeneratorItem(player, type);
        
        sendMessage(player, "§a§l✓ Purchased! §7" + type.getDisplayName());
        sendMessage(player, "§7Place it on your island to start producing!");
        playSound(player, "SUCCESS");
    }
    
    /**
     * Handle upgrade subcommand
     */
    private void handleUpgrade(Object player, PlayerData playerData, String[] args) {
        // Find nearest generator
        Generator generator = findNearestGenerator(player);
        
        if (generator == null) {
            sendMessage(player, "§cNo generator found nearby!");
            return;
        }
        
        // Check ownership
        if (!generator.getOwnerUUID().equals(playerData.getUuid())) {
            sendMessage(player, "§cYou don't own this generator!");
            return;
        }
        
        // Open upgrade GUI
        upgradeGUI.openUpgradeGUI(player, generator);
    }
    
    /**
     * Handle info subcommand
     */
    private void handleInfo(Object player, PlayerData playerData) {
        Generator generator = findNearestGenerator(player);
        
        if (generator == null) {
            sendMessage(player, "§cNo generator found nearby!");
            return;
        }
        
        // Display generator info
        sendMessage(player, "§7§m─────────────────────────────");
        sendMessage(player, "§6§lGenerator Info");
        sendMessage(player, "");
        sendMessage(player, "§7Type: §e" + generator.getType().getDisplayName());
        sendMessage(player, "§7Tier: §e" + generator.getType().getTier());
        sendMessage(player, "§7Level: §a" + generator.getLevel() + "§7/§a100");
        sendMessage(player, "§7Quality: " + generator.getQuality().getColor() + generator.getQuality().getDisplayName());
        sendMessage(player, "");
        sendMessage(player, "§7Base Production: §a" + NumberFormatter.format(generator.getType().getBaseProduction()) + " Essence/s");
        sendMessage(player, "§7Current Production: §a" + NumberFormatter.format(generator.calculateProduction()) + " Essence/s");
        sendMessage(player, "");
        sendMessage(player, "§7Owner: §e" + generator.getOwnerUUID().toString().substring(0, 8));
        sendMessage(player, "§7Location: §e" + generator.getX() + ", " + generator.getY() + ", " + generator.getZ());
        sendMessage(player, "§7§m─────────────────────────────");
    }
    
    /**
     * Handle list subcommand
     */
    private void handleList(Object player, PlayerData playerData) {
        List<Generator> generators = plugin.getGeneratorManager().getPlayerGenerators(playerData.getUuid());
        
        if (generators.isEmpty()) {
            sendMessage(player, "§cYou don't have any generators!");
            sendMessage(player, "§eUse /gen to buy your first generator!");
            return;
        }
        
        sendMessage(player, "§7§m─────────────────────────────");
        sendMessage(player, "§6§lYour Generators §7(" + generators.size() + "/" + playerData.getGeneratorSlots() + ")");
        sendMessage(player, "");
        
        // Group by type
        java.util.Map<GeneratorType, Integer> counts = new java.util.HashMap<>();
        for (Generator gen : generators) {
            counts.put(gen.getType(), counts.getOrDefault(gen.getType(), 0) + 1);
        }
        
        // Display
        for (java.util.Map.Entry<GeneratorType, Integer> entry : counts.entrySet()) {
            GeneratorType type = entry.getKey();
            int count = entry.getValue();
            sendMessage(player, "§8 • §e" + type.getDisplayName() + ": §7x" + count);
        }
        
        sendMessage(player, "");
        sendMessage(player, "§7§m─────────────────────────────");
    }
    
    /**
     * Handle stats subcommand
     */
    private void handleStats(Object player, PlayerData playerData) {
        List<Generator> generators = plugin.getGeneratorManager().getPlayerGenerators(playerData.getUuid());
        
        // Calculate total production
        double totalProduction = 0;
        for (Generator gen : generators) {
            totalProduction += gen.calculateProduction();
        }
        
        // Calculate income
        long hourlyIncome = (long)(totalProduction * 3600);
        long dailyIncome = hourlyIncome * 24;
        
        sendMessage(player, "§7§m─────────────────────────────");
        sendMessage(player, "§6§lGenerator Statistics");
        sendMessage(player, "");
        sendMessage(player, "§7Active Generators: §e" + generators.size() + "§7/§e" + playerData.getGeneratorSlots());
        sendMessage(player, "§7Total Production: §a" + NumberFormatter.format(totalProduction) + " Essence/s");
        sendMessage(player, "");
        sendMessage(player, "§7Estimated Income:");
        sendMessage(player, "§8 • §7Per Minute: §a" + NumberFormatter.formatLong((long)(totalProduction * 60)));
        sendMessage(player, "§8 • §7Per Hour: §a" + NumberFormatter.formatLong(hourlyIncome));
        sendMessage(player, "§8 • §7Per Day: §a" + NumberFormatter.formatLong(dailyIncome));
        sendMessage(player, "");
        sendMessage(player, "§7Lifetime Purchased: §e" + playerData.getGeneratorsPurchased());
        sendMessage(player, "§7§m─────────────────────────────");
    }
    
    /**
     * Handle remove subcommand
     */
    private void handleRemove(Object player, PlayerData playerData) {
        Generator generator = findNearestGenerator(player);
        
        if (generator == null) {
            sendMessage(player, "§cNo generator found nearby!");
            return;
        }
        
        // Check ownership
        if (!generator.getOwnerUUID().equals(playerData.getUuid())) {
            sendMessage(player, "§cYou don't own this generator!");
            return;
        }
        
        // Calculate sell price (50% of total investment)
        BigDecimal sellPrice = generator.calculateSellPrice();
        
        // Remove generator
        plugin.getGeneratorManager().removeGenerator(generator);
        playerData.addEssence(sellPrice);
        
        sendMessage(player, "§a§l✓ Removed! §7Received " + NumberFormatter.format(sellPrice) + " Essence");
        playSound(player, "SUCCESS");
    }
    
    /**
     * Send help message
     */
    private void sendHelp(Object player) {
        sendMessage(player, "§7§m─────────────────────────────");
        sendMessage(player, "§6§lGenerator Commands");
        sendMessage(player, "");
        sendMessage(player, "§e/gen §7- Open generator shop");
        sendMessage(player, "§e/gen buy <type> §7- Buy a generator");
        sendMessage(player, "§e/gen upgrade §7- Upgrade nearest generator");
        sendMessage(player, "§e/gen info §7- Info about nearest generator");
        sendMessage(player, "§e/gen list §7- List all your generators");
        sendMessage(player, "§e/gen stats §7- Generator statistics");
        sendMessage(player, "§e/gen remove §7- Remove nearest generator");
        sendMessage(player, "");
        sendMessage(player, "§7Available Types:");
        sendMessage(player, "§8 • §7stone, coal, iron, gold, diamond");
        sendMessage(player, "§8 • §7emerald, netherite, crystal, quantum");
        sendMessage(player, "§8 • §7reality, void, divine");
        sendMessage(player, "§7§m─────────────────────────────");
    }
    
    /**
     * Find nearest generator to player
     */
    private Generator findNearestGenerator(Object player) {
        // TODO: Implement with Hytale location API
        // Get player location
        // Find generators within 5 blocks
        // Return nearest one
        return null;
    }
    
    // Utility methods
    
    private Object getPlayerUUID(Object player) {
        return null; // TODO
    }
    
    private void sendMessage(Object player, String message) {
        plugin.getLogger().info("[Command] " + message);
        // TODO: player.sendMessage(message);
    }
    
    private void playSound(Object player, String sound) {
        // TODO: player.playSound(sound);
    }
}
