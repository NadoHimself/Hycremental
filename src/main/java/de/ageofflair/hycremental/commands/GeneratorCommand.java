package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandDescriptor;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.generators.Generator;
import de.ageofflair.hycremental.generators.GeneratorType;
import de.ageofflair.hycremental.gui.ShopGUI;
import de.ageofflair.hycremental.gui.UpgradeGUI;
import de.ageofflair.hycremental.utils.NumberFormatter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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
public class GeneratorCommand extends AbstractAsyncCommand {
    
    private final Hycremental plugin;
    private final ShopGUI shopGUI;
    private final UpgradeGUI upgradeGUI;
    
    public GeneratorCommand(Hycremental plugin) {
        this.plugin = plugin;
        this.shopGUI = new ShopGUI(plugin);
        this.upgradeGUI = new UpgradeGUI(plugin);
    }
    
    @Override
    public CommandDescriptor buildDescriptor() {
        return CommandDescriptor.builder()
            .name("generator")
            .description("Manage your generators")
            .aliases("gen", "generators")
            .build();
    }
    
    @Override
    public void run(CommandContext context) {
        if (!context.sender().isPlayer()) {
            context.sender().sendMessage(Message.raw("§cOnly players can use this command!"));
            return;
        }
        
        Player player = context.sender().asPlayer();
        UUID uuid = player.getComponent(PlayerRef.class).getUuid();
        String[] args = context.args();
        
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        if (playerData == null) {
            player.sendMessage(Message.raw("§cError loading player data!"));
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
            case "up":
                handleUpgrade(player, playerData);
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
            case "sell":
                handleRemove(player, playerData);
                break;
                
            case "help":
                sendHelp(player);
                break;
                
            default:
                player.sendMessage(Message.raw("§cUnknown subcommand! Use /gen help"));
        }
    }
    
    /**
     * Handle buy subcommand
     */
    private void handleBuy(Player player, PlayerData playerData, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Message.raw("§cUsage: /gen buy <type>"));
            player.sendMessage(Message.raw("§eExample: /gen buy stone"));
            return;
        }
        
        String typeName = args[1].toUpperCase();
        GeneratorType type;
        
        try {
            type = GeneratorType.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            player.sendMessage(Message.raw("§cInvalid generator type! Use /gen help"));
            return;
        }
        
        // Check prestige requirement
        if (playerData.getPrestigeLevel() < type.getRequiredPrestige()) {
            player.sendMessage(Message.raw("§cYou need Prestige " + type.getRequiredPrestige() + " to purchase this!"));
            return;
        }
        
        // Check cost
        BigDecimal cost = type.getCost();
        if (!playerData.hasEssence(cost)) {
            player.sendMessage(Message.raw("§cInsufficient Essence! Need: " + NumberFormatter.format(cost)));
            return;
        }
        
        // Check generator slots
        int currentGenerators = plugin.getGeneratorManager().getPlayerGeneratorCount(playerData.getUuid());
        if (currentGenerators >= playerData.getGeneratorSlots()) {
            player.sendMessage(Message.raw("§cNo generator slots available! Upgrade your island!"));
            return;
        }
        
        // Purchase
        playerData.removeEssence(cost);
        playerData.incrementGeneratorsPurchased();
        
        // TODO: Give generator item to player with Hytale Inventory API
        // ItemStack generatorItem = createGeneratorItem(type);
        // player.getInventory().addItemStack(generatorItem);
        
        player.sendMessage(Message.raw("§a§l✓ Purchased! §7" + type.getDisplayName()));
        player.sendMessage(Message.raw("§7Place it on your island to start producing!"));
    }
    
    /**
     * Handle upgrade subcommand
     */
    private void handleUpgrade(Player player, PlayerData playerData) {
        // Find nearest generator
        Generator generator = findNearestGenerator(player);
        
        if (generator == null) {
            player.sendMessage(Message.raw("§cNo generator found nearby!"));
            player.sendMessage(Message.raw("§7Stand close to a generator and try again."));
            return;
        }
        
        // Check ownership
        if (!generator.getOwnerUUID().equals(playerData.getUuid())) {
            player.sendMessage(Message.raw("§cYou don't own this generator!"));
            return;
        }
        
        // Open upgrade GUI
        upgradeGUI.openUpgradeGUI(player, generator);
    }
    
    /**
     * Handle info subcommand
     */
    private void handleInfo(Player player, PlayerData playerData) {
        Generator generator = findNearestGenerator(player);
        
        if (generator == null) {
            player.sendMessage(Message.raw("§cNo generator found nearby!"));
            return;
        }
        
        // Display generator info
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§6§lGenerator Info"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Type: §e" + generator.getType().getDisplayName()));
        player.sendMessage(Message.raw("§7Tier: §e" + generator.getType().getTier()));
        player.sendMessage(Message.raw("§7Level: §a" + generator.getLevel() + "§7/§a100"));
        player.sendMessage(Message.raw("§7Quality: " + generator.getQuality().getColor() + generator.getQuality().getDisplayName()));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Base Production: §a" + NumberFormatter.format(generator.getType().getBaseProduction()) + " Essence/s"));
        player.sendMessage(Message.raw("§7Current Production: §a" + NumberFormatter.format(generator.calculateProduction()) + " Essence/s"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Location: §e" + generator.getX() + ", " + generator.getY() + ", " + generator.getZ()));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
    }
    
    /**
     * Handle list subcommand
     */
    private void handleList(Player player, PlayerData playerData) {
        List<Generator> generators = plugin.getGeneratorManager().getPlayerGenerators(playerData.getUuid());
        
        if (generators.isEmpty()) {
            player.sendMessage(Message.raw("§cYou don't have any generators!"));
            player.sendMessage(Message.raw("§eUse /gen to buy your first generator!"));
            return;
        }
        
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§6§lYour Generators §7(" + generators.size() + "/" + playerData.getGeneratorSlots() + ")"));
        player.sendMessage(Message.raw(""));
        
        // Group by type
        java.util.Map<GeneratorType, Integer> counts = new java.util.HashMap<>();
        for (Generator gen : generators) {
            counts.put(gen.getType(), counts.getOrDefault(gen.getType(), 0) + 1);
        }
        
        // Display
        for (java.util.Map.Entry<GeneratorType, Integer> entry : counts.entrySet()) {
            GeneratorType type = entry.getKey();
            int count = entry.getValue();
            player.sendMessage(Message.raw("§8 • §e" + type.getDisplayName() + ": §7x" + count));
        }
        
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
    }
    
    /**
     * Handle stats subcommand
     */
    private void handleStats(Player player, PlayerData playerData) {
        List<Generator> generators = plugin.getGeneratorManager().getPlayerGenerators(playerData.getUuid());
        
        // Calculate total production
        double totalProduction = 0;
        for (Generator gen : generators) {
            totalProduction += gen.calculateProduction();
        }
        
        // Calculate income
        long hourlyIncome = (long)(totalProduction * 3600);
        long dailyIncome = hourlyIncome * 24;
        
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§6§lGenerator Statistics"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Active Generators: §e" + generators.size() + "§7/§e" + playerData.getGeneratorSlots()));
        player.sendMessage(Message.raw("§7Total Production: §a" + NumberFormatter.format(totalProduction) + " Essence/s"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Estimated Income:"));
        player.sendMessage(Message.raw("§8 • §7Per Minute: §a" + NumberFormatter.formatLong((long)(totalProduction * 60))));
        player.sendMessage(Message.raw("§8 • §7Per Hour: §a" + NumberFormatter.formatLong(hourlyIncome)));
        player.sendMessage(Message.raw("§8 • §7Per Day: §a" + NumberFormatter.formatLong(dailyIncome)));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Lifetime Purchased: §e" + playerData.getGeneratorsPurchased()));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
    }
    
    /**
     * Handle remove subcommand
     */
    private void handleRemove(Player player, PlayerData playerData) {
        Generator generator = findNearestGenerator(player);
        
        if (generator == null) {
            player.sendMessage(Message.raw("§cNo generator found nearby!"));
            return;
        }
        
        // Check ownership
        if (!generator.getOwnerUUID().equals(playerData.getUuid())) {
            player.sendMessage(Message.raw("§cYou don't own this generator!"));
            return;
        }
        
        // Calculate sell price (50% of total investment)
        BigDecimal sellPrice = generator.calculateSellPrice();
        
        // Remove generator
        plugin.getGeneratorManager().removeGenerator(generator);
        playerData.addEssence(sellPrice);
        
        player.sendMessage(Message.raw("§a§l✓ Removed! §7Received " + NumberFormatter.format(sellPrice) + " Essence"));
    }
    
    /**
     * Send help message
     */
    private void sendHelp(Player player) {
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§6§lGenerator Commands"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§e/gen §7- Open generator shop"));
        player.sendMessage(Message.raw("§e/gen buy <type> §7- Buy a generator"));
        player.sendMessage(Message.raw("§e/gen upgrade §7- Upgrade nearest generator"));
        player.sendMessage(Message.raw("§e/gen info §7- Info about nearest generator"));
        player.sendMessage(Message.raw("§e/gen list §7- List all your generators"));
        player.sendMessage(Message.raw("§e/gen stats §7- Generator statistics"));
        player.sendMessage(Message.raw("§e/gen remove §7- Remove nearest generator"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Available Types:"));
        player.sendMessage(Message.raw("§8 • §7stone, coal, iron, gold, diamond"));
        player.sendMessage(Message.raw("§8 • §7emerald, netherite, crystal, quantum"));
        player.sendMessage(Message.raw("§8 • §7reality, void, divine"));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
    }
    
    /**
     * Find nearest generator to player
     */
    private Generator findNearestGenerator(Player player) {
        // TODO: Implement with Hytale location API
        // Get player location: player.getComponent(Transform.class).position
        // Find generators within 5 blocks
        // Return nearest one
        return null;
    }
}
