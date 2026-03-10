package org.example.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter SIMPLE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private DateUtils() {}

    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    public static boolean isBefore(String date1, String date2) {
        LocalDateTime d1 = parseDateTime(date1);
        LocalDateTime d2 = parseDateTime(date2);
        return d1.isBefore(d2);
    }

    public static boolean isAfter(String date1, String date2) {
        LocalDateTime d1 = parseDateTime(date1);
        LocalDateTime d2 = parseDateTime(date2);
        return d1.isAfter(d2);
    }

    public static boolean isEqual(String date1, String date2) {
        LocalDateTime d1 = parseDateTime(date1);
        LocalDateTime d2 = parseDateTime(date2);
        return d1.isEqual(d2);
    }

    public static String addDays(String date, int days) {
        LocalDateTime dt = parseDateTime(date);
        return dt.plusDays(days).format(DATE_TIME_FORMATTER);
    }

    public static String addDaysSimple(String date, int days) {
        try {
            LocalDate d = LocalDate.parse(date, DATE_FORMATTER);
            return d.plusDays(days).format(DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            LocalDateTime dt = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
            return dt.plusDays(days).format(DATE_TIME_FORMATTER);
        }
    }

    public static String formatRelativeTime(String date) {
        LocalDateTime target = parseDateTime(date);
        LocalDateTime now = LocalDateTime.now();

        if (target.isBefore(now)) {
            long days = ChronoUnit.DAYS.between(target, now);
            if (days == 0) {
                long hours = ChronoUnit.HOURS.between(target, now);
                if (hours == 0) {
                    long minutes = ChronoUnit.MINUTES.between(target, now);
                    return minutes + " minute" + (minutes != 1 ? "s" : "") + " ago";
                }
                return hours + " hour" + (hours != 1 ? "s" : "") + " ago";
            } else if (days < 7) {
                return days + " day" + (days != 1 ? "s" : "") + " ago";
            } else if (days < 30) {
                long weeks = days / 7;
                return weeks + " week" + (weeks != 1 ? "s" : "") + " ago";
            } else if (days < 365) {
                long months = days / 30;
                return months + " month" + (months != 1 ? "s" : "") + " ago";
            } else {
                long years = days / 365;
                return years + " year" + (years != 1 ? "s" : "") + " ago";
            }
        } else {
            long days = ChronoUnit.DAYS.between(now, target);
            if (days == 0) {
                long hours = ChronoUnit.HOURS.between(now, target);
                if (hours == 0) {
                    long minutes = ChronoUnit.MINUTES.between(now, target);
                    return "in " + minutes + " minute" + (minutes != 1 ? "s" : "");
                }
                return "in " + hours + " hour" + (hours != 1 ? "s" : "");
            } else if (days < 7) {
                return "in " + days + " day" + (days != 1 ? "s" : "");
            } else if (days < 30) {
                long weeks = days / 7;
                return "in " + weeks + " week" + (weeks != 1 ? "s" : "");
            } else if (days < 365) {
                long months = days / 30;
                return "in " + months + " month" + (months != 1 ? "s" : "");
            } else {
                long years = days / 365;
                return "in " + years + " year" + (years != 1 ? "s" : "");
            }
        }
    }

    public static long daysBetween(String date1, String date2) {
        LocalDateTime d1 = parseDateTime(date1);
        LocalDateTime d2 = parseDateTime(date2);
        return ChronoUnit.DAYS.between(d1, d2);
    }

    public static boolean isValidDate(String date) {
        try {
            parseDateTime(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static String formatDate(String date) {
        try {
            LocalDateTime dt = parseDateTime(date);
            return dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (DateTimeParseException e) {
            return date;
        }
    }

    public static String formatDateTime(String date) {
        try {
            LocalDateTime dt = parseDateTime(date);
            return dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        } catch (DateTimeParseException e) {
            return date;
        }
    }

    private static LocalDateTime parseDateTime(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new DateTimeParseException("Date is empty", date, 0);
        }

        date = date.trim();

        try {
            if (date.length() == 10) {
                return LocalDate.parse(date, DATE_FORMATTER).atStartOfDay();
            } else if (date.length() == 16) {
                return LocalDateTime.parse(date, SIMPLE_DATE_TIME_FORMATTER);
            } else {
                return LocalDateTime.parse(date, DATE_TIME_FORMATTER);
            }
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("Invalid date format: " + date, date, 0);
        }
    }
}