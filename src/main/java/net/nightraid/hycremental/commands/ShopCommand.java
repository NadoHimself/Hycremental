package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.system.CommandContext;

public class ShopCommand extends CommandBase {
    
    public ShopCommand() {
        super("shop", "Opens the shop GUI");
    }
    
    @Override
    protected void run(CommandContext context) throws Exception {
        // TODO: Implement shop GUI logic
        // Get player: context.getSender()
        context.sendMessage("Shop command executed!");
    }
}
