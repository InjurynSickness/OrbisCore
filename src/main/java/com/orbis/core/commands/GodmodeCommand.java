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
            sender.sendMessage(MessageUtils.error("This command can only be used by players."));
            return true;
        }

        // No arguments - toggle godmode for self
        if (args.length == 0) {
            Player player = (Player) sender;

            // Check permission
            if (!player.hasPermission("orbiscore.godmode")) {
                player.sendMessage(plugin.getMessageComponent("no-permission"));
                return true;
            }

            toggleGodmode(player);
            return true;
        }

        // With argument - toggle godmode for another player
        if (!sender.hasPermission("orbiscore.godmode.others")) {
            sender.sendMessage(plugin.getMessageComponent("no-permission"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Component errorMsg = MessageUtils.error("Player ")
                .append(Component.text(args[0], NamedTextColor.WHITE))
                .append(Component.text(" is not online!", NamedTextColor.RED));
            sender.sendMessage(errorMsg);
            return true;
        }

        toggleGodmode(target);
        
        // Notify the command sender if they're not the target
        if (!sender.equals(target)) {
            boolean isInGodmode = isInGodmode(target);
            Component senderMsg;
            if (isInGodmode) {
                senderMsg = Component.text("⚡ ", NamedTextColor.GOLD, TextDecoration.BOLD)
                    .append(Component.text("Enabled godmode for ", NamedTextColor.GREEN))
                    .append(Component.text(target.getName(), NamedTextColor.WHITE))
                    .append(Component.text(".", NamedTextColor.GREEN));
            } else {
                senderMsg = Component.text("🛡 ", NamedTextColor.GRAY, TextDecoration.BOLD)
                    .append(Component.text("Disabled godmode for ", NamedTextColor.RED))
                    .append(Component.text(target.getName(), NamedTextColor.WHITE))
                    .append(Component.text(".", NamedTextColor.RED));
            }
            sender.sendMessage(senderMsg);
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
        
        Component message;
        if (godmodePlayers.contains(uuid)) {
            // Disable godmode
            godmodePlayers.remove(uuid);
            player.setInvulnerable(false);
            
            message = Component.text("🛡 ", NamedTextColor.GRAY, TextDecoration.BOLD)
                .append(Component.text("Godmode disabled.", NamedTextColor.RED));
        } else {
            // Enable godmode
            godmodePlayers.add(uuid);
            player.setInvulnerable(true);
            
            message = Component.text("⚡ ", NamedTextColor.GOLD, TextDecoration.BOLD)
                .append(Component.text("Godmode enabled.", NamedTextColor.GREEN))
                .append(Component.text(" You are now invincible!", NamedTextColor.YELLOW, TextDecoration.ITALIC));
        }
        
        player.sendMessage(message);
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