package com.orbis.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Utility class for converting locations to and from strings
 */
public class LocationConverter {

    /**
     * Convert a location to a string
     *
     * @param loc The location to convert
     * @return A string representation of the location
     */
    public static String locationToString(Location loc) {
        return loc.getWorld().getName() + ":" +
                loc.getX() + ":" +
                loc.getY() + ":" +
                loc.getZ() + ":" +
                loc.getYaw() + ":" +
                loc.getPitch();
    }

    /**
     * Convert a string to a location
     *
     * @param str The string to convert
     * @return The location
     */
    public static Location stringToLocation(String str) {
        String[] parts = str.split(":");
        return new Location(
                Bukkit.getWorld(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]),
                Float.parseFloat(parts[4]),
                Float.parseFloat(parts[5])
        );
    }
}