package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to teleport a player to the command sender
 */
public class TpHereCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final PlayerDataManager playerDataManager;

    public TpHereCommand(OrbisCore plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtils.colorize("&cUsage: /tphere [player]"));
            return true;
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(MessageUtils.colorize("&cPlayer " + args[0] + " is not online!"));
            return true;
        }

        // Store current location for /back command
        playerDataManager.recordTeleportLocation(target.getUniqueId(), target.getLocation());

        // Teleport player to sender
        target.teleport(player);

        player.sendMessage(MessageUtils.colorize("&aTeleported " + target.getName() + " to your location."));
        target.sendMessage(MessageUtils.colorize("&aYou were teleported to " + player.getName() + "."));

        return true;
    }
}