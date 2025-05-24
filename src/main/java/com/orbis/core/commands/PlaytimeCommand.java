package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.util.MessageUtils;
import com.orbis.core.util.TimeFormatter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Command to check a player's playtime
 */
public class PlaytimeCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final PlayerDataManager playerDataManager;

    public PlaytimeCommand(OrbisCore plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Show own playtime
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtils.colorize("&cUsage: /playtime [player]"));
                return true;
            }

            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            long totalPlaytime = playerDataManager.getTotalPlaytime(uuid);

            player.sendMessage(MessageUtils.colorize("&aYour total playtime: &f" + TimeFormatter.formatTime(totalPlaytime)));
            return true;
        }

        // Check another player's playtime
        if (!sender.hasPermission("orbiscore.playtime.others")) {
            sender.sendMessage(MessageUtils.colorize("&cYou don't have permission to check other players' playtime!"));
            return true;
        }

        String targetName = args[0].toLowerCase();
        Player onlineTarget = Bukkit.getPlayer(targetName);

        if (onlineTarget != null) {
            // Target is online
            UUID uuid = onlineTarget.getUniqueId();
            long totalPlaytime = playerDataManager.getTotalPlaytime(uuid);

            sender.sendMessage(MessageUtils.colorize("&a" + onlineTarget.getName() + "'s total playtime: &f" + TimeFormatter.formatTime(totalPlaytime)));
            return true;
        }

        // Try to find offline player
        UUID uuid = playerDataManager.getUuidFromName(targetName);
        if (uuid == null) {
            sender.sendMessage(MessageUtils.colorize("&cPlayer " + args[0] + " has never been seen on this server!"));
            return true;
        }

        String storedName = playerDataManager.getPlayerData().getString("players." + uuid + ".name");
        long playtime = playerDataManager.getPlayerData().getLong("players." + uuid + ".playtime", 0);

        sender.sendMessage(MessageUtils.colorize("&a" + storedName + "'s total playtime: &f" + TimeFormatter.formatTime(playtime)));
        return true;
    }
}