package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.Command;
import com.hypixel.hytale.server.core.command.CommandSender;
import com.hypixel.hytale.server.core.Message;

import javax.annotation.Nonnull;

public class EssenceCommand implements Command {

    @Nonnull
    @Override
    public String getName() {
        return "essence";
    }

    @Override
    public void execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        sender.sendMessage(Message.raw("Essence command executed! (Not yet implemented)"));
    }
}