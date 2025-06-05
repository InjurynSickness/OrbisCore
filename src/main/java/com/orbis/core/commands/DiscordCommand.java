package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command to show a clickable link to the Discord server
 */
public class DiscordCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public DiscordCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Create a rich clickable Discord link component with hover effect
        Component discordMessage = Component.text("» ", NamedTextColor.GOLD)
                .append(Component.text("Click here to join our Discord server", NamedTextColor.YELLOW)
                        .decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl("https://discord.gg/sRDHcHz3gR"))
                        .hoverEvent(HoverEvent.showText(
                            Component.text("Click to open Discord in your browser", NamedTextColor.GRAY)
                                .appendNewline()
                                .append(Component.text("https://discord.gg/sRDHcHz3gR", NamedTextColor.BLUE))
                        )));

        sender.sendMessage(discordMessage);
        return true;
    }
}