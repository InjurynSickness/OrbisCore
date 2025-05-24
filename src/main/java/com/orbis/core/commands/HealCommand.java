package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to heal a player
 */
public class HealCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public HealCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players."));
            return true;
        }

        // Heal self
        if (args.length == 0) {
            Player player = (Player) sender;
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.sendMessage(MessageUtils.colorize("&aYou have been healed!"));
            return true;
        }

        // Heal another player
        if (!sender.hasPermission("orbiscore.heal.others")) {
            sender.sendMessage(MessageUtils.colorize("&cYou don't have permission to heal other players!"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtils.colorize("&cPlayer " + args[0] + " is not online!"));
            return true;
        }

        target.setHealth(target.getMaxHealth());
        target.setFoodLevel(20);
        target.setSaturation(20);

        sender.sendMessage(MessageUtils.colorize("&aHealed " + target.getName() + "!"));
        target.sendMessage(MessageUtils.colorize("&aYou have been healed by " + sender.getName() + "!"));

        return true;
    }
}