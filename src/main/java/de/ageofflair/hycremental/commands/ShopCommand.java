package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.Command;
import com.hypixel.hytale.server.core.command.CommandSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import de.ageofflair.hycremental.Hycremental;

/**
 * Shop Command - Open generator shop
 */
public class ShopCommand implements Command {
    
    private final Hycremental plugin;
    
    public ShopCommand(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "shop";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.raw("§cThis command can only be used by players!"));
            return;
        }
        
        Player player = (Player) sender;
        
        // TODO: Open shop GUI
        player.sendMessage(Message.raw("§6§l=== Generator Shop ==="));
        player.sendMessage(Message.raw("§7Shop UI coming soon!"));
    }
}
