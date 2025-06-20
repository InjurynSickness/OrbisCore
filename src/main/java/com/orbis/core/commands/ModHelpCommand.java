package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Command for players to request help from moderators
 */
public class ModHelpCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final Map<UUID, Long> lastHelpRequest = new HashMap<>();
    private static final long COOLDOWN_TIME = 300000; // 5 minutes in milliseconds

    public ModHelpCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.error("This command can only be used by players."));
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (!player.hasPermission("orbiscore.modhelp")) {
            player.sendMessage(plugin.getMessageComponent("no-permission"));
            return true;
        }

        // Check cooldown
        if (lastHelpRequest.containsKey(uuid)) {
            long timeLeft = lastHelpRequest.get(uuid) - System.currentTimeMillis();
            if (timeLeft > 0) {
                String remainingTime = formatTime(timeLeft);
                
                Component cooldownMsg = Component.text("⏱ ", NamedTextColor.RED, TextDecoration.BOLD)
                    .append(Component.text("Please wait ", NamedTextColor.RED))
                    .append(Component.text(remainingTime, NamedTextColor.YELLOW))
                    .append(Component.text(" before requesting help again.", NamedTextColor.RED));
                
                player.sendMessage(cooldownMsg);
                return true;
            }
        }

        if (args.length == 0) {
            player.sendMessage(MessageUtils.error("Usage: /modhelp [reason for help]"));
            return true;
        }

        // Build help reason from args
        StringBuilder reasonBuilder = new StringBuilder();
        for (String arg : args) {
            reasonBuilder.append(arg).append(" ");
        }
        String reason = reasonBuilder.toString().trim();

        // Limit reason length
        if (reason.length() > 100) {
            player.sendMessage(MessageUtils.error("Help reason too long! Max 100 characters."));
            return true;
        }

        // Set cooldown
        lastHelpRequest.put(uuid, System.currentTimeMillis() + COOLDOWN_TIME);

        // Send to all moderators
        Component modMessage = Component.text("🚨 ", NamedTextColor.RED, TextDecoration.BOLD)
            .append(Component.text("MODERATOR HELP NEEDED", NamedTextColor.RED, TextDecoration.BOLD))
            .appendNewline()
            .append(Component.text("Player: ", NamedTextColor.GRAY))
            .append(Component.text(player.getName(), NamedTextColor.WHITE))
            .appendNewline()
            .append(Component.text("Reason: ", NamedTextColor.GRAY))
            .append(Component.text(reason, NamedTextColor.YELLOW))
            .appendNewline()
            .append(Component.text("Location: ", NamedTextColor.GRAY))
            .append(Component.text(String.format("X: %d, Y: %d, Z: %d (%s)", 
                player.getLocation().getBlockX(),
                player.getLocation().getBlockY(), 
                player.getLocation().getBlockZ(),
                player.getWorld().getName()), NamedTextColor.AQUA));

        // Count moderators who received the message
        int modCount = 0;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("orbiscore.modhelp.receive")) {
                onlinePlayer.sendMessage(modMessage);
                modCount++;
            }
        }

        // Send confirmation to player
        Component confirmMsg;
        if (modCount > 0) {
            confirmMsg = Component.text("✅ ", NamedTextColor.GREEN, TextDecoration.BOLD)
                .append(Component.text("Help request sent to ", NamedTextColor.GREEN))
                .append(Component.text(modCount, NamedTextColor.WHITE, TextDecoration.BOLD))
                .append(Component.text(" moderator" + (modCount == 1 ? "" : "s") + "!", NamedTextColor.GREEN))
                .appendNewline()
                .append(Component.text("Please wait for assistance.", NamedTextColor.GRAY, TextDecoration.ITALIC));
        } else {
            confirmMsg = Component.text("⚠ ", NamedTextColor.YELLOW, TextDecoration.BOLD)
                .append(Component.text("No moderators are currently online.", NamedTextColor.YELLOW))
                .appendNewline()
                .append(Component.text("Consider using ", NamedTextColor.GRAY))
                .append(Component.text("/support", NamedTextColor.AQUA))
                .append(Component.text(" for help.", NamedTextColor.GRAY));
        }

        player.sendMessage(confirmMsg);

        // Log to console
        plugin.getLogger().info(String.format("MODHELP: %s requested help: %s (sent to %d moderators)", 
            player.getName(), reason, modCount));

        return true;
    }

    /**
     * Format milliseconds to a readable time string
     */
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;

        if (minutes > 0) {
            seconds %= 60;
            if (seconds > 0) {
                return minutes + " minute" + (minutes != 1 ? "s" : "") + " " + seconds + " second" + (seconds != 1 ? "s" : "");
            }
            return minutes + " minute" + (minutes != 1 ? "s" : "");
        } else {
            return seconds + " second" + (seconds != 1 ? "s" : "");
        }
    }
}