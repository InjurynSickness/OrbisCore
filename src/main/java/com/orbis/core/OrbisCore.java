package com.orbis.core;

import com.orbis.core.commands.*;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.listeners.BloodEffectListener;
import com.orbis.core.listeners.PlayerConnectionListener;
import com.orbis.core.listeners.PlayerDeathListener;
import com.orbis.core.listeners.GodmodeListener;
import com.orbis.core.tab.PlayerTabCompleter;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Main class for the OrbisCore plugin
 */
public class OrbisCore extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private FileConfiguration config;
    private File configFile;
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = 
        LegacyComponentSerializer.legacyAmpersand();

    @Override
    public void onEnable() {
        // Create data folder if it doesn't exist
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // Load configuration
        loadConfiguration();

        // Initialize managers
        playerDataManager = new PlayerDataManager(this);

        // Register event listeners
        registerListeners();

        // Register commands
        registerCommands();

        // Start auto-save task
        startAutoSaveTask();

        getLogger().info("OrbisCore has been enabled!");
        getLogger().info("Loaded configuration with " + getPluginConfig().getKeys(false).size() + " settings");
    }

    @Override
    public void onDisable() {
        // Cancel all tasks
        Bukkit.getScheduler().cancelTasks(this);

        // Clear godmode for all players
        GodmodeCommand.clearAllGodmode();

        // Save all player data
        playerDataManager.saveAllPlayerData();

        getLogger().info("OrbisCore has been disabled!");
    }

    /**
     * Load or create configuration file
     */
    private void loadConfiguration() {
        configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                // Create default config
                config = YamlConfiguration.loadConfiguration(configFile);
                setDefaultConfig();
                saveConfiguration();
                getLogger().info("Created default configuration file");
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Could not create config.yml", e);
            }
        } else {
            config = YamlConfiguration.loadConfiguration(configFile);
        }
    }

    /**
     * Set default configuration values
     */
    private void setDefaultConfig() {
        config.set("settings.auto-save-interval", 300); // 5 minutes
        config.set("settings.max-nickname-length", 16);
        config.set("settings.allow-color-codes", true);

        // Messages (keeping legacy format for config compatibility)
        config.set("messages.no-permission", "&cYou don't have permission to use this command!");
        config.set("messages.player-not-online", "&cPlayer {player} is not online!");
        config.set("messages.player-not-found", "&cPlayer {player} has never been seen on this server!");
        config.set("messages.config-reloaded", "&aConfiguration reloaded successfully!");

        // Command specific messages
        config.set("messages.fly.enabled", "&aFlight mode enabled.");
        config.set("messages.fly.disabled", "&cFlight mode disabled.");
        config.set("messages.heal.self", "&aYou have been healed!");
        config.set("messages.heal.other", "&aHealed {player}!");
        config.set("messages.nickname.set", "&aYour nickname has been set to: &r{nickname}");
        config.set("messages.nickname.reset", "&aYour nickname has been reset!");
        config.set("messages.nickname.too-long", "&cNickname too long! Max {max} characters.");

        // New messages for suicide and blood effect
        config.set("messages.suicide.broadcast", "&c{player} has committed suicide.");
        config.set("messages.suicide.cooldown", "&cTry again in {time}.");
        config.set("messages.blood.enabled", "&aBlood effect enabled.");
        config.set("messages.blood.disabled", "&cBlood effect disabled.");
        
        // Godmode messages
        config.set("messages.godmode.enabled", "&aGodmode enabled.");
        config.set("messages.godmode.disabled", "&cGodmode disabled.");
    }

    /**
     * Save configuration file
     */
    private void saveConfiguration() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save config.yml", e);
        }
    }

    /**
     * Reload configuration and player data
     */
    public void reloadConfiguration() {
        config = YamlConfiguration.loadConfiguration(configFile);
        playerDataManager.reloadPlayerData();
        getLogger().info("Configuration and player data reloaded!");
    }

    /**
     * Start auto-save task
     */
    private void startAutoSaveTask() {
        int interval = config.getInt("settings.auto-save-interval", 300) * 20; // Convert to ticks

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            playerDataManager.saveAllPlayerData();
            getLogger().info("Auto-saved player data");
        }, interval, interval);
    }

    /**
     * Register all event listeners
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this, playerDataManager), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this, playerDataManager), this);
        getServer().getPluginManager().registerEvents(new BloodEffectListener(this, playerDataManager), this);
        getServer().getPluginManager().registerEvents(new GodmodeListener(), this);
    }

    /**
     * Register all commands
     */
    private void registerCommands() {
        // Create tab completer for player names
        PlayerTabCompleter playerTabCompleter = new PlayerTabCompleter();

        // Register all commands
        getCommand("fly").setExecutor(new FlyCommand(this));
        getCommand("tp").setExecutor(new TeleportCommand(this, playerDataManager));
        getCommand("tphere").setExecutor(new TpHereCommand(this, playerDataManager));
        getCommand("tpoffline").setExecutor(new TpOfflineCommand(this, playerDataManager));
        getCommand("speed").setExecutor(new SpeedCommand(this));
        getCommand("flyspeed").setExecutor(new FlySpeedCommand(this));
        getCommand("sudo").setExecutor(new SudoCommand(this));
        getCommand("seen").setExecutor(new SeenCommand(this, playerDataManager));
        getCommand("back").setExecutor(new BackCommand(this, playerDataManager));
        getCommand("ping").setExecutor(new PingCommand(this));
        getCommand("broadcast").setExecutor(new BroadcastCommand(this));
        getCommand("playtime").setExecutor(new PlaytimeCommand(this, playerDataManager));
        getCommand("nickname").setExecutor(new NicknameCommand(this, playerDataManager));
        getCommand("gmc").setExecutor(new GamemodeCommand(this, GameMode.CREATIVE));
        getCommand("gms").setExecutor(new GamemodeCommand(this, GameMode.SURVIVAL));
        getCommand("gma").setExecutor(new GamemodeCommand(this, GameMode.ADVENTURE));
        getCommand("gmsp").setExecutor(new GamemodeCommand(this, GameMode.SPECTATOR));
        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("suicide").setExecutor(new SuicideCommand(this));
        getCommand("toggleblood").setExecutor(new ToggleBloodCommand(this, playerDataManager));
        getCommand("godmode").setExecutor(new GodmodeCommand(this));
        getCommand("map").setExecutor(new MapCommand(this));
        getCommand("discord").setExecutor(new DiscordCommand(this));
        getCommand("store").setExecutor(new StoreCommand(this));
        getCommand("website").setExecutor(new WebsiteCommand(this));
        getCommand("vote").setExecutor(new VoteCommand(this));
        getCommand("orbiscore").setExecutor(new ReloadCommand(this));

        // Set tab completers
        getCommand("fly").setTabCompleter(playerTabCompleter);
        getCommand("tp").setTabCompleter(playerTabCompleter);
        getCommand("tphere").setTabCompleter(playerTabCompleter);
        getCommand("speed").setTabCompleter(playerTabCompleter);
        getCommand("flyspeed").setTabCompleter(playerTabCompleter);
        getCommand("sudo").setTabCompleter(playerTabCompleter);
        getCommand("seen").setTabCompleter(playerTabCompleter);
        getCommand("playtime").setTabCompleter(playerTabCompleter);
        getCommand("nickname").setTabCompleter(playerTabCompleter);
        getCommand("gmc").setTabCompleter(playerTabCompleter);
        getCommand("gms").setTabCompleter(playerTabCompleter);
        getCommand("gma").setTabCompleter(playerTabCompleter);
        getCommand("gmsp").setTabCompleter(playerTabCompleter);
        getCommand("heal").setTabCompleter(playerTabCompleter);
        getCommand("godmode").setTabCompleter(playerTabCompleter);
        getCommand("orbvanish").setTabCompleter(playerTabCompleter);
    }

    /**
     * Get the player data manager
     *
     * @return The player data manager
     */
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    /**
     * Get the plugin configuration
     *
     * @return The plugin configuration
     */
    public FileConfiguration getPluginConfig() {
        return config;
    }

    /**
     * Get a formatted message from config as Component
     *
     * @param key The message key
     * @return The formatted message as Component
     */
    public Component getMessageComponent(String key) {
        String message = config.getString("messages." + key, "&cMessage not found: " + key);
        return MessageUtils.colorize(message);
    }

    /**
     * Get a formatted message from config as Component with placeholders
     *
     * @param key The message key
     * @param placeholders Key-value pairs for placeholders
     * @return The formatted message as Component
     */
    public Component getMessageComponent(String key, String... placeholders) {
        String message = config.getString("messages." + key, "&cMessage not found: " + key);

        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }

        return MessageUtils.colorize(message);
    }

    /**
     * Get a formatted message from config (legacy method for backwards compatibility)
     *
     * @param key The message key
     * @return The formatted message as String
     */
    public String getMessage(String key) {
        return config.getString("messages." + key, "&cMessage not found: " + key);
    }

    /**
     * Get a formatted message from config with placeholders (legacy method)
     *
     * @param key The message key
     * @param placeholders Key-value pairs for placeholders
     * @return The formatted message as String
     */
    public String getMessage(String key, String... placeholders) {
        String message = getMessage(key);

        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }

        return message;
    }
}