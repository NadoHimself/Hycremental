package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.context.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;

import javax.annotation.Nonnull;

/**
 * Prestige Command - Prestige system
 */
public class PrestigeCommand extends CommandBase {
    
    private final Hycremental plugin;
    
    public PrestigeCommand(Hycremental plugin) {
        super("prestige", "Access the prestige system");
        this.plugin = plugin;
    }
    
    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        if (!context.isPlayer()) {
            context.sendMessage(Message.raw("§cThis command can only be used by players!"));
            return;
        }
        
        Player player = context.senderAs(Player.class);
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUuid());
        
        if (playerData == null) {
            context.sendMessage(Message.raw("§cPlayer data not found!"));
            return;
        }
        
        context.sendMessage(Message.raw("§6§l=== Prestige System ==="));
        context.sendMessage(Message.raw("§7Prestige Level: §e" + playerData.getPrestigeLevel()));
        context.sendMessage(Message.raw("§7Ascension Level: §d" + playerData.getAscensionLevel()));
        context.sendMessage(Message.raw("§7Rebirth Count: §c" + playerData.getRebirthCount()));
        context.sendMessage(Message.raw("§7Prestige UI coming soon!"));
    }
}
