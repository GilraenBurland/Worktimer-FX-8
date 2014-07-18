package wfx8.util;

import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    private static final DateTimeFormatter GENERAL_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static String formatGeneralTime(Temporal dateTime) {
        return GENERAL_TIME_FORMATTER.format(dateTime);
    }

    private static final DateTimeFormatter REMAINING_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static String formatRemainingTime(Temporal dateTime) {
        return REMAINING_TIME_FORMATTER.format(dateTime);
    }
}
