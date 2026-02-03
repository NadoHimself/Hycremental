package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.CommandBase;
import com.hypixel.hytale.server.core.command.CommandContext;

import javax.annotation.Nonnull;

/**
 * Command to manage generators.
 * Allows players to view and upgrade their generators.
 */
public class GeneratorCommand extends CommandBase {
    
    public GeneratorCommand() {
        super("generator", "hycremental.commands.generator.description");
    }
    
    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // TODO: Implement generator management
        context.sendMessage(Message.raw("§aGenerator command - Coming soon!"));
    }
}
