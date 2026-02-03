package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.context.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import de.ageofflair.hycremental.Hycremental;

import javax.annotation.Nonnull;

/**
 * Shop Command - Open generator shop
 */
public class ShopCommand extends CommandBase {
    
    private final Hycremental plugin;
    
    public ShopCommand(Hycremental plugin) {
        super("shop", "Open the generator shop");
        this.plugin = plugin;
    }
    
    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        if (!context.isPlayer()) {
            context.sendMessage(Message.raw("§cThis command can only be used by players!"));
            return;
        }
        
        context.sendMessage(Message.raw("§6§l=== Generator Shop ==="));
        context.sendMessage(Message.raw("§7Shop UI coming soon!"));
    }
}
