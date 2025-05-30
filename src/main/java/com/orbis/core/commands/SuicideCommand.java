package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Command for players to commit suicide with cooldown
 */
public class SuicideCommand implements CommandExecutor {

    private final OrbisCore plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 3600000; // 1 hour in milliseconds

    public SuicideCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.colorize("&cThis command can only be used by players."));
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        // Check cooldown
        if (cooldowns.containsKey(uuid)) {
            long timeLeft = cooldowns.get(uuid) - System.currentTimeMillis();
            if (timeLeft > 0) {
                String remainingTime = formatTime(timeLeft);
                player.sendMessage(MessageUtils.colorize("&cTry again in " + remainingTime + "."));
                return true;
            }
        }

        // Set cooldown
        cooldowns.put(uuid, System.currentTimeMillis() + COOLDOWN_TIME);

        // Broadcast suicide message
        Bukkit.broadcastMessage(MessageUtils.colorize("&c" + player.getName() + " has committed suicide."));

        // Kill the player
        player.setHealth(0);

        // Apply debuff effects after respawn (3 second delay)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    // Apply saturation potion of tier -1 (removes saturation)
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.SATURATION,
                            60, // 3 seconds (20 ticks per second)
                            -1, // Tier -1
                            true, // Ambient
                            false, // Show particles
                            false  // Show icon
                    ));

                    // Apply hunger potion of tier 4
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.HUNGER,
                            60, // 3 seconds
                            4, // Tier 4
                            true, // Ambient
                            false, // Show particles
                            false  // Show icon
                    ));

                    // Apply weakness potion of tier 4
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.WEAKNESS,
                            60, // 3 seconds
                            4, // Tier 4
                            true, // Ambient
                            false, // Show particles
                            false  // Show icon
                    ));
                }
            }
        }.runTaskLater(plugin, 60L); // 60 ticks = 3 seconds

        return true;
    }

    /**
     * Format milliseconds to a readable time string
     */
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            minutes %= 60;
            if (minutes > 0) {
                return hours + " hour" + (hours != 1 ? "s" : "") + " " + minutes + " minute" + (minutes != 1 ? "s" : "");
            }
            return hours + " hour" + (hours != 1 ? "s" : "");
        } else if (minutes > 0) {
            seconds %= 60;
            if (seconds > 0) {
                return minutes + " minute" + (minutes != 1 ? "s" : "") + " " + seconds + " second" + (seconds != 1 ? "s" : "");
            }
            return minutes + " minute" + (minutes != 1 ? "s" : "");
        } else {
            return seconds + " second" + (seconds != 1 ? "s" : "");
        }
    }
}