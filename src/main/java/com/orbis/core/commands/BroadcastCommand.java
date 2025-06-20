package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            sender.sendMessage(MessageUtils.error("Usage: /broadcast [message]"));
            return true;
        }

        // Build message from args
        StringBuilder messageBuilder = new StringBuilder();
        for (String arg : args) {
            messageBuilder.append(arg).append(" ");
        }
        String message = messageBuilder.toString().trim();

        // Create broadcast message with player name format: [playername]: message
        Component broadcastMessage;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            broadcastMessage = Component.text("[", NamedTextColor.GRAY)
                .append(Component.text(player.getName(), NamedTextColor.YELLOW))
                .append(Component.text("]: ", NamedTextColor.GRAY))
                .append(Component.text(message, NamedTextColor.WHITE));
        } else {
            // Console broadcast
            broadcastMessage = Component.text("[", NamedTextColor.GRAY)
                .append(Component.text("Console", NamedTextColor.YELLOW))
                .append(Component.text("]: ", NamedTextColor.GRAY))
                .append(Component.text(message, NamedTextColor.WHITE));
        }

        // Broadcast the message
        Bukkit.broadcast(broadcastMessage);
        return true;
    }
}