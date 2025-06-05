package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = 
        LegacyComponentSerializer.legacyAmpersand();

    public NicknameCommand(OrbisCore plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length < 2) {
            sender.sendMessage(MessageUtils.error("This command can only be used by players."));
            return true;
        }

        // Reset or check own nickname
        if (args.length == 0) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            if (!player.hasPermission("orbiscore.nickname")) {
                player.sendMessage(plugin.getMessageComponent("no-permission"));
                return true;
            }

            String nickname = playerDataManager.getNickname(uuid);
            if (nickname == null || nickname.equals("none")) {
                Component errorMsg = Component.text("❌ ", NamedTextColor.RED)
                    .append(Component.text("You don't have a nickname set!", NamedTextColor.RED));
                player.sendMessage(errorMsg);
            } else {
                // Reset nickname
                playerDataManager.setNickname(uuid, "none");
                player.setDisplayName(player.getName());
                
                Component successMsg = Component.text("✓ ", NamedTextColor.GREEN, TextDecoration.BOLD)
                    .append(Component.text("Your nickname has been reset!", NamedTextColor.GREEN));
                player.sendMessage(successMsg);
            }

            return true;
        }

        String nickname = args[0];
        
        // Check nickname length
        int maxLength = plugin.getPluginConfig().getInt("settings.max-nickname-length", 16);
        if (nickname.length() > maxLength) {
            Component errorMsg = MessageUtils.error("Nickname too long! Max ")
                .append(Component.text(maxLength, NamedTextColor.WHITE))
                .append(Component.text(" characters.", NamedTextColor.RED));
            sender.sendMessage(errorMsg);
            return true;
        }

        // Replace color codes if player has permission
        String processedNickname = nickname;
        if (sender.hasPermission("orbiscore.nickname.color")) {
            processedNickname = LEGACY_SERIALIZER.serialize(MessageUtils.colorize(nickname));
        }

        // Set own nickname
        if (args.length == 1) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();

            if (!player.hasPermission("orbiscore.nickname")) {
                player.sendMessage(plugin.getMessageComponent("no-permission"));
                return true;
            }

            playerDataManager.setNickname(uuid, processedNickname);
            player.setDisplayName(processedNickname);
            
            Component successMsg = Component.text("✓ ", NamedTextColor.GREEN, TextDecoration.BOLD)
                .append(Component.text("Your nickname has been set to: ", NamedTextColor.GREEN))
                .append(MessageUtils.colorize(processedNickname));
            player.sendMessage(successMsg);
            return true;
        }

        // Set another player's nickname
        if (!sender.hasPermission("orbiscore.nickname.others")) {
            sender.sendMessage(MessageUtils.error("You don't have permission to change other players' nicknames!"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Component errorMsg = MessageUtils.error("Player ")
                .append(Component.text(args[1], NamedTextColor.WHITE))
                .append(Component.text(" is not online!", NamedTextColor.RED));
            sender.sendMessage(errorMsg);
            return true;
        }

        UUID uuid = target.getUniqueId();
        playerDataManager.setNickname(uuid, processedNickname);
        target.setDisplayName(processedNickname);

        Component senderMsg = MessageUtils.success("Set ")
            .append(Component.text(target.getName(), NamedTextColor.WHITE))
            .append(Component.text("'s nickname to: ", NamedTextColor.GREEN))
            .append(MessageUtils.colorize(processedNickname));
            
        Component targetMsg = Component.text("✓ ", NamedTextColor.GREEN, TextDecoration.BOLD)
            .append(Component.text("Your nickname has been set to: ", NamedTextColor.GREEN))
            .append(MessageUtils.colorize(processedNickname))
            .append(Component.text(" by ", NamedTextColor.GREEN))
            .append(Component.text(sender.getName(), NamedTextColor.WHITE));

        sender.sendMessage(senderMsg);
        target.sendMessage(targetMsg);

        return true;
    }
}