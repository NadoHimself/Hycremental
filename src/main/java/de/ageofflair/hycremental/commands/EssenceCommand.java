package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.context.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import de.ageofflair.hycremental.Hycremental;
import de.ageofflair.hycremental.data.PlayerData;

import javax.annotation.Nonnull;

/**
 * Essence Command - Check and manage essence
 */
public class EssenceCommand extends CommandBase {
    
    private final Hycremental plugin;
    
    public EssenceCommand(Hycremental plugin) {
        super("essence", "Check your essence and currency balance");
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
        
        context.sendMessage(Message.raw("§6§l=== Your Economy ==="));
        context.sendMessage(Message.raw("§7Essence: §e" + playerData.getEssence().toPlainString()));
        context.sendMessage(Message.raw("§7Gems: §b" + playerData.getGems()));
        context.sendMessage(Message.raw("§7Crystals: §d" + playerData.getCrystals()));
        context.sendMessage(Message.raw("§7Lifetime Essence: §6" + playerData.getLifetimeEssence().toPlainString()));
    }
}
