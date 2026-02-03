package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.entity.player.HytaleServerPlayer;

public class ShopCommand extends AbstractPlayerCommand {
    
    public ShopCommand() {
        super("shop", "Opens the shop GUI");
    }
    
    @Override
    protected void execute(CommandContext context, HytaleServerPlayer player) {
        // TODO: Implement shop GUI logic
        context.sendMessage("Shop command executed!");
    }
}
