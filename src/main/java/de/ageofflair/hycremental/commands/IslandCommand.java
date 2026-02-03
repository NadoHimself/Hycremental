package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.CommandBase;
import com.hypixel.hytale.server.core.command.CommandContext;

import javax.annotation.Nonnull;

/**
 * Command to manage player islands.
 * Allows teleportation and island management.
 */
public class IslandCommand extends CommandBase {
    
    public IslandCommand() {
        super("island", "hycremental.commands.island.description");
    }
    
    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // TODO: Implement island management
        context.sendMessage(Message.raw("§aIsland command - Coming soon!"));
    }
}
