package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.context.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.Message;
import de.ageofflair.hycremental.Hycremental;

import javax.annotation.Nonnull;

/**
 * Generator Command - Manage generators
 */
public class GeneratorCommand extends CommandBase {
    
    private final Hycremental plugin;
    
    public GeneratorCommand(Hycremental plugin) {
        super("gen", "Manage your generators");
        this.plugin = plugin;
    }
    
    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        if (!context.isPlayer()) {
            context.sendMessage(Message.raw("§cThis command can only be used by players!"));
            return;
        }
        
        context.sendMessage(Message.raw("§6§l=== Generator Management ==="));
        context.sendMessage(Message.raw("§7Generator commands coming soon!"));
    }
}
