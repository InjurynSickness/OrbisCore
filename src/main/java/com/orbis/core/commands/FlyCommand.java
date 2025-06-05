package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
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
            sender.sendMessage(MessageUtils.error("This command can only be used by players."));
            return true;
        }

        // No arguments - toggle flight for self
        if (args.length == 0) {
            Player player = (Player) sender;

            // Check permission
            if (!player.hasPermission("orbiscore.fly")) {
                player.sendMessage(MessageUtils.colorize(plugin.getMessage("no-permission")));
                return true;
            }

            boolean canFly = !player.getAllowFlight();
            player.setAllowFlight(canFly);

            Component message;
            if (canFly) {
                message = MessageUtils.success("Flight mode enabled.");
            } else {
                message = MessageUtils.error("Flight mode disabled.");
            }
            player.sendMessage(message);

            return true;
        }

        // With argument - toggle flight for another player
        if (!sender.hasPermission("orbiscore.fly.others")) {
            sender.sendMessage(MessageUtils.colorize(plugin.getMessage("no-permission")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Component errorMsg = MessageUtils.error("Player ")
                .append(Component.text(args[0]))
                .append(Component.text(" is not online!"));
            sender.sendMessage(errorMsg);
            return true;
        }

        boolean canFly = !target.getAllowFlight();
        target.setAllowFlight(canFly);

        Component senderMsg, targetMsg;
        if (canFly) {
            senderMsg = MessageUtils.success("Enabled flight mode for " + target.getName() + ".");
            targetMsg = MessageUtils.success("Flight mode enabled.");
        } else {
            senderMsg = MessageUtils.error("Disabled flight mode for " + target.getName() + ".");
            targetMsg = MessageUtils.error("Flight mode disabled.");
        }

        sender.sendMessage(senderMsg);
        target.sendMessage(targetMsg);

        return true;
    }
}