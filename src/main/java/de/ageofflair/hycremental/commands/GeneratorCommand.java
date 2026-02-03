package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.Command;
import com.hypixel.hytale.server.core.command.CommandSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import de.ageofflair.hycremental.Hycremental;

/**
 * Generator Command - Manage generators
 */
public class GeneratorCommand implements Command {
    
    private final Hycremental plugin;
    
    public GeneratorCommand(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "generator";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.raw("§cThis command can only be used by players!"));
            return;
        }
        
        Player player = (Player) sender;
        
        // TODO: Show generator info/management
        player.sendMessage(Message.raw("§6§l=== Your Generators ==="));
        player.sendMessage(Message.raw("§7Generator management coming soon!"));
    }
}
