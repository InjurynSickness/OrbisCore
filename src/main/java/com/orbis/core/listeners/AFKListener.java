package com.orbis.core.listeners;

import com.orbis.core.data.AFKManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles AFK-related events
 */
public class AFKListener implements Listener {

    private final AFKManager afkManager;

    public AFKListener(AFKManager afkManager) {
        this.afkManager = afkManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Check if player moved (not just looking around)
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
            event.getFrom().getBlockY() != event.getTo().getBlockY() ||
            event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            
            // If player is AFK and moved, remove them from AFK
            if (afkManager.isAFK(player)) {
                afkManager.toggleAFK(player, null); // This will remove AFK status
                
                Component backMsg = Component.text("✅ ", NamedTextColor.GREEN)
                    .append(Component.text(player.getName(), NamedTextColor.WHITE))
                    .append(Component.text(" is no longer AFK", NamedTextColor.GREEN));
                
                Bukkit.broadcast(backMsg);
                
                // Send any missed messages
                afkManager.sendMissedMessages(player);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Handle AFK cleanup when player disconnects
        afkManager.handlePlayerDisconnect(event.getPlayer());
    }
}