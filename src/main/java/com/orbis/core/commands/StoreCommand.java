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
 * Command to show a clickable link to the server store
 */
public class StoreCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public StoreCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Create rich hover text
        Component hoverText = Component.text("🛒 Support OrbisMC!", NamedTextColor.GOLD)
            .appendNewline()
            .append(Component.text("Click to browse our store and", NamedTextColor.GRAY))
            .appendNewline()
            .append(Component.text("help keep the server running!", NamedTextColor.GRAY))
            .appendNewline()
            .append(Component.text("https://novus-orbis.tebex.io/", NamedTextColor.AQUA));

        // Create clickable store link component
        Component storeMessage = Component.text("» ", NamedTextColor.GOLD)
                .append(Component.text("Click here to visit the OrbisMC store", NamedTextColor.YELLOW)
                        .decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl("https://novus-orbis.tebex.io/"))
                        .hoverEvent(HoverEvent.showText(hoverText)));

        // Add a thank you message
        Component thankYou = Component.text("💎 ", NamedTextColor.AQUA)
            .append(Component.text("Thank you for supporting OrbisMC!", NamedTextColor.GREEN, TextDecoration.ITALIC));

        sender.sendMessage(storeMessage);
        sender.sendMessage(thankYou);
        return true;
    }
}