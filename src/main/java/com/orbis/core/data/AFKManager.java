package com.orbis.core.data;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages AFK status and missed messages for players
 */
public class AFKManager {

    private final OrbisCore plugin;
    private final Map<UUID, AFKData> afkPlayers = new ConcurrentHashMap<>();
    private final Map<UUID, List<MissedMessage>> missedMessages = new ConcurrentHashMap<>();
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    public AFKManager(OrbisCore plugin) {
        this.plugin = plugin;
    }

    /**
     * Toggle AFK status for a player
     *
     * @param player The player to toggle AFK for
     * @param customMessage Optional custom AFK message
     * @return True if player is now AFK, false if no longer AFK
     */
    public boolean toggleAFK(Player player, String customMessage) {
        UUID uuid = player.getUniqueId();
        
        if (afkPlayers.containsKey(uuid)) {
            // Player is currently AFK, remove them
            afkPlayers.remove(uuid);
            return false;
        } else {
            // Player is not AFK, make them AFK
            AFKData afkData = new AFKData(customMessage, System.currentTimeMillis());
            afkPlayers.put(uuid, afkData);
            return true;
        }
    }

    /**
     * Check if a player is AFK
     *
     * @param player The player to check
     * @return True if the player is AFK
     */
    public boolean isAFK(Player player) {
        return afkPlayers.containsKey(player.getUniqueId());
    }

    /**
     * Get a player's AFK data
     *
     * @param player The player to get data for
     * @return AFKData or null if not AFK
     */
    public AFKData getAFKData(Player player) {
        return afkPlayers.get(player.getUniqueId());
    }

    /**
     * Remove a player from AFK status (used when they disconnect)
     *
     * @param player The player to remove
     */
    public void removeFromAFK(Player player) {
        afkPlayers.remove(player.getUniqueId());
    }

    /**
     * Store a missed message for an AFK player
     *
     * @param recipient The AFK player who should receive the message
     * @param sender The player who sent the message
     * @param message The message content
     */
    public void storeMissedMessage(Player recipient, Player sender, String message) {
        UUID recipientUUID = recipient.getUniqueId();
        
        if (!isAFK(recipient)) {
            return; // Player is not AFK, don't store message
        }

        MissedMessage missedMsg = new MissedMessage(
            sender.getName(),
            message,
            LocalDateTime.now().format(timeFormat)
        );

        missedMessages.computeIfAbsent(recipientUUID, k -> new ArrayList<>()).add(missedMsg);
        
        // Limit stored messages to prevent memory issues (max 10 per player)
        List<MissedMessage> messages = missedMessages.get(recipientUUID);
        if (messages.size() > 10) {
            messages.remove(0); // Remove oldest message
        }
    }

    /**
     * Send all missed messages to a player
     *
     * @param player The player to send messages to
     */
    public void sendMissedMessages(Player player) {
        UUID uuid = player.getUniqueId();
        List<MissedMessage> messages = missedMessages.remove(uuid);
        
        if (messages == null || messages.isEmpty()) {
            return;
        }

        // Send header
        Component header = Component.text("📬 ", NamedTextColor.YELLOW, TextDecoration.BOLD)
            .append(Component.text("You have ", NamedTextColor.GOLD))
            .append(Component.text(messages.size(), NamedTextColor.WHITE, TextDecoration.BOLD))
            .append(Component.text(" missed message" + (messages.size() == 1 ? "" : "s") + ":", NamedTextColor.GOLD));
        
        player.sendMessage(header);

        // Send each missed message
        for (MissedMessage msg : messages) {
            Component messageComponent = Component.text("  [" + msg.getTime() + "] ", NamedTextColor.GRAY)
                .append(Component.text(msg.getSender(), NamedTextColor.WHITE))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(Component.text(msg.getMessage(), NamedTextColor.WHITE));
            
            player.sendMessage(messageComponent);
        }

        // Send footer
        Component footer = Component.text("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.GRAY);
        player.sendMessage(footer);
    }

    /**
     * Clean up data when a player disconnects
     *
     * @param player The disconnecting player
     */
    public void handlePlayerDisconnect(Player player) {
        UUID uuid = player.getUniqueId();
        afkPlayers.remove(uuid);
        // Keep missed messages in case they reconnect soon
    }

    /**
     * Get all AFK players
     *
     * @return Set of UUIDs of AFK players
     */
    public Set<UUID> getAFKPlayers() {
        return new HashSet<>(afkPlayers.keySet());
    }

    /**
     * Clear old missed messages (run periodically)
     */
    public void clearOldMissedMessages() {
        // Remove missed messages for players who haven't been online for 1 hour
        long oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000);
        
        missedMessages.entrySet().removeIf(entry -> {
            Player player = Bukkit.getPlayer(entry.getKey());
            return player == null || !player.isOnline();
        });
    }

    /**
     * Data class to store AFK information
     */
    public static class AFKData {
        private final String customMessage;
        private final long afkSince;

        public AFKData(String customMessage, long afkSince) {
            this.customMessage = customMessage;
            this.afkSince = afkSince;
        }

        public String getCustomMessage() {
            return customMessage;
        }

        public long getAFKSince() {
            return afkSince;
        }

        public boolean hasCustomMessage() {
            return customMessage != null && !customMessage.trim().isEmpty();
        }
    }

    /**
     * Data class to store missed messages
     */
    public static class MissedMessage {
        private final String sender;
        private final String message;
        private final String time;

        public MissedMessage(String sender, String message, String time) {
            this.sender = sender;
            this.message = message;
            this.time = time;
        }

        public String getSender() {
            return sender;
        }

        public String getMessage() {
            return message;
        }

        public String getTime() {
            return time;
        }
    }
}