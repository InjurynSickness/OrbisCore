package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to change a player's gamemode
 */
public class GamemodeCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final GameMode gameMode;

    public GamemodeCommand(OrbisCore plugin, GameMode gameMode) {
        this.plugin = plugin;
        this.gameMode = gameMode;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(MessageUtils.error("This command can only be used by players or specify a target."));
            return true;
        }

        // Set own gamemode
        if (args.length == 0) {
            Player player = (Player) sender;

            // Check permission based on gamemode
            String permission = "orbiscore.gamemode." + gameMode.name().toLowerCase();
            if (!player.hasPermission(permission)) {
                player.sendMessage(plugin.getMessageComponent("no-permission"));
                return true;
            }

            if (player.getGameMode() == gameMode) {
                Component errorMsg = MessageUtils.error("You are already in ")
                    .append(Component.text(gameMode.name().toLowerCase(), NamedTextColor.WHITE))
                    .append(Component.text(" mode!", NamedTextColor.RED));
                player.sendMessage(errorMsg);
                return true;
            }

            player.setGameMode(gameMode);
            
            Component successMsg = MessageUtils.success("Gamemode set to ")
                .append(Component.text(gameMode.name().toLowerCase(), NamedTextColor.WHITE))
                .append(Component.text("!", NamedTextColor.GREEN));
            player.sendMessage(successMsg);
            return true;
        }

        // Set another player's gamemode
        if (!sender.hasPermission("orbiscore.gamemode.others")) {
            sender.sendMessage(plugin.getMessageComponent("no-permission"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Component errorMsg = MessageUtils.error("Player ")
                .append(Component.text(args[0], NamedTextColor.WHITE))
                .append(Component.text(" is not online!", NamedTextColor.RED));
            sender.sendMessage(errorMsg);
            return true;
        }

        if (target.getGameMode() == gameMode) {
            Component errorMsg = Component.text(target.getName(), NamedTextColor.WHITE)
                .append(Component.text(" is already in ", NamedTextColor.RED))
                .append(Component.text(gameMode.name().toLowerCase(), NamedTextColor.WHITE))
                .append(Component.text(" mode!", NamedTextColor.RED));
            sender.sendMessage(errorMsg);
            return true;
        }

        target.setGameMode(gameMode);
        
        String gamemodeName = gameMode.name().toLowerCase();
        
        Component senderMsg = MessageUtils.success("Set ")
            .append(Component.text(target.getName(), NamedTextColor.WHITE))
            .append(Component.text("'s gamemode to ", NamedTextColor.GREEN))
            .append(Component.text(gamemodeName, NamedTextColor.WHITE))
            .append(Component.text("!", NamedTextColor.GREEN));
        
        Component targetMsg = MessageUtils.success("Your gamemode was set to ")
            .append(Component.text(gamemodeName, NamedTextColor.WHITE))
            .append(Component.text(" by ", NamedTextColor.GREEN))
            .append(Component.text(sender.getName(), NamedTextColor.WHITE))
            .append(Component.text("!", NamedTextColor.GREEN));

        sender.sendMessage(senderMsg);
        target.sendMessage(targetMsg);

        return true;
    }
}