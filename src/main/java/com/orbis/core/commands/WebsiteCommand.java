package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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
        // Create clickable website link component
        Component websiteMessage = Component.text("» ", NamedTextColor.GOLD)
                .append(Component.text("Click here to open the OrbisMC website on your browser", NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.openUrl("https://www.orbismc.com/")));

        sender.sendMessage(websiteMessage);
        return true;
    }
}