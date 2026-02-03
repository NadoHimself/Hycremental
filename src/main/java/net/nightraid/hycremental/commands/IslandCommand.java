package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.system.CommandContext;

public class IslandCommand extends CommandBase {
    
    public IslandCommand() {
        super("island", "Manages your island");
    }
    
    @Override
    protected void run(CommandContext context) throws Exception {
        // TODO: Implement island management logic
        context.sendMessage("Island command executed!");
    }
}
