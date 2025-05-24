package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.util.MessageUtils;
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
            sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players."));
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        // Check for teleport location first (more recent)
        Location teleportLoc = playerDataManager.getTeleportLocation(uuid);
        if (teleportLoc != null) {
            Location oldLoc = player.getLocation();
            player.teleport(teleportLoc);

            // Store current location for next /back
            playerDataManager.updateTeleportLocation(uuid, oldLoc);

            player.sendMessage(MessageUtils.colorize("&aTeleported to your previous location."));
            return true;
        }

        // Check for death location if no teleport location
        Location deathLoc = playerDataManager.getDeathLocation(uuid);
        if (deathLoc != null) {
            Location oldLoc = player.getLocation();
            player.teleport(deathLoc);

            // Store current location for next /back
            playerDataManager.updateTeleportLocation(uuid, oldLoc);

            player.sendMessage(MessageUtils.colorize("&aTeleported to your last death location."));
            return true;
        }

        // No location found
        player.sendMessage(MessageUtils.colorize("&cYou don't have a location to teleport back to!"));
        return true;
    }
}