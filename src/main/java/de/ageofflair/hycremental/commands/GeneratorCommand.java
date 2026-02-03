package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.command.Command;
import com.hypixel.hytale.server.core.command.CommandSender;
import com.hypixel.hytale.server.core.universe.message.Message;

import javax.annotation.Nonnull;

public class GeneratorCommand implements Command {

    @Nonnull
    @Override
    public String getName() {
        return "generator";
    }

    @Override
    public void execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        sender.sendMessage(Message.raw("Generator command executed! (Not yet implemented)"));
    }
}