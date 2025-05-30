package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Command to toggle godmode (invincibility) for players
 */
public class GodmodeCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private static final Set<UUID> godmodePlayers = new HashSet<>();

    public GodmodeCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players."));
            return true;
        }

        // No arguments - toggle godmode for self
        if (args.length == 0) {
            Player player = (Player) sender;

            // Check permission
            if (!player.hasPermission("orbiscore.godmode")) {
                player.sendMessage(MessageUtils.colorize(plugin.getMessage("no-permission")));
                return true;
            }

            toggleGodmode(player);
            return true;
        }

        // With argument - toggle godmode for another player
        if (!sender.hasPermission("orbiscore.godmode.others")) {
            sender.sendMessage(MessageUtils.colorize(plugin.getMessage("no-permission")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtils.colorize(plugin.getMessage("player-not-online", "player", args[0])));
            return true;
        }

        toggleGodmode(target);
        
        // Notify the command sender if they're not the target
        if (!sender.equals(target)) {
            boolean isInGodmode = isInGodmode(target);
            if (isInGodmode) {
                sender.sendMessage(MessageUtils.colorize("&aEnabled godmode for " + target.getName() + "."));
            } else {
                sender.sendMessage(MessageUtils.colorize("&cDisabled godmode for " + target.getName() + "."));
            }
        }

        return true;
    }

    /**
     * Toggle godmode for a player
     *
     * @param player The player to toggle godmode for
     */
    private void toggleGodmode(Player player) {
        UUID uuid = player.getUniqueId();
        
        if (godmodePlayers.contains(uuid)) {
            // Disable godmode
            godmodePlayers.remove(uuid);
            player.setInvulnerable(false);
            player.sendMessage(MessageUtils.colorize("&cGodmode disabled."));
        } else {
            // Enable godmode
            godmodePlayers.add(uuid);
            player.setInvulnerable(true);
            player.sendMessage(MessageUtils.colorize("&aGodmode enabled."));
        }
    }

    /**
     * Check if a player is in godmode
     *
     * @param player The player to check
     * @return True if the player is in godmode
     */
    public static boolean isInGodmode(Player player) {
        return godmodePlayers.contains(player.getUniqueId());
    }

    /**
     * Remove a player from godmode (used when they disconnect)
     *
     * @param uuid The player's UUID
     */
    public static void removeFromGodmode(UUID uuid) {
        godmodePlayers.remove(uuid);
    }

    /**
     * Get all players currently in godmode
     *
     * @return Set of UUIDs in godmode
     */
    public static Set<UUID> getGodmodePlayers() {
        return new HashSet<>(godmodePlayers);
    }

    /**
     * Clear all godmode players (used during plugin disable)
     */
    public static void clearAllGodmode() {
        // Disable godmode for all online players before clearing
        for (UUID uuid : godmodePlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.setInvulnerable(false);
            }
        }
        godmodePlayers.clear();
    }
}