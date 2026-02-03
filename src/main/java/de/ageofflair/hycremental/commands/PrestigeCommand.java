package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.Command;
import com.hypixel.hytale.server.core.command.CommandSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;

/**
 * Prestige Command - Prestige system
 */
public class PrestigeCommand implements Command {
    
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
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUuid());
        
        if (playerData == null) {
            player.sendMessage(Message.raw("§cPlayer data not found!"));
            return;
        }
        
        player.sendMessage(Message.raw("§6§l=== Prestige System ==="));
        player.sendMessage(Message.raw("§7Prestige Level: §e" + playerData.getPrestigeLevel()));
        player.sendMessage(Message.raw("§7Ascension Level: §d" + playerData.getAscensionLevel()));
        player.sendMessage(Message.raw("§7Rebirth Count: §c" + playerData.getRebirthCount()));
        player.sendMessage(Message.raw("§7Prestige UI coming soon!"));
    }
}
