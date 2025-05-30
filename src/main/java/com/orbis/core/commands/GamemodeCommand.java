package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to change a player's gamemode
 */
public class GamemodeCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final GameMode gameMode;

    public GamemodeCommand(OrbisCore plugin, GameMode gameMode) {
        this.plugin = plugin;
        this.gameMode = gameMode;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players or specify a target."));
            return true;
        }

        // Set own gamemode
        if (args.length == 0) {
            Player player = (Player) sender;

            // Check permission based on gamemode
            String permission = "orbiscore.gamemode." + gameMode.name().toLowerCase();
            if (!player.hasPermission(permission)) {
                player.sendMessage(MessageUtils.colorize(plugin.getMessage("no-permission")));
                return true;
            }

            if (player.getGameMode() == gameMode) {
                player.sendMessage(MessageUtils.colorize("&cYou are already in " + gameMode.name().toLowerCase() + " mode!"));
                return true;
            }

            player.setGameMode(gameMode);
            player.sendMessage(MessageUtils.colorize("&aGamemode set to &f" + gameMode.name().toLowerCase() + "&a!"));
            return true;
        }

        // Set another player's gamemode
        if (!sender.hasPermission("orbiscore.gamemode.others")) {
            sender.sendMessage(MessageUtils.colorize(plugin.getMessage("no-permission")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtils.colorize(plugin.getMessage("player-not-online", "player", args[0])));
            return true;
        }

        if (target.getGameMode() == gameMode) {
            sender.sendMessage(MessageUtils.colorize("&c" + target.getName() + " is already in " + gameMode.name().toLowerCase() + " mode!"));
            return true;
        }

        target.setGameMode(gameMode);
        sender.sendMessage(MessageUtils.colorize("&aSet " + target.getName() + "'s gamemode to &f" + gameMode.name().toLowerCase() + "&a!"));
        target.sendMessage(MessageUtils.colorize("&aYour gamemode was set to &f" + gameMode.name().toLowerCase() + "&a by " + sender.getName() + "!"));

        return true;
    }
}