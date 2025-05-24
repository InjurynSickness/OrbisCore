package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Command to set a player's nickname
 */
public class NicknameCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final PlayerDataManager playerDataManager;

    public NicknameCommand(OrbisCore plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length < 2) {
            sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players."));
            return true;
        }

        // Reset or check own nickname
        if (args.length == 0) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            String nickname = playerDataManager.getNickname(uuid);
            if (nickname == null || nickname.equals("none")) {
                player.sendMessage(MessageUtils.colorize("&cYou don't have a nickname set!"));
            } else {
                // Reset nickname
                playerDataManager.setNickname(uuid, "none");
                player.setDisplayName(player.getName());
                player.sendMessage(MessageUtils.colorize("&aYour nickname has been reset!"));
            }

            return true;
        }

        String nickname = args[0];

        // Replace color codes if player has permission
        if (sender.hasPermission("orbiscore.nickname.color")) {
            nickname = MessageUtils.colorize(nickname);
        }

        // Set own nickname
        if (args.length == 1) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            playerDataManager.setNickname(uuid, nickname);
            player.setDisplayName(nickname);
            player.sendMessage(MessageUtils.colorize("&aYour nickname has been set to: &r" + nickname));
            return true;
        }

        // Set another player's nickname
        if (!sender.hasPermission("orbiscore.nickname.others")) {
            sender.sendMessage(MessageUtils.colorize("&cYou don't have permission to change other players' nicknames!"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(MessageUtils.colorize("&cPlayer " + args[1] + " is not online!"));
            return true;
        }

        UUID uuid = target.getUniqueId();
        playerDataManager.setNickname(uuid, nickname);
        target.setDisplayName(nickname);

        sender.sendMessage(MessageUtils.colorize("&aSet " + target.getName() + "'s nickname to: &r" + nickname));
        target.sendMessage(MessageUtils.colorize("&aYour nickname has been set to: &r" + nickname));

        return true;
    }
}