package com.orbis.core.data;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.LocationConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Handles player data storage and retrieval
 */
public class PlayerDataManager {

    private final OrbisCore plugin;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private File playerDataFile;
    private FileConfiguration playerData;

    private final Map<UUID, Long> joinTimes = new HashMap<>();
    private final Map<UUID, Location> lastTeleportLocations = new HashMap<>();
    private final Map<UUID, Location> lastDeathLocations = new HashMap<>();

    public PlayerDataManager(OrbisCore plugin) {
        this.plugin = plugin;
        loadPlayerData();
    }

    /**
     * Load player data from file
     */
    private void loadPlayerData() {
        playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");

        // Create the file if it doesn't exist
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create playerdata.yml", e);
            }
        }

        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    /**
     * Save player data to file
     */
    public void savePlayerData() {
        try {
            playerData.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save playerdata.yml", e);
        }
    }

    /**
     * Save data for all online players
     */
    public void saveAllPlayerData() {
        // Update playtime for all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlaytime(player.getUniqueId());
        }

        // Save player data
        savePlayerData();
    }

    /**
     * Record a player's join
     *
     * @param player The player who joined
     */
    public void recordPlayerJoin(Player player) {
        UUID uuid = player.getUniqueId();

        // Set join time for playtime tracking
        joinTimes.put(uuid, System.currentTimeMillis() / 1000);

        // Store player data
        playerData.set("players." + uuid + ".name", player.getName());
        playerData.set("players." + uuid + ".ip", player.getAddress().getAddress().getHostAddress());

        // Set first join time if it's their first time
        if (!playerData.contains("players." + uuid + ".firstJoin")) {
            playerData.set("players." + uuid + ".firstJoin", LocalDateTime.now().format(dateFormat));
        }

        // Map username to UUID for lookup
        playerData.set("usernames." + player.getName().toLowerCase(), uuid.toString());

        // Save data
        savePlayerData();
    }

    /**
     * Record a player's quit
     *
     * @param player The player who quit
     */
    public void recordPlayerQuit(Player player) {
        UUID uuid = player.getUniqueId();

        // Store logout location and time
        playerData.set("players." + uuid + ".lastLogoutLocation", LocationConverter.locationToString(player.getLocation()));
        playerData.set("players." + uuid + ".lastLogout", LocalDateTime.now().format(dateFormat));

        // Update playtime
        updatePlaytime(uuid);

        // Save player data
        savePlayerData();
    }

    /**
     * Record a player's death location
     *
     * @param player The player who died
     * @param location The death location
     */
    public void recordDeathLocation(Player player, Location location) {
        lastDeathLocations.put(player.getUniqueId(), location);
    }

    /**
     * Get a player's death location
     *
     * @param uuid The player's UUID
     * @return The player's death location, or null if not found
     */
    public Location getDeathLocation(UUID uuid) {
        return lastDeathLocations.get(uuid);
    }

    /**
     * Record a teleport location for the back command
     *
     * @param uuid The player's UUID
     * @param location The teleport location
     */
    public void recordTeleportLocation(UUID uuid, Location location) {
        lastTeleportLocations.put(uuid, location);
    }

    /**
     * Get a player's last teleport location
     *
     * @param uuid The player's UUID
     * @return The teleport location, or null if not found
     */
    public Location getTeleportLocation(UUID uuid) {
        return lastTeleportLocations.get(uuid);
    }

    /**
     * Update a player's teleport location
     *
     * @param uuid The player's UUID
     * @param location The new teleport location
     */
    public void updateTeleportLocation(UUID uuid, Location location) {
        lastTeleportLocations.put(uuid, location);
    }

    /**
     * Update a player's playtime
     *
     * @param uuid The player's UUID
     */
    public void updatePlaytime(UUID uuid) {
        if (joinTimes.containsKey(uuid)) {
            long joinTime = joinTimes.get(uuid);
            long currentTime = System.currentTimeMillis() / 1000;
            long sessionSeconds = currentTime - joinTime;

            // Get current playtime and add session time
            long playtime = playerData.getLong("players." + uuid + ".playtime", 0);
            playtime += sessionSeconds;

            // Update playtime
            playerData.set("players." + uuid + ".playtime", playtime);

            // Remove join time
            joinTimes.remove(uuid);
        }
    }

    /**
     * Get a player's total playtime
     *
     * @param uuid The player's UUID
     * @return The player's total playtime in seconds
     */
    public long getTotalPlaytime(UUID uuid) {
        long totalPlaytime = playerData.getLong("players." + uuid + ".playtime", 0);

        // Add current session if online
        if (joinTimes.containsKey(uuid)) {
            long currentTime = System.currentTimeMillis() / 1000;
            long currentSession = currentTime - joinTimes.get(uuid);
            totalPlaytime += currentSession;
        }

        return totalPlaytime;
    }

    /**
     * Set a player's nickname
     *
     * @param uuid The player's UUID
     * @param nickname The nickname to set
     */
    public void setNickname(UUID uuid, String nickname) {
        playerData.set("players." + uuid + ".nickname", nickname);
        savePlayerData();
    }

    /**
     * Get a player's nickname
     *
     * @param uuid The player's UUID
     * @return The player's nickname, or null if not set
     */
    public String getNickname(UUID uuid) {
        if (!playerData.contains("players." + uuid + ".nickname")) {
            return null;
        }
        return playerData.getString("players." + uuid + ".nickname");
    }

    /**
     * Get the UUID for a player name
     *
     * @param name The player name
     * @return The UUID, or null if not found
     */
    public UUID getUuidFromName(String name) {
        String lowerName = name.toLowerCase();
        if (!playerData.contains("usernames." + lowerName)) {
            return null;
        }

        return UUID.fromString(playerData.getString("usernames." + lowerName));
    }

    /**
     * Get the FileConfiguration for direct access
     *
     * @return The player data configuration
     */
    public FileConfiguration getPlayerData() {
        return playerData;
    }
}