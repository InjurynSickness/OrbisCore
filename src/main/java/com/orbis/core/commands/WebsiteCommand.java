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
 * Command to show a clickable link to the server website
 */
public class WebsiteCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public WebsiteCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Create rich hover text
        Component hoverText = Component.text("🌐 Visit OrbisMC.com", NamedTextColor.AQUA, TextDecoration.BOLD)
            .appendNewline()
            .append(Component.text("• Server information", NamedTextColor.GRAY))
            .appendNewline()
            .append(Component.text("• Community updates", NamedTextColor.GRAY))
            .appendNewline()
            .append(Component.text("• Player guides & tutorials", NamedTextColor.GRAY))
            .appendNewline()
            .append(Component.text("https://www.orbismc.com/", NamedTextColor.BLUE));

        // Create clickable website link component
        Component websiteMessage = Component.text("» ", NamedTextColor.GOLD)
                .append(Component.text("Click here to visit the OrbisMC website", NamedTextColor.YELLOW)
                        .decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl("https://www.orbismc.com/"))
                        .hoverEvent(HoverEvent.showText(hoverText)));

        sender.sendMessage(websiteMessage);
        return true;
    }
}