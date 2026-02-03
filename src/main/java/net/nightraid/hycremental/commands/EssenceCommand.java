package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.system.CommandContext;

public class EssenceCommand extends CommandBase {
    
    public EssenceCommand() {
        super("essence", "Manages essence currency");
    }
    
    @Override
    protected void run(CommandContext context) throws Exception {
        // TODO: Implement essence management logic
        context.sendMessage("Essence command executed!");
    }
}
