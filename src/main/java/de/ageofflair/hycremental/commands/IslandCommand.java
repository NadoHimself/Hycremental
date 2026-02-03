package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.Command;
import com.hypixel.hytale.server.core.command.system.CommandSender;

import javax.annotation.Nonnull;

public class IslandCommand implements Command {

    @Nonnull
    @Override
    public String getName() {
        return "island";
    }

    @Override
    public void execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Message.raw("§aInsel-Menü wird geöffnet..."));
        } else {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "create":
                    sender.sendMessage(Message.raw("§aInsel wird erstellt..."));
                    break;
                case "delete":
                    sender.sendMessage(Message.raw("§cInsel wird gelöscht..."));
                    break;
                case "home":
                    sender.sendMessage(Message.raw("§aTeleportiere zur Insel..."));
                    break;
                default:
                    sender.sendMessage(Message.raw("§cUnbekannter Subcommand: " + subCommand));
            }
        }
        // TODO: Implement island logic
    }
}