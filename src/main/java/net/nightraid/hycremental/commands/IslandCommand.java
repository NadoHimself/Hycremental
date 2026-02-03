package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.system.CommandContext;

public class IslandCommand extends CommandBase {
    
    public IslandCommand() {
        super("island", "Manages your island");
    }
    
    @Override
    protected void executeSync(CommandContext context) {
        // TODO: Implement island management logic
        context.sendMessage("Island command executed!");
    }
}
