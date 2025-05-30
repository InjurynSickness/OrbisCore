package com.orbis.core.listeners;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Handles blood effect when players are damaged
 */
public class BloodEffectListener implements Listener {

    private final OrbisCore plugin;
    private final PlayerDataManager playerDataManager;

    public BloodEffectListener(OrbisCore plugin, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.playerDataManager = playerDataManager;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        // Check if victim is a player
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Location bloodLocation = victim.getLocation().add(0, 1, 0); // Location above victim

        // Create dust transition particle data (dark red to red)
        Particle.DustTransition dustTransition = new Particle.DustTransition(
                Color.fromRGB(139, 0, 0), // Dark red
                Color.fromRGB(255, 0, 0), // Red
                3.0f // Size
        );

        // Show blood effect to all players who have it enabled
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            // Skip the victim to avoid showing blood effect to themselves
            if (onlinePlayer.equals(victim)) {
                continue;
            }

            // Check if player has blood effect enabled (default to true)
            boolean bloodEnabled = playerDataManager.getBloodEffectEnabled(onlinePlayer.getUniqueId());

            if (bloodEnabled) {
                // Spawn 3 dust transition particles for this player
                onlinePlayer.spawnParticle(
                        Particle.DUST_COLOR_TRANSITION,
                        bloodLocation,
                        3, // Count
                        0.2, // Offset X
                        0.2, // Offset Y
                        0.2, // Offset Z
                        0, // Extra speed
                        dustTransition
                );
            }
        }
    }
}