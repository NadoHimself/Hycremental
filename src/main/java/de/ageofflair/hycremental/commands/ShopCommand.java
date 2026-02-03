package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.command.Command;
import com.hypixel.hytale.server.command.CommandContext;
import com.hypixel.hytale.server.command.CommandSender;
import com.hypixel.hytale.server.player.ServerPlayer;
import de.ageofflair.hycremental.Hycremental;

/**
 * Shop Command - Open generator shop
 */
public class ShopCommand extends Command {
    
    private final Hycremental plugin;
    
    public ShopCommand(Hycremental plugin) {
        super("shop");
        this.plugin = plugin;
    }
    
    @Override
    public void execute(CommandSender sender, CommandContext context) {
        if (!(sender instanceof ServerPlayer)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return;
        }
        
        ServerPlayer player = (ServerPlayer) sender;
        
        // TODO: Open shop GUI
        player.sendMessage("§6§l=== Generator Shop ===");
        player.sendMessage("§7Shop UI coming soon!");
    }
}
