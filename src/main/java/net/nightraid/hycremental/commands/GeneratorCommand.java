package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.ParseResult;

public class GeneratorCommand extends AbstractCommand {
    
    public GeneratorCommand() {
        super("generator");
    }
    
    @Override
    public void execute(CommandContext context, ParseResult parseResult) {
        // TODO: Implement generator logic
        // Access player via context.getSender() and cast to appropriate type
        context.sendMessage("Generator command executed!");
    }
    
    @Override
    public String getDescription() {
        return "Manages resource generators";
    }
}
