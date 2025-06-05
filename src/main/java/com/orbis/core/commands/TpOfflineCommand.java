package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.util.LocationConverter;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
            sender.sendMessage(MessageUtils.error("This command can only be used by players."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtils.error("Usage: /tpoffline [player]"));
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (!player.hasPermission("orbiscore.tpoffline")) {
            player.sendMessage(plugin.getMessageComponent("no-permission"));
            return true;
        }

        String targetName = args[0].toLowerCase();

        // Try to find UUID from username
        UUID targetUuid = playerDataManager.getUuidFromName(targetName);
        if (targetUuid == null) {
            Component errorMsg = MessageUtils.error("Player ")
                .append(Component.text(args[0], NamedTextColor.WHITE))
                .append(Component.text(" has never been seen on this server!", NamedTextColor.RED));
            player.sendMessage(errorMsg);
            return true;
        }

        // Check if we have a logout location
        String locString = playerDataManager.getPlayerData().getString("players." + targetUuid + ".lastLogoutLocation");
        if (locString == null) {
            Component errorMsg = Component.text("❌ ", NamedTextColor.RED)
                .append(Component.text("No stored logout location for ", NamedTextColor.RED))
                .append(Component.text(args[0], NamedTextColor.WHITE))
                .append(Component.text("!", NamedTextColor.RED));
            player.sendMessage(errorMsg);
            return true;
        }

        // Get location and validate it
        Location location;
        try {
            location = LocationConverter.stringToLocation(locString);
        } catch (Exception e) {
            Component errorMsg = Component.text("⚠ ", NamedTextColor.YELLOW)
                .append(Component.text("Invalid location data for ", NamedTextColor.RED))
                .append(Component.text(args[0], NamedTextColor.WHITE))
                .append(Component.text("!", NamedTextColor.RED));
            player.sendMessage(errorMsg);
            plugin.getLogger().warning("Invalid location string for " + args[0] + ": " + locString);
            return true;
        }

        if (!isValidLocation(location)) {
            Component errorMsg = Component.text("🚫 ", NamedTextColor.RED)
                .append(Component.text("Unsafe or invalid location for ", NamedTextColor.RED))
                .append(Component.text(args[0], NamedTextColor.WHITE))
                .append(Component.text("!", NamedTextColor.RED));
            player.sendMessage(errorMsg);
            return true;
        }

        // Store current location for back command
        playerDataManager.recordTeleportLocation(uuid, player.getLocation());

        player.teleport(location);
        
        // Show coordinates in the success message
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        String worldName = location.getWorld().getName();
        
        Component successMsg = Component.text("📍 ", NamedTextColor.GREEN)
            .append(Component.text("Teleported to ", NamedTextColor.GREEN))
            .append(Component.text(args[0], NamedTextColor.WHITE, TextDecoration.BOLD))
            .append(Component.text("'s last logout location", NamedTextColor.GREEN));
            
        Component locationInfo = Component.text("🗺 ", NamedTextColor.GRAY)
            .append(Component.text("Location: ", NamedTextColor.GRAY))
            .append(Component.text("X: " + x + ", Y: " + y + ", Z: " + z, NamedTextColor.WHITE))
            .append(Component.text(" in ", NamedTextColor.GRAY))
            .append(Component.text(worldName, NamedTextColor.YELLOW));

        player.sendMessage(successMsg);
        player.sendMessage(locationInfo);

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