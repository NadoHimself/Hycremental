package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.Command;
import com.hypixel.hytale.server.core.command.system.CommandSender;

import javax.annotation.Nonnull;

public class GeneratorCommand implements Command {

    @Nonnull
    @Override
    public String getName() {
        return "generator";
    }

    @Override
    public void execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        sender.sendMessage(Message.raw("§eGenerator Command ausgeführt"));
        // TODO: Implement generator logic
    }
}