package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
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
 * Command to teleport to previous location
 */
public class BackCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final PlayerDataManager playerDataManager;

    public BackCommand(OrbisCore plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.error("This command can only be used by players."));
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (!player.hasPermission("orbiscore.back")) {
            player.sendMessage(plugin.getMessageComponent("no-permission"));
            return true;
        }

        Location currentLocation = player.getLocation();

        // Check for teleport location first (more recent)
        Location teleportLoc = playerDataManager.getTeleportLocation(uuid);
        if (teleportLoc != null && isValidLocation(teleportLoc)) {
            player.teleport(teleportLoc);

            // Store current location for next /back
            playerDataManager.updateTeleportLocation(uuid, currentLocation);

            Component successMsg = Component.text("↩ ", NamedTextColor.GREEN, TextDecoration.BOLD)
                .append(Component.text("Teleported to your previous location.", NamedTextColor.GREEN));
            player.sendMessage(successMsg);
            return true;
        }

        // Check for death location if no teleport location
        Location deathLoc = playerDataManager.getDeathLocation(uuid);
        if (deathLoc != null && isValidLocation(deathLoc)) {
            player.teleport(deathLoc);

            // Store current location for next /back
            playerDataManager.updateTeleportLocation(uuid, currentLocation);

            Component successMsg = Component.text("💀 ", NamedTextColor.DARK_RED)
                .append(Component.text("Teleported to your last death location.", NamedTextColor.GREEN));
            player.sendMessage(successMsg);
            return true;
        }

        // No location found
        Component errorMsg = Component.text("❌ ", NamedTextColor.RED)
            .append(Component.text("You don't have a location to teleport back to!", NamedTextColor.RED));
        player.sendMessage(errorMsg);
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