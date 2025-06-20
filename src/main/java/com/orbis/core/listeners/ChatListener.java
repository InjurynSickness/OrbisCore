package com.orbis.core.listeners;

import com.orbis.core.OrbisCore;
import com.orbis.core.data.AFKManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles chat events for item display and AFK message handling
 */
public class ChatListener implements Listener {

    private final OrbisCore plugin;
    private final AFKManager afkManager;
    private final Pattern itemPattern = Pattern.compile("\\[item\\]", Pattern.CASE_INSENSITIVE);

    public ChatListener(OrbisCore plugin, AFKManager afkManager) {
        this.plugin = plugin;
        this.afkManager = afkManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Handle [item] replacement
        if (itemPattern.matcher(message).find() && player.hasPermission("orbiscore.chat.item")) {
            ItemStack heldItem = player.getInventory().getItemInMainHand();
            
            if (heldItem != null && heldItem.getType() != Material.AIR) {
                Component itemComponent = createItemComponent(heldItem);
                String itemText = PlainTextComponentSerializer.plainText().serialize(itemComponent);
                
                // Replace [item] with item display
                String newMessage = itemPattern.matcher(message).replaceAll(itemText);
                event.setMessage(newMessage);
                
                // Send rich component message to all players instead
                event.setCancelled(true);
                
                Component richMessage = Component.text("<" + player.getName() + "> ", NamedTextColor.WHITE)
                    .append(Component.text(itemPattern.matcher(message).replaceAll(""), NamedTextColor.WHITE))
                    .append(itemComponent);
                
                // Find where [item] was and insert the component there
                String[] parts = message.split("(?i)\\[item\\]", -1);
                Component finalMessage = Component.text("<" + player.getName() + "> ", NamedTextColor.WHITE);
                
                for (int i = 0; i < parts.length; i++) {
                    if (!parts[i].isEmpty()) {
                        finalMessage = finalMessage.append(Component.text(parts[i], NamedTextColor.WHITE));
                    }
                    if (i < parts.length - 1) {
                        finalMessage = finalMessage.append(itemComponent);
                    }
                }
                
                Bukkit.broadcast(finalMessage);
                return;
            } else {
                // No item in hand
                player.sendMessage(Component.text("❌ ", NamedTextColor.RED)
                    .append(Component.text("You must be holding an item to use [item]!", NamedTextColor.RED)));
                event.setCancelled(true);
                return;
            }
        }

        // Handle private messages to AFK players
        handleAFKMessages(player, message);
    }

    /**
     * Handle storing messages sent to AFK players
     */
    private void handleAFKMessages(Player sender, String message) {
        // Check if message is a private message (starts with @playername or similar)
        if (message.startsWith("@") || message.startsWith("/msg") || message.startsWith("/tell") || message.startsWith("/w")) {
            String targetName = null;
            
            if (message.startsWith("@")) {
                String[] parts = message.split(" ", 2);
                if (parts.length > 0) {
                    targetName = parts[0].substring(1); // Remove @
                }
            }
            
            if (targetName != null) {
                Player target = Bukkit.getPlayer(targetName);
                if (target != null && afkManager.isAFK(target)) {
                    afkManager.storeMissedMessage(target, sender, message);
                    
                    // Notify sender that player is AFK
                    AFKManager.AFKData afkData = afkManager.getAFKData(target);
                    Component afkNotice = Component.text("💤 ", NamedTextColor.GRAY)
                        .append(Component.text(target.getName(), NamedTextColor.WHITE))
                        .append(Component.text(" is currently AFK", NamedTextColor.GRAY));
                    
                    if (afkData.hasCustomMessage()) {
                        afkNotice = afkNotice.append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(afkData.getCustomMessage(), NamedTextColor.YELLOW, TextDecoration.ITALIC));
                    }
                    
                    afkNotice = afkNotice.append(Component.text(". Your message has been saved.", NamedTextColor.GRAY));
                    sender.sendMessage(afkNotice);
                }
            }
        }
    }

    /**
     * Create a clickable item component for chat
     */
    private Component createItemComponent(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String displayName;
        
        if (meta != null && meta.hasDisplayName()) {
            displayName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        } else {
            // Convert material name to readable format
            displayName = formatMaterialName(item.getType().name());
        }
        
        // Create hover text with item details
        Component hoverText = Component.text(displayName, NamedTextColor.WHITE, TextDecoration.BOLD);
        
        if (meta != null && meta.hasLore()) {
            List<Component> lore = meta.lore();
            if (lore != null) {
                for (Component loreLine : lore) {
                    hoverText = hoverText.appendNewline().append(loreLine);
                }
            }
        }
            
        if (item.getAmount() > 1) {
            hoverText = hoverText.appendNewline()
                .append(Component.text("Amount: ", NamedTextColor.GRAY))
                .append(Component.text(item.getAmount(), NamedTextColor.WHITE));
        }
        
        // Create the main component
        return Component.text("[", NamedTextColor.GRAY)
            .append(Component.text(displayName, NamedTextColor.AQUA, TextDecoration.BOLD))
            .append(Component.text("]", NamedTextColor.GRAY))
            .hoverEvent(HoverEvent.showText(hoverText));
    }

    /**
     * Format material name for display
     */
    private String formatMaterialName(String materialName) {
        String[] words = materialName.toLowerCase().split("_");
        StringBuilder formatted = new StringBuilder();
        
        for (String word : words) {
            if (formatted.length() > 0) {
                formatted.append(" ");
            }
            formatted.append(Character.toUpperCase(word.charAt(0)))
                .append(word.substring(1));
        }
        
        return formatted.toString();
    }
}