package com.orbis.core.util;

import org.bukkit.ChatColor;

/**
 * Utility class for message formatting
 */
public class MessageUtils {

    /**
     * Format a message with color codes
     *
     * @param message The message to format
     * @return The formatted message
     */
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}