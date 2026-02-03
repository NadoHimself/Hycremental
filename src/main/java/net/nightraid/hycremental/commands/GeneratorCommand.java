package net.nightraid.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.system.CommandContext;

public class GeneratorCommand extends CommandBase {
    
    public GeneratorCommand() {
        super("generator", "Manages resource generators");
    }
    
    @Override
    protected void run(CommandContext context) throws Exception {
        // TODO: Implement generator logic
        context.sendMessage("Generator command executed!");
    }
}
