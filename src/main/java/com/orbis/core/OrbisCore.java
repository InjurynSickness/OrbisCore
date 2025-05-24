package com.orbis.core;

import com.orbis.core.commands.*;
import com.orbis.core.data.PlayerDataManager;
import com.orbis.core.listeners.PlayerConnectionListener;
import com.orbis.core.listeners.PlayerDeathListener;
import com.orbis.core.tab.PlayerTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for the OrbisCore plugin
 */
public class OrbisCore extends JavaPlugin {

    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        // Create data folder if it doesn't exist
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // Initialize player data manager
        playerDataManager = new PlayerDataManager(this);

        // Register event listeners
        registerListeners();

        // Register commands
        registerCommands();

        getLogger().info("OrbisCore has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save all player data
        playerDataManager.saveAllPlayerData();

        getLogger().info("OrbisCore has been disabled!");
    }

    /**
     * Register all event listeners
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this, playerDataManager), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this, playerDataManager), this);
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
    }

    /**
     * Get the player data manager
     *
     * @return The player data manager
     */
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}