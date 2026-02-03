package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.Command;
import com.hypixel.hytale.server.core.command.CommandSender;
import com.hypixel.hytale.server.core.Message;

import javax.annotation.Nonnull;

public class IslandCommand implements Command {

    @Nonnull
    @Override
    public String getName() {
        return "island";
    }

    @Override
    public void execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        sender.sendMessage(Message.raw("Island command executed! (Not yet implemented)"));
    }
}