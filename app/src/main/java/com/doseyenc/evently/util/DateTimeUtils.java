package com.doseyenc.evently.util;

import java.util.Calendar;
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
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateMillis);
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return (month != null ? month.toUpperCase(Locale.US) : "") + " " + day;
    }
}
