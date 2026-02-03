package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;

import javax.annotation.Nonnull;

public class ShopCommand {
    
    public void execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        // Shop GUI öffnen oder Shop-Informationen anzeigen
        sender.sendMessage(Message.raw("§6Shop wird geöffnet..."));
        
        // TODO: Implement shop logic
    }
}