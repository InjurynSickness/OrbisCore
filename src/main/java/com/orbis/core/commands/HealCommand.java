package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to heal a player
 */
public class HealCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public HealCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(MessageUtils.error("This command can only be used by players."));
            return true;
        }

        // Heal self
        if (args.length == 0) {
            Player player = (Player) sender;
            
            if (!player.hasPermission("orbiscore.heal")) {
                player.sendMessage(plugin.getMessageComponent("no-permission"));
                return true;
            }
            
            healPlayer(player);
            player.sendMessage(MessageUtils.success("You have been healed!"));
            return true;
        }

        // Heal another player
        if (!sender.hasPermission("orbiscore.heal.others")) {
            sender.sendMessage(MessageUtils.error("You don't have permission to heal other players!"));
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

        healPlayer(target);

        // Send messages
        Component senderMsg = MessageUtils.success("Healed ")
            .append(Component.text(target.getName(), NamedTextColor.WHITE))
            .append(Component.text("!", NamedTextColor.GREEN));
        
        Component targetMsg = MessageUtils.success("You have been healed by ")
            .append(Component.text(sender.getName(), NamedTextColor.WHITE))
            .append(Component.text("!", NamedTextColor.GREEN));

        sender.sendMessage(senderMsg);
        target.sendMessage(targetMsg);

        return true;
    }

    /**
     * Heal a player completely
     */
    private void healPlayer(Player player) {
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20);
        // Clear common negative potion effects
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.POISON);
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.WITHER);
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.HUNGER);
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.WEAKNESS);
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.SLOWNESS);
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.MINING_FATIGUE);
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.NAUSEA);
        player.removePotionEffect(org.bukkit.potion.PotionEffectType.BLINDNESS);
    }
}