package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.AFKManager;
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
 * Command to toggle AFK status with optional custom message
 */
public class AFKCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final AFKManager afkManager;

    public AFKCommand(OrbisCore plugin, AFKManager afkManager) {
        this.plugin = plugin;
        this.afkManager = afkManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.error("This command can only be used by players."));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("orbiscore.afk")) {
            player.sendMessage(plugin.getMessageComponent("no-permission"));
            return true;
        }

        // Build custom AFK message if provided
        String customMessage = null;
        if (args.length > 0) {
            // Check permission for custom AFK messages
            if (!player.hasPermission("orbiscore.afk.message")) {
                player.sendMessage(MessageUtils.error("You don't have permission to set custom AFK messages!"));
                return true;
            }
            
            StringBuilder messageBuilder = new StringBuilder();
            for (String arg : args) {
                messageBuilder.append(arg).append(" ");
            }
            customMessage = messageBuilder.toString().trim();
            
            // Limit message length
            if (customMessage.length() > 50) {
                player.sendMessage(MessageUtils.error("AFK message too long! Max 50 characters."));
                return true;
            }
        }

        // Toggle AFK status
        boolean isNowAFK = afkManager.toggleAFK(player, customMessage);

        if (isNowAFK) {
            // Player is now AFK
            Component afkMsg;
            if (customMessage != null) {
                afkMsg = Component.text("💤 ", NamedTextColor.GRAY, TextDecoration.BOLD)
                    .append(Component.text(player.getName(), NamedTextColor.WHITE))
                    .append(Component.text(" is now AFK: ", NamedTextColor.GRAY))
                    .append(Component.text(customMessage, NamedTextColor.YELLOW, TextDecoration.ITALIC));
            } else {
                afkMsg = Component.text("💤 ", NamedTextColor.GRAY, TextDecoration.BOLD)
                    .append(Component.text(player.getName(), NamedTextColor.WHITE))
                    .append(Component.text(" is now AFK", NamedTextColor.GRAY));
            }
            
            // Broadcast to all players
            Bukkit.broadcast(afkMsg);
        } else {
            // Player is no longer AFK
            Component backMsg = Component.text("✅ ", NamedTextColor.GREEN, TextDecoration.BOLD)
                .append(Component.text(player.getName(), NamedTextColor.WHITE))
                .append(Component.text(" is no longer AFK", NamedTextColor.GREEN));
            
            // Broadcast to all players
            Bukkit.broadcast(backMsg);
            
            // Send any missed messages
            afkManager.sendMissedMessages(player);
        }

        return true;
    }
}