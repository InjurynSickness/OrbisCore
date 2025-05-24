package com.orbis.core.listeners;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Handles player death events
 */
public class PlayerDeathListener implements Listener {

    private final OrbisCore plugin;
    private final PlayerDataManager playerDataManager;

    public PlayerDeathListener(OrbisCore plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Store death location in player data manager
        playerDataManager.recordDeathLocation(player, player.getLocation());
    }
}