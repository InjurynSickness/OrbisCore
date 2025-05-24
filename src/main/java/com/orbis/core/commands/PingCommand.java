package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Command to check a player's ping
 */
public class PingCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public PingCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players."));
            return true;
        }

        Player player = (Player) sender;

        try {
            int ping = getPing(player);
            player.sendMessage(MessageUtils.colorize("&aYour current ping is: &f" + ping + "ms"));
        } catch (Exception e) {
            player.sendMessage(MessageUtils.colorize("&cCould not retrieve ping."));
            plugin.getLogger().log(Level.WARNING, "Could not get ping for " + player.getName(), e);
        }

        return true;
    }

    /**
     * Get a player's ping
     *
     * @param player The player
     * @return The ping in milliseconds
     * @throws Exception If there was an error getting the ping
     */
    private int getPing(Player player) throws Exception {
        // Different server versions have different ways to get ping
        try {
            // Try 1.17+ method
            Method getHandle = player.getClass().getMethod("getHandle");
            Object entityPlayer = getHandle.invoke(player);

            // Try to get the ping field
            try {
                Field pingField = entityPlayer.getClass().getField("ping");
                return pingField.getInt(entityPlayer);
            } catch (NoSuchFieldException e) {
                // For newer versions that may have changed the field name
                try {
                    // Try to get the latency() method which some versions use
                    Method latencyMethod = entityPlayer.getClass().getMethod("getPing");
                    return (int) latencyMethod.invoke(entityPlayer);
                } catch (NoSuchMethodException e2) {
                    // Last resort - try to use the Player's ping method directly (added in newer versions)
                    return player.getPing();
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error getting ping:", e);
            return -1;
        }
    }
}