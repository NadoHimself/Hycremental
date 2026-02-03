package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.system.CommandContext;

public class PrestigeCommand extends CommandBase {
    
    public PrestigeCommand() {
        super("prestige", "Prestige to gain bonuses");
    }
    
    @Override
    protected void executeSync(CommandContext context) {
        // TODO: Implement prestige system logic
        context.sendMessage("Prestige command executed!");
    }
}
