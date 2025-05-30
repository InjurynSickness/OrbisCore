package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.util.LocationConverter;
import com.orbis.core.util.MessageUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Command to teleport to a player's last known logout location
 */
public class TpOfflineCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final PlayerDataManager playerDataManager;

    public TpOfflineCommand(OrbisCore plugin, PlayerDataManager playerDataManager) {
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
            sender.sendMessage(MessageUtils.colorize("&cUsage: /tpoffline [player]"));
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        String targetName = args[0].toLowerCase();

        // Try to find UUID from username
        UUID targetUuid = playerDataManager.getUuidFromName(targetName);
        if (targetUuid == null) {
            player.sendMessage(MessageUtils.colorize(plugin.getMessage("player-not-found", "player", args[0])));
            return true;
        }

        // Check if we have a logout location
        String locString = playerDataManager.getPlayerData().getString("players." + targetUuid + ".lastLogoutLocation");
        if (locString == null) {
            player.sendMessage(MessageUtils.colorize("&cNo stored logout location for " + args[0] + "!"));
            return true;
        }

        // Get location and validate it
        Location location;
        try {
            location = LocationConverter.stringToLocation(locString);
        } catch (Exception e) {
            player.sendMessage(MessageUtils.colorize("&cInvalid location data for " + args[0] + "!"));
            plugin.getLogger().warning("Invalid location string for " + args[0] + ": " + locString);
            return true;
        }

        if (!isValidLocation(location)) {
            player.sendMessage(MessageUtils.colorize("&cUnsafe or invalid location for " + args[0] + "!"));
            return true;
        }

        // Store current location for back command
        playerDataManager.recordTeleportLocation(uuid, player.getLocation());

        player.teleport(location);
        player.sendMessage(MessageUtils.colorize("&aTeleported to " + args[0] + "'s last logout location"));

        return true;
    }

    /**
     * Check if a location is valid and safe
     *
     * @param location The location to check
     * @return True if the location is valid
     */
    private boolean isValidLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }

        // Check if world exists in server
        if (org.bukkit.Bukkit.getWorld(location.getWorld().getName()) == null) {
            return false;
        }

        // Basic safety check - ensure location is not in void
        return location.getY() > -64;
    }
}