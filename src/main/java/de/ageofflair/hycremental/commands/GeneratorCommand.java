package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;

import javax.annotation.Nonnull;

public class GeneratorCommand {
    
    public void execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        // Generator-Management
        sender.sendMessage(Message.raw("§eGenerator Command ausgeführt"));
        
        // TODO: Implement generator logic
    }
}