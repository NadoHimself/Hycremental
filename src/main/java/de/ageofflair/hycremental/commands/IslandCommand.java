package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.command.Command;
import com.hypixel.hytale.server.command.CommandContext;
import com.hypixel.hytale.server.command.CommandSender;
import com.hypixel.hytale.server.player.ServerPlayer;
import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.island.IslandData;

/**
 * Island Command - Teleport to island and manage it
 */
public class IslandCommand extends Command {
    
    private final Hycremental plugin;
    
    public IslandCommand(Hycremental plugin) {
        super("island");
        this.plugin = plugin;
    }
    
    @Override
    public void execute(CommandSender sender, CommandContext context) {
        if (!(sender instanceof ServerPlayer)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return;
        }
        
        ServerPlayer player = (ServerPlayer) sender;
        IslandData island = plugin.getIslandManager().getIsland(player.getUUID());
        
        if (island == null) {
            player.sendMessage("§cYou don't have an island yet!");
            return;
        }
        
        // TODO: Teleport player to island spawn
        player.sendMessage("§6Teleporting to your island...");
        player.sendMessage("§7Island Size: §e" + island.getSize() + "x" + island.getSize());
    }
}
