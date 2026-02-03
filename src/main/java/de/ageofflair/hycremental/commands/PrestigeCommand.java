package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.command.Command;
import com.hypixel.hytale.server.command.CommandContext;
import com.hypixel.hytale.server.command.CommandSender;
import com.hypixel.hytale.server.player.ServerPlayer;
import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;

/**
 * Prestige Command - Prestige system
 */
public class PrestigeCommand extends Command {
    
    private final Hycremental plugin;
    
    public PrestigeCommand(Hycremental plugin) {
        super("prestige");
        this.plugin = plugin;
    }
    
    @Override
    public void execute(CommandSender sender, CommandContext context) {
        if (!(sender instanceof ServerPlayer)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return;
        }
        
        ServerPlayer player = (ServerPlayer) sender;
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUUID());
        
        if (playerData == null) {
            player.sendMessage("§cPlayer data not found!");
            return;
        }
        
        player.sendMessage("§6§l=== Prestige System ===");
        player.sendMessage("§7Prestige Level: §e" + playerData.getPrestigeLevel());
        player.sendMessage("§7Ascension Level: §d" + playerData.getAscensionLevel());
        player.sendMessage("§7Rebirth Count: §c" + playerData.getRebirthCount());
        player.sendMessage("§7Prestige UI coming soon!");
    }
}
