package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
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
            sender.sendMessage(MessageUtils.colorize("&cUsage: /sudo [player] [message/command]"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtils.colorize("&cPlayer " + args[0] + " is not online!"));
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
            sender.sendMessage(MessageUtils.colorize("&aMade " + target.getName() + " execute: " + message));
        } else {
            // It's a chat message
            target.chat(message);
            sender.sendMessage(MessageUtils.colorize("&aMade " + target.getName() + " say: " + message));
        }

        return true;
    }
}