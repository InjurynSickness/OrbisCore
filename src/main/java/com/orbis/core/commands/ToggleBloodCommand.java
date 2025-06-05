package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Command to toggle blood effect visibility
 */
public class ToggleBloodCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final PlayerDataManager playerDataManager;

    public ToggleBloodCommand(OrbisCore plugin, PlayerDataManager playerDataManager) {
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

        if (!player.hasPermission("orbiscore.toggleblood")) {
            player.sendMessage(plugin.getMessageComponent("no-permission"));
            return true;
        }

        // Get current blood effect status (default to true if not set)
        boolean currentStatus = playerDataManager.getBloodEffectEnabled(uuid);
        boolean newStatus = !currentStatus;

        // Update the status
        playerDataManager.setBloodEffectEnabled(uuid, newStatus);

        // Send message to player with appropriate emoji and color
        Component message;
        if (newStatus) {
            message = Component.text("🩸 ", NamedTextColor.RED, TextDecoration.BOLD)
                .append(Component.text("Blood effect enabled.", NamedTextColor.GREEN));
        } else {
            message = Component.text("🚫 ", NamedTextColor.GRAY, TextDecoration.BOLD)
                .append(Component.text("Blood effect disabled.", NamedTextColor.RED));
        }

        player.sendMessage(message);
        return true;
    }
}