package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.command.Command;
import com.hypixel.hytale.server.command.CommandContext;
import com.hypixel.hytale.server.command.CommandSender;
import com.hypixel.hytale.server.player.ServerPlayer;
import de.ageofflair.hycremental.Hycremental;

/**
 * Generator Command - Manage generators
 */
public class GeneratorCommand extends Command {
    
    private final Hycremental plugin;
    
    public GeneratorCommand(Hycremental plugin) {
        super("generator");
        this.plugin = plugin;
    }
    
    @Override
    public void execute(CommandSender sender, CommandContext context) {
        if (!(sender instanceof ServerPlayer)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return;
        }
        
        ServerPlayer player = (ServerPlayer) sender;
        
        // TODO: Show generator info/management
        player.sendMessage("§6§l=== Your Generators ===");
        player.sendMessage("§7Generator management coming soon!");
    }
}
