package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to toggle flight mode
 */
public class FlyCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public FlyCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players."));
            return true;
        }

        // No arguments - toggle flight for self
        if (args.length == 0) {
            Player player = (Player) sender;

            boolean canFly = !player.getAllowFlight();
            player.setAllowFlight(canFly);

            if (canFly) {
                player.sendMessage(MessageUtils.colorize("&aFlight mode enabled."));
            } else {
                player.sendMessage(MessageUtils.colorize("&cFlight mode disabled."));
            }

            return true;
        }

        // With argument - toggle flight for another player
        if (!sender.hasPermission("orbiscore.fly.others")) {
            sender.sendMessage(MessageUtils.colorize("&cYou don't have permission to toggle flight for other players!"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtils.colorize("&cPlayer " + args[0] + " is not online!"));
            return true;
        }

        boolean canFly = !target.getAllowFlight();
        target.setAllowFlight(canFly);

        if (canFly) {
            sender.sendMessage(MessageUtils.colorize("&aEnabled flight mode for " + target.getName() + "."));
            target.sendMessage(MessageUtils.colorize("&aYour flight mode has been enabled."));
        } else {
            sender.sendMessage(MessageUtils.colorize("&cDisabled flight mode for " + target.getName() + "."));
            target.sendMessage(MessageUtils.colorize("&cYour flight mode has been disabled."));
        }

        return true;
    }
}