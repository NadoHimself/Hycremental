package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.entity.player.HytaleServerPlayer;

public class IslandCommand extends AbstractPlayerCommand {
    
    public IslandCommand() {
        super("island", "Manages your island");
    }
    
    @Override
    protected void execute(CommandContext context, HytaleServerPlayer player) {
        // TODO: Implement island management logic
        context.sendMessage("Island command executed!");
    }
}
