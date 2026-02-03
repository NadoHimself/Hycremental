package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.CommandBase;
import com.hypixel.hytale.server.core.command.CommandContext;

import javax.annotation.Nonnull;

/**
 * Command to open the shop UI for players.
 * Allows players to spend essence on various upgrades and items.
 */
public class ShopCommand extends CommandBase {
    
    public ShopCommand() {
        super("shop", "hycremental.commands.shop.description");
    }
    
    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // TODO: Implement shop UI opening
        context.sendMessage(Message.raw("§aShop command - Coming soon!"));
    }
}
