package com.orbis.core.util;

/**
 * Utility class for formatting time
 */
public class TimeFormatter {

    /**
     * Format seconds into a readable time string
     *
     * @param seconds The number of seconds
     * @return A formatted time string
     */
    public static String formatTime(long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        StringBuilder formattedTime = new StringBuilder();

        if (days == 1) {
            formattedTime.append(days).append(" day ");
        } else if (days > 1) {
            formattedTime.append(days).append(" days ");
        }

        if (hours == 1) {
            formattedTime.append(hours).append(" hour ");
        } else if (hours > 1) {
            formattedTime.append(hours).append(" hours ");
        }

        if (minutes == 1) {
            formattedTime.append(minutes).append(" minute ");
        } else if (minutes > 1) {
            formattedTime.append(minutes).append(" minutes ");
        }

        if (secs == 1) {
            formattedTime.append(secs).append(" second");
        } else if (secs > 1) {
            formattedTime.append(secs).append(" seconds");
        }

        if (formattedTime.length() == 0) {
            return "0 seconds";
        }

        return formattedTime.toString().trim();
    }
}