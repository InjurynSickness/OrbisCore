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
 * Command to redirect players to documentation for TPA and RTP features
 */
public class TpaRtpCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final String commandType;

    public TpaRtpCommand(OrbisCore plugin, String commandType) {
        this.plugin = plugin;
        this.commandType = commandType.toLowerCase();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String featureName = commandType.equals("tpa") ? "Teleport Requests (TPA)" : "Random Teleport (RTP)";
        String emoji = commandType.equals("tpa") ? "🔗" : "🎲";
        String description = commandType.equals("tpa") ? 
            "player teleportation and request system" : 
            "random world exploration feature";

        // Create rich hover text
        Component hoverText = Component.text(emoji + " " + featureName + " Documentation", NamedTextColor.BLUE, TextDecoration.BOLD)
            .appendNewline()
            .append(Component.text("Learn about the " + description, NamedTextColor.GRAY))
            .appendNewline()
            .append(Component.text("• How to use commands", NamedTextColor.DARK_GRAY))
            .appendNewline()
            .append(Component.text("• Features & limitations", NamedTextColor.DARK_GRAY))
            .appendNewline()
            .append(Component.text("• Tips & best practices", NamedTextColor.DARK_GRAY))
            .appendNewline()
            .append(Component.text("https://docs.orbismc.com/" + commandType, NamedTextColor.AQUA));

        // Create the main message
        Component message = Component.text(emoji + " ", NamedTextColor.GOLD, TextDecoration.BOLD)
            .append(Component.text(featureName + " is available! ", NamedTextColor.GREEN))
            .append(Component.text("Click here to view the documentation", NamedTextColor.YELLOW)
                .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.openUrl("https://docs.orbismc.com/" + commandType))
                .hoverEvent(HoverEvent.showText(hoverText)));

        // Add a helpful tip
        Component tip = Component.text("💡 ", NamedTextColor.YELLOW)
            .append(Component.text("Tip: ", NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.text("You can also use ", NamedTextColor.GRAY))
            .append(Component.text("/docs", NamedTextColor.AQUA))
            .append(Component.text(" or ", NamedTextColor.GRAY))
            .append(Component.text("/guide", NamedTextColor.AQUA))
            .append(Component.text(" for the full documentation.", NamedTextColor.GRAY));

        sender.sendMessage(message);
        sender.sendMessage(tip);
        return true;
    }
}