package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to make another player say something or execute a command
 */
public class SudoCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public SudoCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(MessageUtils.error("Usage: /sudo [player] [message/command]"));
            return true;
        }

        if (!sender.hasPermission("orbiscore.sudo")) {
            sender.sendMessage(plugin.getMessageComponent("no-permission"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Component errorMsg = MessageUtils.error("Player ")
                .append(Component.text(args[0], NamedTextColor.WHITE))
                .append(Component.text(" is not online!", NamedTextColor.RED));
            sender.sendMessage(errorMsg);
            return true;
        }

        // Rebuild the message/command from args
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) messageBuilder.append(" ");
            messageBuilder.append(args[i]);
        }
        String message = messageBuilder.toString();

        if (message.startsWith("/")) {
            // It's a command
            String cmdText = message.substring(1);
            Bukkit.dispatchCommand(target, cmdText);
            
            Component successMsg = Component.text("⚡ ", NamedTextColor.YELLOW, TextDecoration.BOLD)
                .append(Component.text("Made ", NamedTextColor.GREEN))
                .append(Component.text(target.getName(), NamedTextColor.WHITE))
                .append(Component.text(" execute: ", NamedTextColor.GREEN))
                .append(Component.text(message, NamedTextColor.YELLOW));
            
            sender.sendMessage(successMsg);
        } else {
            // It's a chat message
            target.chat(message);
            
            Component successMsg = Component.text("💬 ", NamedTextColor.BLUE, TextDecoration.BOLD)
                .append(Component.text("Made ", NamedTextColor.GREEN))
                .append(Component.text(target.getName(), NamedTextColor.WHITE))
                .append(Component.text(" say: ", NamedTextColor.GREEN))
                .append(Component.text(message, NamedTextColor.WHITE));
            
            sender.sendMessage(successMsg);
        }

        return true;
    }
}