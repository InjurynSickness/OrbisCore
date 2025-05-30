package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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
        // Create clickable store link component
        Component storeMessage = Component.text("» ", NamedTextColor.GOLD)
                .append(Component.text("Click here to open the OrbisMC store on your browser", NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.openUrl("https://novus-orbis.tebex.io/")));

        sender.sendMessage(storeMessage);
        return true;
    }
}