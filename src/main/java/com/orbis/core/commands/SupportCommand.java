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
 * Command to show a clickable link to the Support Discord server
 */
public class SupportCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public SupportCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Create a rich clickable Support Discord link component with hover effect
        Component supportMessage = Component.text("Support", NamedTextColor.RED, TextDecoration.BOLD)
                .append(Component.text("Need help? ", NamedTextColor.GRAY))
                .append(Component.text("Click here to join our Support Discord", NamedTextColor.YELLOW)
                        .decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl("https://discord.gg/TkUzUk8sf3"))
                        .hoverEvent(HoverEvent.showText(
                                Component.text("Get help from our support team", NamedTextColor.GRAY)
                                        .appendNewline()
                                        .append(Component.text("• Bug reports", NamedTextColor.DARK_GRAY))
                                        .appendNewline()
                                        .append(Component.text("• Technical assistance", NamedTextColor.DARK_GRAY))
                                        .appendNewline()
                                        .append(Component.text("• Account issues", NamedTextColor.DARK_GRAY))
                                        .appendNewline()
                                        .append(Component.text("https://discord.gg/TkUzUk8sf3", NamedTextColor.BLUE))
                        )));

        sender.sendMessage(supportMessage);
        return true;
    }
}