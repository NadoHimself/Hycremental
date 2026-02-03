package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.command.Command;
import com.hypixel.hytale.server.command.CommandContext;
import com.hypixel.hytale.server.command.CommandSender;
import com.hypixel.hytale.server.player.ServerPlayer;
import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;

/**
 * Essence Command - Check and manage essence
 */
public class EssenceCommand extends Command {
    
    private final Hycremental plugin;
    
    public EssenceCommand(Hycremental plugin) {
        super("essence");
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
        
        player.sendMessage("§6§l=== Your Economy ===");
        player.sendMessage("§7Essence: §e" + playerData.getEssence().toPlainString());
        player.sendMessage("§7Gems: §b" + playerData.getGems());
        player.sendMessage("§7Crystals: §d" + playerData.getCrystals());
        player.sendMessage("§7Lifetime Essence: §6" + playerData.getLifetimeEssence().toPlainString());
    }
}
