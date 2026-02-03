package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;

import javax.annotation.Nonnull;

public class PrestigeCommand {
    
    public void execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        // Prestige-System
        sender.sendMessage(Message.raw("§dPrestige Command ausgeführt"));
        
        // TODO: Implement prestige logic
    }
}