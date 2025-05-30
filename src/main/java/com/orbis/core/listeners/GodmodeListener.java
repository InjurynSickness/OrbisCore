package com.orbis.core.listeners;

import com.orbis.core.commands.GodmodeCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles godmode cleanup when players disconnect
 */
public class GodmodeListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove player from godmode when they disconnect
        GodmodeCommand.removeFromGodmode(event.getPlayer().getUniqueId());
    }
}