package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Command to teleport to the highest safe location above the player
 */
public class TopCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final PlayerDataManager playerDataManager;

    public TopCommand(OrbisCore plugin, PlayerDataManager playerDataManager) {
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

        if (!player.hasPermission("orbiscore.top")) {
            player.sendMessage(plugin.getMessageComponent("no-permission"));
            return true;
        }

        Location currentLocation = player.getLocation();
        Location topLocation = findTopLocation(currentLocation);

        if (topLocation == null) {
            Component errorMsg = Component.text("❌ ", NamedTextColor.RED)
                    .append(Component.text("Could not find a safe location above you!", NamedTextColor.RED));
            player.sendMessage(errorMsg);
            return true;
        }

        // Store current location for /back command
        playerDataManager.recordTeleportLocation(uuid, currentLocation);

        // Teleport to top location
        player.teleport(topLocation);

        // Calculate distance traveled
        double distance = currentLocation.getY() - topLocation.getY();
        String distanceStr = String.format("%.1f", Math.abs(distance));

        Component successMsg = Component.text("⬆ ", NamedTextColor.GREEN, TextDecoration.BOLD)
                .append(Component.text("Teleported to the top! ", NamedTextColor.GREEN))
                .append(Component.text("(+" + distanceStr + " blocks)", NamedTextColor.GRAY, TextDecoration.ITALIC));

        player.sendMessage(successMsg);
        return true;
    }

    /**
     * Find the highest safe location above the player
     *
     * @param location The starting location
     * @return The top location, or null if not found
     */
    private Location findTopLocation(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return null;
        }

        int x = location.getBlockX();
        int z = location.getBlockZ();
        int worldMaxHeight = world.getMaxHeight();

        // Start from the top and work down to find the first safe location
        for (int y = worldMaxHeight - 1; y > location.getY(); y--) {
            Location checkLocation = new Location(world, x + 0.5, y, z + 0.5);

            // Check if this location is safe to teleport to
            if (isSafeLocation(checkLocation)) {
                // Set the player's current yaw and pitch
                checkLocation.setYaw(location.getYaw());
                checkLocation.setPitch(location.getPitch());
                return checkLocation;
            }
        }

        return null;
    }

    /**
     * Check if a location is safe for teleportation
     *
     * @param location The location to check
     * @return True if the location is safe
     */
    private boolean isSafeLocation(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return false;
        }

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        // Check if there's solid ground below (within 3 blocks)
        boolean hasGroundBelow = false;
        for (int checkY = y - 1; checkY >= Math.max(y - 3, world.getMinHeight()); checkY--) {
            Material blockBelow = world.getBlockAt(x, checkY, z).getType();
            if (blockBelow.isSolid() && blockBelow != Material.BARRIER) {
                hasGroundBelow = true;
                break;
            }
        }

        if (!hasGroundBelow) {
            return false;
        }

        // Check that the teleport location and the block above are not solid
        Material currentBlock = world.getBlockAt(x, y, z).getType();
        Material blockAbove = world.getBlockAt(x, y + 1, z).getType();

        // The location is safe if both the current block and block above are not solid
        // Also check for dangerous blocks
        return !currentBlock.isSolid() &&
                !blockAbove.isSolid() &&
                !isDangerous(currentBlock) &&
                !isDangerous(blockAbove) &&
                currentBlock != Material.BARRIER &&
                blockAbove != Material.BARRIER;
    }

    /**
     * Check if a block material is dangerous to stand in/on
     *
     * @param material The material to check
     * @return True if the material is dangerous
     */
    private boolean isDangerous(Material material) {
        return material == Material.LAVA ||
                material == Material.FIRE ||
                material == Material.SOUL_FIRE ||
                material == Material.CAMPFIRE ||
                material == Material.SOUL_CAMPFIRE ||
                material == Material.MAGMA_BLOCK ||
                material == Material.SWEET_BERRY_BUSH ||
                material == Material.WITHER_ROSE ||
                material == Material.CACTUS ||
                material == Material.POWDER_SNOW;
    }
}