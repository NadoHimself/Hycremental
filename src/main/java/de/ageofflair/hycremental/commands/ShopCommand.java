package de.ageofflair.hycremental.commands;

import de.ageofflair.hycremental.Hycremental;
// import com.hytale.api.command.Command;
// import com.hytale.api.command.CommandExecutor;
// import com.hytale.api.command.CommandSender;
// import com.hytale.api.entity.Player;

/**
 * Command handler for opening the shop GUI
 * /shop - Opens generator shop
 */
public class ShopCommand { // implements CommandExecutor {
    
    private final Hycremental plugin;
    
    public ShopCommand(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    // @Override
    public boolean onCommand(Object sender, Object command, String label, String[] args) {
        /*
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // TODO: Open shop GUI
        player.sendMessage("§eShop GUI coming soon!");
        player.sendMessage("§7Available generators will be displayed here.");
        */
        return true;
    }
}