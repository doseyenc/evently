package com.doseyenc.evently.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static String formatTimeAgo(long timestampMillis) {
        long diff = System.currentTimeMillis() - timestampMillis;
        if (diff < 60_000) return "just now";
        if (diff < 3600_000) return (diff / 60_000) + "m ago";
        if (diff < 86400_000) return (diff / 3600_000) + "h ago";
        return (diff / 86400_000) + "d ago";
    }

    public static String formatDateLabel(long dateMillis) {
        return Instant.ofEpochMilli(dateMillis)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("MMM d", Locale.US))
                .toUpperCase(Locale.US);
    }
}
