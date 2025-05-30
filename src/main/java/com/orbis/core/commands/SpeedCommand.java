package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to set a player's movement speed
 */
public class SpeedCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public SpeedCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length < 2) {
            sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players or specify a target."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtils.colorize("&cUsage: /speed [value] [player]"));
            return true;
        }

        // Parse speed value
        float speed;
        try {
            speed = Float.parseFloat(args[0]);
            if (speed < 0 || speed > 10) {
                sender.sendMessage(MessageUtils.colorize("&cSpeed must be between 0 and 10!"));
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtils.colorize("&cInvalid speed value! Must be a number between 0 and 10."));
            return true;
        }

        // Convert to Minecraft's speed scale (0-1)
        float mcSpeed = speed / 10f;

        // Set speed for self or another player
        if (args.length == 1) {
            Player player = (Player) sender;

            // Check permission
            if (!player.hasPermission("orbiscore.speed")) {
                player.sendMessage(MessageUtils.colorize(plugin.getMessage("no-permission")));
                return true;
            }

            if (player.isFlying()) {
                player.setFlySpeed(mcSpeed);
            } else {
                player.setWalkSpeed(mcSpeed);
            }
            player.sendMessage(MessageUtils.colorize("&aSet your speed to " + speed));
        } else {
            if (!sender.hasPermission("orbiscore.speed.others")) {
                sender.sendMessage(MessageUtils.colorize(plugin.getMessage("no-permission")));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(MessageUtils.colorize(plugin.getMessage("player-not-online", "player", args[1])));
                return true;
            }

            if (target.isFlying()) {
                target.setFlySpeed(mcSpeed);
            } else {
                target.setWalkSpeed(mcSpeed);
            }

            sender.sendMessage(MessageUtils.colorize("&aSet " + target.getName() + "'s speed to " + speed));
            target.sendMessage(MessageUtils.colorize("&aYour speed was set to " + speed + " by " + sender.getName()));
        }

        return true;
    }
}