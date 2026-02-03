package de.ageofflair.hycremental.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.Command;
import com.hypixel.hytale.server.core.command.system.CommandSender;

import javax.annotation.Nonnull;

public class ShopCommand implements Command {

    @Nonnull
    @Override
    public String getName() {
        return "shop";
    }

    @Override
    public void execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        sender.sendMessage(Message.raw("§6Shop wird geöffnet..."));
        // TODO: Implement shop logic
    }
}