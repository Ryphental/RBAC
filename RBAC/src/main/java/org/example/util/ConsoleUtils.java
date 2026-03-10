package org.example.util;

import java.util.List;
import java.util.Scanner;

public class ConsoleUtils {

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String RED = "\u001B[31m";

    private ConsoleUtils() {}

    public static String promptString(Scanner scanner, String message, boolean required) {
        while (true) {
            System.out.print(BLUE + "? " + RESET + message + ": ");
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            if (!required) {
                return "";
            }

            System.out.println(YELLOW + "⚠ This field cannot be empty. Please try again." + RESET);
        }
    }

    public static int promptInt(Scanner scanner, String message, int min, int max) {
        while (true) {
            System.out.print(BLUE + "? " + RESET + message + " [" + min + "-" + max + "]: ");
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println(YELLOW + "⚠ Please enter a number between " + min + " and " + max + RESET);
            } catch (NumberFormatException e) {
                System.out.println(RED + "✗ Invalid number. Please try again." + RESET);
            }
        }
    }

    public static boolean promptYesNo(Scanner scanner, String message) {
        while (true) {
            System.out.print(BLUE + "? " + RESET + message + " (yes/no): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("yes") || input.equals("y")) {
                return true;
            }
            if (input.equals("no") || input.equals("n")) {
                return false;
            }

            System.out.println(YELLOW + "⚠ Please enter 'yes' or 'no'" + RESET);
        }
    }

    public static <T> T promptChoice(Scanner scanner, String message, List<T> options) {
        return promptChoice(scanner, message, options, true);
    }

    public static <T> T promptChoice(Scanner scanner, String message, List<T> options, boolean showNumbers) {
        if (options.isEmpty()) {
            throw new IllegalArgumentException("No options available");
        }

        System.out.println("\n" + BLUE + "═══════════════════════════════════════" + RESET);
        System.out.println(message);
        System.out.println(BLUE + "───────────────────────────────────────" + RESET);

        for (int i = 0; i < options.size(); i++) {
            System.out.printf("  %d. %s\n", i + 1, options.get(i).toString());
        }
        System.out.println(BLUE + "───────────────────────────────────────" + RESET);

        int choice = promptInt(scanner, "Enter your choice", 1, options.size());
        System.out.println(GREEN + "✓ Selected: " + options.get(choice - 1) + RESET);

        return options.get(choice - 1);
    }

    public static void printHeader(String title) {
        System.out.println("\n" + BLUE + "═══════════════════════════════════════" + RESET);
        System.out.println(BLUE + "  " + title + RESET);
        System.out.println(BLUE + "═══════════════════════════════════════" + RESET);
    }

    public static void printSuccess(String message) {
        System.out.println(GREEN + "✓ " + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(RED + "✗ " + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + "⚠ " + message + RESET);
    }

    public static void printSeparator() {
        System.out.println(BLUE + "───────────────────────────────────────" + RESET);
    }
}