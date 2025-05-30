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
                plugin.getLogger().info("Created new playerdata.yml file");
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create playerdata.yml", e);
            }
        }

        playerData = YamlConfiguration.loadConfiguration(playerDataFile);

        // Initialize default sections if they don't exist
        if (!playerData.contains("players")) {
            playerData.createSection("players");
        }
        if (!playerData.contains("usernames")) {
            playerData.createSection("usernames");
        }

        savePlayerData(); // Save initial structure
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
        plugin.getLogger().info("Saved data for " + Bukkit.getOnlinePlayers().size() + " online players");
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

        // Only store IP if player has address (avoid NPE)
        if (player.getAddress() != null && player.getAddress().getAddress() != null) {
            playerData.set("players." + uuid + ".ip", player.getAddress().getAddress().getHostAddress());
        }

        // Set first join time if it's their first time
        if (!playerData.contains("players." + uuid + ".firstJoin")) {
            playerData.set("players." + uuid + ".firstJoin", LocalDateTime.now().format(dateFormat));
        }

        // Map username to UUID for lookup (case-insensitive)
        playerData.set("usernames." + player.getName().toLowerCase(), uuid.toString());

        // Load stored nickname if exists
        String nickname = getNickname(uuid);
        if (nickname != null && !nickname.equals("none")) {
            player.setDisplayName(nickname);
        }

        // Save data asynchronously to avoid blocking
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::savePlayerData);
    }

    /**
     * Record a player's quit
     *
     * @param player The player who quit
     */
    public void recordPlayerQuit(Player player) {
        UUID uuid = player.getUniqueId();

        // Store logout location and time safely
        try {
            Location location = player.getLocation();
            if (location != null && location.getWorld() != null) {
                playerData.set("players." + uuid + ".lastLogoutLocation", LocationConverter.locationToString(location));
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Could not save logout location for " + player.getName(), e);
        }

        playerData.set("players." + uuid + ".lastLogout", LocalDateTime.now().format(dateFormat));

        // Update playtime
        updatePlaytime(uuid);

        // Save player data asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::savePlayerData);

        // Clean up memory maps
        joinTimes.remove(uuid);
        lastTeleportLocations.remove(uuid);
        lastDeathLocations.remove(uuid);
    }

    /**
     * Record a player's death location
     *
     * @param player The player who died
     * @param location The death location
     */
    public void recordDeathLocation(Player player, Location location) {
        if (location != null && location.getWorld() != null) {
            lastDeathLocations.put(player.getUniqueId(), location.clone());
        }
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
        if (location != null && location.getWorld() != null) {
            lastTeleportLocations.put(uuid, location.clone());
        }
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
        if (location != null && location.getWorld() != null) {
            lastTeleportLocations.put(uuid, location.clone());
        }
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

            // Only add positive session time (prevent time manipulation)
            if (sessionSeconds > 0) {
                // Get current playtime and add session time
                long playtime = playerData.getLong("players." + uuid + ".playtime", 0);
                playtime += sessionSeconds;

                // Update playtime
                playerData.set("players." + uuid + ".playtime", playtime);
            }

            // Update join time for next calculation
            joinTimes.put(uuid, currentTime);
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
            if (currentSession > 0) {
                totalPlaytime += currentSession;
            }
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
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = "none";
        }
        playerData.set("players." + uuid + ".nickname", nickname);

        // Save asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::savePlayerData);
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
        String nickname = playerData.getString("players." + uuid + ".nickname");
        return (nickname != null && !nickname.equals("none")) ? nickname : null;
    }

    /**
     * Set blood effect enabled status for a player
     *
     * @param uuid The player's UUID
     * @param enabled Whether blood effect is enabled
     */
    public void setBloodEffectEnabled(UUID uuid, boolean enabled) {
        playerData.set("players." + uuid + ".bloodEffect", enabled);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::savePlayerData);
    }

    /**
     * Get blood effect enabled status for a player
     *
     * @param uuid The player's UUID
     * @return Whether blood effect is enabled (defaults to true)
     */
    public boolean getBloodEffectEnabled(UUID uuid) {
        return playerData.getBoolean("players." + uuid + ".bloodEffect", true);
    }

    /**
     * Get the UUID for a player name (case-insensitive)
     *
     * @param name The player name
     * @return The UUID, or null if not found
     */
    public UUID getUuidFromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        String lowerName = name.toLowerCase().trim();
        if (!playerData.contains("usernames." + lowerName)) {
            return null;
        }

        try {
            String uuidString = playerData.getString("usernames." + lowerName);
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().log(Level.WARNING, "Invalid UUID stored for player " + name, e);
            return null;
        }
    }

    /**
     * Check if a player has ever joined the server
     *
     * @param uuid The player's UUID
     * @return True if the player has data stored
     */
    public boolean hasPlayerData(UUID uuid) {
        return playerData.contains("players." + uuid);
    }

    /**
     * Get the FileConfiguration for direct access
     *
     * @return The player data configuration
     */
    public FileConfiguration getPlayerData() {
        return playerData;
    }

    /**
     * Reload player data from disk
     */
    public void reloadPlayerData() {
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        plugin.getLogger().info("Reloaded player data from disk");
    }

    /**
     * Get a player's first join date
     *
     * @param uuid The player's UUID
     * @return The first join date as a string, or null if not found
     */
    public String getFirstJoinDate(UUID uuid) {
        return playerData.getString("players." + uuid + ".firstJoin");
    }

    /**
     * Get a player's last logout date
     *
     * @param uuid The player's UUID
     * @return The last logout date as a string, or null if not found
     */
    public String getLastLogoutDate(UUID uuid) {
        return playerData.getString("players." + uuid + ".lastLogout");
    }

    /**
     * Get a player's last known IP address
     *
     * @param uuid The player's UUID
     * @return The IP address as a string, or null if not found
     */
    public String getLastKnownIP(UUID uuid) {
        return playerData.getString("players." + uuid + ".ip");
    }

    /**
     * Get a player's logout location as a string
     *
     * @param uuid The player's UUID
     * @return The logout location string, or null if not found
     */
    public String getLogoutLocationString(UUID uuid) {
        return playerData.getString("players." + uuid + ".lastLogoutLocation");
    }

    /**
     * Get a player's stored name
     *
     * @param uuid The player's UUID
     * @return The stored name, or null if not found
     */
    public String getStoredPlayerName(UUID uuid) {
        return playerData.getString("players." + uuid + ".name");
    }

    /**
     * Update a player's login time
     *
     * @param uuid The player's UUID
     */
    public void updateLoginTime(UUID uuid) {
        joinTimes.put(uuid, System.currentTimeMillis() / 1000);
    }

    /**
     * Get current session time for a player
     *
     * @param uuid The player's UUID
     * @return Session time in seconds, or 0 if not found
     */
    public long getCurrentSessionTime(UUID uuid) {
        if (!joinTimes.containsKey(uuid)) {
            return 0;
        }

        long currentTime = System.currentTimeMillis() / 1000;
        long sessionTime = currentTime - joinTimes.get(uuid);
        return Math.max(0, sessionTime);
    }

    /**
     * Check if a player is currently tracked as online
     *
     * @param uuid The player's UUID
     * @return True if the player has a join time recorded
     */
    public boolean isPlayerTracked(UUID uuid) {
        return joinTimes.containsKey(uuid);
    }

    /**
     * Force save player data synchronously (use sparingly)
     */
    public void forceSavePlayerData() {
        try {
            playerData.save(playerDataFile);
            plugin.getLogger().info("Force saved player data");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not force save playerdata.yml", e);
        }
    }

    /**
     * Clean up data for a specific player
     *
     * @param uuid The player's UUID
     */
    public void cleanupPlayerData(UUID uuid) {
        joinTimes.remove(uuid);
        lastTeleportLocations.remove(uuid);
        lastDeathLocations.remove(uuid);
    }

    
    /**
     * Get all stored player UUIDs
     *
     * @return Set of UUIDs that have data stored
     */
    public java.util.Set<String> getStoredPlayerUUIDs() {
        if (!playerData.contains("players")) {
            return new java.util.HashSet<>();
        }
        return playerData.getConfigurationSection("players").getKeys(false);
    }

    /**
     * Get statistics about stored data
     *
     * @return String with data statistics
     */
    public String getDataStatistics() {
        int totalPlayers = getStoredPlayerUUIDs().size();
        int onlineTracked = joinTimes.size();
        int teleportLocations = lastTeleportLocations.size();
        int deathLocations = lastDeathLocations.size();

        return String.format("Players: %d total, %d online, %d teleport locations, %d death locations",
                totalPlayers, onlineTracked, teleportLocations, deathLocations);
    }

    /**
     * Backup player data to a backup file
     *
     * @return True if backup was successful
     */
    public boolean backupPlayerData() {
        try {
            File backupFile = new File(plugin.getDataFolder(), "playerdata-backup-" + System.currentTimeMillis() + ".yml");
            java.nio.file.Files.copy(playerDataFile.toPath(), backupFile.toPath());
            plugin.getLogger().info("Created player data backup: " + backupFile.getName());
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Could not backup player data", e);
            return false;
        }
    }
}