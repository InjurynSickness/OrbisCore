package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to teleport a player to the command sender
 */
public class TpHereCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final PlayerDataManager playerDataManager;

    public TpHereCommand(OrbisCore plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.error("This command can only be used by players."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtils.error("Usage: /tphere [player]"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("orbiscore.tphere")) {
            player.sendMessage(plugin.getMessageComponent("no-permission"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Component errorMsg = MessageUtils.error("Player ")
                .append(Component.text(args[0], NamedTextColor.WHITE))
                .append(Component.text(" is not online!", NamedTextColor.RED));
            player.sendMessage(errorMsg);
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(MessageUtils.error("You cannot teleport yourself to yourself!"));
            return true;
        }

        // Store current location for /back command
        playerDataManager.recordTeleportLocation(target.getUniqueId(), target.getLocation());

        // Teleport player to sender
        target.teleport(player);

        Component senderMsg = MessageUtils.success("Teleported ")
            .append(Component.text(target.getName(), NamedTextColor.WHITE))
            .append(Component.text(" to your location.", NamedTextColor.GREEN));
        
        Component targetMsg = MessageUtils.success("You were teleported to ")
            .append(Component.text(player.getName(), NamedTextColor.WHITE))
            .append(Component.text(".", NamedTextColor.GREEN));

        player.sendMessage(senderMsg);
        target.sendMessage(targetMsg);

        return true;
    }
}