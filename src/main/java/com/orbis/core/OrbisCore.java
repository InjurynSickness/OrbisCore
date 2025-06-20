package com.orbis.core;

import com.orbis.core.commands.*;
import com.orbis.core.data.AFKManager;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.listeners.*;
import com.orbis.core.tab.PlayerTabCompleter;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
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
    private AFKManager afkManager;
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
        afkManager = new AFKManager(this);

        // Register event listeners
        registerListeners();

        // Register commands
        registerCommands();

        // Start auto-save task
        startAutoSaveTask();

        // Start AFK cleanup task
        startAFKCleanupTask();

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

        // AFK messages
        config.set("messages.afk.enabled", "&7{player} is now AFK");
        config.set("messages.afk.disabled", "&a{player} is no longer AFK");
        config.set("messages.afk.custom", "&7{player} is now AFK: &e{message}");

        // New command messages
        config.set("messages.modhelp.sent", "&aHelp request sent to moderators!");
        config.set("messages.modhelp.no-mods", "&cNo moderators are currently online.");
        config.set("messages.double-barrel", "&6&lDOUBLE BARREL! &eYou received extra loot!");
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
     * Start AFK cleanup task
     */
    private void startAFKCleanupTask() {
        // Clean up old missed messages every 30 minutes
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            afkManager.clearOldMissedMessages();
        }, 36000L, 36000L); // 30 minutes in ticks
    }

    /**
     * Register all event listeners
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this, playerDataManager, afkManager), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this, playerDataManager), this);
        getServer().getPluginManager().registerEvents(new BloodEffectListener(this, playerDataManager), this);
        getServer().getPluginManager().registerEvents(new GodmodeListener(), this);
        getServer().getPluginManager().registerEvents(new PacksGUIListener(), this);
        getServer().getPluginManager().registerEvents(new GoldOreDropListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this, afkManager), this);
        getServer().getPluginManager().registerEvents(new AFKListener(afkManager), this);
    }

    /**
     * Register all commands with null checking
     */
    private void registerCommands() {
        // Create tab completer for player names
        PlayerTabCompleter playerTabCompleter = new PlayerTabCompleter();

        // Helper method to safely register commands
        registerCommand("fly", new FlyCommand(this), playerTabCompleter);
        registerCommand("tp", new TeleportCommand(this, playerDataManager), playerTabCompleter);
        registerCommand("tphere", new TpHereCommand(this, playerDataManager), playerTabCompleter);
        registerCommand("tpoffline", new TpOfflineCommand(this, playerDataManager), null);
        registerCommand("speed", new SpeedCommand(this), playerTabCompleter);
        registerCommand("flyspeed", new FlySpeedCommand(this), playerTabCompleter);
        registerCommand("sudo", new SudoCommand(this), playerTabCompleter);
        registerCommand("seen", new SeenCommand(this, playerDataManager), playerTabCompleter);
        registerCommand("back", new BackCommand(this, playerDataManager), null);
        registerCommand("ping", new PingCommand(this), null);
        registerCommand("broadcast", new BroadcastCommand(this), null);
        registerCommand("playtime", new PlaytimeCommand(this, playerDataManager), playerTabCompleter);
        registerCommand("nickname", new NicknameCommand(this, playerDataManager), playerTabCompleter);
        registerCommand("gmc", new GamemodeCommand(this, GameMode.CREATIVE), playerTabCompleter);
        registerCommand("gms", new GamemodeCommand(this, GameMode.SURVIVAL), playerTabCompleter);
        registerCommand("gma", new GamemodeCommand(this, GameMode.ADVENTURE), playerTabCompleter);
        registerCommand("gmsp", new GamemodeCommand(this, GameMode.SPECTATOR), playerTabCompleter);
        registerCommand("heal", new HealCommand(this), playerTabCompleter);
        registerCommand("suicide", new SuicideCommand(this), null);
        registerCommand("toggleblood", new ToggleBloodCommand(this, playerDataManager), null);
        registerCommand("godmode", new GodmodeCommand(this), playerTabCompleter);
        registerCommand("map", new MapCommand(this), null);
        registerCommand("discord", new DiscordCommand(this), null);
        registerCommand("store", new StoreCommand(this), null);
        registerCommand("website", new WebsiteCommand(this), null);
        registerCommand("vote", new VoteCommand(this), null);
        registerCommand("orbiscore", new ReloadCommand(this), null);
        registerCommand("top", new TopCommand(this, playerDataManager), playerTabCompleter);
        registerCommand("packs", new PacksCommand(this), null);
        registerCommand("support", new SupportCommand(this), null);
        registerCommand("docs", new DocsCommand(this), null);
        registerCommand("guide", new DocsCommand(this), null); // Alias for docs
        registerCommand("afk", new AFKCommand(this, afkManager), null);
        registerCommand("modhelp", new ModHelpCommand(this), null);
    }

    /**
     * Helper method to safely register a command with null checking
     */
    private void registerCommand(String commandName, CommandExecutor executor, PlayerTabCompleter tabCompleter) {
        PluginCommand command = getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
            if (tabCompleter != null) {
                command.setTabCompleter(tabCompleter);
            }
            getLogger().info("Successfully registered command: /" + commandName);
        } else {
            getLogger().warning("Failed to register command: /" + commandName + " (command not found in plugin.yml)");
        }
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
     * Get the AFK manager
     *
     * @return The AFK manager
     */
    public AFKManager getAFKManager() {
        return afkManager;
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