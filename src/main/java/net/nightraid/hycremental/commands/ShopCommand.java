package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.system.CommandContext;

public class ShopCommand extends CommandBase {
    
    public ShopCommand() {
        super("shop", "Opens the shop GUI");
    }
    
    @Override
    protected void executeSync(CommandContext context) {
        // TODO: Implement shop GUI logic
        // Get player via context.getSender()
        context.sendMessage("Shop command executed!");
    }
}
