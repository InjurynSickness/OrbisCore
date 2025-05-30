package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to show a clickable link to the server map
 */
public class MapCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public MapCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();

        int x = location.getBlockX();
        int z = location.getBlockZ();
        String worldName = location.getWorld().getName();

        // Build the map URL with player's coordinates
        String mapUrl = String.format(
                "https://map.orbismc.com/?worldname=%s&zoom=5&x=%d&z=%d",
                worldName, x, z
        );

        // Create clickable component
        Component mapMessage = Component.text("» ", NamedTextColor.GOLD)
                .append(Component.text("Click here to open the OrbisMC map on your browser", NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.openUrl(mapUrl)));

        player.sendMessage(mapMessage);
        return true;
    }
}