package com.orbis.core.listeners;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.AFKManager;
import com.orbis.core.data.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player connection events (join/quit)
 */
public class PlayerConnectionListener implements Listener {

    private final OrbisCore plugin;
    private final PlayerDataManager playerDataManager;
    private final AFKManager afkManager;

    public PlayerConnectionListener(OrbisCore plugin, PlayerDataManager playerDataManager, AFKManager afkManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
        this.afkManager = afkManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Record join in player data manager
        playerDataManager.recordPlayerJoin(player);

        // Debug log
        plugin.getLogger().info("Player " + player.getName() + " joined with UUID: " + player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Record quit in player data manager
        playerDataManager.recordPlayerQuit(player);

        // Handle AFK cleanup
        afkManager.handlePlayerDisconnect(player);

        // Debug log
        plugin.getLogger().info("Player " + player.getName() + " data saved successfully.");
    }
}