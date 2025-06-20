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
 * Command to show a clickable link to the server documentation
 */
public class DocsCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public DocsCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Create rich hover text
        Component hoverText = Component.text("📚 OrbisMC Documentation", NamedTextColor.BLUE, TextDecoration.BOLD)
            .appendNewline()
            .append(Component.text("• Server rules & guidelines", NamedTextColor.GRAY))
            .appendNewline()
            .append(Component.text("• Command reference", NamedTextColor.GRAY))
            .appendNewline()
            .append(Component.text("• Getting started guide", NamedTextColor.GRAY))
            .appendNewline()
            .append(Component.text("• Feature explanations", NamedTextColor.GRAY))
            .appendNewline()
            .append(Component.text("• FAQ & Troubleshooting", NamedTextColor.GRAY))
            .appendNewline()
            .append(Component.text("https://docs.orbismc.com/", NamedTextColor.AQUA));

        // Create clickable documentation link component
        Component docsMessage = Component.text("📖 ", NamedTextColor.BLUE, TextDecoration.BOLD)
                .append(Component.text("Click here to view the OrbisMC documentation", NamedTextColor.YELLOW)
                        .decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl("https://docs.orbismc.com/"))
                        .hoverEvent(HoverEvent.showText(hoverText)));

        sender.sendMessage(docsMessage);
        return true;
    }
}