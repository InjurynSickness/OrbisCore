package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
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
            sender.sendMessage(MessageUtils.colorize("&6=== &fOrbisCore &6==="));
            sender.sendMessage(MessageUtils.colorize("&7Version: &f" + plugin.getDescription().getVersion()));
            sender.sendMessage(MessageUtils.colorize("&7Authors: &f" + String.join(", ", plugin.getDescription().getAuthors())));
            sender.sendMessage(MessageUtils.colorize("&7Commands: &f/orbiscore reload"));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("orbiscore.reload")) {
                sender.sendMessage(MessageUtils.colorize(plugin.getMessage("no-permission")));
                return true;
            }

            try {
                plugin.reloadConfiguration();
                sender.sendMessage(MessageUtils.colorize(plugin.getMessage("config-reloaded")));
                plugin.getLogger().info(sender.getName() + " reloaded the configuration");
            } catch (Exception e) {
                sender.sendMessage(MessageUtils.colorize("&cError reloading configuration: " + e.getMessage()));
                plugin.getLogger().severe("Error reloading configuration: " + e.getMessage());
            }
            return true;
        }

        sender.sendMessage(MessageUtils.colorize("&cUnknown subcommand. Use /orbiscore reload"));
        return true;
    }
}