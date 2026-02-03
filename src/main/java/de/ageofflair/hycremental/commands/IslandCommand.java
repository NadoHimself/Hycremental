package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.Command;
import com.hypixel.hytale.server.core.command.CommandSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.island.IslandData;

/**
 * Island Command - Teleport to island and manage it
 */
public class IslandCommand implements Command {
    
    private final Hycremental plugin;
    
    public IslandCommand(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "island";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.raw("§cThis command can only be used by players!"));
            return;
        }
        
        Player player = (Player) sender;
        IslandData island = plugin.getIslandManager().getIsland(player.getUuid());
        
        if (island == null) {
            player.sendMessage(Message.raw("§cYou don't have an island yet!"));
            return;
        }
        
        // TODO: Teleport player to island spawn
        player.sendMessage(Message.raw("§6Teleporting to your island..."));
        player.sendMessage(Message.raw("§7Island Size: §e" + island.getSize() + "x" + island.getSize()));
    }
}
