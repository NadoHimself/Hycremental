package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.Command;
import com.hypixel.hytale.server.core.command.CommandSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;

/**
 * Essence Command - Check and manage essence
 */
public class EssenceCommand implements Command {
    
    private final Hycremental plugin;
    
    public EssenceCommand(Hycremental plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "essence";
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
        
        player.sendMessage(Message.raw("§6§l=== Your Economy ==="));
        player.sendMessage(Message.raw("§7Essence: §e" + playerData.getEssence().toPlainString()));
        player.sendMessage(Message.raw("§7Gems: §b" + playerData.getGems()));
        player.sendMessage(Message.raw("§7Crystals: §d" + playerData.getCrystals()));
        player.sendMessage(Message.raw("§7Lifetime Essence: §6" + playerData.getLifetimeEssence().toPlainString()));
    }
}
