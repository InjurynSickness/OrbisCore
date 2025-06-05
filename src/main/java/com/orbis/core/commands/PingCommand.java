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
 * Command to check a player's ping
 */
public class PingCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public PingCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check own ping
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtils.error("This command can only be used by players."));
                return true;
            }

            Player player = (Player) sender;
            
            if (!player.hasPermission("orbiscore.ping")) {
                player.sendMessage(plugin.getMessageComponent("no-permission"));
                return true;
            }
            
            int ping = player.getPing();
            
            Component pingMsg = Component.text("📶 ", NamedTextColor.AQUA)
                .append(Component.text("Your current ping is: ", NamedTextColor.GRAY))
                .append(formatPing(ping));
            
            player.sendMessage(pingMsg);
            return true;
        }

        // Check another player's ping
        if (!sender.hasPermission("orbiscore.ping.others")) {
            sender.sendMessage(MessageUtils.error("You don't have permission to check other players' ping!"));
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

        int ping = target.getPing();
        
        Component pingMsg = Component.text("📶 ", NamedTextColor.AQUA)
            .append(Component.text(target.getName(), NamedTextColor.WHITE))
            .append(Component.text("'s ping is: ", NamedTextColor.GRAY))
            .append(formatPing(ping));
            
        sender.sendMessage(pingMsg);

        return true;
    }

    /**
     * Format ping with color coding based on quality
     */
    private Component formatPing(int ping) {
        NamedTextColor color;
        String quality;
        
        if (ping <= 50) {
            color = NamedTextColor.GREEN;
            quality = " (Excellent)";
        } else if (ping <= 100) {
            color = NamedTextColor.YELLOW;
            quality = " (Good)";
        } else if (ping <= 200) {
            color = NamedTextColor.GOLD;
            quality = " (Fair)";
        } else {
            color = NamedTextColor.RED;
            quality = " (Poor)";
        }
        
        return Component.text(ping + "ms", color, TextDecoration.BOLD)
            .append(Component.text(quality, NamedTextColor.GRAY, TextDecoration.ITALIC));
    }
}