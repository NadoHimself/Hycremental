package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.context.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;

import javax.annotation.Nonnull;

/**
 * Island Command - Manage player island
 */
public class IslandCommand extends CommandBase {
    
    private final Hycremental plugin;
    
    public IslandCommand(Hycremental plugin) {
        super("island", "Manage your private island");
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
        
        context.sendMessage(Message.raw("§6§l=== Your Island ==="));
        context.sendMessage(Message.raw("§7Island Size: §e" + playerData.getIslandSize() + "x" + playerData.getIslandSize()));
        context.sendMessage(Message.raw("§7Generator Slots: §e" + playerData.getGeneratorSlots()));
    }
}
