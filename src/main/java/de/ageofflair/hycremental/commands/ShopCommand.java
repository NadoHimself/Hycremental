package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandDescriptor;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;
import de.ageofflair.hycremental.gui.ShopGUI;

import java.util.UUID;

/**
 * Shop Command - Open generator shop GUI
 * 
 * Commands:
 * /shop - Open main shop
 * /shop generators - Open generator shop
 * /shop upgrades - Open upgrade shop
 * 
 * @author Kielian (NadoHimself)
 * @version 1.0.0-ALPHA
 */
public class ShopCommand extends AbstractAsyncCommand {
    
    private final Hycremental plugin;
    private final ShopGUI shopGUI;
    
    public ShopCommand(Hycremental plugin) {
        this.plugin = plugin;
        this.shopGUI = new ShopGUI(plugin);
    }
    
    @Override
    public CommandDescriptor buildDescriptor() {
        return CommandDescriptor.builder()
            .name("shop")
            .description("Open the shop")
            .aliases("store", "buy")
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
        
        // No arguments - open main shop
        if (args.length == 0) {
            shopGUI.openShop(player);
            return;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "generators":
            case "gen":
            case "generator":
                shopGUI.openShop(player);
                break;
                
            case "upgrades":
            case "upgrade":
            case "up":
                player.sendMessage(Message.raw("§7Use /gen upgrade on a generator to upgrade it!"));
                break;
                
            case "help":
                sendHelp(player);
                break;
                
            default:
                player.sendMessage(Message.raw("§cUnknown subcommand! Use /shop help"));
        }
    }
    
    /**
     * Send help message
     */
    private void sendHelp(Player player) {
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
        player.sendMessage(Message.raw("§6§lShop Commands"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§e/shop §7- Open main shop"));
        player.sendMessage(Message.raw("§e/shop generators §7- Open generator shop"));
        player.sendMessage(Message.raw("§e/shop upgrades §7- View upgrade info"));
        player.sendMessage(Message.raw(""));
        player.sendMessage(Message.raw("§7Aliases: §e/store, /buy"));
        player.sendMessage(Message.raw("§7§m─────────────────────────────"));
    }
}
