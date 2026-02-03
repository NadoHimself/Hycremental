package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.CommandBase;
import com.hypixel.hytale.server.core.command.CommandContext;

import javax.annotation.Nonnull;

/**
 * Command to check and manage essence (currency).
 * Shows the player's current essence balance.
 */
public class EssenceCommand extends CommandBase {
    
    public EssenceCommand() {
        super("essence", "hycremental.commands.essence.description");
    }
    
    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // TODO: Implement essence balance display
        context.sendMessage(Message.raw("§aEssence command - Coming soon!"));
    }
}
