package com.orbis.core.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Utility class for message formatting using Adventure API
 */
public class MessageUtils {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = 
        LegacyComponentSerializer.legacyAmpersand();

    /**
     * Convert legacy color codes to Adventure Component
     *
     * @param message The message with legacy color codes
     * @return The Adventure Component
     */
    public static Component colorize(String message) {
        return LEGACY_SERIALIZER.deserialize(message);
    }

    /**
     * Create a simple colored text component
     *
     * @param text The text content
     * @param color The named color
     * @return The Adventure Component
     */
    public static Component text(String text, NamedTextColor color) {
        return Component.text(text, color);
    }

    /**
     * Create a simple colored text component with decoration
     *
     * @param text The text content
     * @param color The named color
     * @param decoration The text decoration
     * @return The Adventure Component
     */
    public static Component text(String text, NamedTextColor color, TextDecoration decoration) {
        return Component.text(text, color, decoration);
    }

    /**
     * Create a simple text component with hex color
     *
     * @param text The text content
     * @param hexColor The hex color (e.g., "#FF5555")
     * @return The Adventure Component
     */
    public static Component text(String text, String hexColor) {
        return Component.text(text, TextColor.fromHexString(hexColor));
    }

    /**
     * Create an error message component
     *
     * @param message The error message
     * @return Red colored error component
     */
    public static Component error(String message) {
        return Component.text(message, NamedTextColor.RED);
    }

    /**
     * Create a success message component
     *
     * @param message The success message
     * @return Green colored success component
     */
    public static Component success(String message) {
        return Component.text(message, NamedTextColor.GREEN);
    }

    /**
     * Create a warning message component
     *
     * @param message The warning message
     * @return Yellow colored warning component
     */
    public static Component warning(String message) {
        return Component.text(message, NamedTextColor.YELLOW);
    }

    /**
     * Create an info message component
     *
     * @param message The info message
     * @return Aqua colored info component
     */
    public static Component info(String message) {
        return Component.text(message, NamedTextColor.AQUA);
    }

    /**
     * Create a prefix component for messages
     *
     * @param prefix The prefix text
     * @param message The message text
     * @return Combined component with prefix
     */
    public static Component prefixed(String prefix, String message) {
        return Component.text("[", NamedTextColor.GRAY)
            .append(Component.text(prefix, NamedTextColor.GOLD))
            .append(Component.text("] ", NamedTextColor.GRAY))
            .append(Component.text(message, NamedTextColor.WHITE));
    }

    /**
     * Create a broadcast message component
     *
     * @param message The broadcast message
     * @return Formatted broadcast component
     */
    public static Component broadcast(String message) {
        return Component.text("[", NamedTextColor.DARK_GRAY)
            .append(Component.text("BROADCAST", NamedTextColor.DARK_RED, TextDecoration.BOLD))
            .append(Component.text("] ", NamedTextColor.DARK_GRAY))
            .append(Component.text(message, NamedTextColor.WHITE));
    }

    /**
     * Create a player info header component
     *
     * @param playerName The player name
     * @return Formatted header component
     */
    public static Component playerHeader(String playerName) {
        return Component.text("=== ", NamedTextColor.GOLD)
            .append(Component.text(playerName, NamedTextColor.WHITE))
            .append(Component.text(" ===", NamedTextColor.GOLD));
    }

    /**
     * Create a labeled info component
     *
     * @param label The label text
     * @param value The value text
     * @return Formatted info component
     */
    public static Component labeledInfo(String label, String value) {
        return Component.text(label + ": ", NamedTextColor.GRAY)
            .append(Component.text(value, NamedTextColor.WHITE));
    }

    /**
     * Create a clickable link component with prefix
     *
     * @param prefixText The prefix text
     * @param linkText The clickable text
     * @return Formatted clickable component
     */
    public static Component clickableLink(String prefixText, String linkText) {
        return Component.text("» ", NamedTextColor.GOLD)
            .append(Component.text(prefixText, NamedTextColor.YELLOW));
    }
}