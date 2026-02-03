package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.Command;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;

import java.util.UUID;

/**
 * Prestige Command - Reset progress for permanent bonuses
 */
public class PrestigeCommand extends Command {
    
    private final Hycremental plugin;
    
    public PrestigeCommand(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "prestige";
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.raw("§cThis command can only be used by players!"));
            return;
        }
        
        Player player = (Player) sender;
        UUID uuid = player.getUuid();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(uuid);
        
        if (playerData == null) {
            player.sendMessage(Message.raw("§cError loading player data!"));
            return;
        }
        
        player.sendMessage(Message.raw("§6§l=== Prestige System ==="));
        player.sendMessage(Message.raw("§7Prestige system coming soon!"));
    }
}
