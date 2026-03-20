package com.dropsett.app.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String today() {
        return LocalDate.now().format(FORMATTER);
    }

    public static String formatDisplay(String isoDate) {
        LocalDate date = LocalDate.parse(isoDate, FORMATTER);
        return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    public static String[] DAY_NAMES = {
            "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday", "Sunday"
    };
}