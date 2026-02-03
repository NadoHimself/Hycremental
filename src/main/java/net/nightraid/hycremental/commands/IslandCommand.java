package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.ParseResult;

public class IslandCommand extends AbstractCommand {
    
    public IslandCommand() {
        super("island");
    }
    
    @Override
    public void execute(CommandContext context, ParseResult parseResult) {
        // TODO: Implement island management logic
        // Access player via context.getSender() and cast to appropriate type
        context.sendMessage("Island command executed!");
    }
    
    @Override
    public String getDescription() {
        return "Manages your island";
    }
}
