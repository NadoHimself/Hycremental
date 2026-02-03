package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.ParseResult;

public class ShopCommand extends AbstractCommand {
    
    public ShopCommand() {
        super("shop");
    }
    
    @Override
    public void execute(CommandContext context, ParseResult parseResult) {
        // TODO: Implement shop GUI logic
        // Access player via context.getSender() and cast to appropriate type
        context.sendMessage("Shop command executed!");
    }
    
    @Override
    public String getDescription() {
        return "Opens the shop GUI";
    }
}
