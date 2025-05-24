package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command to broadcast a message to all players
 */
public class BroadcastCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public BroadcastCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageUtils.colorize("&cUsage: /broadcast [message]"));
            return true;
        }

        // Build message from args
        StringBuilder messageBuilder = new StringBuilder();
        for (String arg : args) {
            messageBuilder.append(arg).append(" ");
        }
        String message = messageBuilder.toString().trim();

        // Broadcast the message
        Bukkit.broadcastMessage(MessageUtils.colorize("&8[&4&lBROADCAST&8] &f" + message));
        return true;
    }
}