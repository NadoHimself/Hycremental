package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.entity.player.HytaleServerPlayer;

public class PrestigeCommand extends AbstractPlayerCommand {
    
    public PrestigeCommand() {
        super("prestige", "Prestige to gain bonuses");
    }
    
    @Override
    protected void execute(CommandContext context, HytaleServerPlayer player) {
        // TODO: Implement prestige system logic
        context.sendMessage("Prestige command executed!");
    }
}
