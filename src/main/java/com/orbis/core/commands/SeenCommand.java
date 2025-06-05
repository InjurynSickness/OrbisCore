package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.util.MessageUtils;
import com.orbis.core.util.TimeFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            sender.sendMessage(MessageUtils.error("Usage: /seen [player]"));
            return true;
        }

        String playerName = args[0].toLowerCase();
        FileConfiguration playerData = playerDataManager.getPlayerData();

        // Check if player is currently online
        Player onlinePlayer = Bukkit.getPlayer(playerName);
        if (onlinePlayer != null) {
            UUID uuid = onlinePlayer.getUniqueId();

            // Send header
            sender.sendMessage(MessageUtils.playerHeader(onlinePlayer.getName()));
            
            // Status
            sender.sendMessage(MessageUtils.labeledInfo("Status", 
                Component.text("Currently Online", NamedTextColor.GREEN).content()));

            // First join
            if (playerData.contains("players." + uuid + ".firstJoin")) {
                sender.sendMessage(MessageUtils.labeledInfo("First joined", 
                    playerData.getString("players." + uuid + ".firstJoin")));
            }

            // Playtime
            long totalPlaytime = playerDataManager.getTotalPlaytime(uuid);
            sender.sendMessage(MessageUtils.labeledInfo("Playtime", 
                TimeFormatter.formatTime(totalPlaytime)));

            // IP address (if permission)
            if (sender.hasPermission("orbiscore.seen.ip") && playerData.contains("players." + uuid + ".ip")) {
                sender.sendMessage(MessageUtils.labeledInfo("Last IP", 
                    playerData.getString("players." + uuid + ".ip")));
            }

            return true;
        }

        // Player is not online, check stored data
        UUID uuid = playerDataManager.getUuidFromName(playerName);
        if (uuid == null) {
            Component errorMsg = MessageUtils.error("Player ")
                .append(Component.text(args[0], NamedTextColor.WHITE))
                .append(Component.text(" has never been seen on this server!", NamedTextColor.RED));
            sender.sendMessage(errorMsg);
            return true;
        }

        String storedName = playerData.getString("players." + uuid + ".name");

        // Send header
        sender.sendMessage(MessageUtils.playerHeader(storedName));

        // Last seen
        if (playerData.contains("players." + uuid + ".lastLogout")) {
            sender.sendMessage(MessageUtils.labeledInfo("Last seen", 
                playerData.getString("players." + uuid + ".lastLogout")));
        }

        // First join
        if (playerData.contains("players." + uuid + ".firstJoin")) {
            sender.sendMessage(MessageUtils.labeledInfo("First joined", 
                playerData.getString("players." + uuid + ".firstJoin")));
        }

        // Playtime
        if (playerData.contains("players." + uuid + ".playtime")) {
            long playtime = playerData.getLong("players." + uuid + ".playtime");
            sender.sendMessage(MessageUtils.labeledInfo("Playtime", 
                TimeFormatter.formatTime(playtime)));
        }

        // IP address (if permission)
        if (sender.hasPermission("orbiscore.seen.ip") && playerData.contains("players." + uuid + ".ip")) {
            sender.sendMessage(MessageUtils.labeledInfo("Last IP", 
                playerData.getString("players." + uuid + ".ip")));
        }

        return true;
    }
}