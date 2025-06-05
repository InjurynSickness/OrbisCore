package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.util.MessageUtils;
import com.orbis.core.util.TimeFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
                sender.sendMessage(MessageUtils.error("Usage: /playtime [player]"));
                return true;
            }

            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            if (!player.hasPermission("orbiscore.playtime")) {
                player.sendMessage(plugin.getMessageComponent("no-permission"));
                return true;
            }

            long totalPlaytime = playerDataManager.getTotalPlaytime(uuid);

            // Create formatted playtime display
            Component playtimeMsg = Component.text("⏱ ", NamedTextColor.GOLD)
                .append(Component.text("Your total playtime: ", NamedTextColor.GRAY))
                .append(Component.text(TimeFormatter.formatTime(totalPlaytime), NamedTextColor.GREEN, TextDecoration.BOLD));

            player.sendMessage(playtimeMsg);
            return true;
        }

        // Check another player's playtime
        if (!sender.hasPermission("orbiscore.playtime.others")) {
            sender.sendMessage(MessageUtils.error("You don't have permission to check other players' playtime!"));
            return true;
        }

        String targetName = args[0].toLowerCase();
        Player onlineTarget = Bukkit.getPlayer(targetName);

        if (onlineTarget != null) {
            // Target is online
            UUID uuid = onlineTarget.getUniqueId();
            long totalPlaytime = playerDataManager.getTotalPlaytime(uuid);

            Component header = Component.text("⏱ ", NamedTextColor.GOLD)
                .append(Component.text(onlineTarget.getName(), NamedTextColor.WHITE, TextDecoration.BOLD))
                .append(Component.text("'s Playtime", NamedTextColor.GRAY));

            Component playtimeMsg = MessageUtils.labeledInfo("Total playtime", TimeFormatter.formatTime(totalPlaytime));
            
            // Show current session if applicable
            long sessionTime = playerDataManager.getCurrentSessionTime(uuid);
            if (sessionTime > 0) {
                Component sessionMsg = MessageUtils.labeledInfo("Current session", TimeFormatter.formatTime(sessionTime));
                sender.sendMessage(header);
                sender.sendMessage(playtimeMsg);
                sender.sendMessage(sessionMsg);
            } else {
                sender.sendMessage(header);
                sender.sendMessage(playtimeMsg);
            }

            return true;
        }

        // Try to find offline player
        UUID uuid = playerDataManager.getUuidFromName(targetName);
        if (uuid == null) {
            Component errorMsg = MessageUtils.error("Player ")
                .append(Component.text(args[0], NamedTextColor.WHITE))
                .append(Component.text(" has never been seen on this server!", NamedTextColor.RED));
            sender.sendMessage(errorMsg);
            return true;
        }

        String storedName = playerDataManager.getPlayerData().getString("players." + uuid + ".name");
        long playtime = playerDataManager.getPlayerData().getLong("players." + uuid + ".playtime", 0);

        Component header = Component.text("⏱ ", NamedTextColor.GOLD)
            .append(Component.text(storedName, NamedTextColor.WHITE, TextDecoration.BOLD))
            .append(Component.text("'s Playtime", NamedTextColor.GRAY));

        Component playtimeMsg = MessageUtils.labeledInfo("Total playtime", TimeFormatter.formatTime(playtime));
        Component offlineNote = Component.text("📴 ", NamedTextColor.GRAY)
            .append(Component.text("(Player is offline)", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC));

        sender.sendMessage(header);
        sender.sendMessage(playtimeMsg);
        sender.sendMessage(offlineNote);

        return true;
    }
}