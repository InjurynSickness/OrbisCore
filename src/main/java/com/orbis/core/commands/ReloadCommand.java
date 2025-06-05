package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command to reload the plugin configuration
 */
public class ReloadCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public ReloadCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Show plugin information
            Component header = Component.text("=== ", NamedTextColor.GOLD)
                .append(Component.text("OrbisCore", NamedTextColor.WHITE, TextDecoration.BOLD))
                .append(Component.text(" ===", NamedTextColor.GOLD));
            
            Component version = MessageUtils.labeledInfo("Version", plugin.getDescription().getVersion());
            Component authors = MessageUtils.labeledInfo("Authors", String.join(", ", plugin.getDescription().getAuthors()));
            Component commands = MessageUtils.labeledInfo("Commands", "/orbiscore reload");
            
            sender.sendMessage(header);
            sender.sendMessage(version);
            sender.sendMessage(authors);
            sender.sendMessage(commands);
            
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("orbiscore.reload")) {
                sender.sendMessage(plugin.getMessageComponent("no-permission"));
                return true;
            }

            try {
                plugin.reloadConfiguration();
                
                Component successMsg = Component.text("✓ ", NamedTextColor.GREEN, TextDecoration.BOLD)
                    .append(Component.text("Configuration reloaded successfully!", NamedTextColor.GREEN));
                
                sender.sendMessage(successMsg);
                plugin.getLogger().info(sender.getName() + " reloaded the configuration");
                
            } catch (Exception e) {
                Component errorMsg = Component.text("✗ ", NamedTextColor.RED, TextDecoration.BOLD)
                    .append(Component.text("Error reloading configuration: ", NamedTextColor.RED))
                    .append(Component.text(e.getMessage(), NamedTextColor.GRAY));
                
                sender.sendMessage(errorMsg);
                plugin.getLogger().severe("Error reloading configuration: " + e.getMessage());
            }
            return true;
        }

        Component errorMsg = MessageUtils.error("Unknown subcommand. Use ")
            .append(Component.text("/orbiscore reload", NamedTextColor.YELLOW))
            .append(Component.text(".", NamedTextColor.RED));
        
        sender.sendMessage(errorMsg);
        return true;
    }
}