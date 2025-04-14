package ru.practicum.stats.server.util;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static final DateTimeFormatter FORMAT_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime parseDateTime(String date) {
        return LocalDateTime.parse(date, FORMAT_DATE_TIME);
    }

    public static LocalDateTime decodeDateTime(String date) {
        return LocalDateTime.parse(URLDecoder.decode(date), FORMAT_DATE_TIME);
    }
}
