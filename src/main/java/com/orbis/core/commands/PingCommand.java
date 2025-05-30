package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
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
                sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players."));
                return true;
            }

            Player player = (Player) sender;
            int ping = player.getPing(); // Direct method available in Paper 1.21.1
            player.sendMessage(MessageUtils.colorize("&aYour current ping is: &f" + ping + "ms"));
            return true;
        }

        // Check another player's ping
        if (!sender.hasPermission("orbiscore.ping.others")) {
            sender.sendMessage(MessageUtils.colorize("&cYou don't have permission to check other players' ping!"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtils.colorize("&cPlayer " + args[0] + " is not online!"));
            return true;
        }

        int ping = target.getPing();
        sender.sendMessage(MessageUtils.colorize("&a" + target.getName() + "'s ping is: &f" + ping + "ms"));

        return true;
    }
}