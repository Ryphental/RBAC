package org.example.util;

import java.util.List;

public class FormatUtils {

    private static final String HORIZONTAL_LINE = "─";
    private static final String VERTICAL_LINE = "│";
    private static final String CROSS = "┼";
    private static final String TOP_LEFT = "┌";
    private static final String TOP_RIGHT = "┐";
    private static final String BOTTOM_LEFT = "└";
    private static final String BOTTOM_RIGHT = "┘";
    private static final String TOP_MID = "┬";
    private static final String BOTTOM_MID = "┴";
    private static final String LEFT_MID = "├";
    private static final String RIGHT_MID = "┤";

    private FormatUtils() {}

    public static String formatTable(String[] headers, List<String[]> rows) {
        if (headers == null || headers.length == 0) {
            return "";
        }

        int[] columnWidths = calculateColumnWidths(headers, rows);

        StringBuilder sb = new StringBuilder();

        // Top border
        sb.append(TOP_LEFT);
        for (int i = 0; i < columnWidths.length; i++) {
            sb.append(repeat(HORIZONTAL_LINE, columnWidths[i] + 2));
            if (i < columnWidths.length - 1) {
                sb.append(TOP_MID);
            }
        }
        sb.append(TOP_RIGHT).append("\n");

        // Header
        sb.append(VERTICAL_LINE);
        for (int i = 0; i < headers.length; i++) {
            sb.append(" ").append(padRight(headers[i], columnWidths[i])).append(" ").append(VERTICAL_LINE);
        }
        sb.append("\n");

        // Header-bottom border
        sb.append(LEFT_MID);
        for (int i = 0; i < columnWidths.length; i++) {
            sb.append(repeat(HORIZONTAL_LINE, columnWidths[i] + 2));
            if (i < columnWidths.length - 1) {
                sb.append(CROSS);
            }
        }
        sb.append(RIGHT_MID).append("\n");

        // Rows
        for (String[] row : rows) {
            sb.append(VERTICAL_LINE);
            for (int i = 0; i < row.length; i++) {
                String cell = i < row.length ? row[i] : "";
                sb.append(" ").append(padRight(truncate(cell, columnWidths[i]), columnWidths[i])).append(" ").append(VERTICAL_LINE);
            }
            sb.append("\n");
        }

        // Bottom border
        sb.append(BOTTOM_LEFT);
        for (int i = 0; i < columnWidths.length; i++) {
            sb.append(repeat(HORIZONTAL_LINE, columnWidths[i] + 2));
            if (i < columnWidths.length - 1) {
                sb.append(BOTTOM_MID);
            }
        }
        sb.append(BOTTOM_RIGHT).append("\n");

        return sb.toString();
    }

    public static String formatBox(String text) {
        String[] lines = text.split("\n");
        int maxLength = 0;
        for (String line : lines) {
            maxLength = Math.max(maxLength, line.length());
        }

        StringBuilder sb = new StringBuilder();

        // Top border
        sb.append(TOP_LEFT).append(repeat(HORIZONTAL_LINE, maxLength + 2)).append(TOP_RIGHT).append("\n");

        // Content
        for (String line : lines) {
            sb.append(VERTICAL_LINE).append(" ").append(padRight(line, maxLength)).append(" ").append(VERTICAL_LINE).append("\n");
        }

        // Bottom border
        sb.append(BOTTOM_LEFT).append(repeat(HORIZONTAL_LINE, maxLength + 2)).append(BOTTOM_RIGHT).append("\n");

        return sb.toString();
    }

    public static String formatHeader(String text) {
        String line = repeat("═", text.length() + 4);
        return String.format("╔%s╗\n║  %s  ║\n╚%s╝", line, text, line);
    }

    public static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    public static String padRight(String text, int length) {
        if (text == null) text = "";
        return String.format("%-" + length + "s", text);
    }

    public static String padLeft(String text, int length) {
        if (text == null) text = "";
        return String.format("%" + length + "s", text);
    }

    public static String padCenter(String text, int length) {
        if (text == null) text = "";
        int padding = length - text.length();
        if (padding <= 0) return text;
        int leftPad = padding / 2;
        int rightPad = padding - leftPad;
        return repeat(" ", leftPad) + text + repeat(" ", rightPad);
    }

    public static String repeat(String str, int count) {
        return String.valueOf(str).repeat(Math.max(0, count));
    }

    private static int[] calculateColumnWidths(String[] headers, List<String[]> rows) {
        int[] widths = new int[headers.length];

        // Initialize with header lengths
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }

        // Update with row data
        for (String[] row : rows) {
            for (int i = 0; i < Math.min(row.length, headers.length); i++) {
                if (row[i] != null) {
                    widths[i] = Math.max(widths[i], row[i].length());
                }
            }
        }

        return widths;
    }
}