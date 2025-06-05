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

import java.util.UUID;

/**
 * Command to teleport players
 */
public class TeleportCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final PlayerDataManager playerDataManager;

    public TeleportCommand(OrbisCore plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length < 2) {
            sender.sendMessage(MessageUtils.error("This command can only be used by players."));
            return true;
        }

        // No arguments - show usage
        if (args.length == 0) {
            Component usage = MessageUtils.error("Usage: ")
                .append(Component.text("/tp [player]", NamedTextColor.YELLOW))
                .append(Component.text(" or ", NamedTextColor.RED))
                .append(Component.text("/tp [player1] [player2]", NamedTextColor.YELLOW));
            sender.sendMessage(usage);
            return true;
        }

        // One argument - teleport self to player
        if (args.length == 1) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            if (!player.hasPermission("orbiscore.tp")) {
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

            // Check if trying to teleport to self
            if (target.equals(player)) {
                player.sendMessage(MessageUtils.error("You cannot teleport to yourself!"));
                return true;
            }

            // Store current location for /back command
            playerDataManager.recordTeleportLocation(uuid, player.getLocation());

            player.teleport(target);
            
            Component successMsg = MessageUtils.success("Teleported to ")
                .append(Component.text(target.getName(), NamedTextColor.WHITE));
            player.sendMessage(successMsg);

            return true;
        }

        // Two arguments - teleport player1 to player2
        if (!sender.hasPermission("orbiscore.tp.others")) {
            sender.sendMessage(plugin.getMessageComponent("no-permission"));
            return true;
        }

        Player player1 = Bukkit.getPlayer(args[0]);
        Player player2 = Bukkit.getPlayer(args[1]);

        if (player1 == null) {
            Component errorMsg = MessageUtils.error("Player ")
                .append(Component.text(args[0], NamedTextColor.WHITE))
                .append(Component.text(" is not online!", NamedTextColor.RED));
            sender.sendMessage(errorMsg);
            return true;
        }

        if (player2 == null) {
            Component errorMsg = MessageUtils.error("Player ")
                .append(Component.text(args[1], NamedTextColor.WHITE))
                .append(Component.text(" is not online!", NamedTextColor.RED));
            sender.sendMessage(errorMsg);
            return true;
        }

        if (player1.equals(player2)) {
            sender.sendMessage(MessageUtils.error("You cannot teleport a player to themselves!"));
            return true;
        }

        // Store current location for /back command
        playerDataManager.recordTeleportLocation(player1.getUniqueId(), player1.getLocation());

        player1.teleport(player2);
        
        Component senderMsg = MessageUtils.success("Teleported ")
            .append(Component.text(player1.getName(), NamedTextColor.WHITE))
            .append(Component.text(" to ", NamedTextColor.GREEN))
            .append(Component.text(player2.getName(), NamedTextColor.WHITE));
        
        Component targetMsg = MessageUtils.success("You were teleported to ")
            .append(Component.text(player2.getName(), NamedTextColor.WHITE));

        sender.sendMessage(senderMsg);
        player1.sendMessage(targetMsg);

        return true;
    }
}