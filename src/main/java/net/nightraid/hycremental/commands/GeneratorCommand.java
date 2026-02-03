package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.entity.player.HytaleServerPlayer;

public class GeneratorCommand extends AbstractPlayerCommand {
    
    public GeneratorCommand() {
        super("generator", "Manages resource generators");
    }
    
    @Override
    protected void execute(CommandContext context, HytaleServerPlayer player) {
        // TODO: Implement generator logic
        context.sendMessage("Generator command executed!");
    }
}
