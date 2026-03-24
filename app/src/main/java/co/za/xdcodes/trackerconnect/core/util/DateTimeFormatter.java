package co.za.xdcodes.trackerconnect.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTimeFormatter {

    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        Locale.US
    );

    static {
        ISO_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String formatToReadable(String isoDateString) {
        if (isoDateString == null || isoDateString.isEmpty()) {
            return "Unknown";
        }

        try {
            Date date = ISO_FORMAT.parse(isoDateString);
            if (date == null) {
                return "Unknown";
            }

            SimpleDateFormat readableFormat = new SimpleDateFormat(
                "MMM dd, yyyy h:mm a",
                Locale.US
            );
            return readableFormat.format(date);
        } catch (ParseException e) {
            return "Invalid date";
        }
    }

    public static String getRelativeTime(String isoDateString) {
        if (isoDateString == null || isoDateString.isEmpty()) {
            return "Unknown";
        }

        try {
            Date date = ISO_FORMAT.parse(isoDateString);
            if (date == null) {
                return "Unknown";
            }

            long now = System.currentTimeMillis();
            long diff = now - date.getTime();
            long absDiff = Math.abs(diff);

            boolean isPast = diff > 0;

            long seconds = TimeUnit.MILLISECONDS.toSeconds(absDiff);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(absDiff);
            long hours = TimeUnit.MILLISECONDS.toHours(absDiff);
            long days = TimeUnit.MILLISECONDS.toDays(absDiff);

            String timeUnit;
            if (days > 0) {
                timeUnit = days + (days == 1 ? " day" : " days");
            } else if (hours > 0) {
                timeUnit = hours + (hours == 1 ? " hour" : " hours");
            } else if (minutes > 0) {
                timeUnit = minutes + (minutes == 1 ? " minute" : " minutes");
            } else {
                timeUnit = seconds + (seconds == 1 ? " second" : " seconds");
            }

            return isPast ? timeUnit + " ago" : "in " + timeUnit;
        } catch (ParseException e) {
            return "Unknown";
        }
    }

    public static String formatToShortDate(String isoDateString) {
        if (isoDateString == null || isoDateString.isEmpty()) {
            return "Unknown";
        }

        try {
            Date date = ISO_FORMAT.parse(isoDateString);
            if (date == null) {
                return "Unknown";
            }

            SimpleDateFormat shortFormat = new SimpleDateFormat(
                "MMM dd, yyyy",
                Locale.US
            );
            return shortFormat.format(date);
        } catch (ParseException e) {
            return "Invalid date";
        }
    }
}
