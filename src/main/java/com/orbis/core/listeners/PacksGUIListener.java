package com.orbis.core.listeners;

import com.orbis.core.commands.PacksCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for handling Packs GUI interactions
 */
public class PacksGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        // Get the inventory title
        Component titleComponent = event.getView().title();
        String title = PlainTextComponentSerializer.plainText().serialize(titleComponent);

        // Handle main Pack Loader GUI
        if (title.equals("Pack Loader")) {
            event.setCancelled(true);

            int slot = event.getRawSlot();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            if (slot == 2) { // OrbisCore button
                PacksCommand.openOrbisCoreVersionsGUI(player);
            } else if (slot == 6) { // OrbisAudio button
                PacksCommand.executePackLoad("orbisaudio", player);
            }
        }

        // Handle OrbisCore versions GUI
        else if (title.equals("OrbisCore - Versions")) {
            event.setCancelled(true);

            int slot = event.getRawSlot();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            if (slot == 1) { // OrbisCore 1.21.3
                PacksCommand.executePackLoad("orbiscore-1.21.3", player);
            } else if (slot == 4) { // OrbisCore 1.21.4
                PacksCommand.executePackLoad("orbiscore-1.21.4", player);
            } else if (slot == 7) { // OrbisCore 1.21.5
                PacksCommand.executePackLoad("orbiscore-1.21.5", player);
            }
        }
    }
}