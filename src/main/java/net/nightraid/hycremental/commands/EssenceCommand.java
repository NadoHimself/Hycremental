package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.ParseResult;

public class EssenceCommand extends AbstractCommand {
    
    public EssenceCommand() {
        super("essence");
    }
    
    @Override
    public void execute(CommandContext context, ParseResult parseResult) {
        // TODO: Implement essence management logic
        // Access player via context.getSender() and cast to appropriate type
        context.sendMessage("Essence command executed!");
    }
    
    @Override
    public String getDescription() {
        return "Manages essence currency";
    }
}
