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
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

/**
 * Command to check when a player was last online
 */
public class SeenCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final PlayerDataManager playerDataManager;

    public SeenCommand(OrbisCore plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageUtils.colorize("&cUsage: /seen [player]"));
            return true;
        }

        String playerName = args[0].toLowerCase();
        FileConfiguration playerData = playerDataManager.getPlayerData();

        // Check if player is currently online
        Player onlinePlayer = Bukkit.getPlayer(playerName);
        if (onlinePlayer != null) {
            UUID uuid = onlinePlayer.getUniqueId();

            sender.sendMessage(MessageUtils.colorize("&6=== &f" + onlinePlayer.getName() + " &6==="));
            sender.sendMessage(MessageUtils.colorize("&7Status: &aCurrently Online"));

            if (playerData.contains("players." + uuid + ".firstJoin")) {
                sender.sendMessage(MessageUtils.colorize("&7First joined: &f" + playerData.getString("players." + uuid + ".firstJoin")));
            }

            long totalPlaytime = playerDataManager.getTotalPlaytime(uuid);
            sender.sendMessage(MessageUtils.colorize("&7Playtime: &f" + TimeFormatter.formatTime(totalPlaytime)));

            if (sender.hasPermission("orbiscore.seen.ip") && playerData.contains("players." + uuid + ".ip")) {
                sender.sendMessage(MessageUtils.colorize("&7Last IP: &f" + playerData.getString("players." + uuid + ".ip")));
            }

            return true;
        }

        // Player is not online, check stored data
        UUID uuid = playerDataManager.getUuidFromName(playerName);
        if (uuid == null) {
            sender.sendMessage(MessageUtils.colorize("&cPlayer " + args[0] + " has never been seen on this server!"));
            return true;
        }

        String storedName = playerData.getString("players." + uuid + ".name");

        sender.sendMessage(MessageUtils.colorize("&6=== &f" + storedName + " &6==="));

        if (playerData.contains("players." + uuid + ".lastLogout")) {
            sender.sendMessage(MessageUtils.colorize("&7Last seen: &f" + playerData.getString("players." + uuid + ".lastLogout")));
        }

        if (playerData.contains("players." + uuid + ".firstJoin")) {
            sender.sendMessage(MessageUtils.colorize("&7First joined: &f" + playerData.getString("players." + uuid + ".firstJoin")));
        }

        if (playerData.contains("players." + uuid + ".playtime")) {
            long playtime = playerData.getLong("players." + uuid + ".playtime");
            sender.sendMessage(MessageUtils.colorize("&7Playtime: &f" + TimeFormatter.formatTime(playtime)));
        }

        if (sender.hasPermission("orbiscore.seen.ip") && playerData.contains("players." + uuid + ".ip")) {
            sender.sendMessage(MessageUtils.colorize("&7Last IP: &f" + playerData.getString("players." + uuid + ".ip")));
        }

        return true;
    }
}