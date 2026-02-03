package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.CommandBase;
import com.hypixel.hytale.server.core.command.CommandContext;

import javax.annotation.Nonnull;

/**
 * Command to prestige and reset progress for bonuses.
 * Allows players to start over with permanent multipliers.
 */
public class PrestigeCommand extends CommandBase {
    
    public PrestigeCommand() {
        super("prestige", "hycremental.commands.prestige.description");
    }
    
    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // TODO: Implement prestige system
        context.sendMessage(Message.raw("§aPrestige command - Coming soon!"));
    }
}
