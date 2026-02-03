package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.entity.player.HytaleServerPlayer;

public class EssenceCommand extends AbstractPlayerCommand {
    
    public EssenceCommand() {
        super("essence", "Manages essence currency");
    }
    
    @Override
    protected void execute(CommandContext context, HytaleServerPlayer player) {
        // TODO: Implement essence management logic
        context.sendMessage("Essence command executed!");
    }
}
