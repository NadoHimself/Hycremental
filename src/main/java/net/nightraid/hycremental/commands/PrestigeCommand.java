package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.ParseResult;

public class PrestigeCommand extends AbstractCommand {
    
    public PrestigeCommand() {
        super("prestige");
    }
    
    @Override
    public void execute(CommandContext context, ParseResult parseResult) {
        // TODO: Implement prestige system logic
        // Access player via context.getSender() and cast to appropriate type
        context.sendMessage("Prestige command executed!");
    }
    
    @Override
    public String getDescription() {
        return "Prestige to gain bonuses";
    }
}
