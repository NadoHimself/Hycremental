package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;

import javax.annotation.Nonnull;

public class EssenceCommand {
    
    public void execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        // Essence-Währung verwalten
        if (args.length == 0) {
            sender.sendMessage(Message.raw("§bEssence-Info wird angezeigt..."));
        } else if (args.length >= 2) {
            String action = args[0].toLowerCase();
            try {
                int amount = Integer.parseInt(args[1]);
                switch (action) {
                    case "add":
                        sender.sendMessage(Message.raw("§a+" + amount + " Essence hinzugefügt"));
                        break;
                    case "remove":
                        sender.sendMessage(Message.raw("§c-" + amount + " Essence entfernt"));
                        break;
                    case "set":
                        sender.sendMessage(Message.raw("§eEssence auf " + amount + " gesetzt"));
                        break;
                    default:
                        sender.sendMessage(Message.raw("§cUnbekannte Aktion: " + action));
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(Message.raw("§cUngültige Zahl: " + args[1]));
            }
        }
        
        // TODO: Implement essence logic
    }
}