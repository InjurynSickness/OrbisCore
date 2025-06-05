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
            sender.sendMessage(MessageUtils.error("This command can only be used by players or specify a target."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtils.error("Usage: /speed [value] [player]"));
            return true;
        }

        // Parse speed value
        float speed;
        try {
            speed = Float.parseFloat(args[0]);
            if (speed < 0 || speed > 10) {
                sender.sendMessage(MessageUtils.error("Speed must be between 0 and 10!"));
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtils.error("Invalid speed value! Must be a number between 0 and 10."));
            return true;
        }

        // Convert to Minecraft's speed scale (0-1)
        float mcSpeed = speed / 10f;

        // Set speed for self or another player
        if (args.length == 1) {
            Player player = (Player) sender;

            // Check permission
            if (!player.hasPermission("orbiscore.speed")) {
                player.sendMessage(plugin.getMessageComponent("no-permission"));
                return true;
            }

            if (player.isFlying()) {
                player.setFlySpeed(mcSpeed);
            } else {
                player.setWalkSpeed(mcSpeed);
            }
            
            String speedType = player.isFlying() ? "fly" : "walk";
            Component successMsg = MessageUtils.success("Set your " + speedType + " speed to ")
                .append(Component.text(speed, NamedTextColor.WHITE));
            player.sendMessage(successMsg);
            
        } else {
            if (!sender.hasPermission("orbiscore.speed.others")) {
                sender.sendMessage(plugin.getMessageComponent("no-permission"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                Component errorMsg = MessageUtils.error("Player ")
                    .append(Component.text(args[1], NamedTextColor.WHITE))
                    .append(Component.text(" is not online!", NamedTextColor.RED));
                sender.sendMessage(errorMsg);
                return true;
            }

            if (target.isFlying()) {
                target.setFlySpeed(mcSpeed);
            } else {
                target.setWalkSpeed(mcSpeed);
            }

            String speedType = target.isFlying() ? "fly" : "walk";
            
            Component senderMsg = MessageUtils.success("Set ")
                .append(Component.text(target.getName(), NamedTextColor.WHITE))
                .append(Component.text("'s " + speedType + " speed to ", NamedTextColor.GREEN))
                .append(Component.text(speed, NamedTextColor.WHITE));
            
            Component targetMsg = MessageUtils.success("Your " + speedType + " speed was set to ")
                .append(Component.text(speed, NamedTextColor.WHITE))
                .append(Component.text(" by ", NamedTextColor.GREEN))
                .append(Component.text(sender.getName(), NamedTextColor.WHITE));

            sender.sendMessage(senderMsg);
            target.sendMessage(targetMsg);
        }

        return true;
    }
}