package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Command to teleport players
 */
public class TeleportCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final PlayerDataManager playerDataManager;

    public TeleportCommand(OrbisCore plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length < 2) {
            sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players."));
            return true;
        }

        // No arguments - show usage
        if (args.length == 0) {
            sender.sendMessage(MessageUtils.colorize("&cUsage: /tp [player] or /tp [player1] [player2]"));
            return true;
        }

        // One argument - teleport self to player
        if (args.length == 1) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(MessageUtils.colorize(plugin.getMessage("player-not-online", "player", args[0])));
                return true;
            }

            // Check if trying to teleport to self
            if (target.equals(player)) {
                player.sendMessage(MessageUtils.colorize("&cYou cannot teleport to yourself!"));
                return true;
            }

            // Store current location for /back command
            playerDataManager.recordTeleportLocation(uuid, player.getLocation());

            player.teleport(target);
            player.sendMessage(MessageUtils.colorize("&aTeleported to " + target.getName()));

            return true;
        }

        // Two arguments - teleport player1 to player2
        if (!sender.hasPermission("orbiscore.tp.others")) {
            sender.sendMessage(MessageUtils.colorize(plugin.getMessage("no-permission")));
            return true;
        }

        Player player1 = Bukkit.getPlayer(args[0]);
        Player player2 = Bukkit.getPlayer(args[1]);

        if (player1 == null) {
            sender.sendMessage(MessageUtils.colorize(plugin.getMessage("player-not-online", "player", args[0])));
            return true;
        }

        if (player2 == null) {
            sender.sendMessage(MessageUtils.colorize(plugin.getMessage("player-not-online", "player", args[1])));
            return true;
        }

        if (player1.equals(player2)) {
            sender.sendMessage(MessageUtils.colorize("&cYou cannot teleport a player to themselves!"));
            return true;
        }

        // Store current location for /back command
        playerDataManager.recordTeleportLocation(player1.getUniqueId(), player1.getLocation());

        player1.teleport(player2);
        sender.sendMessage(MessageUtils.colorize("&aTeleported " + player1.getName() + " to " + player2.getName()));
        player1.sendMessage(MessageUtils.colorize("&aYou were teleported to " + player2.getName()));

        return true;
    }
}