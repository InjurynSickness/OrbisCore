package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import com.orbis.core.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Command to open the pack loader GUI
 */
public class PacksCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public PacksCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.error("This command can only be used by players."));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("orbiscore.packs")) {
            player.sendMessage(plugin.getMessageComponent("no-permission"));
            return true;
        }

        openMainPacksGUI(player);
        return true;
    }

    /**
     * Open the main packs GUI
     *
     * @param player The player to open the GUI for
     */
    public static void openMainPacksGUI(Player player) {
        // Create the main GUI
        Inventory gui = Bukkit.createInventory(null, 9,
                Component.text("Pack Loader", NamedTextColor.GOLD));

        // Create OrbisCore button
        ItemStack orbisCoreButton = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta orbisCoreeMeta = orbisCoreButton.getItemMeta();
        orbisCoreeMeta.displayName(Component.text("OrbisCore", NamedTextColor.GOLD));
        orbisCoreeMeta.lore(Arrays.asList(
                Component.text("Click to load a desired version", NamedTextColor.GRAY)
        ));
        orbisCoreButton.setItemMeta(orbisCoreeMeta);

        // Create OrbisAudio button
        ItemStack orbisAudioButton = new ItemStack(Material.NOTE_BLOCK);
        ItemMeta orbisAudioMeta = orbisAudioButton.getItemMeta();
        orbisAudioMeta.displayName(Component.text("OrbisAudio", NamedTextColor.GOLD));
        orbisAudioMeta.lore(Arrays.asList(
                Component.text("Click to load OrbisAudio", NamedTextColor.GRAY)
        ));
        orbisAudioButton.setItemMeta(orbisAudioMeta);

        // Set items in GUI
        gui.setItem(2, orbisCoreButton);
        gui.setItem(6, orbisAudioButton);

        // Open GUI
        player.openInventory(gui);
    }

    /**
     * Open the OrbisCore versions GUI
     *
     * @param player The player to open the GUI for
     */
    public static void openOrbisCoreVersionsGUI(Player player) {
        // Create the versions GUI
        Inventory gui = Bukkit.createInventory(null, 9,
                Component.text("OrbisCore - Versions", NamedTextColor.GOLD));

        // Create version buttons
        ItemStack version1213 = createVersionButton("OrbisCore 1.21.3", "Click to load OrbisCore 1.21.3");
        ItemStack version1214 = createVersionButton("OrbisCore 1.21.4", "Click to load OrbisCore 1.21.4");
        ItemStack version1215 = createVersionButton("OrbisCore 1.21.5", "Click to load OrbisCore 1.21.5");

        // Set items in GUI
        gui.setItem(1, version1213);
        gui.setItem(4, version1214);
        gui.setItem(7, version1215);

        // Open GUI
        player.openInventory(gui);
    }

    /**
     * Create a version button item
     *
     * @param name The display name
     * @param lore The lore text
     * @return The created ItemStack
     */
    private static ItemStack createVersionButton(String name, String lore) {
        ItemStack button = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta meta = button.getItemMeta();
        meta.displayName(Component.text(name, NamedTextColor.AQUA));
        meta.lore(Arrays.asList(
                Component.text(lore, NamedTextColor.GRAY)
        ));
        button.setItemMeta(meta);
        return button;
    }

    /**
     * Execute a pack load command
     *
     * @param packName The pack name to load
     * @param player The player to load for
     */
    public static void executePackLoad(String packName, Player player) {
        // Close the player's inventory
        player.closeInventory();

        // Execute the console command
        String command = "pack load " + packName + " " + player.getName();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

        // Send confirmation message to player
        Component message = Component.text("📦 ", NamedTextColor.GREEN, TextDecoration.BOLD)
                .append(Component.text("Loading pack: ", NamedTextColor.GREEN))
                .append(Component.text(packName, NamedTextColor.YELLOW, TextDecoration.BOLD));

        player.sendMessage(message);
    }
}